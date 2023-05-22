package com.example.fimae.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.CompositePageTransformer;
import androidx.viewpager2.widget.MarginPageTransformer;
import androidx.viewpager2.widget.ViewPager2;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.fimae.R;
import com.example.fimae.adapters.SliderAdapter;
import com.example.fimae.models.SliderItem;
import com.example.fimae.models.UserInfo;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class WaitingActivity extends AppCompatActivity {

    // slider
    private ViewPager2 viewPager2;
    private List<UserInfo> userInfos;
    private Handler sliderHandler = new Handler();
    private int timeDelay = 2000;
    // timer
    private int timeSelected = 0;
    private CountDownTimer timeCountDown = null;
    private int timeProgress = 0;
    private long pauseOffSet = 0;
    private boolean isStart = true;

    private FrameLayout mBtnPlay;
    private ProgressBar mProcessBar;
    private TextView mTvTimer;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_waiting);

        initViewPager2();
        setTransformer();

        viewPager2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                sliderHandler.removeCallbacks(sliderRunable);
                sliderHandler.postDelayed(sliderRunable, timeDelay); // slide duration 2 seconds
            }
        });
        // timer
        mProcessBar = findViewById(R.id.pbTimer);
        mTvTimer = findViewById(R.id.tv_time_connect);
        mBtnPlay = findViewById(R.id.btn_play);
        mBtnPlay.setOnClickListener(v -> {
            startTimerSetUp();
        });
        setTimeInit();
        startTimerSetUp();
    }
    // timer
    private void setTimeInit() {
        timeSelected = 180; // 3 minutes
        // set text timer
        mTvTimer.setText(formatMinutes(timeSelected));
        // set max processbar
        mProcessBar.setMax(timeSelected);
    }

    private void startTimerSetUp() {
        if(isStart){
            startTimer(pauseOffSet);
            isStart = false;
        }
        else {
            isStart = true;
            timePause();
        }
    }

    private void startTimer(long pauseOffSetL){
        mProcessBar.setProgress(timeProgress);
        timeCountDown = new CountDownTimer((timeSelected* 1000L) - pauseOffSetL*1000, 1000) {
            @Override
            public void onTick(long p0) {
                timeProgress++;
                pauseOffSet = timeSelected- p0/1000;
                mProcessBar.setProgress(timeSelected-timeProgress);
                mTvTimer.setText(formatMinutes(timeSelected - timeProgress));
            }

            @Override
            public void onFinish() {
                resetTime();
                onFinishTimer("Time up");
            }
        }.start();
    }

    private void onFinishTimer(String value) {
        Toast toast = Toast.makeText(this, value, Toast.LENGTH_LONG);
        toast.show();
    }

    private void timePause() {
        if(timeCountDown != null){
            timeCountDown.cancel();
        }
    }

    private void resetTime() {
        if(timeCountDown != null) {
            timeCountDown.cancel();
            timeProgress = 0;
            timeSelected = 0;
            pauseOffSet = 0;
            timeCountDown = null;
            isStart = true;
            mProcessBar.setProgress(0);
            mTvTimer.setText("00:00");
        }
    }

    private String formatMinutes(int minutes) {
        int hours = minutes / 60;
        int remainingMinutes = minutes % 60;

        String formattedHours = String.format("%02d", hours);
        String formattedMinutes = String.format("%02d", remainingMinutes);

        return formattedHours + ":" + formattedMinutes;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(timeCountDown != null){
            timeCountDown.cancel();
            timeProgress = 0;
        }
    }

    // slider
    private void initViewPager2(){
        viewPager2 = findViewById(R.id.view_image_slider);

        userInfos = new ArrayList<UserInfo>(Arrays.asList(UserInfo.dummy));
        viewPager2.setAdapter(new SliderAdapter(userInfos, viewPager2));

        viewPager2.setClipToPadding(false);
        viewPager2.setClipChildren(false);
        viewPager2.setOffscreenPageLimit(3);
        viewPager2.getChildAt(0).setOverScrollMode(RecyclerView.OVER_SCROLL_NEVER);
    }

    private void setTransformer(){
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