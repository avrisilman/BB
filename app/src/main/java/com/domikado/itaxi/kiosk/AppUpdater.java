package com.domikado.itaxi.kiosk;

import android.content.Context;

import com.domikado.itaxi.Constant;
import com.domikado.itaxi.TaxiApplication;
import com.domikado.itaxi.data.service.analytics.AnalyticsGenerator;
import com.domikado.itaxi.data.service.analytics.AnalyticsUploader;
import com.domikado.itaxi.data.service.analytics.LogUploader;
import com.domikado.itaxi.data.service.content.AdsManager;
import com.domikado.itaxi.data.service.content.NewsManager;
import com.domikado.itaxi.utils.Connectivity;
import com.orhanobut.hawk.Hawk;

import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import timber.log.Timber;

public class AppUpdater {

    @Inject
    Context context;

    @Inject
    AdsManager adsManager;

    @Inject
    NewsManager newsManager;

    @Inject
    ApkInstaller apkInstaller;

    @Inject
    AnalyticsGenerator analyticsGenerator;

    @Inject
    AnalyticsUploader analyticsUploader;

    @Inject
    LogUploader logUploader;

    private Subscription wifiSubscription;
    private Subscription apkSubscription;

    AppUpdater() {
        TaxiApplication.getComponent().inject(this);
    }

    public void start() {
        Timber.i("Service start");
        wifiSubscription = Observable.interval(5, TimeUnit.SECONDS)
            .filter(aLong -> matchRules())
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(aLong -> apkCheck(), Timber::e);
    }

    public void stop() {
        Timber.i("Service stop");

        if (wifiSubscription != null) {
            wifiSubscription.unsubscribe();
        }
        if (apkSubscription != null) {
            apkSubscription.unsubscribe();
        }
    }

    private boolean matchRules() {
        Long lastApkCheck = 0L;
        if (Hawk.get(Constant.DataStore.APK_LAST_CHECK) != null) {
            lastApkCheck = Hawk.get(Constant.DataStore.APK_LAST_CHECK);
        }

        return ((Hawk.get(Constant.DataStore.LAST_SETUP) != null)
            && Connectivity.isConnected(context)
            && !apkInstaller.isRunning()
            && !adsManager.isRunning()
            && !newsManager.isRunning()
            && !analyticsGenerator.isRunning()
            && !analyticsUploader.isRunning()
            && !logUploader.isRunning()
            && (System.currentTimeMillis() > (lastApkCheck + (TaxiApplication.getSettings().getApkCheckInterval() * 1000))));
    }

    private void apkCheck() {
        Timber.i("App checking...");
        apkSubscription = apkInstaller
            .call()
            .subscribe(version -> {
                Timber.i("New App (%s) installed successfully", version);
            }, Timber::e);
    }
}
