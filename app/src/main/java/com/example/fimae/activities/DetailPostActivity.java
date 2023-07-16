package com.example.fimae.activities;

import static com.example.fimae.fragments.FeedFragment.REQUEST_CREATEPOST_CODE;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import androidx.databinding.ViewDataBinding;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewbinding.ViewBinding;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import com.example.fimae.R;
import com.example.fimae.adapters.CommentAdapter;
import com.example.fimae.adapters.PostAdapter;
import com.example.fimae.adapters.PostPhotoAdapter;
import com.example.fimae.adapters.SubCommentAdapter;
import com.example.fimae.bottomdialogs.LikedPostListFragment;
import com.example.fimae.databinding.ActivityPostBinding;
import com.example.fimae.databinding.DetailPostBinding;
import com.example.fimae.fragments.ChatBottomSheetFragment;
import com.example.fimae.fragments.CommentEditFragment;
import com.example.fimae.models.BottomSheetItem;
import com.example.fimae.models.Comment;
import com.example.fimae.models.CommentBase;
import com.example.fimae.models.Post;
import com.example.fimae.models.Seed;
import com.example.fimae.models.Fimaers;
import com.example.fimae.models.SubComment;
import com.example.fimae.repository.CommentRepository;
import com.example.fimae.repository.PostRepository;
import com.example.fimae.service.TimerService;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.AggregateQuery;
import com.google.firebase.firestore.AggregateQuerySnapshot;
import com.google.firebase.firestore.AggregateSource;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldPath;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.type.DateTime;
import com.squareup.picasso.Picasso;

import org.checkerframework.checker.units.qual.C;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

public class DetailPostActivity extends AppCompatActivity {
    boolean isLike = false;
    boolean canPost = false;
    boolean isFollow = false;
    public PostMode postMode;
    public String selectedCommentId = "";
    public Fimaers selectedFimaers;
    private Post post;
    private Fimaers fimaers;
    DetailPostBinding binding;
    private PostPhotoAdapter adapter;
    List<String> imageUrls = new ArrayList<>();
    List<Uri> imageUris = new ArrayList<>();
    List<Comment> comments;
    CommentAdapter commentAdapter;
    SubCommentAdapter selectedAdapter;
    CollectionReference postRef = FirebaseFirestore.getInstance().collection("posts");
    CollectionReference fimaesRef = FirebaseFirestore.getInstance().collection("fimaers");
    CommentRepository commentRepository = CommentRepository.getInstance();
    PostRepository postRepository = PostRepository.getInstance();
    List<BottomSheetItem> commentSheetItemList;
    List<BottomSheetItem> postSheetItemList;
    List<BottomSheetItem> reportSheetItemList;
    ChatBottomSheetFragment chatBottomSheetFragment;
    ActivityResultLauncher<Intent> mStartForResult = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == REQUEST_CREATEPOST_CODE) {
                    Intent intent = result.getData();
                    String postId = intent.getStringExtra("postId");
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.detail_post);
        Intent intent = getIntent();
        String postId = intent.getStringExtra("id");
        getPost(postId);
    }
    private void getPost(String postId){
        postRef.document(postId).addSnapshotListener((value, e) -> {
            if (e != null) {
                return;
            }
            if (value != null && value.exists()) {
                Post updatePost = value.toObject(Post.class);
                if(post == null){
                    post = updatePost;
                    fimaesRef.document(post.getPublisher()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            fimaers = documentSnapshot.toObject(Fimaers.class);
                            initView();
                            initListener();

                        }
                    });
                }
                listenToChange(updatePost);
            }
        });

    }
    private void listenToChange(Post updatePost){
        if(imageUrls.size() != post.getPostImages().size()){
            imageUrls = post.getPostImages();
            if(adapter != null) adapter.notifyDataSetChanged();
        }
        if(!post.getContent().equals(updatePost.getContent())){
            binding.content.setText(updatePost.getContent());
        }
        binding.numberOfComment.setText(String.valueOf(updatePost.getNumberOfComments()));
        binding.likeNumber.setText(String.valueOf(updatePost.getNumberTrue()));
        binding.commentNumber.setText(String.valueOf(updatePost.getNumberOfComments()));
        binding.numberOfComment.setText(String.valueOf(updatePost.getNumberOfComments()));
        binding.numberofLike.setText(String.valueOf(updatePost.getNumberTrue()));
        binding.numberofLike.setOnClickListener(view -> {
            LikedPostListFragment likedPostListFragment = LikedPostListFragment.getInstance(updatePost.getLikes(), updatePost.getNumberTrue());
            likedPostListFragment.show(getSupportFragmentManager(), "likelist");
        });
    }
    private void initView(){
        TimerService.setDuration(binding.activeTime, post.getTimeCreated());
        binding.content.setText(post.getContent());
        binding.icMore.setOnClickListener(view -> {
            if(Objects.equals(FirebaseAuth.getInstance().getUid(), post.getPublisher())){
                showPostDialog();
            }
            else {
                showReportDialog();
            }
        });
        Picasso.get().load(fimaers.getAvatarUrl()).placeholder(R.drawable.ic_default_avatar).into(binding.imageAvatar);
        binding.userName.setText(fimaers.getLastName());
        if(imageUrls != null && !imageUrls.isEmpty()){
            for(int i = 0; i < imageUrls.size(); i++){
                imageUris.add(Uri.parse(imageUrls.get(i)));
            }
            adapter = new PostPhotoAdapter(this, imageUris, false);
            binding.imageList.setVisibility(View.VISIBLE);
            LinearLayoutManager layoutManager = new GridLayoutManager(this, PostAdapter.getColumnSpan(imageUris.size()) );
            binding.imageList.setLayoutManager(layoutManager);
            binding.imageList.setAdapter(adapter);
        }
        if(!fimaers.isGender()){
            binding.itemUserIcGender.setImageResource(R.drawable.ic_male);
            binding.genderAgeIcon.setBackgroundResource(R.drawable.shape_gender_border_pink);
        }
        if(post.getLikes().containsKey(fimaers.getUid()) && post.getLikes().get(fimaers.getUid())){
            binding.icLike.setImageResource(R.drawable.ic_heart1);
            isLike = true;
        }
        binding.ageTextView.setText(String.valueOf(fimaers.calculateAge()));
        comments = new ArrayList<>();
        commentAdapter = new CommentAdapter(this, comments, (comment, adapter, fimaers) -> {
            selectedCommentId =  comment;
            binding.addComment.setHint("@"+fimaers.getLastName());
            selectedAdapter = adapter;
            selectedFimaers = fimaers;
        }, post.getPostId(), this::showCommentDialog);
        LinearLayoutManager layoutManager1 = new LinearLayoutManager(this);
        binding.commentRecycler.setLayoutManager(layoutManager1);
        binding.commentRecycler.setAdapter(commentAdapter);
        commentRepository.getComment(post.getPostId(), comments, commentAdapter);
    }
    private void initListener(){
        createCommentDialog();
        //go back
        binding.goBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        //like
        binding.icLike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DocumentReference reference = FirebaseFirestore.getInstance().collection("posts").document(post.getPostId());
                isLike = !isLike;
                String path = "likes." + fimaers.getUid();

                if(isLike){
                    binding.icLike.setImageResource(R.drawable.ic_heart1);
                    reference.update(
                            path, true
                    );
                }
                else{
                    binding.icLike.setImageResource(R.drawable.ic_heart_gray);
                    reference.update(
                            path, false
                    );
                }


            }
        });
        //add comment
        binding.addComment.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(binding.addComment.getText().length() > 0 ){
                    binding.post.setBackgroundResource(R.drawable.canpost);
                    canPost = true;
                }
                else {
                    binding.post.setBackgroundResource(R.drawable.cantpost);
                    canPost = false;
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        //go liked list people
        //
        binding.follow.setOnClickListener(view -> {
            isFollow = true;
            binding.follow.setVisibility(View.INVISIBLE);
            binding.edit.setVisibility(View.VISIBLE);
        });
        binding.edit.setOnClickListener(view -> {
            isFollow = false;
            binding.edit.setVisibility(View.INVISIBLE);
            binding.follow.setVisibility(View.VISIBLE);
        });
        binding.iconEmoji.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            }
        });
        binding.post.setOnClickListener(view -> {
            if(canPost) {
                if ( !selectedCommentId.trim().isEmpty()) {
                    SubComment subComment = new SubComment();
                    subComment.setContent(binding.addComment.getText().toString());
                    subComment.setPublisher(fimaers.getUid());
                    subComment.setParentId(selectedCommentId);
                    commentRepository.postSubComment(post.getPostId(), selectedCommentId,subComment);
                } else {
                    Comment comment = new Comment();
                    comment.setPostId(post.getPostId());
                    comment.setPublisher(fimaers.getUid());
                    comment.setContent(binding.addComment.getText().toString());
                    commentRepository.postComment(post.getPostId(), comment);
                }
                postRepository.updateNumOfComment(post.getPostId());
                binding.addComment.clearFocus();
                binding.addComment.setText("");
                binding.addComment.setHint("Để lại một bình luận");
                selectedCommentId = "";
            }
        });
    }
    private void createCommentDialog(){
        commentSheetItemList = new ArrayList<BottomSheetItem>() {
            {
                add(new BottomSheetItem(R.drawable.ic_edit, "Chỉnh sửa bình luận"));
                add(new BottomSheetItem(R.drawable.ic_user_block, "Xóa bình luận"));
            }
        };
        postSheetItemList = new ArrayList<BottomSheetItem>(){
            {
                add(new BottomSheetItem(R.drawable.ic_edit, "Chỉnh sửa bài đăng"));
                add(new BottomSheetItem(R.drawable.ic_user_block, "Xóa bài đăng"));
            }
        };
        reportSheetItemList = new ArrayList<BottomSheetItem>(){
            {
                add(new BottomSheetItem(R.drawable.ic_chat_dots, "Báo cáo"));
            }
        };

    }
    private void showReportDialog(){
        chatBottomSheetFragment = new ChatBottomSheetFragment(postSheetItemList,
                bottomSheetItem -> {
                    if(bottomSheetItem.getTitle().equals("Xóa bài đăng")){
                        chatBottomSheetFragment.dismiss();
                    }
                });
        chatBottomSheetFragment.show(getSupportFragmentManager(), chatBottomSheetFragment.getTag());
    }

    private void showPostDialog(){
        chatBottomSheetFragment = new ChatBottomSheetFragment(postSheetItemList,
                bottomSheetItem -> {
                    if(bottomSheetItem.getTitle().equals("Chỉnh sửa bài đăng")){
                        Intent intent = new Intent(getApplicationContext(), PostActivity.class );
                        intent.putExtra("id", post.getPostId());
                        mStartForResult.launch(intent);
                        finish();
                    }
                    else if(bottomSheetItem.getTitle().equals("Xóa bài đăng")){
                        chatBottomSheetFragment.dismiss();
                    }
                });
        chatBottomSheetFragment.show(getSupportFragmentManager(), chatBottomSheetFragment.getTag());

    }
    private void showEditCommentDialog(CommentBase comment){
        CommentEditFragment commentEditFragment = new CommentEditFragment(comment, post.getPostId());
        commentEditFragment.show(getSupportFragmentManager(), commentEditFragment.getTag());
        chatBottomSheetFragment.dismiss();
    }
    public void showCommentDialog(CommentBase comment){
            chatBottomSheetFragment = new ChatBottomSheetFragment(commentSheetItemList,
                    bottomSheetItem -> {
                        if(bottomSheetItem.getTitle().equals("Chỉnh sửa bình luận"))
                            showEditCommentDialog(comment);
                        else if(bottomSheetItem.getTitle().equals("Xóa bình luận")){
                            commentRepository.deleteComment(post.getPostId(), comment, getApplicationContext());
                            chatBottomSheetFragment.dismiss();
                        }
                    });
        chatBottomSheetFragment.show(getSupportFragmentManager(), chatBottomSheetFragment.getTag());
    }

//    private void readComments () {
//        CollectionReference commentRef = FirebaseFirestore.getInstance().collection("posts").document(post.getPostId()).collection("comments");
//        commentRef.addSnapshotListener((value, error) -> {
//            if (error != null) {
//                return;
//            }
//            comments.clear();
//            int number = 0;
//            for (QueryDocumentSnapshot doc : value) {
//                Comment comment = doc.toObject(Comment.class);
//                comments.add(comment);
//                number += 1 + comment.getSubComments().size();
//            }
//            postRef.document(post.getPostId()).update("numberOfComments", number);
//            commentAdapter.notifyDataSetChanged();
//        });
//    }
}