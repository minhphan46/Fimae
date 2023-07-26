package com.example.fimae.activities;
import android.content.Intent;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.provider.FontRequest;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;

import com.example.fimae.R;
import com.example.fimae.adapters.ViewPagerAdapter;
import com.example.fimae.models.DisableUser;
import com.example.fimae.repository.AuthRepository;
import com.example.fimae.service.CustomViewPager;
import com.example.fimae.service.UpdateUserActivityTimeService;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.security.cert.Certificate;
import java.util.Date;
import java.util.Objects;

public class HomeActivity extends AppCompatActivity {

    private BottomNavigationView mNavigationView;
    private CustomViewPager mViewPager;
    public static String PACKAGE_NAME;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        listenDisable();
        PACKAGE_NAME = getApplicationContext().getPackageName();
        mNavigationView = findViewById(R.id.bottom_nav);
        mViewPager = findViewById(R.id.view_paper);
        setUpViewPager();

        mNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.action_home:
                        mViewPager.setCurrentItem(0);
                        break;
                    case R.id.action_feed:
                        mViewPager.setCurrentItem(1);
                        break;
                    case R.id.action_date:
                        mViewPager.setCurrentItem(2);
                        break;
                    case R.id.action_chat:
                        mViewPager.setCurrentItem(3);
                        break;
                    case R.id.action_profile:
                        mViewPager.setCurrentItem(4);
                        break;
                }
                return true;
            }
        });
        Intent intent = new Intent(this, UpdateUserActivityTimeService.class);
        startService(intent);
    }

    private void listenDisable(){
        if(FirebaseAuth.getInstance().getUid() == null){
            Intent intent = new Intent(HomeActivity.this, AuthenticationActivity.class);
            startActivity(intent);
        }
        FirebaseFirestore.getInstance().collection("user_disable")
                .document(Objects.requireNonNull(FirebaseAuth.getInstance().getUid()))
                .addSnapshotListener(new EventListener<DocumentSnapshot>() {
                    @Override
                    public void onEvent(@Nullable DocumentSnapshot snapshot, @Nullable FirebaseFirestoreException error) {
                        if (error != null) {
                            return;
                        }
                        if (snapshot != null && snapshot.exists()) {
                            // Document exists, check if the user is disabled
                            DisableUser disableUser = snapshot.toObject(DisableUser.class);
                            if (disableUser != null && disableUser.getTimeEnd().after(new Date())) {
//                                Intent intent = new Intent(HomeActivity.this, AuthenticationActivity.class);
//                                intent.putExtra("signout", true);
//                                startActivity(intent);
                                Intent intent = new Intent(HomeActivity.this, DisabledUserActivity.class);
                                intent.putExtra( "disableId", disableUser.getUserId());
                                startActivity(intent);
                                finish();
                            }
                        }
                    }
                });
    }
    private void setUpViewPager() {
        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager(), FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        mViewPager.setAdapter(viewPagerAdapter);
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                switch (position) {
                    case 0:
                        mNavigationView.getMenu().findItem(R.id.action_home).setChecked(true);
                        break;
                    case 1:
                        mNavigationView.getMenu().findItem(R.id.action_feed).setChecked(true);
                        break;
                    case 2:
                        mNavigationView.getMenu().findItem(R.id.action_date).setChecked(true);
                        break;
                    case 3:
                        mNavigationView.getMenu().findItem(R.id.action_chat).setChecked(true);
                        break;
                    case 4:
                        mNavigationView.getMenu().findItem(R.id.action_profile).setChecked(true);
                        break;
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }


}
