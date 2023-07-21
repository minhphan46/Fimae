package com.example.fimae.fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.fimae.R;
import com.example.fimae.activities.ShortVideoActivity;
import com.example.fimae.adapters.ShortsReviewAdapter;
import com.example.fimae.adapters.SpacingItemDecoration;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ShortTabFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ShortTabFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public ShortTabFragment() {
        // Required empty public constructor
    }

    public static ShortTabFragment newInstance(String param1, String param2) {
        ShortTabFragment fragment = new ShortTabFragment();
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
        View view = inflater.inflate(R.layout.fragment_short_tab, container, false);

        RecyclerView storyRecyclerView = view.findViewById(R.id.recycler_view_shorts_tab);
        LinearLayoutManager storyLinearLayoutManager = new LinearLayoutManager(this.getContext());
        storyLinearLayoutManager.setOrientation(RecyclerView.HORIZONTAL);
        storyRecyclerView.setLayoutManager(storyLinearLayoutManager);
        SpacingItemDecoration itemDecoration = new SpacingItemDecoration(16, 16, 8, 8);
        storyRecyclerView.addItemDecoration(itemDecoration);
        ShortsReviewAdapter shortAdapter = new ShortsReviewAdapter(video -> {
            Intent intent = new Intent(getContext(), ShortVideoActivity.class);
            //intent.putExtra("idVideo", video.getId());  // Truyền một String
            startActivity(intent);
        });
        storyRecyclerView.setAdapter(shortAdapter);

        return view;
    }
}