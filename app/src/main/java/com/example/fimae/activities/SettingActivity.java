package com.example.fimae.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;

import com.example.fimae.R;
import com.example.fimae.fragments.SettingFragment;
import com.example.fimae.repository.AuthRepository;

public class SettingActivity extends AppCompatActivity {

    RelativeLayout editProfile, relativeSignOut;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        editProfile = findViewById(R.id.relativeEditProfile);
        editProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                navToEditProfile();
            }
        });
        relativeSignOut = findViewById(R.id.relativeLogout);
        relativeSignOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                logOut();
            }
        });


    }
    private void logOut()
    {
        AuthRepository.getInstance().signOut();
        Intent intent = new Intent(this, SplashActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }
    private void navToEditProfile() {
        Intent intent = new Intent(this, EditProfileActivity.class);
        startActivity(intent);
    }
}