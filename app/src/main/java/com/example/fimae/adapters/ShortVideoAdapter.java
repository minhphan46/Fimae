package com.example.fimae.adapters;

import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fimae.R;
import com.example.fimae.databinding.LayoutReelBinding;
import com.example.fimae.models.Fimaers;
import com.example.fimae.models.shorts.ShortMedia;

import java.util.ArrayList;

public class ShortVideoAdapter extends RecyclerView.Adapter<ShortVideoAdapter.VideoHolder>{
    Context context;
    ArrayList<ShortMedia> shortMedias = new ArrayList<>();
    private ShortVideoAdapter.IClickCardListener iClickCardListener;

    public interface IClickCardListener {
        void onClickUser(ShortMedia video);
    }

    public ShortVideoAdapter(Context context, ArrayList<ShortMedia> shortMedias, IClickCardListener iClickCardListener) {
        this.context = context;
        this.shortMedias = shortMedias;
        this.iClickCardListener = iClickCardListener;
    }

    @NonNull
    @Override
    public VideoHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_reel, parent, false);
        return new ShortVideoAdapter.VideoHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull VideoHolder holder, int position) {
        ShortMedia media = shortMedias.get(position);
        Log.d("URL VIDEO", (String) media.getMediaUrl());
        holder.binding.videoView.setVideoURI(Uri.parse((String) media.getMediaUrl()));
        holder.binding.videoView.setOnPreparedListener(mp -> {
            //mp.setLooping(true);
            mp.start();
        });
        holder.binding.itemVideoTvName.setText("Minh Phan");
        holder.binding.itemVideoTvDescription.setText(media.getDescription());
        //holder.binding.itemVideoTvLike.setText(media.getUsersLiked().size());
        //holder.binding.itemVideoTvComment.setText(media.getNumOfComments());
        holder.binding.itemVideoIcBack.setOnClickListener(view -> {
            iClickCardListener.onClickUser(media);
        });
    }

    @Override
    public int getItemCount() {
        return shortMedias.size();
    }

    public class VideoHolder extends RecyclerView.ViewHolder {
        LayoutReelBinding binding;
        public VideoHolder(@NonNull View itemView) {
            super(itemView);
            binding = LayoutReelBinding.bind(itemView);
        }
    }
}
