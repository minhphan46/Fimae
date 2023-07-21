package com.example.fimae.adapters;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fimae.R;
import com.example.fimae.activities.DetailPostActivity;
import com.example.fimae.databinding.ItemPostBinding;
import com.example.fimae.models.Follows;
import com.example.fimae.models.Post;
import com.example.fimae.models.Seed;
import com.example.fimae.models.Fimaers;
import com.example.fimae.repository.FollowRepository;
import com.example.fimae.repository.PostRepository;
import com.example.fimae.service.TimerService;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.type.DateTime;
import com.google.type.DateTimeOrBuilder;
import com.squareup.picasso.Picasso;

import org.checkerframework.checker.units.qual.A;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.ViewHolder> {
    public Context mContext;
    public List<Post> posts;
    private IClickCardUserListener iClickCardUserListener;
    private PostPhotoAdapter adapter;
    private FollowRepository followRepository = FollowRepository.getInstance();
    public interface IClickCardUserListener {
        void onClickUser(Post post);
    }
    public interface IShareCardUserListener{
        void onClick(Post post);
    }

    public interface CallBack<T> {
        void onSuccess(T result);
    }

    public void setData(Context mContext, List<Post> mPost, IClickCardUserListener clickCardUserListener) {
        this.mContext = mContext;
        posts = mPost;
        this.iClickCardUserListener = clickCardUserListener;
    }

    public void addUpdate(){
        notifyDataSetChanged();
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
        Post currentPost = posts.get(position);
        boolean isLike = false;

        PostRepository.getInstance().getUserById(currentPost.getPublisher(), new CallBack<Fimaers>() {
            @Override
            public void onSuccess(Fimaers fimaers) {
                Picasso.get().load(fimaers.getAvatarUrl()).placeholder(R.drawable.ic_default_avatar).into(binding.imageAvatar);
                if(!fimaers.isGender()){
                    binding.itemUserIcGender.setImageResource(R.drawable.ic_male);
                    binding.genderAgeIcon.setBackgroundResource(R.drawable.shape_gender_border_pink);
                }

                binding.ageTextView.setText(String.valueOf(fimaers.calculateAge()));
                binding.userName.setText(fimaers.getLastName());
                initListener(binding, currentPost, fimaers);

            }
        });

        ArrayList<String> imageUrls = new ArrayList<>( currentPost.getPostImages());
        List<Uri> imageUris = new ArrayList<>();
        String description = currentPost.getContent();

        binding.content.setText(description);
        binding.commentNumber.setText(String.valueOf(currentPost.getNumberOfComments()));
        binding.likeNumber.setText(String.valueOf(currentPost.getLikes().size()));

        for(int i = 0; i < imageUrls.size();i++){
            imageUris.add(Uri.parse(imageUrls.get(i)));
        }

        if(currentPost.getPostImages() != null && !currentPost.getPostImages().isEmpty()){
            adapter = new PostPhotoAdapter(mContext, imageUrls);
            binding.imageList.setVisibility(View.VISIBLE);
            LinearLayoutManager layoutManager = new GridLayoutManager(mContext, getColumnSpan(imageUris.size()) );
            binding.imageList.setLayoutManager(layoutManager);
            binding.imageList.setAdapter(adapter);
            binding.commentNumber.setText(String.valueOf(currentPost.getNumberOfComments()));
            binding.likeNumber.setText(currentPost.getNumberTrue());
        }

        binding.getRoot().setOnClickListener(view -> {
            iClickCardUserListener.onClickUser(currentPost);
        });

        TimerService.setDuration(binding.activeTime, currentPost.getTimeCreated());
    }
    private void initListener(ItemPostBinding binding, Post currentPost, Fimaers fimaers){
        CollectionReference reference = FirebaseFirestore.getInstance().collection("posts");
        reference.document(currentPost.getPostId()).addSnapshotListener((value, error) -> {
            if (error != null) {
                return;
            }
            Post updatePost = value.toObject(Post.class);

//            if(currentPost.getPostImages().size() != updatePost.getPostImages().size()){
//                adapter.updateImages(new ArrayList<>(updatePost.getPostImages()));
////                LinearLayoutManager layoutManager = new GridLayoutManager(mContext, getColumnSpan(updatePost.getPostImages().size()) );
////                binding.imageList.setLayoutManager(layoutManager);
//            }
            if(currentPost.getPublisher().equals(FirebaseAuth.getInstance().getUid())){
                binding.follow.setVisibility(View.GONE);
            }
            else{
                followRepository.followRef.document(FirebaseAuth.getInstance().getUid()+"_"+currentPost.getPublisher()).addSnapshotListener(new EventListener<DocumentSnapshot>() {
                    @Override
                    public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                        if(error != null ){
                            return;
                        }
                        if(value != null && value.exists()){
                            binding.follow.setVisibility(View.GONE);
                            binding.chat.setVisibility(View.VISIBLE);
                        }
                        else{
                            binding.follow.setVisibility(View.VISIBLE);
                            binding.chat.setVisibility(View.GONE);
                        }
                    }
                });
            }


            binding.follow.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                        followRepository.follow(currentPost.getPublisher());
                        binding.follow.setVisibility(View.GONE);
                        binding.chat.setVisibility(View.VISIBLE);
                    }
                }
            );
            if(!currentPost.getContent().equals(updatePost.getContent())){
                binding.content.setText(updatePost.getContent());
            }
            binding.commentNumber.setText(String.valueOf(updatePost.getNumberOfComments()));
            binding.likeNumber.setText(updatePost.getNumberTrue());

            if(updatePost.getLikes().containsKey(currentPost.getPublisher()) && updatePost.getLikes().get(currentPost.getPublisher())){
                binding.icLike.setImageResource(R.drawable.ic_heart1);
                binding.icLike.setOnClickListener(view -> {
                    String path = "likes." + fimaers.getUid();
                    binding.icLike.setImageResource(R.drawable.ic_heart1);
                    reference.document(currentPost.getPostId()).update(
                            path, false
                    );
                });
            }
            else {
                binding.icLike.setImageResource(R.drawable.ic_heart_gray);
                binding.icLike.setOnClickListener(view -> {
                    String path = "likes." + fimaers.getUid();
                    binding.icLike.setImageResource(R.drawable.ic_heart1);
                    reference.document(currentPost.getPostId()).update(
                            path, true
                    );
                });
            }
            binding.chat.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    PostRepository.getInstance().goToChatWithUser(currentPost.getPublisher(), mContext);
                }
            });
            binding.icShare.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(mContext, DetailPostActivity.class);
                    intent.putExtra("id", currentPost.getPostId());
                    intent.putExtra("share",true);
                    mContext.startActivity(intent);
                }
            });
            binding.icMore.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(mContext, DetailPostActivity.class);
                    intent.putExtra("id", currentPost.getPostId());
                    intent.putExtra("more",true);
                    mContext.startActivity(intent);

                }
            });
        });
        //go back
        //like
//        binding.follow.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                isFollow.set(true);
//                binding.follow.setVisibility(View.INVISIBLE);
//                binding.chat.setVisibility(View.VISIBLE);
//            }
//        });
//        binding.chat.setOnClickListener(view -> {
//            isFollow.set(false);
//            binding.chat.setVisibility(View.INVISIBLE);
//            binding.follow.setVisibility(View.VISIBLE);
//        });
    }

    @Override
    public int getItemCount() {
        return posts.size();
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

