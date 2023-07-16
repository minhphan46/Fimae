package com.example.fimae.fragments;

import android.app.Application;
import android.os.Bundle;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.databinding.DataBindingUtil;

import com.example.fimae.R;
import com.example.fimae.databinding.FragmentCommentEditBinding;
import com.example.fimae.models.Comment;
import com.example.fimae.models.CommentBase;
import com.example.fimae.models.SubComment;
import com.example.fimae.repository.CommentRepository;
import com.example.fimae.utils.AsyncTask;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.util.concurrent.Delayed;

public class CommentEditFragment extends BottomSheetDialogFragment {

    public CommentEditFragment(CommentBase comment, String postId ) {
        // Required empty public constructor
        this.comment = comment;
        this.postId = postId;
    }
    private CommentRepository commentRepository = CommentRepository.getInstance();
    public interface CommentCallBack{
        void callback();
    }
    private FragmentCommentEditBinding binding;
    private String postId;
    private CommentBase comment;
    private static CommentEditFragment commentEditFragment;
    public static CommentEditFragment getInstance(Comment comment, String postId) {
        if(commentEditFragment == null) commentEditFragment = new CommentEditFragment(comment, postId );
        return commentEditFragment;
    }

    private void editComment(){
//        Toast.makeText(getActivity().getApplicationContext(),"djjdf", Toast.LENGTH_SHORT).show();
        String content = binding.tvEdit.getText().toString();
        if(!content.isEmpty() && !content.trim().isEmpty()){
            comment.setContent(content);
            commentRepository.editComment(postId, comment, getActivity().getApplicationContext());
        }
    }
    private void deleteComment(){
        commentRepository.deleteComment(postId, comment, getActivity().getApplicationContext());
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_comment_edit, container, false);
        binding.tvEdit.setText(comment.getContent().toString());
        binding.btnSend.setOnClickListener(view -> { editComment();
            this.dismiss();
        });
        View rootView = binding.getRoot();
        return rootView;
    }
}