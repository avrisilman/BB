package com.domikado.itaxi;

import android.app.Application;

import com.activeandroid.ActiveAndroid;
import com.activeandroid.Configuration;
import com.crashlytics.android.Crashlytics;
import com.domikado.itaxi.data.api.json.ApplicationSettingJson;
import com.domikado.itaxi.data.entity.Session;
import com.domikado.itaxi.data.entity.ads.Ads;
import com.domikado.itaxi.data.entity.ads.AdsResult;
import com.domikado.itaxi.data.entity.ads.CallToAction;
import com.domikado.itaxi.data.entity.ads.Placement;
import com.domikado.itaxi.data.entity.analytics.AdsActionMetric;
import com.domikado.itaxi.data.entity.analytics.AdsMetric;
import com.domikado.itaxi.data.entity.analytics.NewsMetric;
import com.domikado.itaxi.data.entity.analytics.Rating;
import com.domikado.itaxi.data.entity.analytics.ScreenMetric;
import com.domikado.itaxi.data.entity.analytics.SystemMetric;
import com.domikado.itaxi.data.entity.analytics.Upload;
import com.domikado.itaxi.data.entity.analytics.Volume;
import com.domikado.itaxi.data.entity.content.News;
import com.domikado.itaxi.injection.component.ApplicationComponent;
import com.domikado.itaxi.kiosk.PingManager;
import com.domikado.itaxi.utils.CommonUtils;
import com.domikado.itaxi.utils.timber.FileLoggingTree;
import com.facebook.stetho.Stetho;
import com.jakewharton.threetenabp.AndroidThreeTen;
import com.orhanobut.hawk.Hawk;
import com.orhanobut.hawk.HawkBuilder;
import com.squareup.leakcanary.LeakCanary;
import javax.inject.Inject;
import io.fabric.sdk.android.Fabric;
import timber.log.Timber;

public class TaxiApplication extends Application {

    private static ApplicationComponent mComponent;

    @Inject
    PingManager pingManager;

    @Override
    public void onCreate() {
        super.onCreate();
        LeakCanary.install(this);
        Fabric.with(this, new Crashlytics());
        AndroidThreeTen.init(this);

        if (BuildConfig.DEBUG) {
            Timber.plant(new Timber.DebugTree());
            CommonUtils.enableStrictMode();
            Stetho.initializeWithDefaults(this);
        }
        Timber.plant(new FileLoggingTree());

        mComponent = ApplicationComponent.Initializer.init(this);
        mComponent.inject(this);

        setupHawk();
        setupDb();

//        if (BuildConfig.DEBUG) {
//            Timber.plant(new GalgoTree(this, TaxiApplication.getSettings().getOverlayLogLevel()));
//            Timber.plant(new GalgoTree(this, 3));
//        }

        pingManager.start();
    }

    @SuppressWarnings("unchecked")
    private void setupDb() {
        Configuration.Builder config = new Configuration.Builder(this);
        config.addModelClasses(
            Ads.class,
            AdsMetric.class,
            Session.class,
            SystemMetric.class,
            Upload.class,
            Placement.class,
            ScreenMetric.class,

            /* News */
            News.class,
            NewsMetric.class,

            /* Rating */
            Rating.class,

            AdsActionMetric.class,
            Volume.class,
            CallToAction.class,
            AdsResult.class
        );

        config.setDatabaseVersion(1);
        config.setDatabaseName(Constant.DataStore.DB_NAME);
        ActiveAndroid.initialize(config.create());
    }

    private void setupHawk() {
        Hawk.init(this)
            .setEncryptionMethod(HawkBuilder.EncryptionMethod.MEDIUM)
            .setStorage(HawkBuilder.newSharedPrefStorage(this))
            .build();
    }

    public static ApplicationSettingJson getSettings() {
        return Hawk.get(Constant.DataStore.APPLICATION_SETTINGS, ApplicationSettingJson.defaultSetting());
    }

    public static ApplicationComponent getComponent() {
        return mComponent;
    }
}
