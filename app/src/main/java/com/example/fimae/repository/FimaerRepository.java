package com.example.fimae.repository;

import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.MutableLiveData;

import com.example.fimae.R;
import com.example.fimae.models.Conversation;
import com.example.fimae.models.Fimaers;
import com.example.fimae.models.GenderMatch;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskCompletionSource;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.functions.FirebaseFunctions;
import com.google.firebase.functions.HttpsCallableResult;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class FimaerRepository {
    private final String TAG = getClass().getSimpleName();

    private FirebaseFunctions mFunctions = FirebaseFunctions.getInstance();

    private final FirebaseFirestore firestore = FirebaseFirestore.getInstance();
    private FirebaseAuth auth = FirebaseAuth.getInstance();

    private CollectionReference fimaersRef = firestore.collection("fimaers");
    private StorageReference storageReference = FirebaseStorage.getInstance().getReference("AvatarPics");

    @Nullable
    public String getCurrentUserUid()
    {
        return auth.getUid();
    }

    public Task<String> refreshToken()
    {

        return mFunctions
            .getHttpsCallable("refreshToken")
            .call()
            .continueWith(new Continuation<HttpsCallableResult, String>() {
                @Override
                public String then(@NonNull Task<HttpsCallableResult> task) throws Exception {
                    String result = (String) task.getResult().getData();
                    return result;
                }
            });
    }

    public MutableLiveData<Fimaers> getCurrentUser() {
            Log.i("USER", "Let get user");

            getFimaerById(auth.getUid()).addOnCompleteListener(new OnCompleteListener<Fimaers>() {
                @Override
                public void onComplete(@NonNull Task<Fimaers> task) {
                    if(task.isSuccessful())
                    {
                        Log.i(TAG, "onComplete: " + task.getResult().getName());
                        currentUser.setValue(task.getResult());
                    }
                    else
                    {
                        Log.e(TAG,"Fail to get current user");
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.e(TAG, "onFailure: " + e.getMessage().toString());
                }
            });
        return currentUser;

    }

    MutableLiveData<Fimaers> currentUser = new MutableLiveData<Fimaers>();

    private static FimaerRepository instance;

    public static synchronized FimaerRepository getInstance(){
        if(instance == null){
            instance =  new FimaerRepository();
        }
        return instance;
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
    public Task<Void> updateProfile(Fimaers user)
    {
        String uid = user.getUid();
        currentUser.setValue(user);
        return fimaersRef.document(uid).set(user);
    }
    public void uploadAvatar(String uid, Uri imageURI, final UploadAvatarCallback callback) {
        storageReference.child(uid + ".jpg").putFile(imageURI).addOnSuccessListener(taskSnapshot -> {
            Task<Uri> task = taskSnapshot.getStorage().getDownloadUrl();
            task.addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if(task.isSuccessful())
                    {
                        callback.onUploadSuccess(task.getResult());
                    } else {
                       String errorMessage = task.getException() != null ? task.getException().getMessage() : "lỗi không xác định";
                        callback.onUploadError(errorMessage);
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    callback.onUploadError(e.getMessage());
                }
            });
        });
    }
    public Task<ArrayList<Fimaers>> getFimaersByIds(ArrayList<String> id){
        TaskCompletionSource<ArrayList<Fimaers>> taskCompletionSource = new TaskCompletionSource<>();
        ArrayList<Fimaers> fimaers = new ArrayList<>();
        fimaersRef.whereIn("uid", id).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()){
                for (DocumentSnapshot documentSnapshot : task.getResult().getDocuments()){
                    fimaers.add(documentSnapshot.toObject(Fimaers.class));
                }
                taskCompletionSource.setResult(fimaers);
            } else {
                taskCompletionSource.setException(Objects.requireNonNull(task.getException()));
            }
        });
        return taskCompletionSource.getTask();
    }
    public interface UploadAvatarCallback {
        void onUploadSuccess(Uri uri);
        void onUploadError(String errorMessage);
    }
    public interface GetFimaerCallback {
        void updateNumOfUserOnline(int numOfUserOnline);
    }
    public ListenerRegistration getNumOfUserOnline(GetFimaerCallback callback){
        ListenerRegistration listenerRegistration = fimaersRef.addSnapshotListener((value, error) -> {
            if (error != null){
                Log.e(TAG, "Listen failed.", error);
                return;
            }
            if (value != null && !value.isEmpty()){
                ArrayList<Fimaers> fimaers = new ArrayList<>();
                for (DocumentSnapshot documentSnapshot : value.getDocuments()){
                    Fimaers fimaer = documentSnapshot.toObject(Fimaers.class);
                    if (fimaer != null && fimaer.isOnline()){
                        fimaers.add(fimaer);
                    }
                }
               callback.updateNumOfUserOnline(fimaers.size());
            } else {
                Log.e(TAG, "No data");
            }
        });
        return listenerRegistration;
    }

}
