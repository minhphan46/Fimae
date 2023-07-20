package com.example.fimae.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
        import android.view.Window;
        import android.view.WindowManager;

        import com.example.fimae.R;
import com.example.fimae.adapters.PostContentAdapter;
import com.example.fimae.adapters.ShortVideoAdapter;
import com.example.fimae.databinding.ActivityPostContentBinding;
import com.example.fimae.databinding.ActivityShortVideoBinding;
        import com.example.fimae.models.shorts.ShortMedia;

        import java.util.ArrayList;

public class PostContentActivity extends AppCompatActivity {
    ActivityPostContentBinding binding;
    ArrayList<String> imageUrls;
    PostContentAdapter contentAdapter;
    private int index;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = this.getIntent();
        imageUrls= getIntent().getStringArrayListExtra("urls");
        index = getIntent().getIntExtra("index", 0);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        binding = ActivityPostContentBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        this.getTheme().applyStyle(R.style.FullScreen, false);
        contentAdapter = new PostContentAdapter(this, imageUrls );

        binding.viewPagerVideoShort.setAdapter(contentAdapter);
        binding.viewPagerVideoShort.setCurrentItem(index);
    }
}