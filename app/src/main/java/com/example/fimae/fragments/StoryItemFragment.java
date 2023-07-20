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

public class StoryItemFragment  extends Fragment {
    Story story;
    public VideoView videoView;
    public ImageView imageView;
    public StoriesProgressView storiesProgressView;
    private StoriesProgressView.StoriesListener storiesListener;

    public void setStoriesListener(StoriesProgressView.StoriesListener storiesListener) {
        this.storiesListener = storiesListener;
    }

    int ImageDuration = 4000;
    int VideoDuration;
    int total;
    int position;
    public StoryItemFragment(Story story, int position, int total) {
        this.story = story;
        this.position = position;
        this.total = total;
    }
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.layout_story_item, container, false);
        storiesProgressView = view.findViewById(R.id.mStoryProgressView);
        storiesProgressView.setStoriesCount(total);
        storiesProgressView.setStoriesListener(new StoriesProgressView.StoriesListener() {
            @Override
            public void onNext() {
                storiesProgressView.pause();
            }
            @Override
            public void onPrev() {
                Toast.makeText(getContext(), "Prev", Toast.LENGTH_SHORT).show();

            }

            @Override
            public void onComplete() {
                Toast.makeText(getContext(), "Complete", Toast.LENGTH_SHORT).show();

            }
        });
        if(story.getType() == StoryType.VIDEO){
            storiesProgressView.setStoryDuration(ImageDuration);
            videoView = view.findViewById(R.id.mVideoView);
            videoView.setVisibility(View.VISIBLE);
            videoView.setVideoPath(story.getUrl());
            MediaController mediaController = new MediaController(getContext());
            videoView.setMediaController(mediaController);
            mediaController.setAnchorView(videoView);
            mediaController.setMediaPlayer(videoView);
            mediaController.hide();
            videoView.setOnPreparedListener(mp -> {
                VideoDuration = mp.getDuration();
                Toast.makeText(getContext(), "Duration: " + VideoDuration, Toast.LENGTH_SHORT).show();
                storiesProgressView.setStoryDuration(VideoDuration);
                storiesProgressView.startStories(position);

            });
        }else{
            imageView = view.findViewById(R.id.mImageView);
            imageView.setVisibility(View.VISIBLE);
            Glide.with(this).load(story.getUrl()).into(imageView);
            storiesProgressView.setStoryDuration(ImageDuration);
            if(position == 0)
                storiesProgressView.startStories(position);
        }
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        if(story.getType() == StoryType.VIDEO && videoView != null){
                videoView.start();
        }
        else if( story.getType() == StoryType.IMAGE  &&storiesProgressView != null)
            storiesProgressView.startStories(position);
    }

    @Override
    public void onPause() {
        super.onPause();
        if(story.getType() == StoryType.VIDEO && videoView != null && !videoView.isPlaying()){
            videoView.pause();
        }
        if(storiesProgressView != null)
            storiesProgressView.pause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(story.getType() == StoryType.VIDEO && videoView != null)
            videoView.stopPlayback();
    }
}
