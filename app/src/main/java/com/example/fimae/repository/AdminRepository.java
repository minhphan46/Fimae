package com.example.fimae.repository;

import androidx.annotation.NonNull;

import com.example.fimae.models.UserDisable;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskCompletionSource;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class AdminRepository {

    private static AdminRepository adminRepository;
    private AdminRepository(){

    }
    public static AdminRepository getInstance(){
        if(adminRepository == null) adminRepository = new AdminRepository();
        return adminRepository;
    }
    private CollectionReference disableRef = FirebaseFirestore.getInstance().collection("user_disable");
    public Task<Boolean> disableUser(UserDisable userDisable){
        TaskCompletionSource<Boolean> taskCompletionSource = new TaskCompletionSource<>();
        DocumentReference disableUser = disableRef.document(userDisable.getUserId());
        disableUser.set(userDisable).addOnSuccessListener(new OnSuccessListener<Void>() {
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
