package com.example.fimae;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import android.os.Bundle;

import com.example.fimae.adapters.MyStoryAdapter;
import com.example.fimae.adapters.StoryAdapter;

public class StoryActivity extends AppCompatActivity {

    ViewPager2 viewPager2;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_story);
        viewPager2 = findViewById(R.id.viewPager2);
        MyStoryAdapter myStoryAdapter = new MyStoryAdapter(this);
        viewPager2.setAdapter(myStoryAdapter);

    }
}