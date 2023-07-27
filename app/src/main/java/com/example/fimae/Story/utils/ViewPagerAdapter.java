package com.example.fimae.Story.utils;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.fimae.Story.callback.StoryCallbacks;
import com.example.fimae.Story.progress.StoriesProgressView;
import com.example.fimae.fragments.StoryItemFragment;
import com.example.fimae.models.story.Story;

import java.util.ArrayList;

public class ViewPagerAdapter extends FragmentStateAdapter {
        private ArrayList<Story> stories;
    private Context context;
    private StoriesProgressView.StoriesListener storiesListener;
    public ViewPagerAdapter(@NonNull FragmentActivity fragmentActivity, ArrayList<Story> stories, Context context, StoriesProgressView.StoriesListener storiesListener) {
        super(fragmentActivity);
        this.stories = stories;
        context = context;
        this.storiesListener = storiesListener;
    }
    @NonNull
    @Override
    public Fragment createFragment(int position) {
        return new StoryItemFragment(stories.get(position), position, stories.size(), storiesListener);
    }

    @Override
    public int getItemCount() {
        if (stories == null)
            return 0;
        else
            return stories.size();
    }

//    private ArrayList<Story> images;
//    private Context context;
//    private StoryCallbacks storyCallbacks;
//    private boolean storiesStarted = false;
//
//    public ViewPagerAdapter(ArrayList<Story> images, Context context, StoryCallbacks storyCallbacks) {
//        this.images = images;
//        this.context = context;
//        this.storyCallbacks = storyCallbacks;
//    }
//
//    @Override
//    public int getCount() {
//        return images.size();
//    }
//
//    @Override
//    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
//        return view == object;
//    }
//
//    private void setUpImageStoryView( View view, Story currentStory) {
//        final ImageView mImageView = view.findViewById(R.id.mImageView);
//        mImageView.setVisibility(View.VISIBLE);
//        //TODO: add description
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
//
//        Glide.with(context)
//                .load(currentStory.getUrl())
//                .listener(new RequestListener<Drawable>() {
//                    @Override
//                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
//                        storyCallbacks.nextStory();
//                        return false;
//                    }
//
//                    @Override
//                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
//                        if (resource != null) {
//                            PaletteExtraction pe = new PaletteExtraction(view.findViewById(R.id.relativeLayout),
//                                    ((BitmapDrawable) resource).getBitmap());
//                            pe.execute();
//                        }
//                        if (!storiesStarted) {
//                            storiesStarted = true;
//                            storyCallbacks.startStories();
//                        }
//                        return false;
//                    }
//                })
//                .into(mImageView);
//    }
//
//    private void setUpVideoStoryView(View view, Story story) {
//        final VideoView videoView = view.findViewById(R.id.mVideoView);
//        videoView.setVisibility(View.VISIBLE);
//        Uri uri = Uri.parse(story.getUrl());
//        videoView.setVideoURI(uri);
//        // creating object of
//        // media controller class
//        MediaController mediaController = new MediaController(view.getContext());
//
//        // sets the anchor view
//        // anchor view for the videoView
//        mediaController.setAnchorView(videoView);
//
//        // sets the media player to the videoView
//        mediaController.setMediaPlayer(videoView);
//
//        // sets the media controller to the videoView
//
//        // starts the video
//        videoView.start();
//    }
//    @NonNull
//    @Override
//    public Object instantiateItem(@NonNull ViewGroup collection, final int position) {
//
//        LayoutInflater inflater = LayoutInflater.from(context);
//
//        Story currentStory = images.get(position);
//
//        final View view = inflater.inflate(R.layout.layout_story_item, collection, false);
//
//        if(currentStory.getType() == StoryType.IMAGE) {
//            setUpImageStoryView(view, currentStory);
//            view.setTag("IMAGE");
//        } else {
//            setUpVideoStoryView(view, currentStory);
//            view.setTag("VIDEO");
//        }
//        collection.addView(view);
//        return view;
//    }
//
//
//    @Override
//    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
//        (container).removeView((View) object);
//    }
}
