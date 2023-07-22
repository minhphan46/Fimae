package com.example.fimae;

import android.os.Build;
import android.os.Bundle;

import androidx.annotation.RequiresApi;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.media3.common.MediaItem;
import androidx.media3.common.Player;
import androidx.media3.datasource.DataSource;
import androidx.media3.datasource.DefaultHttpDataSource;
import androidx.media3.exoplayer.ExoPlayer;
import androidx.media3.exoplayer.smoothstreaming.SsMediaSource;
import androidx.media3.exoplayer.source.MediaSource;
import androidx.media3.ui.PlayerControlView;
import androidx.media3.ui.PlayerView;

import android.view.LayoutInflater;
import android.view.Surface;
import android.view.SurfaceControl;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowMetrics;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.fimae.adapters.MediaSliderAdapter.MediaSliderItem;
import com.example.fimae.adapters.MediaSliderAdapter.MediaSliderItemType;

public class SliderMediaFragment extends Fragment {

    MediaSliderItem item;

    public SliderMediaFragment(MediaSliderItem item) {
        this.item = item;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    Surface videoSurface;
    private SurfaceView surfaceView;
    private ExoPlayer player;
    ImageView imageView;
    private SurfaceControl surfaceControl;

    ProgressBar progressBar;
    @RequiresApi(api = Build.VERSION_CODES.R)
    public void setSurfaceViewSize() {
        // // Get the dimensions of the video
        int videoWidth = player.getVideoSize().width;
        int videoHeight = player.getVideoSize().height;
        float videoProportion = (float) videoWidth / (float) videoHeight;

        // Get the width of the screen
        WindowMetrics windowMetrics = getActivity().getWindowManager().getCurrentWindowMetrics();
        int screenWidth = windowMetrics.getBounds().width();
        int screenHeight = windowMetrics.getBounds().height();
        float screenProportion = (float) screenWidth / (float) screenHeight;
        // Get the SurfaceView layout parameters
        android.view.ViewGroup.LayoutParams lp = surfaceView.getLayoutParams();
        if (videoProportion > screenProportion) {
            lp.width = screenWidth;
            lp.height = (int) ((float) screenWidth / videoProportion);
        } else {
            lp.width = (int) (videoProportion * (float) screenHeight);
            lp.height = screenHeight;
        }
        // Commit the layout parameters
        surfaceView.setLayoutParams(lp);
    }
    @RequiresApi(api = Build.VERSION_CODES.Q)
    private void SetUpVideo(){
        player = new ExoPlayer.Builder(getContext()).build();
        MediaItem mediaItem = MediaItem.fromUri(item.getUrl());
        player.setMediaItem(mediaItem);
        playerView.setPlayer(player);
        player.prepare();
        player.addListener(new Player.Listener() {
            @RequiresApi(api = Build.VERSION_CODES.R)
            @Override
            public void onPlaybackStateChanged(int playbackState) {
                //if Ready
                switch (playbackState) {
                    case Player.STATE_BUFFERING:
                        progressBar.setVisibility(View.VISIBLE);
                        playerView.hideController();
                        break;
                    case Player.STATE_READY:
                        progressBar.setVisibility(View.GONE);
                        playerView.showController();
                        break;
                    case Player.STATE_ENDED:
                        playerView.showController();
                        break;
                }
            }
        });
//        surfaceView.getHolder().addCallback(new SurfaceHolder.Callback() {
//            @Override
//            public void surfaceCreated(SurfaceHolder holder) {
//                videoSurface = holder.getSurface();
//                player.setVideoSurface(videoSurface);
//                player.setPlayWhenReady(true);
//            }
//
//            @Override
//            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
//                videoSurface = holder.getSurface();
//                player.setVideoSurface(videoSurface);
//                player.setPlayWhenReady(true);
//            }
//
//            @Override
//            public void surfaceDestroyed(SurfaceHolder holder) {
//                videoSurface = null;
//                player.setVideoSurface(null);
//            }
//        });
        player.setPlayWhenReady(true);
    }
    PlayerView playerView;
    @RequiresApi(api = Build.VERSION_CODES.Q)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_slider_media, container, false);
        imageView = view.findViewById(R.id.slider_image_view);
        ConstraintLayout constraintLayout = view.findViewById(R.id.slider_player_view_container);
        progressBar = view.findViewById(R.id.slider_progress_bar);
        if (item.getType() == MediaSliderItemType.IMAGE) {
            Glide.with(getContext()).load(item.getUrl()).into(imageView);
            constraintLayout.setVisibility(View.GONE);
        } else {
            imageView.setVisibility(View.GONE);
            playerView = view.findViewById(R.id.slider_player_control_view);
            SetUpVideo();

        }
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (player != null) {
            playerView.hideController();
            player.play();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (player != null) {
            player.pause();
            surfaceView = null;
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    @Override
    public void onDestroy() {
        super.onDestroy();
//        if (surfaceControl != null) {
//            surfaceControl.release();
//            surfaceControl = null;
//        }

        if (player != null) {
            player.release();
            player = null;
        }
    }
}