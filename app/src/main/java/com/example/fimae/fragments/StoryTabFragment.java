package com.example.fimae.fragments;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.fimae.R;
import com.example.fimae.activities.StoryActivity;
import com.example.fimae.adapters.SpacingItemDecoration;
import com.example.fimae.adapters.StoryAdapter.StoryAdapter;
import com.example.fimae.adapters.StoryAdapter.StoryAdapterItem;
import com.example.fimae.models.story.Story;
import com.example.fimae.repository.StoryRepository;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link StoryTabFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class StoryTabFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public StoryTabFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment StoryTabFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static StoryTabFragment newInstance(String param1, String param2) {
        StoryTabFragment fragment = new StoryTabFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_story_tab, container, false);
        RecyclerView storyRecyclerView = view.findViewById(R.id.recycler_view_story_tab);
        LinearLayoutManager storyLinearLayoutManager = new LinearLayoutManager(this.getContext());
        storyLinearLayoutManager.setOrientation(RecyclerView.HORIZONTAL);
        storyRecyclerView.setLayoutManager(storyLinearLayoutManager);
        SpacingItemDecoration itemDecoration = new SpacingItemDecoration(16, 16, 8, 8);
        storyRecyclerView.addItemDecoration(itemDecoration);
        StoryAdapter storyAdapter = new StoryAdapter(StoryRepository.getInstance().getStoryQuery());
        storyRecyclerView.setAdapter(storyAdapter);
    storyAdapter.setStoryListener(new StoryAdapter.StoryListener() {
        @Override
        public void addStoryClicked() {
            MediaListDialogFragment mediaListDialogFragment = new MediaListDialogFragment();
            mediaListDialogFragment.setOnMediaSelectedListener(new MediaListDialogFragment.OnMediaSelectedListener() {
                @Override
                public void OnMediaSelected(boolean isSelected, ArrayList<String> data) {
                    if(isSelected){
                        StoryRepository.getInstance().createStory(Uri.parse(data.get(0))).addOnCompleteListener(new OnCompleteListener<Story>() {
                            @Override
                            public void onComplete(@NonNull Task<Story> task) {
                                if(task.isSuccessful()) {
                                    Story story = task.getResult();
                                } else {
                                    Toast.makeText(getContext(), "Error", Toast.LENGTH_SHORT).show();
                                    task.getException().printStackTrace();
                                }
                            }
                        });
                    }
                }
            });
            mediaListDialogFragment.show(getChildFragmentManager(), "mediaList");
        }

        @Override
        public void onStoryClicked(StoryAdapterItem storyAdapterItem) {
            Intent intent = new Intent(getContext(), StoryActivity.class);
            intent.putExtra("storyAdapterItems", storyAdapter.getStoryAdapterItems());
            startActivity(intent);
        }
    });
        return view;
    }
}