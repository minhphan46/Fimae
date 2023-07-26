package com.example.fimae.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.fimae.R;
import com.example.fimae.adapters.NewCommentAdapter;
import com.example.fimae.adapters.ShortAdapter.ShortVideoAdapter;
import com.example.fimae.databinding.ActivityShortVideoBinding;
import com.example.fimae.databinding.BottomSheetCommentShortBinding;
import com.example.fimae.fragments.CommentEditFragment;
import com.example.fimae.fragments.FimaeBottomSheet;
import com.example.fimae.models.BottomSheetItem;
import com.example.fimae.models.Comment;
import com.example.fimae.models.CommentItemAdapter;
import com.example.fimae.models.Fimaers;
import com.example.fimae.models.shorts.ShortMedia;
import com.example.fimae.repository.CommentRepository;
import com.example.fimae.repository.FimaerRepository;
import com.example.fimae.repository.ShortsRepository;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ShortVideoActivity extends AppCompatActivity {
    ActivityShortVideoBinding binding;
    ArrayList<ShortMedia> shortMedias = ShortMedia.getFakeData();
    ShortVideoAdapter shortVideoAdapter;
    Query shortQuery;

    List<Comment> comments;
    List<CommentItemAdapter> commentItemAdapters;
    NewCommentAdapter newCommentAdapter;
    CollectionReference postRef = FirebaseFirestore.getInstance().collection("posts");
    public String selectedCommentId = "";
    private Fimaers fimaers;
    FimaeBottomSheet fimaeBottomSheet;

    List<BottomSheetItem> commentSheetItemList;

    CommentRepository commentRepository = CommentRepository.getInstance();

    ShortMedia curVideo;
    boolean canPost = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        binding = ActivityShortVideoBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        this.getTheme().applyStyle(R.style.FullScreen, false);
        // get id video first
        Intent intent = getIntent();
        String idFirstVideo = intent.getStringExtra("idVideo");
        boolean isProfile = intent.getBooleanExtra("isProfile", false);

        if (isProfile) {
            shortQuery = ShortsRepository.getInstance().getShortUserQuery(FirebaseAuth.getInstance().getUid());
        } else {
            shortQuery = ShortsRepository.getInstance().getShortQuery();
        }
        shortVideoAdapter = new ShortVideoAdapter(shortQuery, this, shortMedias, idFirstVideo, new ShortVideoAdapter.IClickCardListener() {
            @Override
            public void onClickUser(ShortMedia video) {
                finish();
            }

            @Override
            public FragmentManager getFragmentManager() {
                return getSupportFragmentManager();
            }

            @Override
            public void showComment(ShortMedia video, Fimaers fimaers) {
                showBottomSheetComment(video, fimaers);
            }
        });
        binding.viewPagerVideoShort.setAdapter(shortVideoAdapter);

        binding.viewPagerVideoShort.registerOnPageChangeCallback(new androidx.viewpager2.widget.ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                //binding.viewPagerVideoShort.setCurrentItem(position);
                shortVideoAdapter.onBeginPlayVideo(position);
                shortVideoAdapter.addWatched(position);
                super.onPageSelected(position);
            }
        });
    }

    TextView mTvNumComments;
    ImageButton mBtnClose;
    RecyclerView mRvComments;
    EditText mEdtComment;
    ImageView mBtnSendComment;
    HashMap<String, Fimaers> fimaersHashMap = new HashMap<>();

    @SuppressLint("SetTextI18n")
    private void showBottomSheetComment(ShortMedia video, Fimaers fimaers) {
        curVideo = video;
        BottomSheetCommentShortBinding binding = BottomSheetCommentShortBinding.inflate(getLayoutInflater());
        @SuppressLint("InflateParams") View dialogSetting = getLayoutInflater().inflate(R.layout.bottom_sheet_comment_short, null);

        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(ShortVideoActivity.this);
        bottomSheetDialog.setContentView(dialogSetting);
        // extended bottom sheet
        bottomSheetDialog.setOnShowListener(dialog -> {
            BottomSheetDialog d = (BottomSheetDialog) dialog;
            FrameLayout bottomSheet = (FrameLayout) d.findViewById((com.google.android.material.R.id.design_bottom_sheet));
            // Right here!
            BottomSheetBehavior.from(bottomSheet)
                    .setState(BottomSheetBehavior.STATE_EXPANDED);
        });
        bottomSheetDialog.show();

        // set component
        mTvNumComments = dialogSetting.findViewById(R.id.title_comment);
        mBtnClose = dialogSetting.findViewById(R.id.btn_close);
        mRvComments = dialogSetting.findViewById(R.id.rv_comments);
        mEdtComment = dialogSetting.findViewById(R.id.edt_comment);
        mBtnSendComment = dialogSetting.findViewById(R.id.btn_send_comment);

        createCommentDialog();
        comments = new ArrayList<>();
        commentItemAdapters = new ArrayList<>();

        Query query = ShortsRepository.getInstance().getShortCommentQuery(video.getId());

        newCommentAdapter = new NewCommentAdapter(this, CommentRepository.SHORT_COLLECTION, query, (comment) -> {
            selectedCommentId = comment.getParentId() != "" ? comment.getParentId() : comment.getId();
            Fimaers fimaer = fimaersHashMap.get(comment.getPublisher());
            if (fimaer != null) {
                mEdtComment.setHint("@" + fimaer.getLastName());
            } else {
                FimaerRepository.getInstance().getFimaerById(comment.getPublisher()).addOnCompleteListener(task -> {
                    task.onSuccessTask(mfimaers -> {
                        mEdtComment.setHint("@" + mfimaers.getLastName());
                        return null;
                    });
                });
            }
        }, video.getId(), this::showCommentDialog);
        LinearLayoutManager layoutManager1 = new LinearLayoutManager(this);
        mRvComments.setLayoutManager(layoutManager1);
        mRvComments.setAdapter(newCommentAdapter);

        mTvNumComments.setText(video.getNumOfComments() + " bình luận");

        // close bottom sheet
        mBtnClose.setOnClickListener(v -> {
            bottomSheetDialog.dismiss();
        });
        // edit text
        mEdtComment.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (mEdtComment.getText().length() > 0) {
                    mBtnSendComment.setBackgroundResource(R.drawable.canpost);
                    canPost = true;
                } else {
                    mBtnSendComment.setBackgroundResource(R.drawable.cantpost);
                    canPost = false;
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        // add comment
        mBtnSendComment.setOnClickListener(v -> {
            if (!canPost) return;
            if (!selectedCommentId.trim().isEmpty()) {
                Comment subComment = new Comment();
                subComment.setContent(mEdtComment.getText().toString());
                subComment.setPublisher(fimaers.getUid());
                subComment.setParentId(selectedCommentId);
                subComment.setPostId(curVideo.getId());
                commentRepository.postComment(curVideo.getId(), CommentRepository.SHORT_COLLECTION, subComment);
            } else {
                Comment comment = new Comment();
                comment.setPostId(curVideo.getId());
                comment.setPublisher(fimaers.getUid());
                comment.setContent(mEdtComment.getText().toString());
                comment.setParentId("");
                commentRepository.postComment(curVideo.getId(), CommentRepository.SHORT_COLLECTION, comment);
            }
            // update text
            ShortsRepository.getInstance().addShortCommentNum(1, video);
            mTvNumComments.setText(video.getNumOfComments() + " bình luận");
            shortVideoAdapter.updateCommentCount(video);

            mEdtComment.clearFocus();
            mEdtComment.setText("");
            mEdtComment.setHint("Để lại một bình luận");
            selectedCommentId = "";
        });
    }

    public void showCommentDialog(Comment comment) {
        fimaeBottomSheet = new FimaeBottomSheet(commentSheetItemList,
                bottomSheetItem -> {
                    if (bottomSheetItem.getTitle().equals("Chỉnh sửa bình luận"))
                        showEditCommentDialog(comment);
                    else if (bottomSheetItem.getTitle().equals("Xóa bình luận")) {
                        commentRepository.deleteComment(curVideo.getId(), comment, CommentRepository.SHORT_COLLECTION);
                        // update text
                        ShortsRepository.getInstance().getShortById(comment.getPostId()).addOnCompleteListener(task -> {
                            task.onSuccessTask(video -> {
                                ShortsRepository.getInstance().addShortCommentNum(-1, video);
                                mTvNumComments.setText(video.getNumOfComments() + " bình luận");
                                shortVideoAdapter.updateCommentCount(video);
                                return null;
                            });
                        });
                        fimaeBottomSheet.dismiss();
                    }
                });
        fimaeBottomSheet.show(getSupportFragmentManager(), fimaeBottomSheet.getTag());
    }

    private void showEditCommentDialog(Comment comment) {
        CommentEditFragment commentEditFragment = new CommentEditFragment(comment, curVideo.getId());
        commentEditFragment.show(getSupportFragmentManager(), commentEditFragment.getTag());
        fimaeBottomSheet.dismiss();
    }

    private void createCommentDialog() {
        commentSheetItemList = new ArrayList<BottomSheetItem>() {
            {
                add(new BottomSheetItem(R.drawable.ic_edit, "Chỉnh sửa bình luận"));
                add(new BottomSheetItem(R.drawable.ic_user_block, "Xóa bình luận"));
            }
        };
    }
}