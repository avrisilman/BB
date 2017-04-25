package com.domikado.itaxi;

import android.content.Context;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import github.nisrulz.easydeviceinfo.base.EasyBatteryMod;
import github.nisrulz.easydeviceinfo.base.EasyDeviceMod;

@Module
public class ApplicationModule {

    private final Context context;

    public ApplicationModule(Context context) {
        this.context = context;
    }

    @Singleton
    @Provides
    Context provideContext() {
        return context;
    }

    @Singleton
    @Provides
    ExecutorService provideExecutorService() {
        return Executors.newSingleThreadExecutor();
    }

    @Singleton
    @Provides
    EasyDeviceMod provideEasyDeviceMod() {
        return new EasyDeviceMod(context);
    }

    @Singleton
    @Provides
    EasyBatteryMod provideEasyBatteryMod() {
        return new EasyBatteryMod(context);
    }
}
