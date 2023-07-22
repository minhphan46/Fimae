package com.example.fimae.utils;
import android.os.Handler;
import android.os.SystemClock;
import android.view.View;
import android.view.View.OnClickListener;

public abstract class DoubleClickListener implements View.OnClickListener {
    private static final long DEFAULT_QUALIFICATION_SPAN = 500;
    private long delta;
    private long deltaClick;
    private Handler han = new Handler();
    private boolean isDoubleClick = false;
    private long lastClickTime = 0;

    public DoubleClickListener() {
        delta = DEFAULT_QUALIFICATION_SPAN;
        deltaClick = 0;
    }

    @Override
    public void onClick(View v) {
        long currentTime = SystemClock.elapsedRealtime();
        if (currentTime - lastClickTime < delta) {
            onDoubleClick();
            // Reset the lastClickTime to prevent consecutive double click
            lastClickTime = 0;
        } else {
            lastClickTime = currentTime;
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    // If lastClickTime is not reset by double click,
                    // it means this is a single click event
                    if (lastClickTime != 0) {
                        onSingleClick();
                    }
                }
            }, delta);
        }
    }

    public abstract void onDoubleClick();

    public abstract void onSingleClick();
}