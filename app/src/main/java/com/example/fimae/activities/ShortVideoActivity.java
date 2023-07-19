package com.example.fimae.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.example.fimae.R;
import com.example.fimae.adapters.ShortVideoAdapter;
import com.example.fimae.databinding.ActivityShortVideoBinding;
import com.example.fimae.models.shorts.ShortMedia;

import java.util.ArrayList;

public class ShortVideoActivity extends AppCompatActivity {
    ActivityShortVideoBinding binding;
    ArrayList<ShortMedia> shortMedias = new ArrayList<>();
    ShortVideoAdapter shortVideoAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityShortVideoBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        shortMedias.add(ShortMedia.createShortVideo("1", "Video \"Ngọt ngào cùng những chú mèo đáng yêu\" là một bộ sưu tập những cảnh quay đáng yêu và đầy hài hước về các chú mèo dễ thương", "android.resource://" + getPackageName() + "/"+ R.raw.video1, PostMode.PUBLIC, true));
        shortMedias.add(ShortMedia.createShortVideo("2", "Video bắt đầu bằng những cảnh mèo con vui đùa, nhảy lên nhảy xuống với niềm vui tột độ trên chiếc giường nhỏ. ", "android.resource://" + getPackageName() + "/"+ R.raw.video2, PostMode.PUBLIC, true));
        shortMedias.add(ShortMedia.createShortVideo("3", "Từ những cánh rừng rậm rạp, thác nước xiết dòng", "android.resource://" + getPackageName() + "/"+ R.raw.video3, PostMode.PUBLIC, true));
        shortMedias.add(ShortMedia.createShortVideo("4", "test", "android.resource://" + getPackageName() + "/"+ R.raw.video4, PostMode.PUBLIC, true));
        shortMedias.add(ShortMedia.createShortVideo("5", "test", "android.resource://" + getPackageName() + "/"+ R.raw.video5, PostMode.PUBLIC, true));
        shortMedias.add(ShortMedia.createShortVideo("6", "test", "android.resource://" + getPackageName() + "/"+ R.raw.video6, PostMode.PUBLIC, true));
        shortMedias.add(ShortMedia.createShortVideo("7", "test", "android.resource://" + getPackageName() + "/"+ R.raw.video7, PostMode.PUBLIC, true));
        shortMedias.add(ShortMedia.createShortVideo("8", "test", "android.resource://" + getPackageName() + "/"+ R.raw.video8, PostMode.PUBLIC, true));
        shortMedias.add(ShortMedia.createShortVideo("9", "test", "android.resource://" + getPackageName() + "/"+ R.raw.video9, PostMode.PUBLIC, true));
        shortMedias.add(ShortMedia.createShortVideo("10", "test", "android.resource://" + getPackageName() + "/"+ R.raw.video10, PostMode.PUBLIC, true));

        shortVideoAdapter = new ShortVideoAdapter(this, shortMedias, new ShortVideoAdapter.IClickCardListener() {
            @Override
            public void onClickUser(ShortMedia video) {
                finish();
            }
        });
        binding.viewPagerVideoShort.setAdapter(shortVideoAdapter);
    }
}