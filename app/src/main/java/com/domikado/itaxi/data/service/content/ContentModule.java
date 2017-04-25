package com.domikado.itaxi.data.service.content;

import android.content.Context;

import com.domikado.itaxi.data.api.DomikadoService;
import com.domikado.itaxi.utils.Downloader;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class ContentModule {

    @Singleton
    @Provides
    AdsManager provideAdsManager(DomikadoService service) {
        return new AdsManager(service, new Downloader());
    }

    @Singleton
    @Provides
    NewsManager provideNewsManager(DomikadoService service) {
        return new NewsManager(service, new Downloader());
    }

    @Singleton
    @Provides
    SettingManager provideSettingManager(Context context, DomikadoService service) {
        return new SettingManager(context, service);
    }

    @Singleton
    @Provides
    ContentUpdater provideContentUpdater() {
        return new ContentUpdater();
    }
}
