package com.domikado.itaxi.data.service.analytics;

import android.content.Context;

import com.domikado.itaxi.Constant;
import com.domikado.itaxi.TaxiApplication;
import com.domikado.itaxi.data.RequestHistory;
import com.domikado.itaxi.data.repository.SessionRepository;
import com.domikado.itaxi.utils.Connectivity;
import com.orhanobut.hawk.Hawk;

import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import timber.log.Timber;

public class DataUploader {

    @Inject
    Context context;

    @Inject
    AnalyticsGenerator analyticsGenerator;

    @Inject
    AnalyticsUploader analyticsUploader;

    @Inject
    LogUploader logUploader;

    private Subscription wifiSubscription;
    private Subscription analyticsSubscription;
    private Subscription logSubscription;

    public DataUploader() {
        TaxiApplication.getComponent().inject(this);
    }

    public void start() {
        Timber.i("Service start");
        wifiSubscription = Observable.interval(5, TimeUnit.SECONDS)
            .filter(connect -> Connectivity.isConnected(context))
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(s -> {
                if (Hawk.get(Constant.DataStore.LAST_SETUP) != null) {
                    analyticsUpload();
                    logUpload();
                }
            }, Timber::e);
    }

    public void stop() {
        Timber.i("Service stop");
        
        if (wifiSubscription != null) {
            wifiSubscription.unsubscribe();
        }
        if (analyticsSubscription != null) {
            analyticsSubscription.unsubscribe();
        }
        if (logSubscription != null) {
            logSubscription.unsubscribe();
        }
    }

    private void analyticsUpload() {
        if (SessionRepository.isAnySession() && !analyticsGenerator.isRunning()) {
            Timber.i("DB checking...");
            analyticsSubscription = analyticsGenerator.generate()
                .subscribe(i -> {
                    Timber.i("Analytics data generated!");
                }, Timber::e);
        }

        Long analyticsLastUpload = 0L;
        if (Hawk.get(Constant.DataStore.ANALYTICS_LAST_UPLOAD) != null) {
            analyticsLastUpload = ((RequestHistory) Hawk.get(Constant.DataStore.ANALYTICS_LAST_UPLOAD)).getTimestamp();
        }
        if (!analyticsUploader.isRunning() &&
            (System.currentTimeMillis() > (analyticsLastUpload + (TaxiApplication.getSettings().getAnalyticsAutoUploadInterval() * 1000)))) {

            analyticsSubscription = analyticsUploader.call()
                .subscribe(i -> {
                    Timber.i("Analytics data uploaded!");
                }, Timber::e);
        }
    }

    private void logUpload() {
        Long logLastUpload = 0L;
        if (Hawk.get(Constant.DataStore.LOG_LAST_UPLOAD) != null) {
            logLastUpload = ((RequestHistory) Hawk.get(Constant.DataStore.LOG_LAST_UPLOAD)).getTimestamp();
        }
        if (!logUploader.isRunning() &&
            (System.currentTimeMillis() > (logLastUpload + (TaxiApplication.getSettings().getLogAutoUploadInterval() * 1000)))) {

            logSubscription = logUploader.call()
                .subscribe(i -> {
                    Timber.i("Log data uploaded!");
                }, Timber::e);
        }
    }
}
