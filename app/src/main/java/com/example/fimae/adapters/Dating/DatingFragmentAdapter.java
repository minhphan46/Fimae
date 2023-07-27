package com.example.fimae.adapters.Dating;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.fimae.DatingGeneralSettings;
import com.example.fimae.DatingProfileFragment;
import com.example.fimae.models.dating.DatingProfile;

public class DatingFragmentAdapter extends FragmentStateAdapter {


    DatingProfile datingProfile;
    public DatingFragmentAdapter(@NonNull FragmentActivity fragmentActivity, DatingProfile datingProfile) {
        super(fragmentActivity);
        this.datingProfile = datingProfile;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        if(position==0)
            return new DatingProfileFragment(datingProfile);
        else
            return new DatingGeneralSettings(datingProfile);
    }

    @Override
    public int getItemCount() {
        return 2;
    }
}
