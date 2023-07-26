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
import com.example.fimae.adapters.ReportItemDetailAdapter;
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
import com.example.fimae.models.ReportDetail;
import com.example.fimae.models.Reports;
import com.example.fimae.models.UserDisable;
import com.example.fimae.repository.AdminRepository;
import com.example.fimae.repository.ChatRepository;
import com.example.fimae.repository.CommentRepository;
import com.example.fimae.repository.FimaerRepository;
import com.example.fimae.repository.FollowRepository;
import com.example.fimae.repository.PostRepository;
import com.example.fimae.repository.ReportRepository;
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
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Date;
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
    private ChatRepository chatRepository = ChatRepository.getDefaultChatInstance();
    private ArrayList<ReportDetail> reportDetails;

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
    ListItemBottomSheetFragment listReportItemBottomSheetFragment;

    public interface BottomItemClickCallback{
        void onClick(Fimaers userInfo);
    }
    public static int REQUEST_EDITPOST_CODE = 2;
    List<BottomSheetItem> addDisableDialog;
    List<BottomSheetItem> removeDisableDialog;
    FimaeBottomSheet addDisableSheet;
    FimaeBottomSheet removeDisableSheet;
    private UserDisable userDisable;

    private void createDialog() {
        addDisableDialog = new ArrayList<BottomSheetItem>() {
            {
                add(new BottomSheetItem(R.drawable.ic_edit, "Vô hiệu hóa đăng tin"));
            }
        };
        removeDisableDialog = new ArrayList<BottomSheetItem>() {
            {
                add(new BottomSheetItem(R.drawable.ic_edit, "Xóa vô hiệu hóa đăng tin"));
            }
        };
    }
    private void showDeleteDisableSheet () {
        removeDisableSheet = new FimaeBottomSheet(removeDisableDialog,
                bottomSheetItem -> {
                    if (bottomSheetItem.getTitle().equals("Xóa vô hiệu hóa đăng tin")) {
                        AdminRepository.getInstance().disableRef.document(userDisable.getUserId()+"POST").delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                userDisable = null;
                            }
                        });
                        removeDisableSheet.dismiss();
                    }
                });
        removeDisableSheet.show(getSupportFragmentManager(), "Remove");
    }
    private void showAddDisableSheet() {
        addDisableSheet = new FimaeBottomSheet(addDisableDialog,
                bottomSheetItem -> {
                    if (bottomSheetItem.getTitle().equals("Vô hiệu hóa đăng tin")) {
                        Intent intent = new Intent(DetailPostActivity.this, DisableUserActivity.class);
                        intent.putExtra("id", post.getPublisher());
                        intent.putExtra("type", "POST");
                        startActivity(intent);
                        addDisableSheet.dismiss();
                    }
                });
        addDisableSheet.show(getSupportFragmentManager(), "Addd");
    }

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
        createDialog();
        isShowShareDialog = intent.getBooleanExtra("share", false);
        isShowMoreDialog = intent.getBooleanExtra("more", false);
        binding.icMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(userDisable == null){
                    showAddDisableSheet();
                }
                else {
                    showDeleteDisableSheet();
                }
            }
        });

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
                            initView(updatePost.getContent(), fimaers.getUid());
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
    private void getNumberOfReport(){
        ReportRepository.getInstance().reportRef.document(post.getPublisher()).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                if(error != null || value == null) return;
                Reports report = value.toObject(Reports.class);
                ReportRepository.getInstance().reportRef.document(post.getPublisher()).collection("reportdetails").addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        if (error != null || value == null) {
                            return;
                        }
                        if(reportDetails == null) reportDetails = new ArrayList<>();
                        else reportDetails.clear();
                        for(DocumentSnapshot dc: value.getDocuments()){
                            ReportDetail reportDetail = dc.toObject(ReportDetail.class);
                            reportDetails.add(reportDetail);
                        }
                        if(userDisable == null)  binding.numberOfReport.setText(reportDetails.size() + " báo cáo");
                    }
                });
            }
        });

    }
    private void initView(String content, String publisherId){
        AdminRepository.getInstance().disableRef.document(post.getPublisher()+"POST").addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                if(error != null || value == null){
                    return;
                }
                userDisable = value.toObject(UserDisable.class);
                if(userDisable != null && userDisable.getTimeEnd().after(new Date()) && userDisable.getType() != null && userDisable.getType().equals("POST")){
                    binding.isDisable.setVisibility(View.VISIBLE);
                }
            }
        });
        getNumberOfReport();
        TimerService.setDuration(binding.activeTime, post.getTimeCreated());
        binding.content.setText(post.getContent());
        Picasso.get().load(fimaers.getAvatarUrl()).placeholder(R.drawable.ic_default_avatar).into(binding.imageAvatar);
        binding.userName.setText(fimaers.getLastName());
        if(imageUrls != null && !imageUrls.isEmpty()){
//            for(int i = 0; i < imageUrls.size(); i++){
//                imageUris.add(Uri.parse(imageUrls.get(i)));
//            }
            adapter = new PostPhotoAdapter(this, publisherId, content,imageUrls);
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
        // comment
        comments = new ArrayList<>();
        commentItemAdapters = new ArrayList<>();

        Query query = postRef.document(postId).collection("comments").orderBy("timeCreated", Query.Direction.ASCENDING);

        newCommentAdapter = new NewCommentAdapter(this, CommentRepository.POST_COLLECTION, query, (comment) -> {
            selectedCommentId = comment.getParentId() != "" ? comment.getParentId() : comment.getId();
            FimaerRepository.getInstance().getFimaerById(comment.getPublisher()).addOnCompleteListener(task -> {
                task.onSuccessTask(mfimaers -> {
                    return null;
                });
            });
            selectedFimaers = fimaers;
        }, post.getPostId(), new NewCommentAdapter.IClickMyCommentItem() {
            @Override
            public void onClick(Comment comment) {

            }
        });
        LinearLayoutManager layoutManager1 = new LinearLayoutManager(this);
        binding.commentRecycler.setLayoutManager(layoutManager1);
        binding.commentRecycler.setAdapter(newCommentAdapter);
        // share
//        commentRepository.getComment(post.getPostId(), commentItemAdapters, newCommentAdapter);
        if(isShowShareDialog){
            showSharePostDialog();
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
        ReportRepository.getInstance().reportRef.document(post.getPostId()).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                if (error != null || value == null) return;
                Reports report = value.toObject(Reports.class);
                ReportRepository.getInstance().reportRef.document(post.getPostId()).collection("reportdetails").addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        if (error != null || value == null) {
                            return;
                        }
                        if (reportDetails == null) reportDetails = new ArrayList<>();
                        else reportDetails.clear();
                        for (DocumentSnapshot dc : value.getDocuments()) {
                            ReportDetail reportDetail = dc.toObject(ReportDetail.class);
                            reportDetails.add(reportDetail);
                        }
                        binding.numberOfReport.setText(reportDetails.size() + " báo cáo");
                    }
                });
            }
        });
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
                //add comment
                //go liked list people
                //
                binding.numberOfReport.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        showReport();
                    }
                });
            }

            private void createCommentDialog() {
                commentSheetItemList = new ArrayList<BottomSheetItem>() {
                    {
                        add(new BottomSheetItem(R.drawable.ic_edit, "Chỉnh sửa bình luận"));
                        add(new BottomSheetItem(R.drawable.ic_user_block, "Xóa bình luận"));
                    }
                };
                postSheetItemList = new ArrayList<BottomSheetItem>() {
                    {
                        add(new BottomSheetItem(R.drawable.ic_edit, "Chỉnh sửa bài đăng"));
                        add(new BottomSheetItem(R.drawable.ic_user_block, "Xóa bài đăng"));
                    }
                };
                reportSheetItemList = new ArrayList<BottomSheetItem>() {
                    {
                        add(new BottomSheetItem(R.drawable.ic_chat_dots, "Báo cáo"));
                    }
                };

            }

            private void showReportDialog() {
                fimaeBottomSheet = new FimaeBottomSheet(reportSheetItemList,
                        bottomSheetItem -> {
                            if (bottomSheetItem.getTitle().equals("Báo cáo")) {
                                fimaeBottomSheet.dismiss();
                            }
                        });
                fimaeBottomSheet.show(getSupportFragmentManager(), fimaeBottomSheet.getTag());
            }

            private void showPostDialog() {
                fimaeBottomSheet = new FimaeBottomSheet(postSheetItemList,
                        bottomSheetItem -> {
                            if (bottomSheetItem.getTitle().equals("Chỉnh sửa bài đăng")) {
                                Intent intent = new Intent(getApplicationContext(), PostActivity.class);
                                intent.putExtra("id", post.getPostId());

                                mStartForResult.launch(intent);
                                fimaeBottomSheet.dismiss();
                            } else if (bottomSheetItem.getTitle().equals("Xóa bài đăng")) {
                                fimaeBottomSheet.dismiss();
                            }
                        });
                fimaeBottomSheet.show(getSupportFragmentManager(), fimaeBottomSheet.getTag());

            }

            private void showEditCommentDialog(Comment comment) {
                CommentEditFragment commentEditFragment = new CommentEditFragment(comment, post.getPostId());
                commentEditFragment.show(getSupportFragmentManager(), commentEditFragment.getTag());
                fimaeBottomSheet.dismiss();
            }

            public void showCommentDialog(Comment comment) {
                fimaeBottomSheet = new FimaeBottomSheet(commentSheetItemList,
                        bottomSheetItem -> {
                            if (bottomSheetItem.getTitle().equals("Chỉnh sửa bình luận"))
                                showEditCommentDialog(comment);
                            else if (bottomSheetItem.getTitle().equals("Xóa bình luận")) {
                                commentRepository.deleteComment(post.getPostId(), comment, CommentRepository.POST_COLLECTION);
                                fimaeBottomSheet.dismiss();
                            }
                        });
                fimaeBottomSheet.show(getSupportFragmentManager(), fimaeBottomSheet.getTag());
            }

            private void showMoreDialog() {
                if (Objects.equals(FirebaseAuth.getInstance().getUid(), post.getPublisher())) {
                    showPostDialog();
                } else {
                    showReportDialog();
                }
            }
            private void showReport(){
                Query query = ReportRepository.getInstance().getReportDetailRef(postId);
                ReportItemDetailAdapter adapter = new ReportItemDetailAdapter(query);
                LinearLayoutManager layoutManager = new LinearLayoutManager(this);
                listReportItemBottomSheetFragment = new ListItemBottomSheetFragment("Danh sách báo cáo bài đăng", adapter);
                listReportItemBottomSheetFragment.show(getSupportFragmentManager(), "reportList");
            }
        }