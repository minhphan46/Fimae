package com.example.fimae.service;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Objects;

public class UpdateUserActivityTimeService extends Service {
    private static final long INTERVAL = 5 * 60 * 1000; // Thời gian giữa các lần gửi là 5 phút
    private Handler handler;
    private Runnable runnable;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d("UpdateUserActivityTimeService", "onCreate");
        handler = new Handler();
        runnable = new Runnable() {
            @Override
            public void run() {
// Gửi API lên Server để cập nhật thời gian hoạt động của User
                FirebaseFirestore.getInstance().collection("fimaers").document(Objects.requireNonNull(FirebaseAuth.getInstance().getUid())).update("lastActive", FieldValue.serverTimestamp()).addOnCompleteListener(
                        new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if(task.isSuccessful()){
                                    handler.postDelayed(runnable, INTERVAL);
                                    Log.d("UpdateUserActivityTimeService", "Update success");
                                } else {
                                    Log.d("UpdateUserActivityTimeService", "Update failed " + task.getException().getMessage());
                                }
                            }
                        }
                );

            }
        };
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        handler.post(runnable);

        // Trả về START_STICKY để Service được khởi động lại tự động nếu bị hủy bởi hệ điều hành
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d("UpdateUserActivityTimeService", "onDestroy");
        // Hủy bỏ việc gửi API khi Service bị hủy
        handler.removeCallbacks(runnable);
    }
}
