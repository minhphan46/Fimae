package com.example.fimae.fragments;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.fimae.R;
import com.example.fimae.activities.EditProfileActivity;
import com.example.fimae.databinding.FragmentProfileBinding;
import com.example.fimae.models.Fimaers;
import com.example.fimae.repository.FimaerRepository;
import com.example.fimae.viewmodels.ProfileViewModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.squareup.picasso.Picasso;

import androidx.databinding.BindingAdapter;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileFragment extends Fragment {

    FimaerRepository userRepo = FimaerRepository.getInstance();
    ImageButton btnEditProfile, editBioBtn, editChipBtn;
    ImageView backgroundImg;
    CircleImageView avatarBtn;
    Fimaers user;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ProfileViewModel viewModel = new ViewModelProvider(this).get(ProfileViewModel.class);
        FragmentProfileBinding binding = DataBindingUtil.inflate(inflater,R.layout.fragment_profile,container,false);
        View view = binding.getRoot();

        binding.setViewmodel(viewModel);
        viewModel.getUser().observe(getViewLifecycleOwner(),user -> {
            binding.setUser(user);
        });

        btnEditProfile = view.findViewById(R.id.editProfileBtn);
        editBioBtn = view.findViewById(R.id.editBioBtn);
        editChipBtn = view.findViewById(R.id.editChipBtn);
        avatarBtn = view.findViewById(R.id.avatarBtn);
        backgroundImg = view.findViewById(R.id.backgroundImage);

        avatarBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                BottomSheetDialog mBottomSheetDialog = new BottomSheetDialog(getActivity(),R.style.AppBottomSheetDialogTheme);
                View sheetView = getActivity().getLayoutInflater().inflate(R.layout.choose_image_bottom_modal, null);
                mBottomSheetDialog.setContentView(sheetView);
                mBottomSheetDialog.show();
            }
        });
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
