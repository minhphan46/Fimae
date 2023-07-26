package com.example.fimae.activities;

import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.provider.FontRequest;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;
import androidx.viewpager2.widget.ViewPager2;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;

import com.example.fimae.R;
import com.example.fimae.adapters.ViewPagerAdapter;
import com.example.fimae.service.CustomViewPager;
import com.example.fimae.service.UpdateUserActivityTimeService;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

import java.security.cert.Certificate;

public class HomeActivity extends AppCompatActivity {

    private BottomNavigationView mNavigationView;
    private ViewPager2 mViewPager;
    public static String PACKAGE_NAME;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        PACKAGE_NAME = getApplicationContext().getPackageName();

        mNavigationView = findViewById(R.id.bottom_nav);
        mViewPager = findViewById(R.id.view_paper);
        setUpViewPager();

        mNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.action_home:
                        mViewPager.setCurrentItem(0, false);
                        break;
                    case R.id.action_feed:
                        mViewPager.setCurrentItem(1, false);
                        break;
                    case R.id.action_date:
                        mViewPager.setCurrentItem(2, false);
                        break;
                    case R.id.action_chat:
                        mViewPager.setCurrentItem(3, false);
                        break;
                    case R.id.action_profile:
                        mViewPager.setCurrentItem(4, false);
                        break;
                }
                return true;
            }
        });
        Intent intent = new Intent(this, UpdateUserActivityTimeService.class);
        startService(intent);
    }
void init(){

}
    private void setUpViewPager() {
        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(this);
        mViewPager.setAdapter(viewPagerAdapter);
        //turn off smooth scroll
        mViewPager.setUserInputEnabled(false);

        mViewPager.setOffscreenPageLimit(5);
        //Add listener to change icon when page change
        mViewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                mNavigationView.getMenu().getItem(position).setChecked(true);
            }
        });
    }


}
