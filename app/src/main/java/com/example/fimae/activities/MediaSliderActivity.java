package com.example.fimae.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.media3.common.MediaItem;
import androidx.media3.common.Player;
import androidx.media3.exoplayer.ExoPlayer;
import androidx.media3.ui.PlayerView;
import androidx.viewpager.widget.ViewPager;
import androidx.viewpager2.widget.ViewPager2;

import android.net.Uri;
import android.os.Bundle;

import com.example.fimae.R;
import com.example.fimae.adapters.MediaSliderAdapter.MediaSliderAdapter;
import com.example.fimae.adapters.MediaSliderAdapter.MediaSliderItem;
import com.example.fimae.adapters.MediaSliderAdapter.MediaSliderItemType;
import com.example.fimae.utils.FileUtils;

import java.util.ArrayList;

public class MediaSliderActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_media_slider);
        ViewPager2 viewPager = findViewById(R.id.view_paper);
        int currentIndex = getIntent().getIntExtra("currentIndex", 0);
        ArrayList<String> urls = getIntent().getStringArrayListExtra("urls");
        ArrayList<MediaSliderItem> items = new ArrayList<>();
        if (urls != null) {
            for (String url : urls) {
                if(FileUtils.isImageFile(url))
                    items.add(new MediaSliderItem(url, MediaSliderItemType.IMAGE));
                else
                    items.add(new MediaSliderItem(url, MediaSliderItemType.VIDEO));
            }
            MediaSliderAdapter adapter = new MediaSliderAdapter(this, items);
            viewPager.setAdapter(adapter);
        }

        MediaSliderAdapter adapter = new MediaSliderAdapter(this, items);
        viewPager.setAdapter(adapter);
        viewPager.setCurrentItem(currentIndex, false);
    }

}