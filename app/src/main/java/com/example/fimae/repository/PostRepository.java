package com.example.fimae.repository;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.fimae.activities.DetailPostActivity;
import com.example.fimae.activities.OnChatActivity;
import com.example.fimae.activities.PostMode;
import com.example.fimae.adapters.PostAdapter;
import com.example.fimae.models.Conversation;
import com.example.fimae.models.Fimaers;
import com.example.fimae.models.Post;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskCompletionSource;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import android.content.ContentResolver;

import com.google.type.DateTime;
import com.stringee.messaging.User;

import java.lang.reflect.Type;
import java.sql.Time;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class PostRepository {
    FirebaseAuth firebaseAuth;
    private FirebaseFirestore firestore;
    private CollectionReference fimaersRef;
    private StorageReference storageReference;
    private CollectionReference postRef;
    List<Post> postList;
    Fimaers currentUser;
    DatabaseReference postReference;
    private PostRepository(){
        postList = new ArrayList<>();
        storageReference = FirebaseStorage.getInstance().getReference("posts");
        postReference = FirebaseDatabase.getInstance().getReference("Posts");
        firestore = FirebaseFirestore.getInstance();
        fimaersRef = firestore.collection("fimaers");
        postRef = firestore.collection("posts");
        firebaseAuth = FirebaseAuth.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference("AvatarPics");
        init();
    };
    private void init(){
        fimaersRef.document(firebaseAuth.getCurrentUser().getUid()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                currentUser = documentSnapshot.toObject(Fimaers.class);
            }
        });
    }
    private static PostRepository postRepository;
    public static PostRepository getInstance(){
        if(postRepository == null) postRepository = new PostRepository();
        return  postRepository;
    }
    public Task<Post> getPostById(String id){
        TaskCompletionSource<Post> taskCompletionSource = new TaskCompletionSource<>();
        postRef.document(id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                 taskCompletionSource.setResult( task.getResult().toObject(Post.class));
            }
        });
        return  taskCompletionSource.getTask();
    }
    public void getUserById(String id, final PostAdapter.CallBack<Fimaers> callBack){
        fimaersRef.document(id).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                  Fimaers fimaers = documentSnapshot.toObject(Fimaers.class);
                  callBack.onSuccess(fimaers);
            }
        });
    }
    public void goToChatWithUser(String userId, Context context){
        ChatRepository.getInstance().getOrCreateFriendConversation(userId).addOnCompleteListener(new OnCompleteListener<Conversation>() {
            @Override
            public void onComplete(@NonNull Task<Conversation> task) {
                if(task.getResult() != null){
                    Intent intent = new Intent(context, OnChatActivity.class);
                    intent.putExtra("conversationID", task.getResult().getId());
                    context.startActivity(intent);
                }
            }
        });
    }
    public Fimaers getCurrentUser(){
        return currentUser;
    }
    public void addNewPost2(List<Uri> imageList, String description, PostMode mode, Context context)  {
        List<String> downloadUrls = new ArrayList<>();
        if (imageList != null && imageList.size() > 0) {
            List<Task<Uri>> uploadTasks = new ArrayList<>();
            for (Uri imageUri : imageList) {

                StorageReference fileRef = storageReference.child(System.currentTimeMillis() + "." + getFileExtension(imageUri, context));
                Task<Uri> uploadTask = fileRef.putFile(imageUri)
                        .continueWithTask(task -> {
                            if (!task.isSuccessful()) {
                                throw task.getException();
                            }
                            return fileRef.getDownloadUrl();
                        });
                uploadTasks.add(uploadTask);
            }

            Tasks.whenAllComplete(uploadTasks)
                    .addOnCompleteListener(taskList -> {
                        for (Task<Uri> task : uploadTasks) {
                            if (task.isSuccessful()) {
                                Uri downloadUri = task.getResult();
                                downloadUrls.add(downloadUri.toString());
                            } else {
                                task.getException().getMessage();
                            }
                        }
                        DocumentReference postdoc = postRef.document();
                        HashMap<String, Object> hashMap = new HashMap<>();
                        hashMap.put("postId", postdoc.getId());
                        hashMap.put("postImages", downloadUrls);
                        hashMap.put("content", description);
                        hashMap.put("publisher", currentUser.getUid());
                        hashMap.put("postMode", mode);
                        hashMap.put("likes", new HashMap<>());
                        hashMap.put("saves", new HashMap<>());
                        hashMap.put("numberOfComments", 0);
                        hashMap.put("timeCreated", new Timestamp(new Date()));
                        postdoc.set(hashMap).addOnCompleteListener(task ->
                                Toast.makeText(context, "Thêm bài viết thành công", Toast.LENGTH_SHORT).show());
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
        }
    }
    public Task<Boolean> addNewPost(List<Uri> imageList, String description, PostMode mode, Context context) {
        TaskCompletionSource<Boolean> taskCompletionSource = new TaskCompletionSource<>();
        List<String> downloadUrls = new ArrayList<>();

        if (imageList != null && imageList.size() > 0) {
            List<Task<Uri>> uploadTasks = new ArrayList<>();

            for (Uri imageUri : imageList) {
                StorageReference fileRef = storageReference.child(System.currentTimeMillis() + "." + getFileExtension(imageUri, context));
                Task<Uri> uploadTask = fileRef.putFile(imageUri)
                        .continueWithTask(task -> {
                            if (!task.isSuccessful()) {
                                throw task.getException();
                            }
                            return fileRef.getDownloadUrl();
                        });
                uploadTasks.add(uploadTask);
            }

            Tasks.whenAll(uploadTasks)
                    .addOnSuccessListener(taskList -> {
                        for (Task<Uri> task : uploadTasks) {
                            if (task.isSuccessful()) {
                                Uri downloadUri = task.getResult();
                                downloadUrls.add(downloadUri.toString());
                            }
                        }

                        DocumentReference postdoc = postRef.document();
                        HashMap<String, Object> hashMap = new HashMap<>();
                        hashMap.put("postId", postdoc.getId());
                        hashMap.put("postImages", downloadUrls);
                        hashMap.put("content", description);
                        hashMap.put("publisher", currentUser.getUid());
                        hashMap.put("postMode", mode);
                        hashMap.put("likes", new HashMap<>());
                        hashMap.put("saves", new HashMap<>());
                        hashMap.put("numberOfComments", 0);
                        hashMap.put("timeCreated", new Timestamp(new Date()));

                        postdoc.set(hashMap).addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                Toast.makeText(context, "Thêm bài viết thành công", Toast.LENGTH_SHORT).show();
                                taskCompletionSource.setResult(true);
                            } else {
                                Toast.makeText(context, "Thêm bài viết thất bại", Toast.LENGTH_SHORT).show();
                                taskCompletionSource.setResult(false);
                            }
                        });
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
                        taskCompletionSource.setResult(false);
                    });
        } else {
            DocumentReference postdoc = postRef.document();
            HashMap<String, Object> hashMap = new HashMap<>();
            hashMap.put("postId", postdoc.getId());
            hashMap.put("postImages", downloadUrls);
            hashMap.put("content", description);
            hashMap.put("publisher", currentUser.getUid());
            hashMap.put("postMode", mode);
            hashMap.put("likes", new HashMap<>());
            hashMap.put("saves", new HashMap<>());
            hashMap.put("numberOfComments", 0);
            hashMap.put("timeCreated", new Timestamp(new Date()));

            postdoc.set(hashMap).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Toast.makeText(context, "Thêm bài viết thành công", Toast.LENGTH_SHORT).show();
                    taskCompletionSource.setResult(true);
                } else {
                    Toast.makeText(context, "Thêm bài viết thất bại", Toast.LENGTH_SHORT).show();
                    taskCompletionSource.setResult(false);
                }
            });

        }
        return taskCompletionSource.getTask();
    }

    public Task<Boolean> editPost(List<String> editedImageList, List<Uri> imageList, String description, PostMode mode, Context context, String postId) {
        TaskCompletionSource<Boolean> taskCompletionSource = new TaskCompletionSource<>();
        List<String> downloadUrls = new ArrayList<>();

        if (imageList != null && imageList.size() > 0) {
            List<Task<Uri>> uploadTasks = new ArrayList<>();

            for (Uri imageUri : imageList) {
                StorageReference fileRef = storageReference.child(System.currentTimeMillis() + "." + getFileExtension(imageUri, context));
                Task<Uri> uploadTask = fileRef.putFile(imageUri)
                        .continueWithTask(task -> {
                            if (!task.isSuccessful()) {
                                throw task.getException();
                            }
                            return fileRef.getDownloadUrl();
                        });
                uploadTasks.add(uploadTask);
            }

            Tasks.whenAll(uploadTasks)
                    .addOnSuccessListener(uriTasks -> {
                        for (Task<Uri> task : uploadTasks) {
                            if (task.isSuccessful()) {
                                Uri downloadUri = task.getResult();
                                downloadUrls.add(downloadUri.toString());
                            }
                        }

                        editedImageList.addAll(downloadUrls);
                        HashMap<String, Object> hashMap = new HashMap<>();
                        hashMap.put("postImages", editedImageList);
                        hashMap.put("content", description);
                        hashMap.put("postMode", mode);
                        hashMap.put("timeEdited", new Timestamp(new Date()));

                        postRef(postId).update(hashMap).addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                Toast.makeText(context, "Chỉnh sửa bài viết thành công", Toast.LENGTH_SHORT).show();
                                taskCompletionSource.setResult(true);
                            } else {
                                Toast.makeText(context, "Chỉnh sửa bài viết thất bại", Toast.LENGTH_SHORT).show();
                                taskCompletionSource.setResult(false);
                            }
                        });
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
                        taskCompletionSource.setResult(false);
                    });
        } else {
            HashMap<String, Object> hashMap = new HashMap<>();
            hashMap.put("postImages", editedImageList);
            hashMap.put("content", description);
            hashMap.put("postMode", mode);
            hashMap.put("timeEdited", new Timestamp(new Date()));
            postRef(postId).update(hashMap).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Toast.makeText(context, "Chỉnh sửa bài viết thành công", Toast.LENGTH_SHORT).show();
                    taskCompletionSource.setResult(true);
                } else {
                    Toast.makeText(context, "Chỉnh sửa bài viết thất bại", Toast.LENGTH_SHORT).show();
                    taskCompletionSource.setResult(false);
                }
            });
        }

        return taskCompletionSource.getTask();
    }

    public void editPost2(List<String> editedImageList ,List<Uri> imageList, String description, PostMode mode, Context context, String postId)  {
        List<String> downloadUrls = new ArrayList<>();
        if (imageList != null && imageList.size() > 0) {
            List<Task<Uri>> uploadTasks = new ArrayList<>();
            for (Uri imageUri : imageList) {

                StorageReference fileRef = storageReference.child(System.currentTimeMillis() + "." + getFileExtension(imageUri,context));
                Task<Uri> uploadTask = fileRef.putFile(imageUri)
                        .continueWithTask(task -> {
                            if (!task.isSuccessful()) {
                                throw task.getException();
                            }
                            return fileRef.getDownloadUrl();
                        });
                uploadTasks.add(uploadTask);
            }

            Tasks.whenAllComplete(uploadTasks)
                    .addOnCompleteListener(taskList -> {
                        for (Task<Uri> task : uploadTasks) {
                            if (task.isSuccessful()) {
                                Uri downloadUri = task.getResult();
                                downloadUrls.add(downloadUri.toString());
                            } else {
                                task.getException().getMessage();
                            }
                        }
                        editedImageList.addAll(downloadUrls);
                        HashMap<String , Object> hashMap = new HashMap<>();
                        hashMap.put("postImages" ,editedImageList);
                        hashMap.put("content" , description);
                        hashMap.put("postMode", mode);
                        hashMap.put("timeEdited", new Timestamp(new Date()));
                        postRef(postId).update(hashMap).addOnCompleteListener(task ->
                                Toast.makeText(context, "Chỉnh sửa bài viết thành công",Toast.LENGTH_SHORT).show());
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
        }
    }

    public void updateNumOfComment(String postId){
        final int[] number = {0};

        postRef(postId).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
               Post post = documentSnapshot.toObject(Post.class);
               number[0] = (post.getNumberOfComments() + 1);
                postRef(postId).update("numberOfComments", number[0]);
            }
        });
    }
    public DocumentReference postRef(String id){
        DocumentReference ref =  FirebaseFirestore.getInstance().collection("posts")
                .document(id);
        return ref;

    }
    private String getFileExtension(Uri uri, Context context) {
        ContentResolver contentResolver = context.getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();

        return mime.getExtensionFromMimeType(contentResolver.getType(uri));
    }
}
