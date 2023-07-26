package com.example.fimae.repository;

import android.content.Intent;

import androidx.annotation.NonNull;

import com.example.fimae.activities.CreateProfileActivity;
import com.example.fimae.activities.HomeActivity;
import com.example.fimae.activities.SplashActivity;
import com.example.fimae.models.Fimaers;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskCompletionSource;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.concurrent.Executor;

public class AuthRepository {

    private final FirebaseFirestore firestore = FirebaseFirestore.getInstance();
    private static AuthRepository INSTANCE = new AuthRepository();
    private CollectionReference fimaersRef = firestore.collection("fimaers");

    FirebaseAuth auth = FirebaseAuth.getInstance();

    private AuthRepository() {
    };

    public FirebaseUser getCurrentUser()
    {
        return auth.getCurrentUser();
    }
    public Task<Boolean> hasProfile()
    {
        TaskCompletionSource<Boolean> taskCompletionSource = new TaskCompletionSource<>();
        fimaersRef.document(auth.getUid()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull @NotNull Task<DocumentSnapshot> task) {
                if (task.getResult().exists()) {
                    taskCompletionSource.setResult(true);
                } else {
                    taskCompletionSource.setResult(false);
                }
                if(!task.isSuccessful())
                {
                    taskCompletionSource.setException(Objects.requireNonNull(task.getException()));
                }
            }
        });
        return taskCompletionSource.getTask();
    }

    public static AuthRepository getInstance() {
        return(INSTANCE);
    }

    public void signIn(String email, String password, final SignInCallback callback) {
        if (!(email.isEmpty() && password.isEmpty())) {
            auth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                // Đăng nhập thành công
                                FirebaseUser user = auth.getCurrentUser();
                                OneSignalRepo.setExternalId(user.getUid());
                                callback.onSignInSuccess(user);
                            } else {
                                // Đăng nhập thất bại
                                String errorMessage = task.getException() != null ? task.getException().getMessage() : "lỗi không xác định";
                                callback.onSignInError(errorMessage);
                            }
                        }
                    });
        }
    }

    public void signUp(String email, String password, final SignUpCallback callback) {
        if (!(email.isEmpty() && password.isEmpty())) {
            auth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        // Đăng nhập thành công
                        FirebaseUser user = auth.getCurrentUser();
                        callback.onSignUpSuccess(user);
                    } else {
                        // Đăng nhập thất bại
                        String errorMessage = task.getException() != null ? task.getException().getMessage() : "lỗi không xác định";
                        callback.onSignUpError(errorMessage);
                    }
                }
            });
        }
    }

    public void googleAuth(String idToken, final GoogleSignInCallback callback) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken,null);
        auth.signInWithCredential(credential)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful())
                        {
                            FirebaseUser user = auth.getCurrentUser();
                            OneSignalRepo.setExternalId(user.getUid());
                            if(task.getResult().getAdditionalUserInfo().isNewUser())
                            {
                                callback.onFirstTimeSignIn(user);
                            }
                            else
                            {

                                hasProfile().addOnCompleteListener(new OnCompleteListener<Boolean>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Boolean> task) {
                                        if(task.isSuccessful())
                                        {
                                            if(task.getResult())
                                            {
                                                callback.onSignInSuccess(user);
                                            }
                                            else
                                            {
                                                callback.onFirstTimeSignIn(user);
                                            }
                                        }
                                    }
                                });
                            }
                        } else {
                            // Đăng ký thất bại
                            String errorMessage = task.getException() != null ? task.getException().getMessage() : "lỗi không xác định";
                            callback.onSignInError(errorMessage);
                        }
                    }
                });
    }

    public void signOut()
    {
        OneSignalRepo.removeExternalId();
        auth.signOut();
    }

    public interface SignInCallback {
        void onSignInSuccess(FirebaseUser user);
        void onSignInError(String errorMessage);

    }
    public interface GoogleSignInCallback {
        void onSignInSuccess(FirebaseUser user);
        void onSignInError(String errorMessage);
        void onFirstTimeSignIn(FirebaseUser user);
    }
    public interface SignUpCallback {
        void onSignUpSuccess(FirebaseUser user);
        void onSignUpError(String errorMessage);
    }


}
