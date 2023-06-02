package com.example.fimae.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.fimae.R;
import com.example.fimae.activities.EditProfileActivity;

public class ProfileFragment extends Fragment {

    ImageButton btnEditProfile, editBioBtn, editChipBtn;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        btnEditProfile = view.findViewById(R.id.editProfileBtn);
        editBioBtn = view.findViewById(R.id.editBioBtn);
        editChipBtn = view.findViewById(R.id.editChipBtn);
        editChipBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                navToEditProfile();
            }
        });
        editBioBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                navToEditProfile();
            }
        });
        btnEditProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                navToEditProfile();
            }
        });
        return view;
    }

    private void navToEditProfile() {
        Intent intent = new Intent(getContext(), EditProfileActivity.class);
        startActivity(intent);
    }
}
