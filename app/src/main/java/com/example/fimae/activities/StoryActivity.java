package com.example.fimae.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import android.os.Bundle;

import com.example.fimae.R;
import com.example.fimae.adapters.MyStoryAdapter;
import com.example.fimae.adapters.StoryAdapter.StoryAdapterItem;

public class StoryActivity extends AppCompatActivity {

    ViewPager2 viewPager2;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StoryAdapterItem storyAdapterItem = (StoryAdapterItem) getIntent().getSerializableExtra("storyAdapterItem");
        setContentView(R.layout.activity_story);
        viewPager2 = findViewById(R.id.viewPager2);
        MyStoryAdapter myStoryAdapter = new MyStoryAdapter(this, storyAdapterItem);
        viewPager2.setAdapter(myStoryAdapter);
    }
}