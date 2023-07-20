package com.example.fimae.Story.utils;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.media3.exoplayer.ExoPlayer;
import androidx.viewpager.widget.PagerAdapter;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.example.fimae.R;
import com.example.fimae.Story.callback.StoryCallbacks;
import com.example.fimae.models.story.Story;
import com.example.fimae.models.story.StoryType;

import java.util.ArrayList;

public class ViewPagerAdapter extends PagerAdapter {

    private ArrayList<Story> images;
    private Context context;
    private StoryCallbacks storyCallbacks;
    private boolean storiesStarted = false;

    public ViewPagerAdapter(ArrayList<Story> images, Context context, StoryCallbacks storyCallbacks) {
        this.images = images;
        this.context = context;
        this.storyCallbacks = storyCallbacks;
    }

    @Override
    public int getCount() {
        return images.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }

    private void setUpImageStoryView( View view, Story currentStory) {
        final ImageView mImageView = view.findViewById(R.id.mImageView);
        mImageView.setVisibility(View.VISIBLE);
        //TODO: add description
//        if (!TextUtils.isEmpty(currentStory.getDescription())) {
//            TextView textView = view.findViewById(R.id.descriptionTextView);
//            textView.setVisibility(View.VISIBLE);
//            textView.setText(currentStory.getDescription());
//            textView.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    storyCallbacks.onDescriptionClickListener(position);
//                }
//            });
//        }

        Glide.with(context)
                .load(currentStory.getUrl())
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        storyCallbacks.nextStory();
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        if (resource != null) {
                            PaletteExtraction pe = new PaletteExtraction(view.findViewById(R.id.relativeLayout),
                                    ((BitmapDrawable) resource).getBitmap());
                            pe.execute();
                        }
                        if (!storiesStarted) {
                            storiesStarted = true;
                            storyCallbacks.startStories();
                        }
                        return false;
                    }
                })
                .into(mImageView);
    }

    private void setUpVideoStoryView(View view, Story story) {
        final VideoView videoView = view.findViewById(R.id.mVideoView);
        videoView.setVisibility(View.VISIBLE);
        Uri uri = Uri.parse(story.getUrl());
        videoView.setVideoURI(uri);
        // creating object of
        // media controller class
        MediaController mediaController = new MediaController(view.getContext());

        // sets the anchor view
        // anchor view for the videoView
        mediaController.setAnchorView(videoView);

        // sets the media player to the videoView
        mediaController.setMediaPlayer(videoView);

        // sets the media controller to the videoView

        // starts the video
        videoView.start();
    }
    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup collection, final int position) {

        LayoutInflater inflater = LayoutInflater.from(context);

        Story currentStory = images.get(position);

        final View view = inflater.inflate(R.layout.layout_story_item, collection, false);

        if(currentStory.getType() == StoryType.IMAGE) {
            setUpImageStoryView(view, currentStory);
            view.setTag("IMAGE");
        } else {
            setUpVideoStoryView(view, currentStory);
            view.setTag("VIDEO");
        }
        collection.addView(view);
        return view;
    }


    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        (container).removeView((View) object);
    }
}
