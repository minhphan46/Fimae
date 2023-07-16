package com.example.fimae.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fimae.R;
import com.example.fimae.databinding.CommentItemBinding;
import com.example.fimae.models.Comment;
import com.example.fimae.models.CommentBase;
import com.example.fimae.models.Fimaers;
import com.example.fimae.models.SubComment;
import com.example.fimae.repository.CommentRepository;
import com.example.fimae.repository.PostRepository;
import com.example.fimae.service.TimerService;
import com.google.firebase.auth.FirebaseAuth;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.ViewHolder> {
    public Context mContext;
    public List<Comment> mComment;
    final PostRepository postRepo = PostRepository.getInstance();
    final CommentRepository commentRepository = CommentRepository.getInstance();
    final String postId;
    private SubCommentAdapter.IClickCommentItem iClickCommentItem;
    private IClickMyCommentItem iClickMyCommentItem;
    //interface
    public interface IClickMyCommentItem{
        void onClick(CommentBase comment);
    }
    //constructor
    public CommentAdapter(Context mContext, List<Comment> mComment, SubCommentAdapter.IClickCommentItem listener,String postId, IClickMyCommentItem longClickListener) {
        this.mContext = mContext;
        this.mComment = mComment;
        this.iClickCommentItem = listener;
        this.postId = postId;
        this.iClickMyCommentItem = longClickListener;
    }

    public void addUpdate(){
        this.notifyItemInserted(mComment.size() -1 );
    }
    public void addModify(int pos){
        this.notifyItemChanged(pos);
    }
    @NonNull
    @Override
    public CommentAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        CommentItemBinding commentItemBinding = CommentItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new ViewHolder(commentItemBinding);
    }


    @Override
    public void onBindViewHolder(@NonNull CommentAdapter.ViewHolder holder, int position) {
        CommentItemBinding binding = holder.binding;
        Comment currentComment = mComment.get(position);

        binding.content.setText(currentComment.getContent());
        if(currentComment.getTimeEdited() == null ) {
            TimerService.setDuration(binding.time, currentComment.getTimeCreated());
        }
        else{
            binding.isEdited.setVisibility(View.VISIBLE);
            TimerService.setDuration(binding.time, currentComment.getTimeEdited());
        }
        postRepo.getUserById(currentComment.getPublisher(), userInfo -> {
            if(userInfo != null){

                Picasso.get().load(userInfo.getAvatarUrl()).placeholder(R.drawable.ic_default_avatar).into(binding.imageAvatar);
                binding.userName.setText(userInfo.getLastName());

                if(!userInfo.isGender()){
                    binding.itemUserIcGender.setImageResource(R.drawable.ic_male);
                    binding.itemUserLayoutGenderAge.setBackgroundResource(R.drawable.shape_gender_border_pink);
                }

                binding.itemUserTvAge.setText(String.valueOf(userInfo.calculateAge()));
                commentRepository.commentListener(binding, currentComment, userInfo);

                List<SubComment> subComments = new ArrayList<>();
                SubCommentAdapter adapter = new SubCommentAdapter(mContext, subComments, this.iClickCommentItem, this.iClickMyCommentItem ,  currentComment.getPostId(), currentComment.getId());
                binding.subComment.setVisibility(View.VISIBLE);
                LinearLayoutManager layoutManager = new LinearLayoutManager(mContext);
                binding.subComment.setLayoutManager(layoutManager);
                binding.subComment.setAdapter(adapter);

                binding.constraintLayout.setOnClickListener(view -> {
                    if(Objects.equals(currentComment.getPublisher(), FirebaseAuth.getInstance().getUid())){
                        iClickMyCommentItem.onClick(currentComment);
                    }
                    else{
                        iClickCommentItem.onClick(currentComment.getId(), adapter, userInfo );
                    }
                });
                commentRepository.getSubComment(postId, currentComment.getId(), subComments, adapter);

            }
        });
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
