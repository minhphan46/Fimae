package com.example.fimae;

import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import com.example.fimae.activities.AuthenticationActivity;
import com.example.fimae.activities.HomeActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SplashActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        mAuth = FirebaseAuth.getInstance();
        // Kiểm tra trạng thái đăng nhập của người dùng
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            // Người dùng đã đăng nhập, chuyển đến MainActivity
            startActivity(new Intent(SplashActivity.this, HomeActivity.class));
        } else {
            // Người dùng chưa đăng nhập, chuyển đến Authentication
            startActivity(new Intent(SplashActivity.this, AuthenticationActivity.class));
        }
    }
}
