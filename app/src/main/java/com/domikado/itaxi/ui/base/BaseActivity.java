package com.domikado.itaxi.ui.base;

import android.app.ActivityManager;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;

import com.domikado.itaxi.TaxiApplication;
import com.domikado.itaxi.data.service.analytics.AnalyticsAgent;
import com.domikado.itaxi.data.service.analytics.DeviceInfoCollector;
import com.domikado.itaxi.injection.HasComponent;
import com.domikado.itaxi.injection.component.UiComponent;
import com.domikado.itaxi.ui.ads.FareActivity;
import com.domikado.itaxi.ui.ads.MainActivity;
import com.domikado.itaxi.ui.ads.PremiumActivity;
import com.domikado.itaxi.ui.ads.SplashActivity;
import com.domikado.itaxi.ui.ads.VacantActivity;
import com.trello.rxlifecycle.components.support.RxAppCompatActivity;

import java.math.BigDecimal;

import javax.inject.Inject;

public class BaseActivity extends RxAppCompatActivity implements HasComponent<UiComponent> {

    @Inject
    protected AnalyticsAgent analyticsAgent;

/***    Move to the functionality to  Ping Manager
*/
//    @Inject
//    DeviceInfoCollector deviceInfoCollector;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getComponent().inject(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
//        deviceInfoCollector.start();
    }

    @Override
    protected void onPause() {
//        deviceInfoCollector.stop();
        super.onPause();
    }

    protected boolean isAppInLockTaskMode() {
        ActivityManager activityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            int lockTaskMode = activityManager.getLockTaskModeState();
            return lockTaskMode != ActivityManager.LOCK_TASK_MODE_NONE;
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP &&
            Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return activityManager.isInLockTaskMode();
        } else {
            return false;
        }
    }

    protected void logScreen() {
        analyticsAgent.reportScreen(getClass().getSimpleName());
    }

    public void navigateToFare(BigDecimal fare) {
        FareActivity.start(this, fare);
        finish();
    }

    public void navigateToMain() {
        MainActivity.start(this);
        finish();
    }

    public void navigateToVacant() {
        VacantActivity.start(this);
        finish();
    }

    public void navigateToSplash() {
        SplashActivity.start(this);
        finish();
    }

    public void navigateToPremium() {
        PremiumActivity.start(this);
        finish();
    }

    @Override
    public UiComponent getComponent() {
        return UiComponent.Initializer.init(TaxiApplication.getComponent());
    }
}
