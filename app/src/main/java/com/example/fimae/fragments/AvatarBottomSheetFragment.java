package com.example.fimae.fragments;

import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.FragmentManager;

import com.example.fimae.R;
import com.example.fimae.databinding.FragmentAvatarBottomSheetBinding;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.squareup.picasso.Picasso;

public class AvatarBottomSheetFragment extends BottomSheetDialogFragment {

    static AvatarBottomSheetFragment newInstance(String url) {
        AvatarBottomSheetFragment frag = new AvatarBottomSheetFragment();
        Bundle args = new Bundle();
        args.putString("url", url);
        frag.setArguments(args);
        return frag;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_avatar_bottom_sheet, container, false);
        FragmentAvatarBottomSheetBinding binding = DataBindingUtil.inflate(inflater,R.layout.fragment_avatar_bottom_sheet,container,false);
        view = binding.getRoot();
        ImageView avatar = view.findViewById(R.id.avatarBtn);
        Button editAvabtn = view.findViewById(R.id.editAvaBtn);
        editAvabtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PickImageBottomSheetFragment pickImageFragment = new PickImageBottomSheetFragment();
                pickImageFragment.setCallBack(new PickImageBottomSheetFragment.PickImageCallBack() {
                    @Override
                    public void pickImageComplete(Uri uri) {
                        Picasso.get().load(uri).into(avatar);
                    }
                });
                FragmentManager fragmentManager = getChildFragmentManager(); // For fragments
                pickImageFragment.show(fragmentManager, "pick_image_bottom_sheet");
            }
        });
        binding.setLifecycleOwner(this.getViewLifecycleOwner());
        String url = getArguments() != null ? getArguments().getString("url") : null;
        binding.setImageURL(url);
        return view;
    }
}
