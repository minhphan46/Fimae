package com.example.fimae.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

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
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class NewCommentAdapter extends FirestoreAdapter<NewCommentAdapter.ViewHolder> {
    public interface IClickMyCommentItem {
        void onClick(Comment comment);
    }

    private Context mContext;
    private List<CommentItemAdapter> mCommentItem;
    private final PostRepository postRepo;
    //    private final CommentRepository commentRepository;
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
//        commentRepository = CommentRepository.getInstance();
    }

    ArrayList<CommentItemAdapter> commentItemAdapters;
    HashMap<String, SubCommentAdapter> subCommentAdapters = new HashMap<>();

    @Override
    public void OnSuccessQueryListener(ArrayList<DocumentSnapshot> queryDocumentSnapshots, ArrayList<DocumentChange> documentChanges) {
        if (commentItemAdapters == null) {
            commentItemAdapters = new ArrayList<>();
            for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                Comment comment = documentSnapshot.toObject(Comment.class);
                assert comment != null;
                if (comment.getParentId() == null || comment.getParentId().isEmpty()) {
                    commentItemAdapters.add(new CommentItemAdapter(comment));
                }
            }
            for (CommentItemAdapter commentItemAdapter : commentItemAdapters) {
                for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                    Comment comment = documentSnapshot.toObject(Comment.class);
                    if (comment.getParentId() != null && comment.getParentId().equals(commentItemAdapter.getComment().getId())) {
                        commentItemAdapter.addNewSubComment(comment);
                    }
                }
            }
            notifyDataSetChanged();
            return;
        }
        for (DocumentChange dc : documentChanges) {
            Comment comment = dc.getDocument().toObject(Comment.class);
            switch (dc.getType()) {
                case ADDED:
                    if (comment.getParentId() == null || comment.getParentId().isEmpty()) {
                        commentItemAdapters.add(new CommentItemAdapter(comment));
                        notifyItemInserted(commentItemAdapters.size() - 1);
                        continue;
                    } else {
                        for (int i = 0; i < commentItemAdapters.size(); i++) {
                            if (commentItemAdapters.get(i).getComment().getId().equals(comment.getParentId())) {
                                CommentItemAdapter commentItemAdapter = commentItemAdapters.get(i);
                                commentItemAdapter.addNewSubComment(comment);
                                break;
                            }
                        }
                    }
                    break;
                case MODIFIED:
                    if (comment.getParentId() == null || comment.getParentId().isEmpty()) {
                        for (int i = 0; i < commentItemAdapters.size(); i++) {
                            if (commentItemAdapters.get(i).getComment().getId().equals(comment.getId())) {
                                commentItemAdapters.get(i).modifyComment(comment);
                                notifyItemChanged(i);
                                break;
                            }
                        }
                        continue;
                    } else {
                        for (int i = 0; i < commentItemAdapters.size(); i++) {
                            if (commentItemAdapters.get(i).getComment().getId().equals(comment.getParentId())) {
                                CommentItemAdapter commentItemAdapter = commentItemAdapters.get(i);
                                commentItemAdapter.modifySubComment(comment);
                                break;
                            }
                        }
                    }
                    break;
                case REMOVED:
                    if (comment.getParentId() == null || comment.getParentId().isEmpty()) {
                        for (int i = 0; i < commentItemAdapters.size(); i++) {
                            if (commentItemAdapters.get(i).getComment().getId().equals(comment.getId())) {
                                commentItemAdapters.remove(i);
                                notifyItemRemoved(i);
                                break;
                            }
                        }
                        continue;
                    } else {
                        for (int i = 0; i < commentItemAdapters.size(); i++) {
                            if (commentItemAdapters.get(i).getComment().getId().equals(comment.getParentId())) {
                                CommentItemAdapter commentItemAdapter = commentItemAdapters.get(i);
                                commentItemAdapter.removeSubComment(comment);
                                break;
                            }
                        }
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
        CommentItemAdapter currentCommentItem = commentItemAdapters.get(position);
        Comment currentComment = currentCommentItem.getComment();
        PostRepository.getInstance().getUserById(currentComment.getPublisher(), userInfo -> {
            binding(binding, position, userInfo);
        });
    }

    @Override
    public int getItemCount() {
        if (commentItemAdapters == null) return 0;
        return commentItemAdapters.size();
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

    private void binding(CommentItemBinding binding, int position, Fimaers userInfo) {

        CommentItemAdapter commentItemAdapter = commentItemAdapters.get(position);
        Comment currentComment = commentItemAdapter.getComment();
        binding.content.setText(currentComment.getContent());
        if (currentComment.getTimeEdited() != null) {
            binding.isEdited.setVisibility(View.VISIBLE);
            TimerService.setDuration(binding.time, currentComment.getTimeEdited());
        }

        String uid = userInfo.getUid();
        DocumentReference reference = CommentRepository.getInstance().getCommentRef(currentComment.getPostId(), CommentRepository.POST_COLLECTION).document(currentComment.getId());
        binding.likeNumber.setText(String.valueOf(PostService.getInstance().getNumberOfLikes(currentComment.getLikes())));
        boolean isLike = currentComment.getLikes().containsKey(currentComment.getPublisher()) && Boolean.TRUE.equals(currentComment.getLikes().get(uid));
        binding.heart.setImageResource(isLike ? R.drawable.ic_heart1 : R.drawable.ic_heart_gray);
        binding.heart.setOnClickListener(view -> {
            boolean oldValue = isLike;
            String path = "likes." + uid;
            binding.heart.setImageResource(!oldValue ? R.drawable.ic_heart1 : R.drawable.ic_heart_gray);
            reference.update(
                    path, !oldValue
            )
                    .addOnFailureListener(e -> {
                binding.heart.setImageResource(oldValue ? R.drawable.ic_heart1 : R.drawable.ic_heart_gray);
            });
        });


        binding.content.setText(currentComment.getContent());
        if (currentComment.getTimeEdited() == null) {
            TimerService.setDuration(binding.time, (Date) currentComment.getTimeCreated());
        } else {
            binding.isEdited.setVisibility(View.VISIBLE);
            TimerService.setDuration(binding.time, currentComment.getTimeEdited());
        }
        Glide.with(mContext).load(userInfo.getAvatarUrl()).placeholder(R.drawable.ic_default_avatar).into(binding.imageAvatar);
        binding.userName.setText(userInfo.getLastName());
        if (!userInfo.isGender()) {
            binding.itemUserIcGender.setImageResource(R.drawable.ic_male);
            binding.itemUserLayoutGenderAge.setBackgroundResource(R.drawable.shape_gender_border_pink);
        }
        binding.itemUserTvAge.setText(String.valueOf(userInfo.calculateAge()));
        SubCommentAdapter adapter = commentItemAdapter.getSubAdapter();
        binding.subComment.setVisibility(View.VISIBLE);
        LinearLayoutManager layoutManager = new LinearLayoutManager(mContext);
        binding.subComment.setLayoutManager(layoutManager);
        binding.subComment.setAdapter(adapter);
        commentItemAdapter.setSubAdapter(adapter);
        binding.constraintLayout.setOnClickListener(view -> {
            if (Objects.equals(currentComment.getPublisher(), FirebaseAuth.getInstance().getUid())) {
                iClickMyCommentItem.onClick(currentComment);
            } else {
                iClickCommentItem.onClick(currentComment);
            }
        });
        adapter.setiClickMyCommentItem(iClickMyCommentItem);
        adapter.setiClickCommentItem(new IClickMyCommentItem() {
            @Override
            public void onClick(Comment comment) {
                iClickCommentItem.onClick(comment);
            }
        });
    }

}
