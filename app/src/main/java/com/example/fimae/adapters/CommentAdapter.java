package com.example.fimae.adapters;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fimae.R;
import com.example.fimae.databinding.CommentItemBinding;
import com.example.fimae.models.Comment;
import com.example.fimae.models.Post;
import com.example.fimae.models.Seed;
import com.example.fimae.models.UserInfo;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.ViewHolder> {
    private Seed seed = new Seed();
    public Context mContext;
    public List<Comment> mComment;
    private CommentAdapter.IClickCommentItem iClickCommentItem;
    public interface IClickCommentItem {
        void onClick(Comment comment, CommentAdapter commentAdapter);
    }

    public CommentAdapter(Context mContext, List<Comment> mComment, CommentAdapter.IClickCommentItem listener) {
        this.mContext = mContext;
        this.mComment = mComment;
        this.iClickCommentItem = listener;
    }

    @NonNull
    @Override
    public CommentAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        CommentItemBinding commentItemBinding = CommentItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new ViewHolder(commentItemBinding);
    }
    public void addComment(Comment comment){
        mComment.add(comment);
        notifyDataSetChanged();
    }

    @Override
    public void onBindViewHolder(@NonNull CommentAdapter.ViewHolder holder, int position) {
        CommentItemBinding binding = holder.binding;
        Comment currentComment = mComment.get(position);
        UserInfo userInfo = seed.getUserInfoByID(currentComment.getPublisher());
        String description = currentComment.getComment();
        binding.content.setText(description);
        List<Comment> subComments = currentComment.getSubComment();
        if(currentComment.getIdParent() ==null && subComments != null){
            CommentAdapter adapter = new CommentAdapter(mContext,currentComment.getSubComment(), this.iClickCommentItem);
            binding.subComment.setVisibility(View.VISIBLE);
            LinearLayoutManager layoutManager = new LinearLayoutManager(mContext);
            binding.subComment.setLayoutManager(layoutManager);
            binding.subComment.setAdapter(adapter);
            binding.constraintLayout.setOnClickListener(view -> iClickCommentItem.onClick(mComment.get(holder.getAdapterPosition()), adapter));

        }
        else {
            binding.constraintLayout.setOnClickListener(view -> iClickCommentItem.onClick(mComment.get(holder.getAdapterPosition()), CommentAdapter.this));
        }

        if(userInfo != null){
            Picasso.get().load(userInfo.getAvatarUrl()).placeholder(R.drawable.ic_default_avatar).into(binding.imageAvatar);
            binding.userName.setText(userInfo.getName());
            if(!userInfo.isMale()){
                binding.itemUserIcGender.setImageResource(R.drawable.ic_male);
                binding.itemUserLayoutGenderAge.setBackgroundResource(R.drawable.shape_gender_border_pink);
            }
            binding.itemUserTvAge.setText(String.valueOf(userInfo.getAge()));
        }
    }

    @Override
    public int getItemCount() {
        return mComment.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        CommentItemBinding binding;
        public ViewHolder(CommentItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
