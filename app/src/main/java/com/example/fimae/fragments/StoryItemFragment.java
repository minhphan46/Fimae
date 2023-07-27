package com.example.fimae.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.media3.common.MediaItem;
import androidx.media3.common.Player;
import androidx.media3.datasource.DataSource;
import androidx.media3.datasource.DefaultHttpDataSource;
import androidx.media3.datasource.cache.CacheDataSource;
import androidx.media3.exoplayer.ExoPlayer;
import androidx.media3.exoplayer.smoothstreaming.SsMediaSource;
import androidx.media3.exoplayer.source.MediaSource;

import com.bumptech.glide.Glide;
import com.example.fimae.R;
import com.example.fimae.Story.progress.StoriesProgressView;
import com.example.fimae.models.story.Story;
import com.example.fimae.models.story.StoryType;

public class StoryItemFragment extends Fragment {
    private Story story;
    private int ImageDuration = 4000;
    private long VideoDuration;
    private int total;
    private int position;
    private Surface videoSurface;
    private SurfaceView surfaceView;
    ExoPlayer player;
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
        surfaceView = view.findViewById(R.id.surfaceVideoView);
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

        if (position == 0 && story.getType()== StoryType.IMAGE)
            storiesProgressView.startStories(position);
    }
    private void setupVideoView() {
        surfaceView.setVisibility(View.VISIBLE);
        ExoPlayer player = new ExoPlayer.Builder(getContext()).build();
        MediaItem mediaItem = MediaItem.fromUri(story.getUrl());
        player.setMediaItem(mediaItem);
        player.addListener(
                new Player.Listener() {
                    @Override
                    public void onPlaybackStateChanged(int playbackState) {
                        switch (playbackState) {
                            case Player.STATE_BUFFERING:
                                // Buffering
                                storiesProgressView.pause();
                                break;
                            case Player.STATE_ENDED:
                                // The media source has reached its end.
                                break;
                            case Player.STATE_IDLE:
                                // Idle
                                break;
                            case Player.STATE_READY:
                                VideoDuration = player.getDuration();
                                storiesProgressView.setStoryDuration(VideoDuration);
                                storiesProgressView.startStories(position);
                                break;
                            default:
                                // Unknown state
                                break;
                        }
                    }
                });
        surfaceView.getHolder().addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(@NonNull SurfaceHolder holder) {
                videoSurface = holder.getSurface();
                player.setVideoSurface(videoSurface);
                player.setPlayWhenReady(true);
            }

            @Override
            public void surfaceChanged(@NonNull SurfaceHolder holder, int format, int width, int height) {
                videoSurface = holder.getSurface();
                player.setVideoSurface(videoSurface);
                player.setPlayWhenReady(true);
            }

            @Override
            public void surfaceDestroyed(@NonNull SurfaceHolder holder) {
                player.pause();
                Toast.makeText(getContext(), "Surface Destroyed", Toast.LENGTH_SHORT).show();
            }
        });
        player.prepare();
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
        if (story.getType() == StoryType.VIDEO && player != null) {
            storiesProgressView.startStories(position);
            storiesProgressView.pause();
            player.play();
        } else if(story.getType() == StoryType.IMAGE) {
            storiesProgressView.startStories(position);
        }
    }
    @Override
    public void onPause() {
        super.onPause();
        if (story.getType() == StoryType.VIDEO && player != null) {
            player.pause();
        }
        storiesProgressView.pause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (player != null) {
            player.release();
        }
        if (videoSurface != null) {
            videoSurface.release();
            videoSurface = null;
        }
    }
}
