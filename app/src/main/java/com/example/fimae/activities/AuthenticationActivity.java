package com.example.fimae.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import com.example.fimae.R;
import com.example.fimae.models.FimaeUser;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.*;
import com.google.firebase.firestore.FirebaseFirestore;


public class AuthenticationActivity extends AppCompatActivity {

    private EditText editTextEmail, editTextPassword, editTextUsername;
    Button btnSignIn, btnSignUp, btnGoogleSignIn;
    FirebaseAuth auth = FirebaseAuth.getInstance();

    GoogleSignInClient mGoogleSignInClient;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_authentication);
        editTextEmail = findViewById(R.id.editTextEmail);
        editTextPassword = findViewById(R.id.editTextPassword);
        editTextUsername = findViewById(R.id.editUserName);
        btnSignUp = findViewById(R.id.buttonRegister);
        btnSignIn = findViewById(R.id.buttonLogin);
        btnGoogleSignIn = findViewById(R.id.buttonGoogle);
        progressDialog = new ProgressDialog(AuthenticationActivity.this);
        progressDialog.setTitle("Creating account");
        progressDialog.setMessage("We are creating your account");

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this,gso);

        btnGoogleSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signInWithGoogle();
            }
        });

        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signUp();
            }
        });
        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signIn();
            }
        });

        FirebaseUser currentUser = auth.getCurrentUser();
        if (currentUser != null) {
            successAuthentication();
        }
    }
    int RC_SIGN_IN = 9001;

    private void signInWithGoogle() {
        Intent intent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(intent,RC_SIGN_IN);
    }

    void successAuthentication(){
        Intent intent = new Intent(AuthenticationActivity.this, HomeActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }
    private void signIn(){
        String email = String.valueOf(editTextEmail.getText());
        String password = String.valueOf(editTextPassword.getText());
        if (!(email.isEmpty() && password.isEmpty())) {
            auth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                // Đăng nhập thành công
                                FirebaseUser user = auth.getCurrentUser();
                                successAuthentication();
                                // Tiến hành xử lý sau khi đăng nhập thành công
                            } else {
                                // Đăng nhập thất bại
                                Toast.makeText(getApplicationContext(), String.format("Đăng nhập thất bại. Vì %s", task.getException() != null ? task.getException().getMessage() : "lỗi không xác định"),
                                        Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
    }
    private void signUp() {
        String email = String.valueOf(editTextEmail.getText());
        String password = String.valueOf(editTextPassword.getText());
        String username = String.valueOf(editTextUsername.getText());
        if (!(email.isEmpty() && password.isEmpty() && username.isEmpty())) {
            auth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, task -> {
                        if (task.isSuccessful()) {
                            // Đăng ký thành công
                            FirebaseUser user = auth.getCurrentUser();
//                            String uid = user.getUid();
//                            UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
//                                    .setDisplayName(username).build();
//                            Random random = new Random();
//
//                            // Sinh số ngẫu nhiên từ 1 đến 1000
//                            int randomNumber = random.nextInt(1000) + 1;
//                            FimaeUser fimaeUser = new FimaeUser(
//                                    "1",
//                                    "minh",
//                                    "https://picsum.photos/200/300?random=" + randomNumber,
//                                    25,
//                                    true,
//                                    "lanh lung ik loi",
//                                    "eyJjdHkiOiJzdHJpbmdlZS1hcGk7dj0xIiwidHlwIjoiSldUIiwiYWxnIjoiSFMyNTYifQ.eyJqdGkiOiJTSy4wLnM1OFRaMnBJbkIwMFdWMlZmTlQ1RXRmU2xLQ2g3cy0xNjgyODk0NzE0IiwiaXNzIjoiU0suMC5zNThUWjJwSW5CMDBXVjJWZk5UNUV0ZlNsS0NoN3MiLCJleHAiOjE2ODU0ODY3MTQsInVzZXJJZCI6Im1pbmgifQ.rtlgkQhsZMhSUFnxfBk0zSeg0BPHRHHh4SQ54A1GTm8"
//                            );
                            successAuthentication();
                        } else {
                            // Đăng ký thất bại
                            Toast.makeText(getApplicationContext(), String.format("Đăng ký thất bại. Vì %s", task.getException() != null ? task.getException().getMessage() : "lỗi không xác định"),
                                    Toast.LENGTH_SHORT).show();
                        }
                    });
        } else {
            Toast.makeText(getApplicationContext(), "Nhập tk mk đi bạn êi!",
                    Toast.LENGTH_SHORT).show();
        }
    }
    private  void firebaseAuth(String idToken)
    {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken,null);
        auth.signInWithCredential(credential)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful())
                        {
                            FirebaseUser user = auth.getCurrentUser();

                            FimaeUser users = new FimaeUser(
                                    user.getUid(),
                                    user.getDisplayName(),
                                    user.getPhotoUrl().toString(),
                                    25,
                                    true,
                                    "",
                                    "eyJjdHkiOiJzdHJpbmdlZS1hcGk7dj0xIiwidHlwIjoiSldUIiwiYWxnIjoiSFMyNTYifQ.eyJqdGkiOiJTSy4wLnM1OFRaMnBJbkIwMFdWMlZmTlQ1RXRmU2xLQ2g3cy0xNjgyODk0NzE0IiwiaXNzIjoiU0suMC5zNThUWjJwSW5CMDBXVjJWZk5UNUV0ZlNsS0NoN3MiLCJleHAiOjE2ODU0ODY3MTQsInVzZXJJZCI6Im1pbmgifQ.rtlgkQhsZMhSUFnxfBk0zSeg0BPHRHHh4SQ54A1GTm8"
                            );

                            successAuthentication();
                        } else {
                            // Đăng ký thất bại
                            Toast.makeText(getApplicationContext(), String.format("Đăng ký thất bại. Vì %s", task.getException() != null ? task.getException().getMessage() : "lỗi không xác định"),
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == RC_SIGN_IN)
        {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuth(account.getIdToken());
            } catch (ApiException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
