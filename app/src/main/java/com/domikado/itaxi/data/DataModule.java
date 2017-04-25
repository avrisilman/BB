package com.domikado.itaxi.data;

import com.domikado.itaxi.data.service.analytics.AnalyticsModule;
import com.domikado.itaxi.data.service.content.ContentModule;
import com.domikado.itaxi.data.api.ApiModule;

import dagger.Module;

@Module(includes = {
    ApiModule.class,
    ContentModule.class,
    AnalyticsModule.class
})
public class DataModule {
}
