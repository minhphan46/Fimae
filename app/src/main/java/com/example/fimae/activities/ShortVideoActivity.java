package com.example.fimae.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

import com.example.fimae.R;
import com.example.fimae.adapters.ShortVideoAdapter;
import com.example.fimae.databinding.ActivityShortVideoBinding;
import com.example.fimae.models.shorts.ShortMedia;

import java.util.ArrayList;

public class ShortVideoActivity extends AppCompatActivity {
    ActivityShortVideoBinding binding;
    ArrayList<ShortMedia> shortMedias = ShortMedia.getFakeData();
    ShortVideoAdapter shortVideoAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        binding = ActivityShortVideoBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        this.getTheme().applyStyle(R.style.FullScreen, false);

        shortVideoAdapter = new ShortVideoAdapter(this, shortMedias, new ShortVideoAdapter.IClickCardListener() {
            @Override
            public void onClickUser(ShortMedia video) {
                finish();
            }
        });
        binding.viewPagerVideoShort.setAdapter(shortVideoAdapter);
    }
}