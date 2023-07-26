package com.example.fimae.repository;

import android.net.Uri;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.fimae.activities.PostMode;
import com.example.fimae.models.Post;
import com.example.fimae.models.shorts.ShortMedia;
import com.example.fimae.models.shorts.ShortMediaType;
import com.example.fimae.service.FirebaseService;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskCompletionSource;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.function.BiConsumer;

public class ShortsRepository {
    public final static String ShortsStoragePath = "shorts";
    static private ShortsRepository instance;
    private FollowRepository followRepository = FollowRepository.getInstance();

    static public ShortsRepository getInstance() {
        if (instance == null) {
            instance = new ShortsRepository();
        }
        return instance;
    }

    private FirebaseFirestore firestore;
    private CollectionReference shortsRef;
    private StorageReference storageReference;

    private ShortsRepository() {
        firestore = FirebaseFirestore.getInstance();
        shortsRef = firestore.collection("shorts");
        storageReference = FirebaseStorage.getInstance().getReference("shorts");
    }

    public Task<ShortMedia> createShortVideo(String description, Uri uri, PostMode postMode, boolean allowComment){
        ArrayList<Uri> uris = new ArrayList<>();
        uris.add(uri);
        return createShort(description, ShortMediaType.VIDEO, uris, postMode, allowComment);
    }

    public Task<ShortMedia> createShortImages(String description, ArrayList<Uri> uris, PostMode postMode, boolean allowComment){
        return createShort(description, ShortMediaType.IMAGES, uris, postMode, allowComment);
    }

    private Task<ShortMedia> createShort(String description, ShortMediaType type, ArrayList<Uri> uris, PostMode postMode, boolean allowComment) {
        TaskCompletionSource<ShortMedia> taskCompletionSource = new TaskCompletionSource<>();
        if(type == ShortMediaType.VIDEO && uris.size() != 1){
            taskCompletionSource.setException(new IllegalArgumentException("Video must have only one uri"));
        }
        String uid = FirebaseAuth.getInstance().getUid();
        DocumentReference documentReference = shortsRef.document();
        String path = ShortsStoragePath + "/" + uid + "/" + documentReference.getId();
        FirebaseService.getInstance().uploadTaskFiles(path, uris).whenComplete(new BiConsumer<List<String>, Throwable>() {
            @Override
            public void accept(List<String> strings, Throwable throwable) {
                if(throwable != null){
                    taskCompletionSource.setException((Exception) throwable);
                }else{
                    ShortMedia shortMedia;
                    if(type == ShortMediaType.VIDEO){
                        shortMedia = ShortMedia.createShortVideo(documentReference.getId(), description, strings.get(0), postMode, allowComment);
                    }else{
                        shortMedia = ShortMedia.createShortImages(documentReference.getId(), description, (ArrayList<String>) strings, postMode, allowComment);
                    }
                    documentReference.set(shortMedia).addOnCompleteListener(task -> {
                        if(task.isSuccessful()){
                            taskCompletionSource.setResult(shortMedia);
                        }else{
                            taskCompletionSource.setException(task.getException());
                        }
                    });
                }
            }
        });
        return taskCompletionSource.getTask();
    };

    public Query getShortQuery() {
        return shortsRef.orderBy("timeCreated", Query.Direction.DESCENDING);
    }

    public Query getShortUserQuery(String uid) {
        return shortsRef.whereEqualTo("uid", uid).orderBy("timeCreated", Query.Direction.DESCENDING);
    }

    public interface CanWatchResultListener {
        void onCanWatch(boolean canWatch);
    }

    public void checkIsCanWatch(ShortMedia shortMedia, CanWatchResultListener listener) {
        if(FirebaseAuth.getInstance().getUid().equals(shortMedia.getUid())) {
            listener.onCanWatch(true);
        }
        else if (shortMedia.getPostMode() == PostMode.PUBLIC) {
            listener.onCanWatch(true);
        } else if (shortMedia.getPostMode() == PostMode.PRIVATE) {
            // Check if the current user is the owner
            boolean isCurrentUser = FirebaseAuth.getInstance().getUid().equals(shortMedia.getUid());
            listener.onCanWatch(isCurrentUser);
        } else {
            // Mode: FRIENDS
            ShortsRepository.getInstance().checkUserFollowed(FirebaseAuth.getInstance().getUid(), shortMedia, new FollowRepository.FollowCheckListener() {
                @Override
                public void onFollowCheckResult(boolean isFollowed) {
                    // User đã follow
                    listener.onCanWatch(isFollowed);
                }
            });
        }
    }

    public Task<ShortMedia> updateShort(ShortMedia shortMedia) {
        TaskCompletionSource<ShortMedia> taskCompletionSource = new TaskCompletionSource<>();
        shortsRef.document(shortMedia.getId()).set(shortMedia).addOnCompleteListener(task -> {
            if(task.isSuccessful()){
                taskCompletionSource.setResult(shortMedia);
            }else{
                taskCompletionSource.setException(task.getException());
            }
        });
        return taskCompletionSource.getTask();
    }

    // hàm likeShort, truyền vào là uid người like, id của short, trả về là short đã like
    // kiểm tra xem người đó đã like hay chưa, nếu chưa thì like, nếu rồi thì unlike
    // like thì thêm uid của người đó vào list like, unlike thì xóa uid của người đó khỏi list like
    // sau đó update short đó lên firestore
    public Task<ShortMedia> handleLikeShort(String uid, ShortMedia shortMedia) {
        TaskCompletionSource<ShortMedia> taskCompletionSource = new TaskCompletionSource<>();
        boolean isLiked = checkUserLiked(uid, shortMedia);
        shortsRef.document(shortMedia.getId()).update("usersLiked." + uid, !isLiked).addOnCompleteListener(task -> {
            if(task.isSuccessful()){
                shortMedia.getUsersLiked().put(uid, !isLiked);
                taskCompletionSource.setResult(shortMedia);
            }else{
                taskCompletionSource.setException(Objects.requireNonNull(task.getException()));
            }
        });
        return taskCompletionSource.getTask();
    }

    // hàm kiểm tra user đã like bài post hay chưa, truyền vào là uid của user và ShortMedia shortMedia
    // trả về là true nếu user đã like, false nếu chưa like
    public boolean checkUserLiked(String uid, ShortMedia shortMedia) {
        if(shortMedia.getUsersLiked() == null) return false;
        return shortMedia.getUsersLiked().containsKey(uid) && Boolean.TRUE.equals(shortMedia.getUsersLiked().get(uid));
    }

    // hàm lấy số lượng like, đếm value là true trong map usersLiked
    public int getLikeCount(ShortMedia shortMedia) {
        if(shortMedia.getUsersLiked() == null) return 0;
        int count = 0;
        for (Boolean value : shortMedia.getUsersLiked().values()) {
            if (Boolean.TRUE.equals(value)) {
                count++;
            }
        }
        return count;
    }

    // hàm thêm user watched vào list usersWatched
    public void handleWatchedShort(String uidUser, ShortMedia shortMedia){
        TaskCompletionSource<ShortMedia> taskCompletionSource = new TaskCompletionSource<>();
        shortsRef.document(shortMedia.getId()).update("usersWatched." + uidUser, new Date()).addOnCompleteListener(task -> {
            if(task.isSuccessful()){
            shortMedia.getUsersWatched().put(uidUser, new Date());
                taskCompletionSource.setResult(shortMedia);
            }else{
                taskCompletionSource.setException(Objects.requireNonNull(task.getException()));
            }
        });
        taskCompletionSource.getTask();
    }

    public int getNumOfWatched(ShortMedia shortMedia){
        if(shortMedia.getUsersWatched() == null) return 0;
        return shortMedia.getUsersWatched().values().size();
    }
    // get short by id
    public Task<ShortMedia> getShortById(String id) {
        TaskCompletionSource<ShortMedia> taskCompletionSource = new TaskCompletionSource<>();
        shortsRef.document(id).get().addOnCompleteListener(task -> {
            if(task.isSuccessful()){
                taskCompletionSource.setResult(Objects.requireNonNull(task.getResult()).toObject(ShortMedia.class));
            }else{
                taskCompletionSource.setException(Objects.requireNonNull(task.getException()));
            }
        });
        return taskCompletionSource.getTask();
    }

    // check user is followed or not
    public void checkUserFollowed(String uidCurrenUser, ShortMedia shortMedia, FollowRepository.FollowCheckListener followCheckListener) {
        // nếu là post của chính user thì trả về true
        if (shortMedia.getUid().equals(uidCurrenUser)) {
            followCheckListener.onFollowCheckResult(true);
            return;
        }

        // nếu không phải, kiểm tra xem user có trong list followers của user khác hay không
        followRepository.followRef.document(uidCurrenUser + "_" + shortMedia.getUid()).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                if (error != null) {
                    followCheckListener.onFollowCheckResult(false);
                    return;
                }

                if (value != null && value.exists()) {
                    followCheckListener.onFollowCheckResult(true);
                } else {
                    followCheckListener.onFollowCheckResult(false);
                }
            }
        });
    }
    // comment
    public Query getShortCommentQuery(String idShort) {
        return shortsRef.document(idShort).collection("comments").orderBy("timeCreated", Query.Direction.ASCENDING);
    }
    // add comment
    public void addShortCommentNum(int numOfComment, ShortMedia video) {
        int newNumOfComment = video.getNumOfComments() + numOfComment;
        video.setNumOfComments(newNumOfComment);
        // update
        shortsRef.document(video.getId()).update("numOfComments", newNumOfComment);
    }
}
