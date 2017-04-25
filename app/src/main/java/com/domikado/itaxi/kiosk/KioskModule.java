package com.domikado.itaxi.kiosk;

import android.content.Context;

import com.domikado.itaxi.utils.Downloader;
import com.domikado.itaxi.data.api.DomikadoService;
import com.domikado.itaxi.utils.TokenGenerator;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class KioskModule {

    @Singleton
    @Provides
    TokenGenerator provideTokenGenerator() {
        return new TokenGenerator();
    }

    @Singleton
    @Provides
    Locker provideLocker() {
        return new Locker();
    }

    @Singleton
    @Provides
    AutoShutdown provideAutoShutdown() {
        return new AutoShutdown();
    }

    @Singleton
    @Provides
    PingManager providePingManager(DomikadoService service, Context context) {
        return new PingManager(service, context);
    }

    @Singleton
    @Provides
    ApkInstaller provideApkInstaller(DomikadoService service) {
        return new ApkInstaller(service, new Downloader());
    }

    @Singleton
    @Provides
    AppUpdater provideAppUpdater() {
        return new AppUpdater();
    }
}
