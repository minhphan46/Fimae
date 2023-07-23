package com.example.fimae.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import android.content.Intent;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.example.fimae.R;
import com.example.fimae.adapters.ShortAdapter.ShortVideoAdapter;
import com.example.fimae.databinding.ActivityShortVideoBinding;
import com.example.fimae.models.shorts.ShortMedia;
import com.example.fimae.repository.ShortsRepository;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;

public class ShortVideoActivity extends AppCompatActivity {
    ActivityShortVideoBinding binding;
    ArrayList<ShortMedia> shortMedias = ShortMedia.getFakeData();
    ShortVideoAdapter shortVideoAdapter;
    Query shortQuery;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        binding = ActivityShortVideoBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        this.getTheme().applyStyle(R.style.FullScreen, false);
        // get id video first
        Intent intent = getIntent();
        String idFirstVideo = intent.getStringExtra("idVideo");
        boolean isProfile = intent.getBooleanExtra("isProfile", false);

        if (isProfile) {
            shortQuery = ShortsRepository.getInstance().getShortUserQuery(FirebaseAuth.getInstance().getUid());
        } else {
            shortQuery = ShortsRepository.getInstance().getShortQuery();
        }
        shortVideoAdapter = new ShortVideoAdapter(shortQuery, this, shortMedias, idFirstVideo, new ShortVideoAdapter.IClickCardListener() {
            @Override
            public void onClickUser(ShortMedia video) {
                finish();
            }

            @Override
            public FragmentManager getFragmentManager() {
                return getSupportFragmentManager();
            }
        });
        binding.viewPagerVideoShort.setAdapter(shortVideoAdapter);

        binding.viewPagerVideoShort.registerOnPageChangeCallback(new androidx.viewpager2.widget.ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                //binding.viewPagerVideoShort.setCurrentItem(position);
                shortVideoAdapter.onBeginPlayVideo(position);
                shortVideoAdapter.addWatched(position);
                super.onPageSelected(position);
            }
        });
    }
}