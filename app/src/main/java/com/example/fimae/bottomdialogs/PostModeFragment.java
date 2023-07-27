package com.example.fimae.bottomdialogs;

import android.app.Dialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.fimae.R;
import com.example.fimae.activities.PostMode;
import com.example.fimae.models.Post;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

public class PostModeFragment extends BottomSheetDialogFragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String POST_MODE = "postmode";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private ConstraintLayout publicLayout;
    private ConstraintLayout privateLayout;
    private ConstraintLayout friendLayout;
    private ImageView publicCheck;
    private ImageView privateCheck;
    private ImageView friendCheck;
    private PostMode postMode;
    private static PostModeFragmentListener postModeFragmentListener;
    public interface PostModeFragmentListener {
        void onSelectItem(PostMode postMode);
    }

    public PostModeFragment(PostMode postMode) {
        // Required empty public constructor
        this.postMode = postMode;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        return super.onCreateDialog(savedInstanceState);
    }

    private static PostModeFragment instance;
    public static PostModeFragment getInstance(PostModeFragmentListener listener, PostMode defaultMode) {
        postModeFragmentListener = listener;
            instance = new PostModeFragment(defaultMode);
        return instance;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            postMode =(PostMode)getArguments().getSerializable(POST_MODE);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.bottomsheet_post_mode, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        publicLayout = view.findViewById(R.id.public_layout);
        privateLayout = view.findViewById(R.id.private_layout);
        friendLayout = view.findViewById(R.id.friend_layout);
        privateCheck = view.findViewById(R.id.ic_check_private);
        publicCheck = view.findViewById(R.id.ic_check_public);
        friendCheck = view.findViewById(R.id.ic_check_friend);
        setMode();
        initListener();
    }
    private void setMode(){
        if(postMode == PostMode.PUBLIC){
            friendCheck.setVisibility(View.INVISIBLE);
            publicCheck.setVisibility(View.VISIBLE);
            privateCheck.setVisibility(View.INVISIBLE);
        }
        else if(postMode == PostMode.FRIEND){
            friendCheck.setVisibility(View.VISIBLE);
            publicCheck.setVisibility(View.INVISIBLE);
            privateCheck.setVisibility(View.INVISIBLE);
        }
        else {
            friendCheck.setVisibility(View.INVISIBLE);
            publicCheck.setVisibility(View.INVISIBLE);
            privateCheck.setVisibility(View.VISIBLE);
        }
        postModeFragmentListener.onSelectItem(postMode);
    }
    private void initListener(){
        friendLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                postMode = PostMode.FRIEND;
                setMode();
            }
        });
        privateLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                postMode = PostMode.PRIVATE;
                setMode();

            }
        });
        publicLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                postMode = PostMode.PUBLIC;
                setMode();
            }
        });
    }
}