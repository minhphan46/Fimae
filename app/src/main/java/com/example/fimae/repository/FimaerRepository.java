package com.example.fimae.repository;

import androidx.annotation.NonNull;

import com.example.fimae.models.Conversation;
import com.example.fimae.models.Fimaers;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskCompletionSource;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Objects;

public class FimaerRepository {
    private final FirebaseFirestore firestore = FirebaseFirestore.getInstance();

    CollectionReference fimaersRef = firestore.collection("fimaers");
    private static FimaerRepository instance;
    public static synchronized FimaerRepository getInstance(){
        if(instance == null){
            return new FimaerRepository();
        }
        else return instance;
    }
    public Task<Fimaers> getFimaerById(String id){
        TaskCompletionSource<Fimaers> taskCompletionSource = new TaskCompletionSource<>();
        fimaersRef.document(id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()){
                    Fimaers fimaers = task.getResult().toObject(Fimaers.class);
                    taskCompletionSource.setResult(fimaers);
                } else {
                    taskCompletionSource.setException(Objects.requireNonNull(task.getException()));
                }
            }
        });
        return taskCompletionSource.getTask();
    }
}