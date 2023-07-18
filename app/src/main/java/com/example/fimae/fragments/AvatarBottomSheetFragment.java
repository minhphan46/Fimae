package com.example.fimae.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;

import com.example.fimae.R;
import com.example.fimae.databinding.FragmentAvatarBottomSheetBinding;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

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
        Button editAvabtn = view.findViewById(R.id.editAvaBtn);
        editAvabtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                BottomSheetDialog mBottomSheetDialog = new BottomSheetDialog(getActivity(),R.style.AppBottomSheetDialogTheme);
                View sheetView = getActivity().getLayoutInflater().inflate(R.layout.choose_image_bottom_modal, null);
                mBottomSheetDialog.setContentView(sheetView);
                mBottomSheetDialog.show();
            }
        });
        binding.setLifecycleOwner(this.getViewLifecycleOwner());
        String url = getArguments() != null ? getArguments().getString("url") : null;
        binding.setImageURL(url);
        return view;
    }
}
