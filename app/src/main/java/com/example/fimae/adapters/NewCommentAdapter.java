package com.example.fimae.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.fimae.R;
import com.example.fimae.databinding.CommentItemBinding;
import com.example.fimae.models.Comment;
import com.example.fimae.models.CommentItemAdapter;
import com.example.fimae.models.Fimaers;
import com.example.fimae.repository.CommentRepository;
import com.example.fimae.repository.PostRepository;
import com.example.fimae.service.PostService;
import com.example.fimae.service.TimerService;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

public class NewCommentAdapter extends FirestoreAdapter<NewCommentAdapter.ViewHolder> {
    public interface IClickMyCommentItem{
        void onClick(Comment comment);
    }

    private Context mContext;
    private List<CommentItemAdapter> mCommentItem;
    private final PostRepository postRepo;
    private final CommentRepository commentRepository;
    private final String postId;
    private IClickMyCommentItem iClickMyCommentItem;
    private IClickMyCommentItem iClickCommentItem;

    public NewCommentAdapter(Context mContext, Query query, IClickMyCommentItem listener, String postId, IClickMyCommentItem longClickListener) {
        super(query);
        this.mContext = mContext;
        this.iClickCommentItem = listener;
        this.postId = postId;
        this.iClickMyCommentItem = longClickListener;

        postRepo = PostRepository.getInstance();
        commentRepository = CommentRepository.getInstance();
    }
    @Override
    public void OnSuccessQueryListener(ArrayList<DocumentSnapshot> queryDocumentSnapshots) {
        if(mCommentItem == null){
            mCommentItem = new ArrayList<>();
        }
        mCommentItem.clear();
        for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
            Comment comment = documentSnapshot.toObject(Comment.class);
            assert comment != null;
            if(comment.getParentId() == null || comment.getParentId().isEmpty()){
                mCommentItem.add(new CommentItemAdapter(comment));
            }
        }
        for (CommentItemAdapter commentItemAdapter : mCommentItem) {
            for(DocumentSnapshot documentSnapshot : queryDocumentSnapshots){
                Comment comment = documentSnapshot.toObject(Comment.class);
                if(comment.getParentId() != null && comment.getParentId().equals(commentItemAdapter.getComment().getId())){
                    commentItemAdapter.getSubComment().add(new CommentItemAdapter(comment));
                }
            }
        }
    }
    @NonNull
    @Override
    public NewCommentAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        CommentItemBinding commentItemBinding = CommentItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new ViewHolder(commentItemBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull NewCommentAdapter.ViewHolder holder, int position) {
        CommentItemBinding binding = holder.binding;
        CommentItemAdapter currentCommentItem = mCommentItem.get(position);
        Comment currentComment = currentCommentItem.getComment();
        PostRepository.getInstance().getUserById(currentComment.getPublisher(), userInfo -> {
            binding(binding, currentCommentItem, userInfo);
        });
    }

    @Override
    public int getItemCount() {
        if(mCommentItem == null) return 0;
        return mCommentItem.size();
    }

    public void addUpdate() {
        this.notifyItemInserted(mCommentItem.size() - 1);
    }

    public void addModify(int pos) {
        this.notifyItemChanged(pos);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        CommentItemBinding binding;

        public ViewHolder(CommentItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }



    @Override
    public int getItemViewType(int position) {
        return position;
    }

    private void binding(CommentItemBinding binding, CommentItemAdapter currentCommentItem, Fimaers userInfo) {
        Comment currentComment = currentCommentItem.getComment();;

        binding.content.setText(currentComment.getContent());
        if (currentComment.getTimeEdited() != null) {
            binding.isEdited.setVisibility(View.VISIBLE);
            TimerService.setDuration(binding.time, currentComment.getTimeEdited());
        }
        binding.likeNumber.setText(String.valueOf(PostService.getInstance().getNumberOfLikes(currentComment.getLikes())));

        if(currentComment.getLikes().containsKey(currentComment.getPublisher())){
            binding.heart.setImageResource(R.drawable.ic_heart1);
            binding.heart.setOnClickListener(view -> {
                String path = "likes." + userInfo.getUid();
                binding.heart.setImageResource(R.drawable.ic_heart_gray);
//                reference.update(
//                        path, false
//                );
            });
        }else {
            binding.heart.setImageResource(R.drawable.ic_heart_gray);
            binding.heart.setOnClickListener(view -> {
                String path = "likes." + userInfo.getUid();
                binding.heart.setImageResource(R.drawable.ic_heart1);
//                reference.update(
//                        path, true
//                );
            });
        }

        binding.content.setText(currentComment.getContent());
        if (currentComment.getTimeEdited() == null) {
            TimerService.setDuration(binding.time, (Date) currentComment.getTimeCreated());
        } else {
            binding.isEdited.setVisibility(View.VISIBLE);
            TimerService.setDuration(binding.time, currentComment.getTimeEdited());
        }
        Glide.with(mContext).load(userInfo.getAvatarUrl()).placeholder(R.drawable.ic_default_avatar).into(binding.imageAvatar);
        // Coil.load(mContext, userInfo.getAvatarUrl()).placeholder(R.drawable.ic_default_avatar).into(binding.imageAvatar);
        binding.userName.setText(userInfo.getLastName());
        if (!userInfo.isGender()) {
            binding.itemUserIcGender.setImageResource(R.drawable.ic_male);
            binding.itemUserLayoutGenderAge.setBackgroundResource(R.drawable.shape_gender_border_pink);
        }
        binding.itemUserTvAge.setText(String.valueOf(userInfo.calculateAge()));

        SubCommentAdapter adapter = new SubCommentAdapter(mContext, currentCommentItem, this.iClickCommentItem, this.iClickMyCommentItem);
        binding.subComment.setVisibility(View.VISIBLE);
        LinearLayoutManager layoutManager = new LinearLayoutManager(mContext);
        binding.subComment.setLayoutManager(layoutManager);
        binding.subComment.setAdapter(adapter);
        currentCommentItem.setSubAdapter(adapter);

        binding.constraintLayout.setOnClickListener(view -> {
            if (Objects.equals(currentComment.getPublisher(), FirebaseAuth.getInstance().getUid())) {
                iClickMyCommentItem.onClick(currentComment);
            } else {
                iClickCommentItem.onClick(currentComment);
            }
        });

        commentRepository.getSubComment(currentCommentItem);
    }

}
