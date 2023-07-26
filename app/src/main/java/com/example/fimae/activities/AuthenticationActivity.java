package com.example.fimae.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.os.Bundle;
import com.example.fimae.R;
import com.example.fimae.models.DisableUser;
import com.example.fimae.repository.AuthRepository;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.example.fimae.models.Fimaers;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.*;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Date;


public class AuthenticationActivity extends AppCompatActivity {

    private EditText editTextEmail, editTextPassword, editTextRepeatPass;
    TextInputLayout repeatPassLayout;
    Button btnSignIn;
    ImageButton btnGoogleSignIn;
    TextView signUpTextView;
    FirebaseAuth auth = FirebaseAuth.getInstance();
    AuthRepository authRepository = AuthRepository.getInstance();
    ProgressBar progressBar;
    ConstraintLayout contentLayout;
    GoogleSignInClient mGoogleSignInClient;
    FirebaseFirestore firestore = FirebaseFirestore.getInstance();
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    CollectionReference fimaeUsersRefer = firestore.collection("fimae-users");
    DatabaseReference usersRef = database.getReference("fimae-users");
    boolean isRegister = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        setContentView(R.layout.activity_authentication);
        editTextEmail = findViewById(R.id.editTextEmail);
        editTextPassword = findViewById(R.id.editTextPassword);
        editTextRepeatPass = findViewById(R.id.editTextRepeatPassword);
        repeatPassLayout = findViewById(R.id.repeatPassInput);
        signUpTextView = findViewById(R.id.textViewRegister);
        btnSignIn = findViewById(R.id.buttonLogin);
        btnGoogleSignIn = findViewById(R.id.googleImgBtn);
        progressBar = findViewById(R.id.progressBar);
        contentLayout = findViewById(R.id.content_layout);
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
                //signUp();
                if(!isRegister)
                {
                    repeatPassLayout.setVisibility(View.VISIBLE);
                    btnSignIn.setText("Register");
                    signUpTextView.setText("Has an account? Login here.");
                    isRegister = true;
                }
                else
                {
                    repeatPassLayout.setVisibility(View.GONE);
                    btnSignIn.setText("Login");
                    signUpTextView.setText("New user? Register Now");
                    isRegister = false;
                }

            }
        });
        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!isRegister)
                {
                    signIn();
                }
                else
                {
                    signUp();
                }
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
        Intent intent = new Intent(AuthenticationActivity.this, CreateProfileActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }
    private void signIn(){
        String email = String.valueOf(editTextEmail.getText());
        String password = String.valueOf(editTextPassword.getText());
        progressBar.setVisibility(View.VISIBLE);
        contentLayout.setVisibility(View.GONE);
        authRepository.signIn(email, password, new AuthRepository.SignInCallback() {
            @Override
            public void onSignInSuccess(FirebaseUser user) {
                FirebaseFirestore.getInstance().collection("user_disable")
                        .document(user.getUid())
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                if (task.isSuccessful()) {
                                    DocumentSnapshot documentSnapshot = task.getResult();
                                    if (documentSnapshot != null) {
                                        DisableUser disableUser = documentSnapshot.toObject(DisableUser.class);
                                        if (disableUser != null && disableUser.getTimeEnd().after(new Date())) {
                                            Intent intent = new Intent(AuthenticationActivity.this, DisabledUserActivity.class);
                                            intent.putExtra("disableId", user.getUid());
                                            startActivity(intent);
                                        }
                                    }
                                }
                                // Call successAuthentication() here, as this will be triggered after the get() operation completes, whether it succeeds or fails.
                                successAuthentication();
                            }
                        });
            }

            @Override
            public void onSignInError(String errorMessage) {
                editTextEmail.setError(errorMessage);
                Toast.makeText(getApplicationContext(),errorMessage,Toast.LENGTH_SHORT).show();
            }
        });
        progressBar.setVisibility(View.GONE);
        contentLayout.setVisibility(View.VISIBLE);

    }
    private void signUp() {
        String email = String.valueOf(editTextEmail.getText());
        String password = String.valueOf(editTextPassword.getText());
        String repeatPass = String.valueOf(editTextRepeatPass.getText());
        if (!(email.isEmpty() && password.isEmpty() && repeatPass.isEmpty())) {
            if(!repeatPass.equals(password))
            {
                editTextRepeatPass.setError("Vui lòng nhập lại mật khẩu giống phía trên");
                return;
            }
            authRepository.signUp(email, password, new AuthRepository.SignUpCallback() {
                @Override
                public void onSignUpSuccess(FirebaseUser user) {
                    navToUpdateProfile();
                }

                @Override
                public void onSignUpError(String errorMessage) {
                    editTextEmail.setError(errorMessage);
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
