package com.example.fimae.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;
import androidx.viewpager2.widget.ViewPager2;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ProgressBar;
import android.widget.TableLayout;

import com.example.fimae.DatingAddImages;
import com.example.fimae.R;
import com.example.fimae.adapters.Dating.DatingFragmentAdapter;
import com.example.fimae.repository.DatingRepository;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

public class DatingSettings extends AppCompatActivity {


    private ViewPager2 viewPager;
    private TabLayout tabLayout;

    String uid;
    public void getArgs(){
        Bundle args = getIntent().getExtras();
        if(args!=null){
            uid = args.getString("uid");
        }
        if (uid==null)
            throw new RuntimeException("uid is null");
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dating_settings);
        getArgs();
        ProgressBar progressBar = findViewById(R.id.dating_progress_bar);
        DatingRepository.getInstance().getDatingProfileByUid(uid).addOnSuccessListener(datingProfile -> {
            if(datingProfile==null) {
                Intent intent = new Intent(this, DatingAddImages.class);
                intent.putExtra("uid", uid);
                intent.putExtra("isCreate", true);
                startActivity(intent);
            } else {
                progressBar.setVisibility(ProgressBar.GONE);
                viewPager = findViewById(R.id.dating_view_pager);
                tabLayout = findViewById(R.id.dating_tab_layout);
                tabLayout.setVisibility(TabLayout.VISIBLE);
                viewPager.setVisibility(ViewPager2.VISIBLE);
                DatingFragmentAdapter datingFragmentAdapter = new DatingFragmentAdapter(this, datingProfile);
                viewPager.setAdapter(datingFragmentAdapter);
                new TabLayoutMediator(tabLayout, viewPager,
                        (tab, position) -> {
                            if(position==0)
                                tab.setText("Mẫu người lý tưởng");
                            else
                                tab.setText("Chung");
                        }
                ).attach();
            }
        });



    }
}