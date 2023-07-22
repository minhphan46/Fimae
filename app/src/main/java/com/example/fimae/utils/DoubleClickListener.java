package com.example.fimae.utils;
import android.os.Handler;
import android.os.SystemClock;
import android.view.View;
import android.view.View.OnClickListener;

public abstract class DoubleClickListener implements View.OnClickListener {
    private static final long DEFAULT_QUALIFICATION_SPAN = 200;
    private long delta;
    private long deltaClick;
    private Handler han = new Handler();
    private boolean isDoubleClick = false;

    public DoubleClickListener() {
        delta = DEFAULT_QUALIFICATION_SPAN;
        deltaClick = 0;
    }

    @Override
    public void onClick(View v) {
        han.removeCallbacksAndMessages(null);

        if (isDoubleClick) {
            isDoubleClick = false;
            han.removeCallbacksAndMessages(null);
            onDoubleClick();
        } else {
            isDoubleClick = true;
            han.postDelayed(new Runnable() {
                @Override
                public void run() {
                    isDoubleClick = false;
                    onSingleClick();
                }
            }, delta);
        }

        deltaClick = SystemClock.elapsedRealtime();
    }

    public abstract void onDoubleClick();

    public abstract void onSingleClick();
}