package com.example.fimae.adapters.Dating;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.fimae.DatingGeneralSettings;
import com.example.fimae.DatingProfile;

public class DatingFragmentAdapter extends FragmentStateAdapter {


    public DatingFragmentAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        if(position==0)
            return new DatingProfile();
        else
            return new DatingGeneralSettings();
    }

    @Override
    public int getItemCount() {
        return 2;
    }
}
