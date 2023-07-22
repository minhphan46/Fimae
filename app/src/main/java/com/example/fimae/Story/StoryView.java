package com.example.fimae.Story;

import static com.example.fimae.Story.utils.Utils.getDurationBetweenDates;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.viewpager.widget.ViewPager;
import androidx.viewpager2.widget.ViewPager2;

import com.bumptech.glide.Glide;
import com.example.fimae.R;
import com.example.fimae.Story.callback.OnStoryChangedCallback;
import com.example.fimae.Story.callback.StoryCallbacks;
import com.example.fimae.Story.callback.StoryClickListeners;

import com.example.fimae.Story.callback.TouchCallbacks;
import com.example.fimae.Story.progress.StoriesProgressView;
import com.example.fimae.Story.utils.PullDismissLayout;
import com.example.fimae.Story.utils.ViewPagerAdapter;
import com.example.fimae.models.Fimaers;
import com.example.fimae.models.story.Story;
import com.example.fimae.models.story.StoryType;

import java.util.ArrayList;
import java.util.Calendar;


public class StoryView extends Fragment implements StoriesProgressView.StoriesListener,
        StoryCallbacks,
        PullDismissLayout.Listener,
        TouchCallbacks {

    private static final String TAG = StoryView.class.getSimpleName();


    private final static String IMAGES_KEY = "IMAGES";

    private long duration = 2000; //Default Duration

    private static final String DURATION_KEY = "DURATION";

    private static final String HEADER_INFO_KEY = "HEADER_INFO";

    private static final String STARTING_INDEX_TAG = "STARTING_INDEX";

    private static final String IS_RTL_TAG = "IS_RTL";

//    private StoriesProgressView storiesProgressView;

    private ViewPager2 mViewPager;

    private int counter = 0;

    private int startingIndex = 0;

    private boolean isHeadlessLogoMode = false;

    //Heading
    private TextView titleTextView, subtitleTextView;
    private CardView titleCardView;
    private ImageView titleIconImageView;
    private ImageButton closeImageButton;

    //Touch Events
    private boolean isDownClick = false;
    private long elapsedTime = 0;
    private Thread timerThread;
    private boolean isPaused = false;
    private int width, height;
    private float xValue = 0, yValue = 0;


    private StoryClickListeners storyClickListeners;
    private OnStoryChangedCallback onStoryChangedCallback;

    private boolean isRtl;

    private Fimaers fimaers;
    private ArrayList<Story> storiesList = new ArrayList<>();

    public StoryView(Fimaers fimaers, ArrayList<Story> stories) {
        this.fimaers = fimaers;
        this.storiesList = stories;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        return inflater.inflate(R.layout.dialog_stories, container);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        DisplayMetrics displaymetrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        width = displaymetrics.widthPixels;
        height = displaymetrics.heightPixels;
        // Get field from view
        readArguments();
        setupViews(view);
        setupStories();

        startStories();
    }

    private ViewPagerAdapter adapter;
    private void setupStories() {
//        storiesProgressView.setStoriesCount(storiesList.size());
//        storiesProgressView.setStoryDuration(duration);
        updateHeading();
        adapter = new ViewPagerAdapter(getActivity(),storiesList , getContext(), this);
        mViewPager.setAdapter(adapter);
    }

    private void readArguments() {
        //assert getArguments() != null;
        duration = 5000;
        startingIndex = 0;
        isRtl = false;
    }

    private void setupViews(View view) {
        ((PullDismissLayout) view.findViewById(R.id.pull_dismiss_layout)).setListener(this);
        ((PullDismissLayout) view.findViewById(R.id.pull_dismiss_layout)).setmTouchCallbacks(this);
//        storiesProgressView = view.findViewById(R.id.storiesProgressView);
        mViewPager = view.findViewById(R.id.storiesViewPager);
        titleTextView = view.findViewById(R.id.title_textView);
        subtitleTextView = view.findViewById(R.id.subtitle_textView);
        titleIconImageView = view.findViewById(R.id.title_imageView);
        titleCardView = view.findViewById(R.id.titleCardView);
        closeImageButton = view.findViewById(R.id.imageButton);
//        storiesProgressView.setStoriesListener(this);
        mViewPager.setOnTouchListener((v, event) -> true);
        //TODO: Add close button
        //closeImageButton.setOnClickListener(v -> dismissAllowingStateLoss());
        if (storyClickListeners != null) {
            titleCardView.setOnClickListener(v -> storyClickListeners.onTitleIconClickListener(counter));
        }
        mViewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                if (onStoryChangedCallback != null)
                    onStoryChangedCallback.storyChanged(position);
            }
        });
        if (isRtl) {
//            storiesProgressView.setLayoutDirection(View.LAYOUT_DIRECTION_LTR);
//            storiesProgressView.setRotation(180);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onNext() {
        mViewPager.setCurrentItem(++counter, false);
        updateHeading();
    }

    @Override
    public void onPrev() {
        if (counter <= 0) return;
        mViewPager.setCurrentItem(--counter, false);
        updateHeading();
    }

    @Override
    public void onComplete() {
//        dismissAllowingStateLoss();
    }

    @Override
    public void startStories() {
        counter = startingIndex;
//        storiesProgressView.startStories(startingIndex);
        mViewPager.setCurrentItem(startingIndex, false);
        updateHeading();
    }

    @Override
    public void pauseStories() {
//        storiesProgressView.pause();
    }

    private void previousStory() {
        if (counter - 1 < 0) return;
        mViewPager.setCurrentItem(--counter, false);
//        storiesProgressView.setStoriesCount(storiesList.size());
//        storiesProgressView.setStoryDuration(duration);
//        storiesProgressView.startStories(counter);
        updateHeading();
    }

    @Override
    public void nextStory() {
        if (counter + 1 >= storiesList.size()) {
//            dismissAllowingStateLoss();
            return;
        }
        mViewPager.setCurrentItem(++counter, false);
//        storiesProgressView.startStories(counter);
        updateHeading();
    }

    @Override
    public void onDescriptionClickListener(int position) {
        if (storyClickListeners == null) return;
        storyClickListeners.onDescriptionClickListener(position);
    }

    @Override
    public void onDestroy() {
        timerThread = null;
        storiesList = null;
//        storiesProgressView.destroy();
        super.onDestroy();
    }

    private void updateHeading() {
        if (fimaers.getAvatarUrl() != null) {
            titleCardView.setVisibility(View.VISIBLE);
            if (getContext() == null) return;
            Glide.with(getContext())
                    .load(fimaers.getAvatarUrl())
                    .into(titleIconImageView);
        } else {
            titleCardView.setVisibility(View.GONE);
            isHeadlessLogoMode = true;
        }

        String name = fimaers.getFirstName() + " " + fimaers.getLastName();

        titleTextView.setVisibility(View.VISIBLE);
        titleTextView.setText(name);

        if (fimaers.getBio() != null) {
            subtitleTextView.setVisibility(View.VISIBLE);
            subtitleTextView.setText(fimaers.getBio());
        } else {
            subtitleTextView.setVisibility(View.GONE);
        }

        if (storiesList.get(counter).getTimeCreated() != null) {
            titleTextView.setText(titleTextView.getText()
                    + " "
                    + getDurationBetweenDates(storiesList.get(counter).getTimeCreated(), Calendar.getInstance().getTime())
            );
        }
    }

    private void setHeadingVisibility(int visibility) {
        if (isHeadlessLogoMode && visibility == View.VISIBLE) {
            titleTextView.setVisibility(View.GONE);
            titleCardView.setVisibility(View.GONE);
            subtitleTextView.setVisibility(View.GONE);
        } else {
            titleTextView.setVisibility(visibility);
            titleCardView.setVisibility(visibility);
            subtitleTextView.setVisibility(visibility);
        }

        closeImageButton.setVisibility(visibility);
//        storiesProgressView.setVisibility(visibility);
    }


    private void createTimer() {
        timerThread = new Thread(() -> {
            while (isDownClick) {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                elapsedTime += 100;
                if (elapsedTime >= 500 && !isPaused) {
                    isPaused = true;
                    if (getActivity() == null) return;
                    getActivity().runOnUiThread(() -> {
//                        storiesProgressView.pause();
                        setHeadingVisibility(View.GONE);
                    });
                }
            }
            isPaused = false;
            if (getActivity() == null) return;
            if (elapsedTime < 500) return;
            getActivity().runOnUiThread(() -> {
                setHeadingVisibility(View.VISIBLE);
//                storiesProgressView.resume();
            });
        });
    }

    private void runTimer() {
        isDownClick = true;
        createTimer();
        timerThread.start();
    }

    private void stopTimer() {
        isDownClick = false;
    }

    @Override
    public void onDismissed() {
        //TODO: Add listener
        //dismissAllowingStateLoss();
    }

    @Override
    public boolean onShouldInterceptTouchEvent() {
        return false;
    }

    @Override
    public void touchPull() {
        elapsedTime = 0;
        stopTimer();
//        storiesProgressView.pause();
    }

    @Override
    public void touchDown(float xValue, float yValue) {
        this.xValue = xValue;
        this.yValue = yValue;
        if (!isDownClick) {
            runTimer();
        }
    }

    @Override
    public void touchUp() {
        if (isDownClick && elapsedTime < 500) {
            stopTimer();
            if (((int) (height - yValue) <= 0.8 * height)) {

                if ((int) xValue <= (width / 2)) {
                    //Left
                    if (isRtl) {
                        nextStory();
                    } else {
                        previousStory();
                    }
                } else {
                    //Right
                    if (isRtl) {
                        previousStory();
                    } else {
                        nextStory();
                    }
                }
            }
        } else {
            stopTimer();
            setHeadingVisibility(View.VISIBLE);
//            storiesProgressView.resume();
        }
        elapsedTime = 0;
    }

    public void setStoryClickListeners(StoryClickListeners storyClickListeners) {
        this.storyClickListeners = storyClickListeners;
    }

    public void setOnStoryChangedCallback(OnStoryChangedCallback onStoryChangedCallback) {
        this.onStoryChangedCallback = onStoryChangedCallback;
    }

}
