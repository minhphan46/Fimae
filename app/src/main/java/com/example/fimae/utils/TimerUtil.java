package com.example.fimae.utils;

import android.os.CountDownTimer;
import android.util.Log;

import java.util.Timer;
import java.util.TimerTask;

public class TimerUtil {
    private CountDownTimer timerTask;
    private long totalTimeInMillis;

    TimerUtilTask timerUtilTask;

    //miliseconds to mm:ss format, Don't use TimeUnit
    public static String formatMinutes(long millis) {
        String output = "";
        long seconds = millis / 1000;
        long minutes = seconds / 60;
        seconds = seconds % 60;
        output = String.format("%02d:%02d", minutes, seconds);
        return output;
    }





    public TimerUtil(long totalTimeInMillis ,TimerUtilTask task) {
        this.totalTimeInMillis = totalTimeInMillis;
        this.timerUtilTask = task;
        this.timerTask = new CountDownTimer(totalTimeInMillis , 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                long milliseconds = totalTimeInMillis - millisUntilFinished;
                timerUtilTask.onTick(milliseconds);
                Log.d("TimerUtil", "onTick: " + (milliseconds));
            }

            @Override
            public void onFinish() {
                timerUtilTask.onFinish();
            }
        };
    }

    public interface TimerUtilTask{
        void onTick(long totalTimeInMillis);
        void onFinish();
    }

    public void start() {
        if (timerTask != null) {
            timerTask.start();
        }
    }

    /**
     * Stop the timer and cancel the task.
     */
    public void stop() {
        if (timerTask != null) {
            timerTask.cancel();
            timerTask = null;
        }
    }


    /**
     * Get the current interval of the timer.
     *
     * @return The interval in milliseconds.
     */
    public long getTotalTimeInMillis() {
        return totalTimeInMillis;
    }
}
