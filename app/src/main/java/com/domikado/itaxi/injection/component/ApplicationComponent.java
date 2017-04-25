package com.domikado.itaxi.injection.component;

import android.content.Context;

import com.domikado.itaxi.ApplicationModule;
import com.domikado.itaxi.TaxiApplication;
import com.domikado.itaxi.data.DataModule;
import com.domikado.itaxi.data.service.analytics.AnalyticsUploader;
import com.domikado.itaxi.data.service.analytics.DataUploader;
import com.domikado.itaxi.data.service.analytics.LogUploader;
import com.domikado.itaxi.data.service.content.ContentUpdater;
import com.domikado.itaxi.kiosk.ApkInstaller;
import com.domikado.itaxi.kiosk.AppUpdater;
import com.domikado.itaxi.kiosk.AutoShutdown;
import com.domikado.itaxi.kiosk.KioskModule;
import com.domikado.itaxi.ui.UiModule;

import java.util.concurrent.ExecutorService;

import javax.inject.Singleton;

import dagger.Component;
import github.nisrulz.easydeviceinfo.base.EasyDeviceMod;

@Singleton
@Component(modules = {
    ApplicationModule.class,
    KioskModule.class,
    DataModule.class,
})
public interface ApplicationComponent {

    void inject(TaxiApplication application);

    void inject(AnalyticsUploader uploader);
    void inject(LogUploader uploader);
    void inject(ApkInstaller installer);
    void inject(ContentUpdater updater);
    void inject(DataUploader uploader);
    void inject(AutoShutdown shutdown);
    void inject(AppUpdater updater);

    UiComponent plus(UiModule module);

    Context context();
    EasyDeviceMod device();
    ExecutorService executor();

    final class Initializer {
        public static ApplicationComponent init(Context context) {
            return DaggerApplicationComponent.builder()
                .dataModule(new DataModule())
                .applicationModule(new ApplicationModule(context))
                .build();
        }
    }
}
