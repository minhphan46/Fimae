package com.example.fimae.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.fimae.R;
import com.example.fimae.viewmodels.CreateProfileViewModel;

public class CreateBioFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_create_bio, container, false);
        ImageView backBtn = view.findViewById(R.id.backBtn);
        CreateProfileViewModel viewModel = new ViewModelProvider(getActivity()).get(CreateProfileViewModel.class);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().onBackPressed();
            }
        });
        TextView passTxt = view.findViewById(R.id.passTxt);
        passTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                navToNext();
            }
        });
        EditText bioTxt = view.findViewById(R.id.editText);
        Button nextBtn = view.findViewById(R.id.nextBtn);
        nextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                viewModel.user.setBio(bioTxt.getText().toString());
                navToNext();
            }
        });
        return view;
    }
    public void navToNext()
    {
        CreateTagFragment profileFragment = new CreateTagFragment();
        FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();

        // Replace the contents of the container with the new Fragment
        transaction.replace(R.id.container, profileFragment);

        // Add the transaction to the back stack
        transaction.addToBackStack(null);

        // Commit the FragmentTransaction
        transaction.commit();
    }
}