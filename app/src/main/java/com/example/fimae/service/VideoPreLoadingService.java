package com.example.fimae.service;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.media3.datasource.DataSpec;
import androidx.media3.datasource.DefaultDataSourceFactory;
import androidx.media3.datasource.DefaultHttpDataSource;
import androidx.media3.datasource.HttpDataSource;
import androidx.media3.datasource.cache.CacheDataSource;
import androidx.media3.datasource.cache.CacheWriter;
import androidx.media3.datasource.cache.SimpleCache;

import com.example.fimae.FimaeApp;

import java.util.ArrayList;

public class VideoPreLoadingService extends IntentService {
    private static final String TAG = "VideoPreLoadingService";
    private Context mContext;
    private Job cachingJob;
    private ArrayList<String> videosList;

    private HttpDataSource.Factory httpDataSourceFactory;
    private DefaultDataSourceFactory defaultDataSourceFactory;
    private CacheDataSource cacheDataSourceFactory;
    private SimpleCache simpleCache = FimaeApp.getSimpleCache(this);

    public VideoPreLoadingService() {
        super(VideoPreLoadingService.class.getSimpleName());
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        mContext = getApplicationContext();

        httpDataSourceFactory = new DefaultHttpDataSource.Factory()
                .setAllowCrossProtocolRedirects(true);

        defaultDataSourceFactory = new DefaultDataSourceFactory(
                this, httpDataSourceFactory
        );

        cacheDataSourceFactory = new CacheDataSource.Factory()
                .setCache(simpleCache)
                .setUpstreamDataSourceFactory(httpDataSourceFactory)
                .createDataSource();

        if (intent != null) {
            Bundle extras = intent.getExtras();
            if (extras != null) {
                videosList = extras.getStringArrayList(Constants.VIDEO_LIST);

                if (videosList != null && !videosList.isEmpty()) {
                    preCacheVideo(videosList);
                }
            }
        }
    }

    private void preCacheVideo(ArrayList<String> videosList) {
        String videoUrl = null;
        if (!videosList.isEmpty()) {
            videoUrl = videosList.get(0);
            videosList.remove(0);
        } else {
            stopSelf();
        }

        if (videoUrl != null && !videoUrl.trim().isEmpty()) {
            Uri videoUri = Uri.parse(videoUrl);
            DataSpec dataSpec = new DataSpec(videoUri);

            CacheWriter.ProgressListener progressListener = (requestLength, bytesCached, newBytesCached) -> {
                double downloadPercentage = (bytesCached * 100.0 / requestLength);
                Log.d(TAG, "downloadPercentage " + downloadPercentage + " videoUri: " + videoUri);
            };

            cachingJob = new Job(dataSpec, progressListener);
            cachingJob.start();
        }
    }

    private void cacheVideo(DataSpec dataSpec, CacheWriter.ProgressListener progressListener) {
        try {
            CacheWriter cacheWriter = new CacheWriter(
                    cacheDataSourceFactory,
                    dataSpec,
                    null,
                    progressListener
                    );
            cacheWriter.cache();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (cachingJob != null) {
            cachingJob.cancel();
        }
    }

    private class Job implements Runnable {
        private DataSpec dataSpec;
        private CacheWriter.ProgressListener progressListener;
        private Thread thread;

        Job(DataSpec dataSpec, CacheWriter.ProgressListener progressListener) {
            this.dataSpec = dataSpec;
            this.progressListener = progressListener;
        }

        void start() {
            thread = new Thread(this);
            thread.start();
        }

        void cancel() {
            if (thread != null) {
                thread.interrupt();
            }
        }

        @Override
        public void run() {
            cacheVideo(dataSpec, progressListener);
            preCacheVideo(videosList);
        }
    }
}
