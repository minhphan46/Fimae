package com.example.fimae.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;
import android.widget.Toast;

import com.example.fimae.R;
import com.example.fimae.databinding.ActivityCreateProfileBinding;
import com.example.fimae.fragments.CreateProfileFragment;
import com.example.fimae.fragments.FeedFragment;
import com.example.fimae.viewmodels.CreateProfileViewModel;

public class CreateProfileActivity extends AppCompatActivity {

    MutableLiveData<Boolean> gender = new MutableLiveData<>(true);
    ActivityCreateProfileBinding binding;
    CreateProfileViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_profile);
        viewModel = new ViewModelProvider(this).get(CreateProfileViewModel.class);
        CreateProfileFragment profileFragment = new CreateProfileFragment();

        viewModel.getMessage().observe(this, new Observer<String>() {
            @Override
            public void onChanged(String s) {
                if(!s.isEmpty())
                {
                    Toast.makeText(CreateProfileActivity.this, s, Toast.LENGTH_SHORT).show();
                }
            }
        });

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

        // Replace the contents of the container with the new Fragment
        transaction.replace(R.id.container, profileFragment);

        // Add the transaction to the back stack
        transaction.addToBackStack(null);

        // Commit the FragmentTransaction
        transaction.commit();
    }

}