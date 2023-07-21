package com.example.fimae.adapters;

import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.fimae.R;
import com.example.fimae.databinding.LayoutReelBinding;
import com.example.fimae.databinding.PostImageItemBinding;
import com.example.fimae.models.Fimaers;
import com.example.fimae.models.shorts.ShortMedia;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class PostContentAdapter extends RecyclerView.Adapter<PostContentAdapter.PostContentHolder>{
    Context context;
    ArrayList<String> imageLists = new ArrayList<>();
    private ShortVideoAdapter.IClickCardListener iClickCardListener;
    boolean isPlaying = false;

    public interface IClickCardListener {
        void onClickUser(ShortMedia video);
    }

    public PostContentAdapter(Context context, ArrayList<String> imageLists) {
        this.context = context;
        this.imageLists = imageLists;
//        this.iClickCardListener = iClickCardListener;
    }

    @NonNull
    @Override
    public PostContentHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.post_image_item, parent, false);
        return new PostContentAdapter.PostContentHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull PostContentHolder holder, int position) {
        String imageUrl = imageLists.get(position);
        Glide.with(context).load(imageUrl).into(holder.binding.mImageView);
    }
    @Override
    public int getItemCount() {
        return imageLists.size();
    }

    public class PostContentHolder extends RecyclerView.ViewHolder {
        PostImageItemBinding binding;
        public PostContentHolder(@NonNull View itemView) {
            super(itemView);
            binding = PostImageItemBinding.bind(itemView);
        }
    }
}
