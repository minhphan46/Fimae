package com.example.fimae.adapters;

import static com.example.fimae.repository.CommentRepository.POST_COLLECTION;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
import com.google.firebase.firestore.DocumentReference;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import java.util.Objects;

public class SubCommentAdapter extends RecyclerView.Adapter<SubCommentAdapter.ViewHolder> {

    private PostRepository postRepo = PostRepository.getInstance();
    private List<Comment> mSubComment;
    private NewCommentAdapter.IClickMyCommentItem iClickCommentItem;
    private NewCommentAdapter.IClickMyCommentItem iClickMyCommentItem;

    public void setiClickCommentItem(NewCommentAdapter.IClickMyCommentItem iClickCommentItem) {
        this.iClickCommentItem = iClickCommentItem;
    }

    public void setiClickMyCommentItem(NewCommentAdapter.IClickMyCommentItem iClickMyCommentItem) {
        this.iClickMyCommentItem = iClickMyCommentItem;
    }

    public List<Comment> getmSubComment() {
        return mSubComment;
    }

    public void setmSubComment(List<Comment> mSubComment) {
        this.mSubComment = mSubComment;
    }

    public SubCommentAdapter() {

        mSubComment = new ArrayList<>();
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
        Comment comment = mSubComment.get(position);
        postRepo.getUserById(comment.getPublisher(), result -> {
            Fimaers userInfo = result;
            if (userInfo != null) {
                Picasso.get().load(userInfo.getAvatarUrl()).placeholder(R.drawable.ic_default_avatar).into(binding.imageAvatar);
                binding.userName.setText(userInfo.getLastName());
                if (!userInfo.isGender()) {
                    binding.itemUserIcGender.setImageResource(R.drawable.ic_male);
                    binding.itemUserLayoutGenderAge.setBackgroundResource(R.drawable.shape_gender_border_pink);
                }
                binding.itemUserTvAge.setText(String.valueOf(userInfo.calculateAge()));
                binding.constraintLayout.setOnClickListener(view -> {
                    if (Objects.equals(FirebaseAuth.getInstance().getUid(), comment.getPublisher())) {
                        if (iClickMyCommentItem != null) {
                            iClickMyCommentItem.onClick(comment);
                        }
                    } else {
                        if (iClickCommentItem != null)
                            iClickCommentItem.onClick(comment);
                    }
                });
                //commentRepository.getCommentRef(currentComment.getPostId(), POST_COLLECTION ).document(currentComment.getId()), currentComment

            }
        });


        String userId = FirebaseAuth.getInstance().getUid();
        DocumentReference reference = CommentRepository.getInstance().getCommentRef(comment.getPostId(), CommentRepository.POST_COLLECTION).document(comment.getId());
        binding.likeNumber.setText(String.valueOf(PostService.getInstance().getNumberOfLikes(comment.getLikes())));
        boolean isLike = comment.getLikes().containsKey(comment.getPublisher()) && Boolean.TRUE.equals(comment.getLikes().get(userId));
        binding.heart.setImageResource(isLike ? R.drawable.ic_heart1 : R.drawable.ic_heart_gray);
        binding.heart.setOnClickListener(view -> {
            boolean oldValue = isLike;
            String path = "likes." + userId;
            binding.heart.setImageResource(!oldValue ? R.drawable.ic_heart1 : R.drawable.ic_heart_gray);
            reference.update(
                            path, !oldValue
                    )
                    .addOnFailureListener(e -> {
                        binding.heart.setImageResource(oldValue ? R.drawable.ic_heart1 : R.drawable.ic_heart_gray);
                    });
        });

        binding.content.setText(comment.getContent());
        TimerService.setDuration(binding.time, (Date) comment.getTimeCreated());
    }

    public void addUpdate() {
        this.notifyItemInserted(mSubComment.size() - 1);
    }

    public void addModify(int pos) {
        this.notifyItemChanged(pos);
    }

    private void updateComment(CommentItemBinding binding, Fimaers fimaers, Comment comment) {
        //truong hop xoa thi update comment se = null
        if (comment == null) return;
        //listen content change
        if (!comment.getContent().equals(comment.getContent())) {
            binding.content.setText(comment.getContent());
        }
        //edit time change
        if (comment.getTimeEdited() != null) {
            binding.isEdited.setVisibility(View.VISIBLE);
            TimerService.setDuration(binding.time, comment.getTimeEdited());
        }
        DocumentReference reference = CommentRepository.getInstance().getCommentRef(comment.getPostId(), CommentRepository.POST_COLLECTION).document(comment.getId());
        binding.likeNumber.setText(String.valueOf(PostService.getInstance().getNumberOfLikes(comment.getLikes())));
        boolean isLike = comment.getLikes().containsKey(comment.getPublisher()) && comment.getLikes().get(comment.getPublisher());
        binding.heart.setImageResource(isLike ? R.drawable.ic_heart1 : R.drawable.ic_heart_gray);
        binding.heart.setOnClickListener(view -> {
            boolean oldValue = isLike;
            String path = "likes." + fimaers.getUid();
            binding.heart.setImageResource(!oldValue ? R.drawable.ic_heart1 : R.drawable.ic_heart_gray);
            reference.update(
                            path, !oldValue
                    )
                    .addOnFailureListener(e -> {
                        binding.heart.setImageResource(oldValue ? R.drawable.ic_heart1 : R.drawable.ic_heart_gray);
                    });
        });

        //like change
//            binding.likeNumber.setText(String.valueOf(PostService.getInstance().getNumberOfLikes(updateComment.getLikes())));
//            if (updateComment.getLikes().containsKey(current.getPublisher()) && Boolean.TRUE.equals(updateComment.getLikes().get(current.getPublisher()))) {
//                binding.heart.setImageResource(R.drawable.ic_heart1);
//                binding.heart.setOnClickListener(view -> {
//                    String path = "likes." + fimaers.getUid();
//                    binding.heart.setImageResource(R.drawable.ic_heart1);
//                    reference.update(
//                            path, false
//                    );
//                });
//            } else {
//                binding.heart.setImageResource(R.drawable.ic_heart_gray);
//                binding.heart.setOnClickListener(view -> {
//                    String path = "likes." + fimaers.getUid();
//                    binding.heart.setImageResource(R.drawable.ic_heart1);
//                    reference.update(
//                            path, true
//                    );
//                });
//            }
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

