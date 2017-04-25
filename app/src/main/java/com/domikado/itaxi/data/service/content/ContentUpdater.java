package com.domikado.itaxi.data.service.content;

import android.content.Context;
import android.util.Log;

import com.domikado.itaxi.Constant;
import com.domikado.itaxi.TaxiApplication;
import com.domikado.itaxi.data.RequestHistory;
import com.domikado.itaxi.utils.Connectivity;
import com.orhanobut.hawk.Hawk;

import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import timber.log.Timber;

public class ContentUpdater {

    @Inject
    Context context;

    @Inject
    SettingManager settingManager;

    @Inject
    AdsManager adsManager;

    @Inject
    NewsManager newsManager;

    private Subscription wifiSubscription;
    private Subscription settingSubscription;
    private Subscription newsSubscription;
    private Subscription adsSubscription;

    public ContentUpdater() {
        TaxiApplication.getComponent().inject(this);
    }

    public void start() {
        Log.d("INTERNET CONNECT ==== " , String.valueOf(Connectivity.isConnected(context)));
        Timber.i("Service start");
        wifiSubscription = Observable.interval(5, TimeUnit.SECONDS)
            .filter(connect -> Connectivity.isConnected(context))
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(s -> {
                if (Hawk.get(Constant.DataStore.LAST_SETUP) != null) {
                    settingCheck();
                    adsCheck();
//                    newsCheck();
                }
            }, Timber::e);
    }

    public void stop() {
        Timber.i("Service stop");
        
        if (wifiSubscription != null) {
            wifiSubscription.unsubscribe();
        }
        if (settingSubscription != null) {
            settingSubscription.unsubscribe();
        }
        if (adsSubscription != null) {
            adsSubscription.unsubscribe();
        }
        if (newsSubscription != null) {
            newsSubscription.unsubscribe();
        }
    }

    private void settingCheck() {
        if (Hawk.get(Constant.DataStore.LAST_UPDATE_SETTINGS) != null) {
            Long settingsLastUpdate = ((RequestHistory) Hawk.get(Constant.DataStore.LAST_UPDATE_SETTINGS)).getTimestamp();
            if (!settingManager.isRunning() &&
                (System.currentTimeMillis() > (settingsLastUpdate + (TaxiApplication.getSettings().getSettingAutoUpdateInterval() * 1000)))) {

                Timber.i("Setting checking...");
                settingSubscription = settingManager.update()
                    .subscribe(i -> {
                        Timber.i("Setting updated!");
                    }, Timber::e);
            }
        }
    }

    private void adsCheck() {
        if (Hawk.get(Constant.DataStore.ADS_LAST_CHECK) != null) {
            Long adsLastChecked = Hawk.get(Constant.DataStore.ADS_LAST_CHECK);
            if (!adsManager.isRunning() &&
                (System.currentTimeMillis() > (adsLastChecked + (TaxiApplication.getSettings().getAdsAutoUpdateInterval() * 1000)))) {

                Timber.i("Ads checking...");
                adsSubscription = adsManager.sync()
                    .subscribe(i -> {
                        Timber.i("Ads updated!");
                    }, Timber::e);
            }
        }
    }

//    private void newsCheck() {
//        if (Hawk.get(Constant.DataStore.NEWS_LAST_CHECK) != null) {
//            Long newsLastChecked = Hawk.get(Constant.DataStore.NEWS_LAST_CHECK);
//            if (!newsManager.isRunning() &&
//                (System.currentTimeMillis() > (newsLastChecked + (TaxiApplication.getSettings().getNewsAutoUpdateInterval() * 1000)))) {
//
//                Timber.i("News checking...");
//                newsSubscription = newsManager.sync()
//                    .subscribe(i -> {
//                        Timber.i("News updated!");
//                    }, Timber::e);
//            }
//        }
//    }
}
