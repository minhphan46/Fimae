package com.example.fimae.bottomdialogs;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fimae.R;
import com.example.fimae.activities.PostMode;
import com.example.fimae.adapters.LikedAdapeter;
import com.example.fimae.databinding.BottomsheetLikedPeopleBinding;
import com.example.fimae.models.Fimaers;
import com.example.fimae.models.Post;
import com.example.fimae.models.Seed;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ListItemBottomSheetFragment extends BottomSheetDialogFragment {
    private BottomsheetLikedPeopleBinding binding;
    public ListItemBottomSheetFragment(String title, RecyclerView.Adapter adapter) {
        this.title = title;
        this.adapter = adapter;
    }
    RecyclerView.Adapter adapter;
    String title;
    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {

        return super.onCreateDialog(savedInstanceState);
    }
    
    private static ListItemBottomSheetFragment instance;
    public static ListItemBottomSheetFragment getInstance(String title, RecyclerView.Adapter adapter){
        instance = new ListItemBottomSheetFragment(title, adapter);
        return instance;
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        binding = BottomsheetLikedPeopleBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding.title.setText(title);
        binding.userList.setAdapter(adapter);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        binding.userList.setLayoutManager(linearLayoutManager);
    }
}