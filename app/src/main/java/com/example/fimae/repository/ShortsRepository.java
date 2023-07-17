package com.example.fimae.repository;

import android.net.Uri;

import com.example.fimae.activities.PostMode;
import com.example.fimae.models.shorts.ShortMedia;
import com.example.fimae.models.shorts.ShortMediaType;
import com.example.fimae.service.FirebaseService;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskCompletionSource;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;


/**
 * Repository for managing short video and image posts.
 */
public class ShortsRepository {
    public final static String ShortsStoragePath = "shorts";
    static private ShortsRepository instance;

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



    /**
     * Creates a short video post with the provided description, video URI, post mode, and comment settings.
     *
     * @param description The description or caption for the video post.
     * @param uri The URI of the video file.
     * @param postMode The post mode for the video post (e.g., PUBLIC, PRIVATE, FRIENDS).
     * @param allowComment Determines whether comments are allowed on the video post.
     * @return A Task that represents the asynchronous operation, which resolves to a ShortMedia object representing the created video post.
     */
    public Task<ShortMedia> createShortVideo(String description, Uri uri, PostMode postMode, boolean allowComment){
        ArrayList<Uri> uris = new ArrayList<>();
        uris.add(uri);
        return createShort(description, ShortMediaType.VIDEO, uris, postMode, allowComment);
    }

    /**
     * Creates a short image post with the provided description, image URIs, post mode, and comment settings.
     *
     * @param description The description or caption for the image post.
     * @param uris The URIs of the image files.
     * @param postMode The post mode for the image post (e.g., PUBLIC, PRIVATE, FRIENDS).
     * @param allowComment Determines whether comments are allowed on the image post.
     * @return A Task that represents the asynchronous operation, which resolves to a ShortMedia object representing the created image post.
     */
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
}
