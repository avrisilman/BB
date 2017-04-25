package com.domikado.itaxi.kiosk;

import android.view.KeyEvent;

import timber.log.Timber;

public class Locker {

    private static final int THRESHOLD = 5000;

    private int combination[] = {
        KeyEvent.KEYCODE_BACK,
        KeyEvent.KEYCODE_BACK,
        KeyEvent.KEYCODE_BACK,
        KeyEvent.KEYCODE_BACK
    };

    private int mCurrentSequnce = 0;
    private long mLastAction = -1;

    public boolean enterCombination(int key) {
        if (mLastAction == -1)
            mLastAction = System.currentTimeMillis();

        int combinationKey = combination[mCurrentSequnce];

        Timber.i("Combination Key:" + combinationKey);
        Timber.i("Is in session:" + isInSession());

        if (combinationKey == key && isInSession()) {
            if (mCurrentSequnce == combination.length - 1) {
                reset();
                return true;
            }
            mCurrentSequnce++;
        } else
            reset();

        return false;
    }

    private boolean isInSession() {
        return System.currentTimeMillis() - mLastAction <= THRESHOLD;
    }

    private void reset() {
        mLastAction = -1;
        mCurrentSequnce = 0;
    }
}
