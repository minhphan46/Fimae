package com.example.fimae.adapters;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.fimae.R;
import com.example.fimae.utils.FileUtils;
import com.squareup.picasso.Picasso;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class ImageMessageAdapter extends RecyclerView.Adapter<ImageMessageAdapter.ViewHolder> {
    private ArrayList<String> urls;
    public interface IClickMediaItem {
        void onClick(String url, int position);
    }
    private IClickMediaItem onClickMediaItem;
    public void setOnClickMediaItem(IClickMediaItem onClickMediaItem) {
        this.onClickMediaItem = onClickMediaItem;
    }
    public ImageMessageAdapter(ArrayList<String> urls) {
        this.urls = urls;
    }

    @NonNull
    @NotNull
    @Override
    public ImageMessageAdapter.ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.fragment_item_list_dialog_list_dialog_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull ImageMessageAdapter.ViewHolder holder, int position) {
        String url = urls.get(position);
        Glide.with(holder.imageView)
                .load(url)
                .into(holder.imageView);
        if (FileUtils.isImageFile(url)) {
            holder.videoDurationTextView.setVisibility(View.GONE);
        } else {
            holder.videoDurationTextView.setText("Video");

        }
        if(onClickMediaItem != null) {
            holder.layout.setOnClickListener(v -> onClickMediaItem.onClick(url, position));
        }

    }

    @Override
    public int getItemCount() {
        return urls.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView imageView;
        public TextView videoDurationTextView;
        CardView layout;

        public ViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imageView);
            itemView.findViewById(R.id.picked_index).setVisibility(View.INVISIBLE);
            videoDurationTextView = itemView.findViewById(R.id.video_duration);
            layout = itemView.findViewById(R.id.media_item_layout);
        }
    }
}
