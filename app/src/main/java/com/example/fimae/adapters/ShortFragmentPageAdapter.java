package com.example.fimae.adapters;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.fimae.fragments.ShortTabFragment;
import com.example.fimae.fragments.StoryTabFragment;

public class ShortFragmentPageAdapter extends FragmentStateAdapter {

    public ShortFragmentPageAdapter(@NonNull FragmentManager fragmentManager, @NonNull Lifecycle lifecycle) {
        super(fragmentManager, lifecycle);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        if(position == 0) {
            return new ShortTabFragment();
        } else {
            return new StoryTabFragment();
        }
    }

    @Override
    public int getItemCount() {
        return 2;
    }
}
