package com.example.fimae.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fimae.R;
import com.example.fimae.databinding.CommentItemBinding;
import com.example.fimae.databinding.LikedItemUserBinding;
import com.example.fimae.models.Comment;
import com.example.fimae.models.Fimaers;
import com.example.fimae.models.Seed;
import com.example.fimae.repository.FollowRepository;
import com.example.fimae.repository.PostRepository;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;
import com.stringee.messaging.User;

import java.util.List;

public class LikedAdapeter extends RecyclerView.Adapter<LikedAdapeter.ViewHolder> {
    private Seed seed = new Seed();
    public Context mContext;
    public List<Fimaers> userInfos;
    public LikedAdapeter(Context mContext, List<Fimaers> userInfos) {
        this.mContext = mContext;
        this.userInfos = userInfos;
    }

    @NonNull
    @Override
    public LikedAdapeter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LikedItemUserBinding binding = LikedItemUserBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull LikedAdapeter.ViewHolder holder, int position) {
        LikedItemUserBinding binding = holder.binding;
        Fimaers userInfo = userInfos.get(position);
        binding.userName.setText(userInfo.getLastName());
        Picasso.get().load(userInfo.getAvatarUrl()).placeholder(R.drawable.ic_default_avatar).into(binding.imageAvatar);
        binding.userName.setText(userInfo.getLastName());
        if(!userInfo.isGender()){
            binding.itemUserIcGender.setImageResource(R.drawable.ic_male);
            binding.genderAgeIcon.setBackgroundResource(R.drawable.shape_gender_border_pink);
        }
        binding.ageTextView.setText(String.valueOf(userInfo.calculateAge()));
        if(userInfo.getUid().equals(FirebaseAuth.getInstance().getUid())){
            binding.follow.setVisibility(View.GONE);
        }
        else{
            FollowRepository.getInstance().followRef.document(FirebaseAuth.getInstance().getUid()+"_"+userInfo.getUid()).addSnapshotListener(new EventListener<DocumentSnapshot>() {
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
                        binding.chat.setVisibility(View.GONE);
                        binding.follow.setVisibility(View.VISIBLE);
                    }
                }
            });

        }
        binding.follow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FollowRepository.getInstance().follow(userInfo.getUid());
            }
        });
        binding.chat.setOnClickListener(view -> {
            PostRepository.getInstance().goToChatWithUser(userInfo.getUid(), mContext);
        });
    }

    @Override
    public int getItemCount() {
        return userInfos.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        LikedItemUserBinding binding;
        public ViewHolder(LikedItemUserBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
