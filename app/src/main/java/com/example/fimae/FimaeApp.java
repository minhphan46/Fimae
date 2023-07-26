package com.example.fimae;

import android.app.Application;
import android.content.Intent;

import androidx.annotation.NonNull;

import com.example.fimae.activities.SplashActivity;
import com.example.fimae.repository.OneSignalRepo;
import com.google.firebase.auth.FirebaseAuth;
import com.onesignal.OneSignal;


public class FimaeApp extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        OneSignalRepo.init(this);
        FirebaseAuth.getInstance().addAuthStateListener(firebaseAuth -> {
            if(firebaseAuth.getCurrentUser()==null){
                Intent intent = new Intent(getApplicationContext(), SplashActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });
    }
}
