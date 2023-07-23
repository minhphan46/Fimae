package com.example.fimae;

import android.app.Application;
import android.content.Context;

import androidx.media3.database.ExoDatabaseProvider;
import androidx.media3.database.StandaloneDatabaseProvider;
import androidx.media3.datasource.cache.LeastRecentlyUsedCacheEvictor;
import androidx.media3.datasource.cache.SimpleCache;


public class FimaeApp extends Application {
    private static SimpleCache simpleCache;
    private static final long exoPlayerCacheSize = 90 * 1024 * 1024;
    private static LeastRecentlyUsedCacheEvictor leastRecentlyUsedCacheEvictor;
    private static StandaloneDatabaseProvider standaloneDatabaseProvider;

    public static SimpleCache getSimpleCache(Context context) {
        if (simpleCache == null) {
            leastRecentlyUsedCacheEvictor = new LeastRecentlyUsedCacheEvictor(exoPlayerCacheSize);
            standaloneDatabaseProvider = new StandaloneDatabaseProvider(context);
            simpleCache = new SimpleCache(context.getCacheDir(), leastRecentlyUsedCacheEvictor, standaloneDatabaseProvider);
        }
        return simpleCache;
    }
    @Override
    public void onCreate() {
        super.onCreate();

    }
}
