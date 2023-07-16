package com.example.fimae.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fimae.R;
import com.example.fimae.databinding.CommentItemBinding;
import com.example.fimae.databinding.LikedItemUserBinding;
import com.example.fimae.models.Comment;
import com.example.fimae.models.Fimaers;
import com.example.fimae.models.Seed;
import com.squareup.picasso.Picasso;
import com.stringee.messaging.User;

import java.util.List;

public class LikedAdapeter extends RecyclerView.Adapter<LikedAdapeter.ViewHolder> {
    private Seed seed = new Seed();
    public Context mContext;
    public List<Fimaers> userInfos;
    boolean isFollow = false;
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
        binding.follow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isFollow = true;
                binding.follow.setVisibility(View.INVISIBLE);
                binding.chat.setVisibility(View.VISIBLE);
            }
        });
        binding.chat.setOnClickListener(view -> {
            isFollow = false;
            binding.chat.setVisibility(View.INVISIBLE);
            binding.follow.setVisibility(View.VISIBLE);
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
