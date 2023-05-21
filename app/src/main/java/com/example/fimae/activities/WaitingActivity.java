package com.example.fimae.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.CompositePageTransformer;
import androidx.viewpager2.widget.MarginPageTransformer;
import androidx.viewpager2.widget.ViewPager2;

import android.os.Bundle;
import android.os.Handler;
import android.view.View;

import com.example.fimae.R;
import com.example.fimae.adapters.SliderAdapter;
import com.example.fimae.models.SliderItem;
import com.example.fimae.models.UserInfo;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class WaitingActivity extends AppCompatActivity {

    private ViewPager2 viewPager2;
    private List<UserInfo> userInfos;
    private Handler sliderHandler = new Handler();

    private int timeDelay = 2000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_waiting);

        viewPager2 = findViewById(R.id.view_image_slider);

        userInfos = new ArrayList<UserInfo>(Arrays.asList(UserInfo.dummy));

        viewPager2.setAdapter(new SliderAdapter(userInfos, viewPager2));

        viewPager2.setClipToPadding(false);
        viewPager2.setClipChildren(false);
        viewPager2.setOffscreenPageLimit(3);
        viewPager2.getChildAt(0).setOverScrollMode(RecyclerView.OVER_SCROLL_NEVER);

        CompositePageTransformer compositePageTransformer = new CompositePageTransformer();
        compositePageTransformer.addTransformer(new MarginPageTransformer(10));
        compositePageTransformer.addTransformer(new ViewPager2.PageTransformer() {
            @Override
            public void transformPage(@NonNull View page, float position) {
                float r = 1 - Math.abs(position);
                float size = 0.85f + r * 0.15f;
                page.setScaleX(size);
                page.setScaleY(size);
            }
        });

        viewPager2.setPageTransformer(compositePageTransformer);

        viewPager2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                sliderHandler.removeCallbacks(sliderRunable);
                sliderHandler.postDelayed(sliderRunable, timeDelay); // slide duration 3 seconds
            }
        });
    }

    private Runnable sliderRunable = new Runnable() {
        @Override
        public void run() {
            viewPager2.setCurrentItem(viewPager2.getCurrentItem() + 1);
        }
    };

    @Override
    protected void onPause() {
        super.onPause();
        sliderHandler.removeCallbacks(sliderRunable);
    }

    @Override
    protected void onResume() {
        super.onResume();
        sliderHandler.postDelayed(sliderRunable, timeDelay);
    }
}