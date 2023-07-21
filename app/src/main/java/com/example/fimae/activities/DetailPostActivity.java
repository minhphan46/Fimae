package com.example.fimae.activities;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;

import com.example.fimae.R;
import com.example.fimae.adapters.NewCommentAdapter;
import com.example.fimae.adapters.PostAdapter;
import com.example.fimae.adapters.PostPhotoAdapter;
import com.example.fimae.adapters.ShareAdapter;
import com.example.fimae.bottomdialogs.LikedPostListFragment;
import com.example.fimae.bottomdialogs.ListItemBottomSheetFragment;
import com.example.fimae.databinding.DetailPostBinding;
import com.example.fimae.fragments.FimaeBottomSheet;
import com.example.fimae.fragments.CommentEditFragment;
import com.example.fimae.models.BottomSheetItem;
import com.example.fimae.models.Comment;
import com.example.fimae.models.CommentItemAdapter;
import com.example.fimae.models.Conversation;
import com.example.fimae.models.Post;
import com.example.fimae.models.Fimaers;
import com.example.fimae.repository.ChatRepository;
import com.example.fimae.repository.CommentRepository;
import com.example.fimae.repository.FollowRepository;
import com.example.fimae.repository.PostRepository;
import com.example.fimae.service.TimerService;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class DetailPostActivity extends AppCompatActivity {
    boolean isLike = false;
    boolean canPost = false;
    public PostMode postMode;
    public String selectedCommentId = "";
    private Boolean isShowShareDialog;
    private Boolean isShowMoreDialog;
    public Fimaers selectedFimaers;
    private Post post;
    private Fimaers fimaers;
    DetailPostBinding binding;
    private PostPhotoAdapter adapter;
    private ChatRepository chatRepository = ChatRepository.getInstance();
    ArrayList<String> imageUrls = new ArrayList<>();
//    List<Uri> imageUris = new ArrayList<>();
    List<Comment> comments;
    List<CommentItemAdapter> commentItemAdapters;
    NewCommentAdapter newCommentAdapter;
//    SubCommentAdapter selectedAdapter;
    CollectionReference postRef = FirebaseFirestore.getInstance().collection("posts");
    CollectionReference fimaesRef = FirebaseFirestore.getInstance().collection("fimaers");
    CommentRepository commentRepository = CommentRepository.getInstance();
    PostRepository postRepository = PostRepository.getInstance();
    List<BottomSheetItem> commentSheetItemList;
    List<BottomSheetItem> postSheetItemList;
    List<BottomSheetItem> reportSheetItemList;
    FimaeBottomSheet fimaeBottomSheet;
    ListItemBottomSheetFragment listShareItemBottomSheetFragment;

    public interface BottomItemClickCallback{
        void onClick(Fimaers userInfo);
    }
    public static int REQUEST_EDITPOST_CODE = 2;
    ActivityResultLauncher<Intent> mStartForResult = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == REQUEST_EDITPOST_CODE) {

                }
            });
    String postId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.detail_post);
        Intent intent = getIntent();
        postId = intent.getStringExtra("id");
        getPost(postId);
        isShowShareDialog = intent.getBooleanExtra("share", false);
        isShowMoreDialog = intent.getBooleanExtra("more", false);
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
            imageUrls = new ArrayList<>(updatePost.getPostImages());

            if(adapter != null){
                adapter.updateImages(imageUrls);
                adapter.notifyDataSetChanged();
                LinearLayoutManager layoutManager = new GridLayoutManager(this, PostAdapter.getColumnSpan(imageUrls.size()) );
                binding.imageList.setLayoutManager(layoutManager);
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
            LikedPostListFragment likedPostListFragment = LikedPostListFragment.getInstance(updatePost.getLikes(), updatePost.getNumberTrue(), post);
            likedPostListFragment.show(getSupportFragmentManager(), "likelist");
        });
    }
    private void initView(){
        TimerService.setDuration(binding.activeTime, post.getTimeCreated());
        binding.content.setText(post.getContent());
        binding.icMore.setOnClickListener(view -> {
            showMoreDialog();
        });
        Picasso.get().load(fimaers.getAvatarUrl()).placeholder(R.drawable.ic_default_avatar).into(binding.imageAvatar);
        binding.userName.setText(fimaers.getLastName());
        if(imageUrls != null && !imageUrls.isEmpty()){
//            for(int i = 0; i < imageUrls.size(); i++){
//                imageUris.add(Uri.parse(imageUrls.get(i)));
//            }
            adapter = new PostPhotoAdapter(this, imageUrls);
            binding.imageList.setVisibility(View.VISIBLE);
            LinearLayoutManager layoutManager = new GridLayoutManager(this, PostAdapter.getColumnSpan(imageUrls.size()) );
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
        commentItemAdapters = new ArrayList<>();

        Query query = postRef.document(postId).collection("comments").orderBy("timeCreated", Query.Direction.ASCENDING);

        newCommentAdapter = new NewCommentAdapter(this, query, (comment) -> {
            selectedCommentId =  comment.getParentId() != "" ? comment.getParentId() : comment.getId();
            binding.addComment.setHint("@"+fimaers.getLastName());
            selectedFimaers = fimaers;
        }, post.getPostId(), this::showCommentDialog);
        LinearLayoutManager layoutManager1 = new LinearLayoutManager(this);
        binding.commentRecycler.setLayoutManager(layoutManager1);
        binding.commentRecycler.setAdapter(newCommentAdapter);
        binding.icShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showSharePostDialog();
            }
        });
//        commentRepository.getComment(post.getPostId(), commentItemAdapters, newCommentAdapter);
        if(isShowShareDialog){
            showSharePostDialog();
        }
        createCommentDialog();
        if(isShowMoreDialog){
            showMoreDialog();
        }
    }
    private void showSharePostDialog(){
        FollowRepository.getInstance().getFollowers(post.getPublisher()).addOnSuccessListener(new OnSuccessListener<ArrayList<Fimaers>>() {
            @Override
            public void onSuccess(ArrayList<Fimaers> fimaers) {
                ShareAdapter adapter = new ShareAdapter(DetailPostActivity.this, fimaers, new BottomItemClickCallback() {
                    @Override
                    public void onClick(Fimaers userInfo) {
                        if(listShareItemBottomSheetFragment != null){
                            listShareItemBottomSheetFragment.dismiss();
                        }
//                        binding.progressBar.setVisibility(View.VISIBLE);
//                        binding.contentLayout.setVisibility(View.GONE);
                        chatRepository.getOrCreateFriendConversation(userInfo.getUid()).addOnCompleteListener(new OnCompleteListener<Conversation>() {
                            @Override
                            public void onComplete(@NonNull Task<Conversation> task) {
                                if(task.getResult() != null){
                                    chatRepository.sendPostLink(task.getResult().getId(), postId);
                                    Intent intent = new Intent(DetailPostActivity.this, OnChatActivity.class);
                                    intent.putExtra("conversationID", task.getResult().getId());
                                    startActivity(intent);
//                                    binding.progressBar.setVisibility(View.GONE);
//                                    binding.contentLayout.setVisibility(View.VISIBLE);
                                }
                            }
                        });
                    }
                });
                String title = "Chia sẻ bài viết";
                listShareItemBottomSheetFragment = ListItemBottomSheetFragment.getInstance(title,  adapter);
                listShareItemBottomSheetFragment.show(getSupportFragmentManager(), "shareList");
            }
        });
    }
    private void initListener(){
        //go back
        /*binding.goBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });*/
        binding.toolbar.setNavigationOnClickListener(new View.OnClickListener() {
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
        if(post.getPublisher().equals(FirebaseAuth.getInstance().getUid())){
            binding.follow.setVisibility(View.GONE);
        }
        else{
            FollowRepository.getInstance().followRef.document(FirebaseAuth.getInstance().getUid()+"_"+post.getPublisher()).addSnapshotListener(new EventListener<DocumentSnapshot>() {
                @Override
                public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                    if(error != null ){
                        return;
                    }
                    if(value != null && value.exists()){
                        binding.follow.setVisibility(View.GONE);
                        binding.edit.setVisibility(View.VISIBLE);
                    }
                    else{
                        binding.follow.setVisibility(View.VISIBLE);
                        binding.edit.setVisibility(View.GONE);
                    }
                }
            });

        }
        binding.edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                postRepository.goToChatWithUser(post.getPublisher(), DetailPostActivity.this);
            }
        });
        binding.follow.setOnClickListener(view -> FollowRepository.getInstance().follow(post.getPublisher()).addOnCompleteListener(new OnCompleteListener<Boolean>() {
            @Override
            public void onComplete(@NonNull Task<Boolean> task) {
                if(task.getResult()){
                    binding.follow.setVisibility(View.GONE);
                    binding.edit.setVisibility(View.VISIBLE);
                }
            }
        }));
        binding.iconEmoji.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            }
        });
        binding.post.setOnClickListener(view -> {
            if(canPost) {
                if ( !selectedCommentId.trim().isEmpty()) {
                    Comment subComment = new Comment();
                    subComment.setContent(binding.addComment.getText().toString());
                    subComment.setPublisher(fimaers.getUid());
                    subComment.setParentId(selectedCommentId);
                    subComment.setPostId(post.getPostId());
                    commentRepository.postComment(post.getPostId(), "posts", subComment);
                } else {
                    Comment comment = new Comment();
                    comment.setPostId(post.getPostId());
                    comment.setPublisher(fimaers.getUid());
                    comment.setContent(binding.addComment.getText().toString());
                    comment.setParentId("");
                    commentRepository.postComment(post.getPostId(), "posts",comment);
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
        fimaeBottomSheet = new FimaeBottomSheet(reportSheetItemList,
                bottomSheetItem -> {
                    if(bottomSheetItem.getTitle().equals("Báo cáo")){
                        fimaeBottomSheet.dismiss();
                    }
                });
        fimaeBottomSheet.show(getSupportFragmentManager(), fimaeBottomSheet.getTag());
    }

    private void showPostDialog(){
        fimaeBottomSheet = new FimaeBottomSheet(postSheetItemList,
                bottomSheetItem -> {
                    if(bottomSheetItem.getTitle().equals("Chỉnh sửa bài đăng")){
                        Intent intent = new Intent(getApplicationContext(), PostActivity.class );
                        intent.putExtra("id", post.getPostId());

                        mStartForResult.launch(intent);
                        fimaeBottomSheet.dismiss();
                    }
                    else if(bottomSheetItem.getTitle().equals("Xóa bài đăng")){
                        fimaeBottomSheet.dismiss();
                    }
                });
        fimaeBottomSheet.show(getSupportFragmentManager(), fimaeBottomSheet.getTag());

    }
    private void showEditCommentDialog(Comment comment){
        CommentEditFragment commentEditFragment = new CommentEditFragment(comment, post.getPostId());
        commentEditFragment.show(getSupportFragmentManager(), commentEditFragment.getTag());
        fimaeBottomSheet.dismiss();
    }
    public void showCommentDialog(Comment comment){
            fimaeBottomSheet = new FimaeBottomSheet(commentSheetItemList,
                    bottomSheetItem -> {
                        if(bottomSheetItem.getTitle().equals("Chỉnh sửa bình luận"))
                            showEditCommentDialog(comment);
                        else if(bottomSheetItem.getTitle().equals("Xóa bình luận")){
                            commentRepository.deleteComment(post.getPostId(), comment);
                            fimaeBottomSheet.dismiss();
                        }
                    });
        fimaeBottomSheet.show(getSupportFragmentManager(), fimaeBottomSheet.getTag());
    }
    private void showMoreDialog(){
        if(Objects.equals(FirebaseAuth.getInstance().getUid(), post.getPublisher())){
            showPostDialog();
        }
        else {
            showReportDialog();
        }
    }
}