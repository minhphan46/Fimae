package com.example.fimae.bottomdialogs;

import android.app.Dialog;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProvider;

import com.example.fimae.R;
import com.example.fimae.databinding.FragmentAvatarBottomSheetBinding;
import com.example.fimae.repository.FimaerRepository;
import com.example.fimae.viewmodels.AvatarBottomSheetViewModel;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.squareup.picasso.Picasso;

public class AvatarBottomSheetFragment extends BottomSheetDialogFragment {

    public static AvatarBottomSheetFragment newInstance(String url) {
        AvatarBottomSheetFragment frag = new AvatarBottomSheetFragment();
        Bundle args = new Bundle();
        args.putString("url", url);
        frag.setArguments(args);
        return frag;
    }

    private FragmentAvatarBottomSheetBinding binding;
    private Uri imageUri;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        AvatarBottomSheetViewModel viewModel = new ViewModelProvider(this).get(AvatarBottomSheetViewModel.class);
        View view = inflater.inflate(R.layout.fragment_avatar_bottom_sheet, container, false);
        binding = DataBindingUtil.inflate(inflater,R.layout.fragment_avatar_bottom_sheet,container,false);
        view = binding.getRoot();
        ImageView avatar = view.findViewById(R.id.avatarBtn);
        TextView text = view.findViewById(R.id.saveTextView);
        Button editAvabtn = view.findViewById(R.id.editAvaBtn);
        text.setVisibility(View.GONE);
        text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setCancelable(false);
                viewModel.updateAvatar(imageUri, new FimaerRepository.UploadAvatarCallback() {
                    @Override
                    public void onUploadSuccess(Uri uri) {
                        dismiss();
                    }

                    @Override
                    public void onUploadError(String errorMessage) {

                    }
                });
            }
        });
        editAvabtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PickImageBottomSheetFragment pickImageFragment = new PickImageBottomSheetFragment();
                pickImageFragment.setCallBack(new PickImageBottomSheetFragment.PickImageCallBack() {
                    @Override
                    public void pickImageComplete(Uri uri) {
                        imageUri = uri;
                        Picasso.get().load(uri).into(avatar);
                        text.setVisibility(View.VISIBLE);
                    }
                });
                FragmentManager fragmentManager = getChildFragmentManager(); // For fragments
                pickImageFragment.show(fragmentManager, "pick_image_bottom_sheet");
            }
        });
        binding.setLifecycleOwner(this.getViewLifecycleOwner());
        String url = getArguments() != null ? getArguments().getString("url") : null;
        viewModel.setImageUrl(url);
        binding.setUrl(url);

        return view;
    }
}
