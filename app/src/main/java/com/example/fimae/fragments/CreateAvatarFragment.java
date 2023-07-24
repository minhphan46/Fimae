package com.example.fimae.fragments;

import static android.app.Activity.RESULT_OK;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.fimae.R;
import com.example.fimae.databinding.FragmentCreateAvatarBinding;
import com.example.fimae.repository.FimaerRepository;
import com.example.fimae.viewmodels.CreateProfileViewModel;
import com.google.android.material.chip.Chip;

public class CreateAvatarFragment extends Fragment {


    private static final int PICK_IMAGE = 1;
    Uri imageURI;

    ImageView avatar;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_create_avatar, container, false);
        Resources resources = getResources();
        int drawableResId = R.drawable.avatar;
        CreateProfileViewModel viewModel = new ViewModelProvider(getActivity()).get(CreateProfileViewModel.class);

        imageURI = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" +
                resources.getResourcePackageName(drawableResId) + '/' +
                resources.getResourceTypeName(drawableResId) + '/' +
                resources.getResourceEntryName(drawableResId));

        Chip changeAva = rootView.findViewById(R.id.changeAvatar);

        changeAva.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pickImage();
            }
        });

        ImageView back = rootView.findViewById(R.id.backBtn);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().onBackPressed();
            }
        });

        avatar = rootView.findViewById(R.id.display_pic);
        avatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pickImage();
            }
        });
        Button finish = rootView.findViewById(R.id.buttonFinish);
        ProgressDialog progressdialog = new ProgressDialog(getContext());
        progressdialog.setMessage("Please Wait....");
        progressdialog.setCancelable(false);
        finish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressdialog.show();
                viewModel.uploadAvatar(imageURI, new FimaerRepository.UploadAvatarCallback() {
                    @Override
                    public void onUploadSuccess(Uri uri) {
                        viewModel.user.setAvatarUrl(uri.toString());
                        progressdialog.dismiss();
                        navToNext();
                    }

                    @Override
                    public void onUploadError(String errorMessage) {
                        progressdialog.dismiss();
                        Toast.makeText(CreateAvatarFragment.this.getContext(), "Error", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        avatar.setImageURI(imageURI);
        return rootView;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE && resultCode == RESULT_OK) {
            imageURI = data.getData();
            avatar.setImageURI(imageURI);
        }
    }

    private void navToNext()
    {

        CreateBioFragment profileFragment = new CreateBioFragment();

        FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();

        // Replace the contents of the container with the new Fragment
        transaction.replace(R.id.container, profileFragment);

        // Add the transaction to the back stack
        transaction.addToBackStack(null);

        // Commit the FragmentTransaction
        transaction.commit();
    }

    private void pickImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE);
    }



}