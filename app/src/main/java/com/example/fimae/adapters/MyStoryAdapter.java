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

import java.util.ArrayList;

public class MyStoryAdapter extends FragmentStateAdapter {


    ArrayList<StoryAdapterItem> storyAdapterItems;
    public MyStoryAdapter(@NonNull FragmentActivity fragmentActivity, ArrayList<StoryAdapterItem> storyAdapterItems, StoryView.onCloseCallBack closeCallBack) {
        super(fragmentActivity);
        this.storyAdapterItems = storyAdapterItems;
        this.storyOnCloseCallBack = closeCallBack;
    }
    private StoryView.onCloseCallBack storyOnCloseCallBack;

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        StoryAdapterItem storyAdapterItem = storyAdapterItems.get(position);
        StoryView storyView =  new StoryView(storyAdapterItem.getFimaer(), storyAdapterItem.getStories());
        storyView.setmOncloseCallBack(storyOnCloseCallBack);
        return storyView;
    }

    @Override
    public int getItemCount() {

        if (storyAdapterItems != null)
            return storyAdapterItems.size();
        else
            return 0;
    }
}
