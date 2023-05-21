package com.example.fimae.adapters;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fimae.R;
import com.example.fimae.databinding.ItemPostBinding;
import com.example.fimae.models.Post;
import com.example.fimae.models.Seed;
import com.example.fimae.models.UserInfo;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.ViewHolder> {
    private Seed seed = new Seed();
    public Context mContext;
    public List<Post> mPost;
    private IClickCardUserListener iClickCardUserListener;

    public interface IClickCardUserListener {
        void onClickUser(Post post);
    }

    public PostAdapter(Context mContext, List<Post> mPost, IClickCardUserListener clickCardUserListener) {
        this.mContext = mContext;
        this.mPost = mPost;
        this.iClickCardUserListener = clickCardUserListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemPostBinding postBinding = ItemPostBinding.inflate(LayoutInflater.from(mContext), parent, false );
        return new PostAdapter.ViewHolder(postBinding);

    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ItemPostBinding binding = holder.binding;
        Post currentPost = mPost.get(position);
        UserInfo userInfo = seed.getUserInfoByID(currentPost.getPublisher());
        List<String> imageUrls = currentPost.getPostImages();
        List<Uri> imageUris = new ArrayList<>();
        String description = currentPost.getContent();
        binding.content.setText(description);
        for(int i = 0; i < imageUrls.size();i++){
            imageUris.add(Uri.parse(imageUrls.get(i)));
        }
        Picasso.get().load(userInfo.getAvatarUrl()).placeholder(R.drawable.ic_default_avatar).into(binding.imageAvatar);
        if(!userInfo.isMale()){
            binding.itemUserIcGender.setImageResource(R.drawable.ic_male);
            binding.genderAgeIcon.setBackgroundResource(R.drawable.shape_gender_border_pink);
        }
        binding.ageTextView.setText(String.valueOf(userInfo.getAge()));
        binding.userName.setText(userInfo.getName());
        PostPhotoAdapter adapter = new PostPhotoAdapter(mContext, imageUris, false);
        binding.imageList.setVisibility(View.VISIBLE);
        LinearLayoutManager layoutManager = new GridLayoutManager(mContext, getColumnSpan(imageUris.size()) );
        binding.imageList.setLayoutManager(layoutManager);
        binding.imageList.setAdapter(adapter);
        binding.getRoot().setOnClickListener(view -> {
            iClickCardUserListener.onClickUser(mPost.get(position));
        });
        initListener(binding);

    }
    private void initListener(ItemPostBinding binding){
        AtomicBoolean isLike = new AtomicBoolean(false);
        AtomicBoolean isFollow = new AtomicBoolean(false);

        //go back
        //like
        binding.icLike.setOnClickListener(view -> {
            isLike.set(!isLike.get());
            if(isLike.get()){
                binding.icLike.setImageResource(R.drawable.ic_heart1);
            }
            else binding.icLike.setImageResource(R.drawable.ic_heart);
        });
        binding.follow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isFollow.set(true);
                binding.follow.setVisibility(View.INVISIBLE);
                binding.chat.setVisibility(View.VISIBLE);
            }
        });
        binding.chat.setOnClickListener(view -> {
            isFollow.set(false);
            binding.chat.setVisibility(View.INVISIBLE);
            binding.follow.setVisibility(View.VISIBLE);
        });
    }

    @Override
    public int getItemCount() {
        return mPost.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ItemPostBinding binding;
        public ViewHolder(ItemPostBinding commentItemBinding) {
            super(commentItemBinding.getRoot());
            binding = commentItemBinding;
        }
    }
    public static int getColumnSpan(int number){
        if(number <= 2) return number;
        else return 2;
    }
}

