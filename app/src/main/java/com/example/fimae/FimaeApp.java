package com.example.fimae;

import android.app.Application;

import com.example.fimae.repository.OneSignalRepo;
import com.onesignal.OneSignal;


public class FimaeApp extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        OneSignalRepo.init(this);
    }
}
