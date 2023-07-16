package com.example.fimae.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import com.example.fimae.R;
import com.example.fimae.repository.AuthRepository;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.example.fimae.models.Fimaers;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.*;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;


public class AuthenticationActivity extends AppCompatActivity {

    private EditText editTextEmail, editTextPassword, editTextUsername;
    Button btnSignIn;
    ImageButton btnGoogleSignIn;
    TextView signUpTextView;
    FirebaseAuth auth = FirebaseAuth.getInstance();
    AuthRepository authRepository = AuthRepository.getInstance();

    GoogleSignInClient mGoogleSignInClient;
    FirebaseFirestore firestore = FirebaseFirestore.getInstance();
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    CollectionReference fimaeUsersRefer = firestore.collection("fimae-users");
    DatabaseReference usersRef = database.getReference("fimae-users");
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_authentication);
        editTextEmail = findViewById(R.id.editTextEmail);
        editTextPassword = findViewById(R.id.editTextPassword);
        signUpTextView = findViewById(R.id.textViewRegister);
        btnSignIn = findViewById(R.id.buttonLogin);
        btnGoogleSignIn = findViewById(R.id.googleImgBtn);
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

        signUpTextView.setOnClickListener(new View.OnClickListener() {
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
    void navToUpdateProfile(){
        Intent intent = new Intent(AuthenticationActivity.this, UpdateProfileActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }
    private void signIn(){
        String email = String.valueOf(editTextEmail.getText());
        String password = String.valueOf(editTextPassword.getText());
        authRepository.signIn(email, password, new AuthRepository.SignInCallback() {
            @Override
            public void onSignInSuccess(FirebaseUser user) {
                successAuthentication();
                Toast.makeText(getApplicationContext(), "DONE",
                        Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onSignInError(String errorMessage) {
                Toast.makeText(getApplicationContext(),errorMessage,Toast.LENGTH_SHORT).show();
            }
        });

    }
    private void signUp() {
        String email = String.valueOf(editTextEmail.getText());
        String password = String.valueOf(editTextPassword.getText());
        if (!(email.isEmpty() && password.isEmpty())) {
            authRepository.signUp(email, password, new AuthRepository.SignUpCallback() {
                @Override
                public void onSignUpSuccess(FirebaseUser user) {
                    navToUpdateProfile();
                }

                @Override
                public void onSignUpError(String errorMessage) {
                    Toast.makeText(getApplicationContext(), errorMessage,Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            Toast.makeText(getApplicationContext(), "Nhập tk mk đi bạn êi!",
                    Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == RC_SIGN_IN)
        {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                authRepository.googleAuth(account.getIdToken(), new AuthRepository.GoogleSignInCallback() {
                    @Override
                    public void onSignInSuccess(FirebaseUser user) {
                        successAuthentication();
                    }
                    @Override
                    public void onFirstTimeSignIn(FirebaseUser user) {
                        navToUpdateProfile();
                    }
                    @Override
                    public void onSignInError(String errorMessage) {
                        Toast.makeText(getApplicationContext(), errorMessage,Toast.LENGTH_SHORT).show();
                    }
                });
            } catch (ApiException e) {
                throw new RuntimeException(e);
            }
        }
    }


    }
