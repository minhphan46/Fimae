package com.example.fimae.adapters;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.fimae.Story.StoryView;
import com.example.fimae.adapters.StoryAdapter.StoryAdapterItem;
import com.example.fimae.models.Fimaers;
import com.example.fimae.models.story.Story;

public class MyStoryAdapter extends FragmentStateAdapter {


    StoryAdapterItem storyAdapterItem;
    public MyStoryAdapter(@NonNull FragmentActivity fragmentActivity, StoryAdapterItem storyAdapterItem) {
        super(fragmentActivity);
        this.storyAdapterItem = storyAdapterItem;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        return new StoryView(storyAdapterItem.getFimaer(), storyAdapterItem.getStories());
    }

    @Override
    public int getItemCount() {
        return 2;
    }
}
