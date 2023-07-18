package com.example.fimae.adapters;

import static com.example.fimae.repository.CommentRepository.POST_COLLECTION;

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
import com.example.fimae.models.CommentItemAdapter;
import com.example.fimae.models.Fimaers;
import com.example.fimae.repository.CommentRepository;
import com.example.fimae.repository.PostRepository;
import com.example.fimae.service.PostService;
import com.example.fimae.service.TimerService;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.squareup.picasso.Picasso;

import java.util.Date;
import java.util.List;

import java.util.Objects;
public class SubCommentAdapter extends RecyclerView.Adapter<SubCommentAdapter.ViewHolder> {
    public Context mContext;
    private NewCommentAdapter.IClickMyCommentItem iClickCommentItem;
    private PostRepository postRepo = PostRepository.getInstance();
    final CommentRepository commentRepository = CommentRepository.getInstance();
    private CommentItemAdapter commentItemAdapter;
    private List<CommentItemAdapter> mSubComment;
    final NewCommentAdapter.IClickMyCommentItem iClickMyCommentItem;
//    public interface IClickCommentItem {
//        void onClick(String commentId, SubCommentAdapter commentAdapter, Fimaers fimaers);
//    }

    public SubCommentAdapter(Context mContext, CommentItemAdapter commentItemAdapter, NewCommentAdapter.IClickMyCommentItem listener, NewCommentAdapter.IClickMyCommentItem iClickMyCommentItem ) {
        this.mContext = mContext;
        this.iClickCommentItem = listener;
        this.iClickMyCommentItem = iClickMyCommentItem;
        this.commentItemAdapter = commentItemAdapter;
        this.mSubComment = commentItemAdapter.getSubComment();
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
        CommentItemAdapter currentCommentItem = mSubComment.get(position);
        Comment currentComment = mSubComment.get(position).getComment();
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
                        iClickCommentItem.onClick(currentComment);
                    }
                } );
                SubCommentAdapter adapter = new SubCommentAdapter(mContext, currentCommentItem, this.iClickCommentItem, this.iClickMyCommentItem);
                binding.subComment.setVisibility(View.VISIBLE);
                LinearLayoutManager layoutManager = new LinearLayoutManager(mContext);
                binding.subComment.setLayoutManager(layoutManager);
                binding.subComment.setAdapter(adapter);
                currentCommentItem.setSubAdapter(adapter);
                commentRepository.getSubComment(currentCommentItem);
                updateComment(binding, commentRepository.getCommentRef(currentComment.getPostId(), POST_COLLECTION ).document(currentComment.getId()), currentComment, userInfo);
            }
        });
        binding.content.setText(currentComment.getContent());
        TimerService.setDuration(binding.time, (Date) currentComment.getTimeCreated());
    }
    public void addUpdate(){
        this.notifyItemInserted(mSubComment.size() - 1);
    }
    public void addModify(int pos){
        this.notifyItemChanged(pos);
    }
    private void updateComment(CommentItemBinding binding , DocumentReference reference, Comment current, Fimaers fimaers){
        reference.addSnapshotListener((value, error) -> {
            if (error != null) {
                return;
            }
            Comment updateComment = value.toObject(Comment.class);
            //truong hop xoa thi update comment se = null
            if(updateComment == null) return;
            //listen content change
            if(!updateComment.getContent().equals(current.getContent())){
                binding.content.setText(updateComment.getContent());
            }
            //edit time change
            if(updateComment.getTimeEdited() != null){
                binding.isEdited.setVisibility(View.VISIBLE);
                TimerService.setDuration(binding.time, updateComment.getTimeEdited());
            }
            //like change
            binding.likeNumber.setText(String.valueOf(PostService.getInstance().getNumberOfLikes(updateComment.getLikes())));
            if (updateComment.getLikes().containsKey(current.getPublisher()) && Boolean.TRUE.equals(updateComment.getLikes().get(current.getPublisher()))) {
                binding.heart.setImageResource(R.drawable.ic_heart1);
                binding.heart.setOnClickListener(view -> {
                    String path = "likes." + fimaers.getUid();
                    binding.heart.setImageResource(R.drawable.ic_heart1);
                    reference.update(
                            path, false
                    );
                });
            } else {
                binding.heart.setImageResource(R.drawable.ic_heart_gray);
                binding.heart.setOnClickListener(view -> {
                    String path = "likes." + fimaers.getUid();
                    binding.heart.setImageResource(R.drawable.ic_heart1);
                    reference.update(
                            path, true
                    );
                });
            }
        });
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

