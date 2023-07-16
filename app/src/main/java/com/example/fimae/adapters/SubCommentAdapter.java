package com.example.fimae.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fimae.R;
import com.example.fimae.databinding.CommentItemBinding;
import com.example.fimae.models.Fimaers;
import com.example.fimae.models.SubComment;
import com.example.fimae.repository.CommentRepository;
import com.example.fimae.repository.PostRepository;
import com.example.fimae.service.TimerService;
import com.google.firebase.auth.FirebaseAuth;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
public class SubCommentAdapter extends RecyclerView.Adapter<SubCommentAdapter.ViewHolder> {
    public Context mContext;
    public List<SubComment> mSubComment;
    private IClickCommentItem iClickCommentItem;
    private PostRepository postRepo = PostRepository.getInstance();
    final CommentRepository commentRepository = CommentRepository.getInstance();
    private String postId;
    private String commentId;
    final CommentAdapter.IClickMyCommentItem iClickMyCommentItem;
    public interface IClickCommentItem {
        void onClick(String commentId, SubCommentAdapter commentAdapter, Fimaers fimaers);
    }

    public SubCommentAdapter(Context mContext, List<SubComment> mSubComment, IClickCommentItem listener, CommentAdapter.IClickMyCommentItem iClickMyCommentItem, String postId, String commentId ) {
        this.mContext = mContext;
        this.mSubComment = mSubComment;
        this.iClickCommentItem = listener;
        this.postId = postId;
        this.commentId = commentId;
        this.iClickMyCommentItem = iClickMyCommentItem;
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        CommentItemBinding commentItemBinding = CommentItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new SubCommentAdapter.ViewHolder(commentItemBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        CommentItemBinding binding = holder.binding;
        SubComment currentComment = mSubComment.get(position);
        postRepo.getUserById(currentComment.getPublisher(), result -> {
            Fimaers userInfo = result;
            if(userInfo != null){
                Picasso.get().load(userInfo.getAvatarUrl()).placeholder(R.drawable.ic_default_avatar).into(binding.imageAvatar);
                binding.userName.setText(userInfo.getLastName());
                if(!userInfo.isGender()){
                    binding.itemUserIcGender.setImageResource(R.drawable.ic_male);
                    binding.itemUserLayoutGenderAge.setBackgroundResource(R.drawable.shape_gender_border_pink);
                }
                binding.itemUserTvAge.setText(String.valueOf(userInfo.calculateAge()));
                binding.constraintLayout.setOnClickListener(view -> {
                    if(Objects.equals(FirebaseAuth.getInstance().getUid(), currentComment.getPublisher())){
                        iClickMyCommentItem.onClick(currentComment);
                    }
                    else{
                        iClickCommentItem.onClick(commentId, this, result);
                    }
                } );

                commentRepository.subCommentListener(binding, result, postId, commentId, currentComment);
            }
        });
        binding.content.setText(currentComment.getContent());
        TimerService.setDuration(binding.time, currentComment.getTimeCreated());
    }
    public void addUpdate(){
        this.notifyItemInserted(mSubComment.size() - 1);
    }
    public void addModify(int pos){
        this.notifyItemChanged(pos);
    }

    @Override
    public int getItemCount() {
        return mSubComment.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        CommentItemBinding binding;
        public ViewHolder(CommentItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }

}

