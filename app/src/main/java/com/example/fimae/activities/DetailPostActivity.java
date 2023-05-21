package com.example.fimae.activities;

import androidx.annotation.NonNull;
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

import com.example.fimae.R;
import com.example.fimae.adapters.CommentAdapter;
import com.example.fimae.adapters.PostAdapter;
import com.example.fimae.adapters.PostPhotoAdapter;
import com.example.fimae.bottomdialogs.LikedPostListFragment;
import com.example.fimae.databinding.ActivityDetailPostBinding;
import com.example.fimae.databinding.ActivityPostBinding;
import com.example.fimae.databinding.DetailPostBinding;
import com.example.fimae.models.Comment;
import com.example.fimae.models.Post;
import com.example.fimae.models.Seed;
import com.example.fimae.models.UserInfo;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class DetailPostActivity extends AppCompatActivity {
    private Seed seed = new Seed();
    boolean isLike = false;
    boolean canPost = false;
    boolean isFollow = false;
//    public TextView follow;
//    public TextView chat;
    public PostMode postMode;
    public Comment selectedComment;
    private Post post;
    private UserInfo userInfo;
    DetailPostBinding binding;
    List<String> imageUrls;
    List<Uri> imageUris = new ArrayList<>();
    List<Comment> comments;
    CommentAdapter commentAdapter;
    CommentAdapter selectedAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.detail_post);
        
        //get post, userinfo
        Intent intent = getIntent();
        String postId = intent.getStringExtra("id");
        post = seed.getPostbyId(postId);
        userInfo = seed.getUserInfoByID(post.getPublisher());
        imageUrls = post.getPostImages();
        for(int i = 0; i < imageUrls.size();i++){
            imageUris.add(Uri.parse(imageUrls.get(i)));
        }
        bindingView();
        initListener();
    }
    private void bindingView(){
        binding.content.setText(post.getContent());
        Picasso.get().load(userInfo.getAvatarUrl()).placeholder(R.drawable.ic_default_avatar).into(binding.imageAvatar);
        PostPhotoAdapter adapter = new PostPhotoAdapter(this, imageUris, false);
        binding.imageList.setVisibility(View.VISIBLE);
        LinearLayoutManager layoutManager = new GridLayoutManager(this, PostAdapter.getColumnSpan(imageUris.size()) );
        binding.imageList.setLayoutManager(layoutManager);
        binding.imageList.setAdapter(adapter);
        binding.userName.setText(userInfo.getName());

        if(!userInfo.isMale()){
            binding.itemUserIcGender.setImageResource(R.drawable.ic_male);
            binding.genderAgeIcon.setBackgroundResource(R.drawable.shape_gender_border_pink);
        }
        binding.ageTextView.setText(String.valueOf(userInfo.getAge()));
        comments = seed.getSeedComment(post.getPostId());
        commentAdapter = new CommentAdapter(this, comments, new CommentAdapter.IClickCommentItem() {
            @Override
            public void onClick(Comment comment, CommentAdapter adapter) {
                selectedComment = comment;
                binding.addComment.setHint("@"+seed.getUserInfoByID(selectedComment.getPublisher()).getName());
                selectedAdapter = adapter;
            }
        });
        LinearLayoutManager layoutManager1 = new LinearLayoutManager(this);
        binding.commentRecycler.setLayoutManager(layoutManager1);
        binding.commentRecycler.setAdapter(commentAdapter);
    }
    private void initListener(){

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
                isLike = !isLike;
                if(isLike){
                    binding.icLike.setImageResource(R.drawable.ic_heart1);
                }
                else binding.icLike.setImageResource(R.drawable.ic_heart);
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
        binding.numberofLike.setOnClickListener(view -> {
            LikedPostListFragment likedPostListFragment = LikedPostListFragment.getInstance(post.getPostId());
            likedPostListFragment.show(getSupportFragmentManager(), "likelist");
        });
        //
        binding.follow.setOnClickListener(view -> {
            isFollow = true;
            binding.follow.setVisibility(View.INVISIBLE);
            binding.chat.setVisibility(View.VISIBLE);
        });
        binding.chat.setOnClickListener(view -> {
            isFollow = false;
            binding.chat.setVisibility(View.INVISIBLE);
            binding.follow.setVisibility(View.VISIBLE);
        });
        binding.iconEmoji.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            }
        });
        binding.post.setOnClickListener(view -> {
            if(canPost) {
                if (selectedComment != null) {
                    String idParent = selectedComment.getIdParent();
                    String publisher = "@" + seed.getUserInfoByID(selectedComment.getPublisher()).getName();
                    String content = publisher + " " + binding.addComment.getText().toString();
                    Comment comment = new Comment(content, userInfo.getId(),
                            post.getPostId(), UUID.randomUUID().toString(), idParent, null);
                    selectedAdapter.addComment(comment);
                } else {
                    Comment comment = new Comment(binding.addComment.getText().toString(), userInfo.getId(),
                            post.getPostId(), UUID.randomUUID().toString(), null, null);
                    comments.add(comment);
                    commentAdapter.notifyDataSetChanged();
                }
                binding.addComment.clearFocus();
                binding.addComment.setText("");
                binding.addComment.setHint("Để lại một bình luận");
                selectedComment = null;
            }
        });
    }

}