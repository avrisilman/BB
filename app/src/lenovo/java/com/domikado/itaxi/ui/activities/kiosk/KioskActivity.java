package com.domikado.itaxi.ui.activities.kiosk;

import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;

import com.domikado.itaxi.TaxiApp;

public class KioskActivity extends UsbSerialActivity {

    private View decorView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TaxiApp.getComponent().inject(this);

        setupMainWindowDisplayMode();
//        if (!isAppInLockTaskMode()) {
//            ServiceUtil.callSystemService(
//                getApplicationContext(),
//                Constant.Operation.SCREEN_PINNING_APPLICATION,
//                BuildConfig.APPLICATION_ID);
//            startLockTask();
//        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        hideSystemUI();
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);

//        ServiceUtil.startUsbSerialService(getApplicationContext());
    }

    @Override
    public void onBackPressed() {}

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) setSystemUiVisibilityMode();
    }

    private void setupMainWindowDisplayMode() {
        decorView = setSystemUiVisibilityMode();
        decorView.setOnSystemUiVisibilityChangeListener(visibility -> setSystemUiVisibilityMode());
    }

    private View setSystemUiVisibilityMode() {
        decorView = getWindow().getDecorView();
        getWindow().getDecorView().setSystemUiVisibility(
            View.SYSTEM_UI_FLAG_LAYOUT_STABLE |
                View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION |
                View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN |
                View.SYSTEM_UI_FLAG_HIDE_NAVIGATION |
                View.SYSTEM_UI_FLAG_FULLSCREEN |
                View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
        );
        return decorView;
    }

    protected void hideSystemUI() {
        decorView = getWindow().getDecorView();
        getWindow().getDecorView().setSystemUiVisibility(
            View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION |
                View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN |
                View.SYSTEM_UI_FLAG_HIDE_NAVIGATION |
                View.SYSTEM_UI_FLAG_FULLSCREEN |
                View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
        );
    }
}
