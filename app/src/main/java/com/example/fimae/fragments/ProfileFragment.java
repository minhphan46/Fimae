package com.example.fimae.fragments;

import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ImageSpan;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

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
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileFragment extends Fragment {

    FimaerRepository userRepo = FimaerRepository.getInstance();
    ImageButton btnEditProfile, editBioBtn, editChipBtn, copyLitId;
    ImageView backgroundImg;
    CircleImageView avatarBtn;
    TextView bioTextView;
    FragmentProfileBinding binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ProfileViewModel viewModel = new ViewModelProvider(this).get(ProfileViewModel.class);
        binding = DataBindingUtil.inflate(inflater,R.layout.fragment_profile,container,false);
        View view = binding.getRoot();
        binding.setLifecycleOwner(this);
        binding.setViewmodel(viewModel);

        Log.i("PROFILE", "onCreateView: ");
        viewModel.getUser().observe(getViewLifecycleOwner(), new Observer<Fimaers>() {
            @Override
            public void onChanged(Fimaers fimaers) {
                setTextSpan();
                Log.i("PROFILE", "onChanged: " + fimaers.getName());
            }
        });

        bioTextView = view.findViewById(R.id.bioTxt);
        btnEditProfile = view.findViewById(R.id.editProfileBtn);
        editBioBtn = view.findViewById(R.id.editBioBtn);
        editChipBtn = view.findViewById(R.id.editChipBtn);
        avatarBtn = view.findViewById(R.id.avatarBtn);
        backgroundImg = view.findViewById(R.id.backgroundImage);
        copyLitId = view.findViewById(R.id.copyBtn);
        avatarBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                AvatarBottomSheetFragment avatarFragment = AvatarBottomSheetFragment.newInstance(viewModel.getUser().getValue().getAvatarUrl());

                FragmentManager fragmentManager = getChildFragmentManager(); // For fragments

                avatarFragment.show(fragmentManager, "avatar_bottom_sheet");
            }
        });
        copyLitId.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                copyLitId();
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

    private void copyLitId() {
        // Get the text from the TextView
        String litId = binding.getViewmodel().getUser().getValue().getUid();

        // Copy the text to the clipboard
        ClipboardManager clipboardManager = (ClipboardManager) getActivity().getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clipData = ClipData.newPlainText("Lit ID", litId);
        if (clipboardManager != null) {
            clipboardManager.setPrimaryClip(clipData);
        }

        // Show a toast message
        Toast.makeText(getActivity(), "Lit ID copied", Toast.LENGTH_SHORT).show();
    }

    private void setTextSpan()
    {
        String text = binding.getViewmodel().getUser().getValue().getBio();
        // Create a SpannableString with the text and the edit icon
        SpannableString spannableString = new SpannableString( text + " "); // Add some extra space after text for the icon

        // Get the drawable for the edit icon
        int iconSize = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 18,
                getResources().getDisplayMetrics());
        Drawable icon = getResources().getDrawable(R.drawable.ic_edit);
        icon.setBounds(0, 0, iconSize, iconSize);

        // Create an ImageSpan with the edit icon and add it to the SpannableString
        ImageSpan imageSpan = new ImageSpan(icon, ImageSpan.ALIGN_BASELINE);
        spannableString.setSpan(imageSpan, text.length(), text.length() + 1, Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
        // Create a ClickableSpan for the icon
        ClickableSpan clickableSpan = new ClickableSpan() {
            @Override
            public void onClick(View widget) {
                // Handle the click event here
                navToEditProfile();
            }
        };
        // Set the ClickableSpan to the ImageSpan
        spannableString.setSpan(clickableSpan, text.length(), text.length() + 1, Spannable.SPAN_INCLUSIVE_EXCLUSIVE);

        Log.i("TAG", "setTextSpan: " + spannableString.toString());
        // Set the SpannableString to the TextView
        bioTextView.setText(spannableString);
        bioTextView.setMovementMethod(LinkMovementMethod.getInstance()); // Make the links clickable
    }

    private void navToEditProfile() {
        Intent intent = new Intent(getContext(), EditProfileActivity.class);
        startActivity(intent);
    }

}
