package com.example.fimae.repository;

import androidx.annotation.NonNull;

import com.example.fimae.models.Follows;
import com.example.fimae.models.Post;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskCompletionSource;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

public class FollowRepository {
    FirebaseAuth firebaseAuth;
    private FirebaseFirestore firestore;
    private CollectionReference fimaersRef;
    private StorageReference storageReference;
    public CollectionReference followRef;
    String currentUser;
    DatabaseReference postReference;
    private FollowRepository(){
        firestore = FirebaseFirestore.getInstance();
        fimaersRef = firestore.collection("fimaers");
        followRef = firestore.collection("follows");
        firebaseAuth = FirebaseAuth.getInstance();
        currentUser = firebaseAuth.getUid();
    };
    private static  FollowRepository followRepository;
    public static FollowRepository getInstance(){
        if(followRepository == null) followRepository = new FollowRepository();
        return followRepository;
    }
    public Task<Boolean> isFollowing(String userId){
        TaskCompletionSource<Boolean> taskCompletionSource = new TaskCompletionSource<>();
        String path = currentUser + "_" + userId;
        followRef.document(path).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                taskCompletionSource.setResult(true);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                taskCompletionSource.setResult(false);
            }
        });
        return taskCompletionSource.getTask();
    }
    public Task<Boolean> follow(String userId){
        TaskCompletionSource<Boolean> taskCompletionSource = new TaskCompletionSource<>();
        String path = currentUser + "_" + userId;
        Follows follows = new Follows();
        follows.setFollower(userId);
        follows.setFollowing(currentUser);
        followRef.add(follows).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
            @Override
            public void onSuccess(DocumentReference reference) {
                taskCompletionSource.setResult(true);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                taskCompletionSource.setResult(false);
            }
        });
        return taskCompletionSource.getTask();
    }
    public Task<Boolean> unFollow(String userId){
        TaskCompletionSource<Boolean> taskCompletionSource = new TaskCompletionSource<>();
        String path = currentUser + "_" + userId;
        Follows follows = new Follows();
        follows.setFollower(userId);
        follows.setFollowing(currentUser);
        followRef.document(path).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                taskCompletionSource.setResult(true);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                taskCompletionSource.setResult(false);
            }
        });
        return taskCompletionSource.getTask();
    }
}
