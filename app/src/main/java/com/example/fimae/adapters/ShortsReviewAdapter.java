package com.example.fimae.adapters;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.fimae.R;
import com.example.fimae.models.shorts.ShortMedia;
import com.example.fimae.models.story.Story;
import com.google.firebase.storage.FirebaseStorage;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class ShortsReviewAdapter extends RecyclerView.Adapter<ShortsReviewAdapter.ShortsReviewHolder> {

    ArrayList<ShortMedia> shortMedias = ShortMedia.getFakeData();

    private ShortsReviewAdapter.IClickCardListener iClickCardListener;

    public interface IClickCardListener {
        void onClickUser(ShortMedia video);
    }

    public ShortsReviewAdapter(IClickCardListener iClickCardListener) {
        this.iClickCardListener = iClickCardListener;
    }

    @NonNull
    @Override
    public ShortsReviewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.short_review_item, parent, false);
        return new ShortsReviewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ShortsReviewHolder holder, int position) {
        // Get the data at the specified position
        ShortMedia shortMedia = shortMedias.get(position);
        //Picasso.get().load(shortMedia.getURL()).placeholder(R.drawable.ic_default_avatar).into(holder.shortAvatar);
        Glide.with(holder.itemView)
                .load(shortMedia.getMediaUrl())
                .into(holder.shortImage);
        holder.shortImage.setOnClickListener(view -> {
            iClickCardListener.onClickUser(shortMedia);
        });
        holder.shortViewCount.setText("120K");
    }


    @Override
    public int getItemCount() {
        return shortMedias.size();
    }

    public class ShortsReviewHolder extends RecyclerView.ViewHolder {
        ImageView shortImage;
        CircleImageView shortAvatar;
        TextView shortViewCount;

        public ShortsReviewHolder(View itemView) {
            super(itemView);
            // Initialize the views from the layout
            shortImage = itemView.findViewById(R.id.short_image);
            shortAvatar = itemView.findViewById(R.id.short_avatar);
            shortViewCount = itemView.findViewById(R.id.short_view_count);
        }
    }
}
