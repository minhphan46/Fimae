package com.example.fimae.fragments;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.fimae.R;
//import com.example.fimae.activities.CreateProfileActivity;
import com.example.fimae.activities.HomeActivity;
import com.example.fimae.databinding.FragmentCreateMatchProfileBinding;
import com.example.fimae.models.GenderMatch;
import com.example.fimae.viewmodels.CreateProfileViewModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

public class CreateMatchProfileFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        FragmentCreateMatchProfileBinding binding = DataBindingUtil.inflate(inflater, R.layout.fragment_create_match_profile, container, false);
        CreateProfileViewModel viewmodel = new ViewModelProvider(getActivity()).get(CreateProfileViewModel.class);
        binding.nextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int id = binding.radioGroup.getCheckedRadioButtonId();
                int minAge;
                int maxAge;
                switch (id) {
                    case R.id.radio1:
                        minAge = 13;
                        maxAge = 16;
                        break;
                    case R.id.radio2:
                        minAge = 17;
                        maxAge = 25;
                        break;
                    case R.id.radio3:
                        minAge = 26;
                        maxAge = 40;
                    default:
                        minAge = 13;
                        maxAge = 16;
                }
                viewmodel.user.setMinAgeMatch(minAge);
                viewmodel.user.setMaxAgeMatch(maxAge);
                viewmodel.user.setGenderMatch(GenderMatch.both);
                ProgressDialog progressdialog = new ProgressDialog(getContext());
                progressdialog.setMessage("Please Wait....");
                progressdialog.setCancelable(false);
                progressdialog.show();
                viewmodel.updateUser().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful())
                        {
                            Intent intent = new Intent(getActivity(), HomeActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                            getActivity().finish();
                        }
                        else
                        {
                            Toast.makeText(getContext(),"Error", Toast.LENGTH_SHORT).show();
                        }
                        progressdialog.dismiss();
                    }
                });
            }
        });
        return binding.getRoot();
    }
}