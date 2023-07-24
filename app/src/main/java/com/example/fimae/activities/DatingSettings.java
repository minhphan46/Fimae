package com.example.fimae.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;
import androidx.viewpager2.widget.ViewPager2;

import android.os.Bundle;
import android.widget.TableLayout;

import com.example.fimae.R;
import com.example.fimae.adapters.Dating.DatingFragmentAdapter;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

public class DatingSettings extends AppCompatActivity {


    private ViewPager2 viewPager;
    private TabLayout tabLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dating_settings);
        viewPager = findViewById(R.id.dating_view_pager);
        tabLayout = findViewById(R.id.dating_tab_layout);
        DatingFragmentAdapter datingFragmentAdapter = new DatingFragmentAdapter(this);
        viewPager.setAdapter(datingFragmentAdapter);
        new TabLayoutMediator(tabLayout, viewPager,
                (tab, position) -> {
                    if(position==0)
                        tab.setText("Profile");
                    else
                        tab.setText("Settings");
                }
        ).attach();

    }
}