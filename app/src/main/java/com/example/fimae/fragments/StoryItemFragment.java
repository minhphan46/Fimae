package com.example.fimae.fragments;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.example.fimae.R;
import com.example.fimae.Story.progress.StoriesProgressView;
import com.example.fimae.models.Fimaers;
import com.example.fimae.models.story.Story;
import com.example.fimae.models.story.StoryType;

public class StoryItemFragment extends Fragment {
    private Story story;
    private int ImageDuration = 4000;
    private int VideoDuration;
    private int total;
    private int position;
    private VideoView videoView;
    private ImageView imageView;
    private StoriesProgressView storiesProgressView;
    private StoriesProgressView.StoriesListener storiesListener;

    public StoryItemFragment(Story story, int position, int total, StoriesProgressView.StoriesListener storiesListener) {
        this.story = story;
        this.position = position;
        this.total = total;
        this.storiesListener = storiesListener;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.layout_story_item, container, false);
        storiesProgressView = view.findViewById(R.id.mStoryProgressView);
        storiesProgressView.setStoriesListener(storiesListener);
        initializeViews(view);
        initializeStoriesProgress();
        return view;
    }

    private void initializeViews(View view) {
        videoView = view.findViewById(R.id.mVideoView);
        imageView = view.findViewById(R.id.mImageView);
    }

    private void initializeStoriesProgress() {

        storiesProgressView.setStoriesCount(total);
        switch (story.getType()) {
            case VIDEO:
                setupVideoView();
                break;
            case IMAGE:
                setupImageView();
                break;
        }

        if (position == 0)
            storiesProgressView.startStories(position);
    }

    private void setupVideoView() {
        videoView.setVisibility(View.VISIBLE);
        videoView.setVideoPath(story.getUrl());
        videoView.setOnPreparedListener(mp -> {
            VideoDuration = mp.getDuration();
            storiesProgressView.setStoryDuration(VideoDuration);
            storiesProgressView.startStories(position);
        });
    }

    private void setupImageView() {
        imageView.setVisibility(View.VISIBLE);
        Glide.with(this).load(story.getUrl()).into(imageView);
        storiesProgressView.setStoryDuration(ImageDuration);
    }

    @Override
    public void onResume() {
        super.onResume();
        initializeStoriesProgress();
        if (story.getType() == StoryType.VIDEO && videoView != null) {
            videoView.start();
        } else {
            storiesProgressView.startStories(position);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (story.getType() == StoryType.VIDEO && videoView != null && videoView.isPlaying()) {
            videoView.pause();
        }
        storiesProgressView.pause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (story.getType() == StoryType.VIDEO && videoView != null) {
            videoView.stopPlayback();
        }
    }
}
