package com.domikado.itaxi.ui.ads.banner;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

class CancelableTimerTask {

    interface TickListener {
        void onTick();
    }

    private static final int SLIDER_DELAY = 5000;
    private TickListener mSliderListener;
    private int mDelay = SLIDER_DELAY;
    private boolean isLoop;

    CancelableTimerTask(int mDelay, boolean isLoop) {
        this.mDelay = mDelay;
        this.isLoop = isLoop;
    }

    private Handler mHandler = new Handler(Looper.getMainLooper());
    private Runnable mSlideTask = new Runnable() {
        @Override
        public void run() {
            if (mSliderListener != null)
                mSliderListener.onTick();

            if (isLoop)
                reset();
        }
    };

    void start() {
        mHandler.postDelayed(mSlideTask, mDelay);
    }

    void stop() {
        mHandler.removeCallbacks(mSlideTask);
    }

    private void reset() {
        stop();
        start();
    }

    void setListener(TickListener listener) {
        mSliderListener = listener;
    }
}

