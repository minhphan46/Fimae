package com.example.fimae.activities;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentActivity;

import com.example.fimae.R;
import com.example.fimae.fragments.ProfileFragment;

public class ProfileActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String uid = getIntent().getStringExtra("uid");
        setContentView(R.layout.activity_profile);

        if (savedInstanceState == null) {
            ProfileFragment profileFragment = ProfileFragment.newInstance(uid);
            // Add the initial fragment when the activity is first created
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.fragment_container, profileFragment)
                    .commit();
        }
    }
}
