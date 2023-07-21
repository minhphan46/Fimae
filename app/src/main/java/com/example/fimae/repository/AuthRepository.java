package com.example.fimae.repository;

import android.content.Intent;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.fimae.activities.AuthenticationActivity;
import com.example.fimae.activities.UpdateProfileActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

import java.util.concurrent.Executor;

public class AuthRepository {
    private static AuthRepository INSTANCE = new AuthRepository();

    FirebaseAuth auth = FirebaseAuth.getInstance();

    private AuthRepository() {
    };

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
                    .addOnCompleteListener((Executor) this, task -> {
                        if (task.isSuccessful()) {
                            // Đăng nhập thành công
                            FirebaseUser user = auth.getCurrentUser();
                            callback.onSignUpSuccess(user);
                        } else {
                            // Đăng nhập thất bại
                            String errorMessage = task.getException() != null ? task.getException().getMessage() : "lỗi không xác định";
                            callback.onSignUpError(errorMessage);
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
                            if(task.getResult().getAdditionalUserInfo().isNewUser())
                            {
                                callback.onFirstTimeSignIn(user);
                            }
                            else
                            {
                                callback.onSignInSuccess(user);
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
