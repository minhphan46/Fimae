package com.example.fimae.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentActivity;

import com.example.fimae.R;
import com.example.fimae.fragments.ProfileFragment;
import com.example.fimae.repository.FollowRepository;
import com.example.fimae.repository.PostRepository;

public class ProfileActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String uid = getIntent().getStringExtra("uid");
        setContentView(R.layout.activity_profile);
        Button follow = findViewById(R.id.followButton);
        follow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FollowRepository.getInstance().follow(uid);
                follow.setVisibility(View.GONE);
            }
        });
        Button chat = findViewById(R.id.startChat);
        chat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PostRepository.getInstance().goToChatWithUser(uid, ProfileActivity.this);
            }
        });
        if (savedInstanceState == null) {
            ProfileFragment profileFragment = ProfileFragment.newInstance(uid);
            // Add the initial fragment when the activity is first created
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.fragment_container, profileFragment)
                    .commit();
        }
    }
}
