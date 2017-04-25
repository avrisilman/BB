package com.domikado.itaxi.data.service.analytics;

import android.content.Context;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class AnalyticsModule {

    @Singleton
    @Provides
    AnalyticsAgent provideAnalyticsAgent(Context context) {
        return new AnalyticsAgent(context);
    }

    @Singleton
    @Provides
    AnalyticsGenerator provideReportGenerator(Context context) {
        return new AnalyticsGenerator(context);
    }

    @Singleton
    @Provides
    DeviceInfoCollector provideDeviceInfoCollector(Context context) {
        return new DeviceInfoCollector(context);
    }

    @Singleton
    @Provides
    AnalyticsUploader provideAnalyticsUploader() {
        return new AnalyticsUploader();
    }

    @Singleton
    @Provides
    LogUploader provideLogUploader() {
        return new LogUploader();
    }

    @Singleton
    @Provides
    DataUploader provideDataUploader() {
        return new DataUploader();
    }
}
