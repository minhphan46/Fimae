//package com.example.fimae.adapters;
//
//import static com.example.fimae.repository.CommentRepository.POST_COLLECTION;
//
//import android.content.Context;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//
//import androidx.annotation.NonNull;
//import androidx.recyclerview.widget.LinearLayoutManager;
//import androidx.recyclerview.widget.RecyclerView;
//
//import com.example.fimae.R;
//import com.example.fimae.databinding.CommentItemBinding;
//import com.example.fimae.models.Comment;
//import com.example.fimae.models.CommentItemAdapter;
//import com.example.fimae.models.Fimaers;
//import com.example.fimae.repository.CommentRepository;
//import com.example.fimae.repository.PostRepository;
//import com.example.fimae.service.PostService;
//import com.example.fimae.service.TimerService;
//import com.google.firebase.auth.FirebaseAuth;
//import com.google.firebase.firestore.DocumentReference;
//import com.squareup.picasso.Picasso;
//
//import java.util.ArrayList;
//import java.util.Date;
//import java.util.List;
//import java.util.Objects;
//
//@Deprecated
//public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.ViewHolder> {
//    public Context mContext;
////    public List<Comment> mComment;
//    public List<CommentItemAdapter> mCommentItem;
//    final PostRepository postRepo = PostRepository.getInstance();
//    final CommentRepository commentRepository = CommentRepository.getInstance();
//    final String postId;
////    private SubCommentAdapter.IClickCommentItem iClickCommentItem;
//    private IClickMyCommentItem iClickMyCommentItem;
//    private IClickMyCommentItem iClickCommentItem;
//
//    //interface
//    public interface IClickMyCommentItem{
//        void onClick(Comment comment);
//    }
//
//    //constructor
//    public CommentAdapter(Context mContext, List<CommentItemAdapter> mCommentItem, IClickMyCommentItem listener,String postId, IClickMyCommentItem longClickListener) {
//        this.mContext = mContext;
//        this.mCommentItem = mCommentItem;
//        this.iClickCommentItem = listener;
//        this.postId = postId;
//        this.iClickMyCommentItem = longClickListener;
//    }
//
//    public void addUpdate(){
//        this.notifyItemInserted(mCommentItem.size() -1 );
//    }
//    public void addModify(int pos){
//        this.notifyItemChanged(pos);
//    }
//    @NonNull
//    @Override
//    public CommentAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
//        CommentItemBinding commentItemBinding = CommentItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
//        return new ViewHolder(commentItemBinding);
//    }
//
//
//    @Override
//    public void onBindViewHolder(@NonNull CommentAdapter.ViewHolder holder, int position) {
//        CommentItemBinding binding = holder.binding;
//        CommentItemAdapter currentCommentItem = mCommentItem.get(position);
//        Comment currentComment = currentCommentItem.getComment();
//        postRepo.getUserById(currentComment.getPublisher(), userInfo -> {
//            if(userInfo != null){
//                binding(binding, userInfo, currentCommentItem);
//            }
//        });
//    }
//    @Override
//    public int getItemCount() {
//        return mCommentItem.size();
//    }
//
//
//    public class ViewHolder extends RecyclerView.ViewHolder {
//        CommentItemBinding binding;
//        public ViewHolder(CommentItemBinding binding) {
//            super(binding.getRoot());
//            this.binding = binding;
//        }
//    }
//
//    private void binding(CommentItemBinding binding, Fimaers userInfo, CommentItemAdapter currentCommentItem){
//        Comment currentComment = currentCommentItem.getComment();
//        binding.content.setText(currentComment.getContent());
//        if(currentComment.getTimeEdited() == null ) {
//            TimerService.setDuration(binding.time, (Date) currentComment.getTimeCreated());
//        }
//        else{
//            binding.isEdited.setVisibility(View.VISIBLE);
//            TimerService.setDuration(binding.time, currentComment.getTimeEdited());
//        }
//        Picasso.get().load(userInfo.getAvatarUrl()).placeholder(R.drawable.ic_default_avatar).into(binding.imageAvatar);
//        binding.userName.setText(userInfo.getLastName());
//        if(!userInfo.isGender()){
//            binding.itemUserIcGender.setImageResource(R.drawable.ic_male);
//            binding.itemUserLayoutGenderAge.setBackgroundResource(R.drawable.shape_gender_border_pink);
//        }
//
//        binding.itemUserTvAge.setText(String.valueOf(userInfo.calculateAge()));
//        //init listener
//        initListener(binding, commentRepository.getCommentRef(postId, POST_COLLECTION ).document(currentComment.getId()), currentComment, userInfo);
//        SubCommentAdapter adapter = new SubCommentAdapter(mContext, currentCommentItem, (NewCommentAdapter.IClickMyCommentItem) this.iClickCommentItem, (NewCommentAdapter.IClickMyCommentItem) this.iClickMyCommentItem);
//        binding.subComment.setVisibility(View.VISIBLE);
//        LinearLayoutManager layoutManager = new LinearLayoutManager(mContext);
//        binding.subComment.setLayoutManager(layoutManager);
//        binding.subComment.setAdapter(adapter);
//        currentCommentItem.setSubAdapter(adapter);
//        binding.constraintLayout.setOnClickListener(view -> {
//            if(Objects.equals(currentComment.getPublisher(), FirebaseAuth.getInstance().getUid())){
//                iClickMyCommentItem.onClick(currentComment);
//            }
//            else{
//                iClickCommentItem.onClick(currentComment);
//            }
//        });
//        commentRepository.getSubComment(currentCommentItem);
//
//    }
//    private void initListener(CommentItemBinding binding , DocumentReference reference, Comment current, Fimaers fimaers){
//        reference.addSnapshotListener((value, error) -> {
//            if (error != null) {
//                return;
//            }
//            Comment updateComment = value.toObject(Comment.class);
//            //truong hop xoa thi update comment se = null
//            if(updateComment == null) return;
//            //hien da chinh sua
//            if(!updateComment.getContent().equals(current.getContent())){
//                binding.content.setText(updateComment.getContent());
//            }
//            if(updateComment.getTimeEdited() != null){
//                binding.isEdited.setVisibility(View.VISIBLE);
//                TimerService.setDuration(binding.time, updateComment.getTimeEdited());
//            }
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
//        });
//    }
//
//}
