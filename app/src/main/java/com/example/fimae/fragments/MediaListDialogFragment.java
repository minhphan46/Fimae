package com.example.fimae.fragments;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fimae.R;
import com.example.fimae.adapters.MediaAdapter;
import com.example.fimae.databinding.FragmentItemListDialogListDialogBinding;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class MediaListDialogFragment extends BottomSheetDialogFragment {
    MediaAdapter mediaAdapter;
    private FragmentItemListDialogListDialogBinding binding;
    private OnMediaSelectedListener onMediaSelectedListener;
    public void setOnMediaSelectedListener(OnMediaSelectedListener onMediaSelectedListener) {
        this.onMediaSelectedListener = onMediaSelectedListener;
    }
    public final static int IMAGE = 0;
    public final static int VIDEO = 1;
    public final static int ALL = 2;
    int mediaType = ALL;
    public MediaListDialogFragment() {
    }
    public MediaListDialogFragment(int mediaType) {
        this.mediaType = mediaType;
    }
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        binding = FragmentItemListDialogListDialogBinding.inflate(inflater, container, false);
        binding.btnSend.setOnClickListener(v -> {
            sendMediaSelectedToParent(true, mediaAdapter.getSelectedMedias());
            dismiss();
        });
        return binding.getRoot();

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        final RecyclerView recyclerView = view.findViewById(R.id.list);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 3));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            mediaAdapter = new MediaAdapter(getContext(), mediaType);
        }
        recyclerView.setAdapter(mediaAdapter);
    }

    private void sendMediaSelectedToParent(boolean isSelected, ArrayList<String> data) {
        if (onMediaSelectedListener != null) {
            onMediaSelectedListener.OnMediaSelected(isSelected, data);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    public interface OnMediaSelectedListener {
        void OnMediaSelected(boolean isSelected, ArrayList<String> data);
    }

}
