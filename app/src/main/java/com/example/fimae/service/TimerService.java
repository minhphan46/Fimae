package com.example.fimae.service;

import android.os.CountDownTimer;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.fimae.models.UserInfo;

public class TimerService {
    public ProgressBar mProcessBar;
    public TextView mTvTimer;

    public int TIME_CAN_CALL = 0;
    public int timeSelected = 0;
    public CountDownTimer timeCountDown = null;
    public int timeProgress = 0;
    public long pauseOffSet = 0;
    public boolean isStart = true;

    private IOnTimeUp  iOnTimeUp;

    public interface IOnTimeUp {
        void onTimeUp();
    }

    public TimerService(int timeCanCall, ProgressBar progressBar, TextView textView, IOnTimeUp iOnTimeUp) {
        TIME_CAN_CALL = timeCanCall;
        mProcessBar = progressBar;
        mTvTimer = textView;
        this.iOnTimeUp = iOnTimeUp;
    }

    public void setTimeInit() {
        timeSelected = TIME_CAN_CALL;
        // set text timer
        mTvTimer.setText(formatMinutes(timeSelected));
        // set max processbar
        mProcessBar.setMax(timeSelected);
    }

    public void startTimerSetUp() {
        if(isStart){
            startTimer(pauseOffSet);
            isStart = false;
        }
        else {
            isStart = true;
            timePause();
        }
    }

    public void startTimer(long pauseOffSetL){
        mProcessBar.setProgress(timeProgress);
        timeCountDown = new CountDownTimer((timeSelected* 1000L) - pauseOffSetL*1000, 1000) {
            @Override
            public void onTick(long p0) {
                timeProgress++;
                pauseOffSet = timeSelected- p0/1000;
                mProcessBar.setProgress(timeSelected-timeProgress);
                mTvTimer.setText(formatMinutes(timeSelected - timeProgress));
            }

            @Override
            public void onFinish() {
                resetTime();
                onFinishTimer("Time up");
            }
        }.start();
    }

    public void onFinishTimer(String value) {
        // when time up
        iOnTimeUp.onTimeUp();
    }

    public void timePause() {
        if(timeCountDown != null){
            timeCountDown.cancel();
        }
    }

    public void resetTime() {
        if(timeCountDown != null) {
            timeCountDown.cancel();
            timeProgress = 0;
            timeSelected = 0;
            pauseOffSet = 0;
            timeCountDown = null;
            isStart = true;
            mProcessBar.setProgress(0);
            mTvTimer.setText("00:00");
        }
    }

    public String formatMinutes(int minutes) {
        int hours = minutes / 60;
        int remainingMinutes = minutes % 60;

        String formattedHours = String.format("%02d", hours);
        String formattedMinutes = String.format("%02d", remainingMinutes);

        return formattedHours + ":" + formattedMinutes;
    }

    public void onDestroy() {
        if(timeCountDown != null){
            timeCountDown.cancel();
            timeProgress = 0;
        }
    }
}
