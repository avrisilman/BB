package com.domikado.itaxi.kiosk;

import android.content.Context;
import android.content.Intent;

import com.domikado.itaxi.Constant;
import com.domikado.itaxi.TaxiApplication;
import com.domikado.itaxi.data.service.analytics.AnalyticsGenerator;
import com.domikado.itaxi.data.service.analytics.AnalyticsUploader;
import com.domikado.itaxi.data.service.content.AdsManager;
import com.domikado.itaxi.data.service.content.NewsManager;
import com.domikado.itaxi.utils.Connectivity;
import com.orhanobut.hawk.Hawk;

import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import github.nisrulz.easydeviceinfo.base.EasyBatteryMod;
import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import timber.log.Timber;

public class AutoShutdown {

    @Inject
    Context context;

    @Inject
    EasyBatteryMod battery;

    @Inject
    AdsManager adsManager;

    @Inject
    NewsManager newsManager;

    @Inject
    AnalyticsGenerator statsGenerator;

    @Inject
    AnalyticsUploader uploader;

    @Inject
    ApkInstaller apkInstaller;

    private Subscription subscription;

    AutoShutdown() {
        TaxiApplication.getComponent().inject(this);
    }

    public void stop() {
        if (subscription != null && subscription.isUnsubscribed()) {
            subscription.unsubscribe();
        }
    }

    public void start() {
        if (subscription == null) {
            subscription = Observable.interval(10, TimeUnit.SECONDS)
                .filter(aLong -> matchRules())
                .doOnNext(aLong -> {
                    Timber.i("Shutdown device");

                    Intent intent = new Intent();
                    intent.setAction(Constant.Operation.DEVICE_SHUTDOWN);
                    context.sendBroadcast(intent);
                    // ServiceUtil.callSystemService(context, Constant.Operation.DEVICE_SHUTDOWN, BuildConfig.APPLICATION_ID);
                })
                .doOnError(Timber::e)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe();
        }
    }

    private boolean matchRules() {
        return
            !battery.isDeviceCharging() &&
                Connectivity.isWifiConnected(context) &&
                !apkInstaller.isRunning() &&
                !adsManager.isRunning() &&
                !newsManager.isRunning() &&
                !statsGenerator.isRunning() &&
                !uploader.isRunning() &&
                (TaxiApplication.getSettings().getIdleTimeBeforeShutdown() > 0) &&
                (Hawk.get(Constant.DataStore.DEVICE_ON_BATTERY_TIME) != null) &&
                (System.currentTimeMillis() > ((long) Hawk.get(Constant.DataStore.DEVICE_ON_BATTERY_TIME)
                    + (TaxiApplication.getSettings().getIdleTimeBeforeShutdown() * 1000)));

    }
}
