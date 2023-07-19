package com.example.fimae.adapters.MediaSliderAdapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.media3.common.MediaItem;
import androidx.media3.common.Player;
import androidx.media3.exoplayer.ExoPlayer;
import androidx.media3.ui.PlayerView;
import androidx.viewpager.widget.PagerAdapter;


import com.bumptech.glide.Glide;
import com.example.fimae.R;

import java.util.ArrayList;
import java.util.Objects;

public class MediaSliderAdapter extends PagerAdapter {
    Context context;
    ArrayList<MediaSliderItem> mediaSliderItems;

    public MediaSliderAdapter(Context context, ArrayList<MediaSliderItem> items) {
        this.context = context;
        this.mediaSliderItems = items;
    }

    @Override
    public int getCount() {
        if (mediaSliderItems != null)
            return mediaSliderItems.size();
        return 0;
    }


    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }
    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        MediaSliderItem item = mediaSliderItems.get(position);
        View view;
        if(item.getType() == MediaSliderItemType.IMAGE ){
            view = LayoutInflater.from(context).inflate(R.layout.slider_image, container, false);
            ImageView imageView = view.findViewById(R.id.imageViewSliderImage);
            Glide.with(context).load(item.getUrl()).into(imageView);
        } else {
            view = LayoutInflater.from(context).inflate(R.layout.slider_video, container, false);
            PlayerView playerView = view.findViewById(R.id.playerViewSliderVideo);
            ExoPlayer player = new ExoPlayer.Builder(context).build();
            MediaItem mediaItem = MediaItem.fromUri(item.getUrl());
            player.setMediaItem(mediaItem);
            player.prepare();
            playerView.setPlayer(player);

        }
        view.addOnAttachStateChangeListener(new View.OnAttachStateChangeListener() {
            @Override
            public void onViewAttachedToWindow(@NonNull View v) {
                if(item.getType() == MediaSliderItemType.VIDEO){
                    PlayerView playerView = v.findViewById(R.id.playerViewSliderVideo);
                    ExoPlayer player = (ExoPlayer) playerView.getPlayer();
                    if (player != null)
                        player.play();
                }
            }

            @Override
            public void onViewDetachedFromWindow(@NonNull View v) {
                if(item.getType() == MediaSliderItemType.VIDEO){
                    PlayerView playerView = v.findViewById(R.id.playerViewSliderVideo);
                    ExoPlayer player = (ExoPlayer) playerView.getPlayer();
                    if (player != null)
                        player.pause();
                }
            }
        });
        container.addView(view);
        return view;
    }
    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        //Remove view from container because it is not used anymore.
        //If we don't remove it, it will be added to the container again.
        View view = (View) object;
        if(mediaSliderItems.get(position).getType() == MediaSliderItemType.VIDEO){
            PlayerView playerView = view.findViewById(R.id.playerViewSliderVideo);
            if(playerView != null){
                Player player = playerView.getPlayer();
                player.release();
            }
        }
        container.removeView(((View) object));
    }
}
