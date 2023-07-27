package com.example.fimae.adapters.MediaSliderAdapter;

import android.content.Context;
import android.transition.Slide;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.media3.common.MediaItem;
import androidx.media3.common.Player;
import androidx.media3.exoplayer.ExoPlayer;
import androidx.media3.ui.PlayerView;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager2.adapter.FragmentStateAdapter;


import com.bumptech.glide.Glide;
import com.example.fimae.R;
import com.example.fimae.SliderMediaFragment;

import java.util.ArrayList;
import java.util.Objects;

public class MediaSliderAdapter extends FragmentStateAdapter {
    Context context;
    ArrayList<MediaSliderItem> mediaSliderItems;

    public MediaSliderAdapter(@NonNull FragmentActivity fragmentActivity, ArrayList<MediaSliderItem> items) {
        super(fragmentActivity);
        this.mediaSliderItems = items;
    }


    @NonNull
    @Override
    public Fragment createFragment(int position) {
        return new SliderMediaFragment(mediaSliderItems.get(position));
    }

    @Override
    public int getItemCount() {
        if (mediaSliderItems != null)
            return mediaSliderItems.size();
        return 0;
    }

}
