package com.example.fimae.bottomdialogs;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.fimae.R;
import com.example.fimae.activities.PostMode;
import com.example.fimae.adapters.LikedAdapeter;
import com.example.fimae.databinding.BottomsheetLikedPeopleBinding;
import com.example.fimae.models.Post;
import com.example.fimae.models.Seed;
import com.example.fimae.models.UserInfo;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.util.List;

public class LikedPostListFragment extends BottomSheetDialogFragment {
    Seed seed = new Seed();
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private BottomsheetLikedPeopleBinding binding;
    private Post post;
    private UserInfo[] userInfoList;
    public LikedPostListFragment(String postId) {
        post = seed.getPostbyId(postId);
        userInfoList = UserInfo.dummy;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        return super.onCreateDialog(savedInstanceState);
    }

    private static LikedPostListFragment instance;
    public static LikedPostListFragment getInstance(String postId) {

        if(instance == null){
            instance = new LikedPostListFragment(postId);
        }
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
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        binding.userList.setLayoutManager(linearLayoutManager);
        LikedAdapeter adapeter = new LikedAdapeter(getContext(), userInfoList);
        binding.userList.setAdapter(adapeter);
        binding.title.setText("Lượt thích và cảm xúc " +String.valueOf(userInfoList.length));
    }
}