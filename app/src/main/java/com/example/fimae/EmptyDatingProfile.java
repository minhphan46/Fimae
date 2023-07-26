package com.example.fimae;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.button.MaterialButton;
public class EmptyDatingProfile extends Fragment {
    private static final String ARG_UID = "param1";
    private String uid;

    public EmptyDatingProfile() {
        // Required empty public constructor
    }

    public static EmptyDatingProfile newInstance(String uid) {
        EmptyDatingProfile fragment = new EmptyDatingProfile();
        Bundle args = new Bundle();
        args.putString(ARG_UID, uid);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            uid = getArguments().getString(ARG_UID);
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_empty_dating_profile, container, false);

        MaterialButton createProfile = view.findViewById(R.id.btn_empty_dating_profile);
        createProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), DatingAddImages.class);
                intent.putExtra("uid", uid);
                intent.putExtra("isCreate", true);
                startActivity(intent);
            }
        });

        return view;
    }
}