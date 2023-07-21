package com.example.fimae.repository;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.fimae.models.Fimaers;
import com.example.fimae.models.Follows;
import com.example.fimae.models.Post;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskCompletionSource;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
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

        // ...

//        public Task<Boolean> isFollowing(String userId) {
//            TaskCompletionSource<Boolean> taskCompletionSource = new TaskCompletionSource<>();
//            String path = currentUser + "_" + userId;
//            followRef.document(path).addSnapshotListener(new EventListener<DocumentSnapshot>() {
//                        @Override
//                        public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
//
//                        }
//                    })
//                    .addOnSuccessListener(documentSnapshot -> {
//                        if (documentSnapshot.exists()) {
//                            taskCompletionSource.setResult(true);
//                        } else {
//                            taskCompletionSource.setResult(false);
//                        }
//                    })
//                    .addOnFailureListener(taskCompletionSource::setException);
//
//            return taskCompletionSource.getTask();
//        }
    public Task<ArrayList<Fimaers>> getFollowers2(String userId){
            ArrayList<Fimaers> fimaers= new ArrayList<>();
            TaskCompletionSource<ArrayList<Fimaers>> taskCompletionSource = new TaskCompletionSource<>();
            String path = currentUser + "_" + userId;
            followRef.whereEqualTo("following",userId).get()
                    .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                        @Override
                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                            for(DocumentChange doc: queryDocumentSnapshots.getDocumentChanges()){
                                Follows follows = doc.getDocument().toObject(Follows.class);
                                fimaersRef.document(follows.getFollower()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                    @Override
                                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                                        fimaers.add( documentSnapshot.toObject(Fimaers.class));
                                    }
                                });
                            }
                            taskCompletionSource.setResult(fimaers);
                        }
                    })
                    .addOnFailureListener(taskCompletionSource::setException);
            return taskCompletionSource.getTask();
    }
    public Task<ArrayList<Fimaers>> getFollowers(String userId) {
        ArrayList<Fimaers> fimaers = new ArrayList<>();
        TaskCompletionSource<ArrayList<Fimaers>> taskCompletionSource = new TaskCompletionSource<>();
        followRef.whereEqualTo("following", userId).get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    ArrayList<Task<DocumentSnapshot>> followerTasks = new ArrayList<>();

                    for (DocumentChange doc : queryDocumentSnapshots.getDocumentChanges()) {
                        Follows follows = doc.getDocument().toObject(Follows.class);
                        followerTasks.add(fimaersRef.document(follows.getFollower()).get());
                    }

                    // Wait for all the follower document tasks to complete
                    Tasks.whenAllSuccess(followerTasks)
                            .addOnSuccessListener(followerSnapshots -> {
                                for (Object snapshot :  followerSnapshots) {
                                    fimaers.add(((DocumentSnapshot)snapshot).toObject(Fimaers.class));
                                }
                                taskCompletionSource.setResult(fimaers);
                            })
                            .addOnFailureListener(taskCompletionSource::setException);
                })
                .addOnFailureListener(taskCompletionSource::setException);

        return taskCompletionSource.getTask();
    }


    public Task<Boolean> follow(String userId){
        TaskCompletionSource<Boolean> taskCompletionSource = new TaskCompletionSource<>();
        String path = currentUser + "_" + userId;
        Follows follows = new Follows();
        follows.setFollower(userId);
        follows.setFollowing(currentUser);
        follows.setId(path);

        followRef.document(path).set(follows).addOnSuccessListener(new OnSuccessListener<Void>() {
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
