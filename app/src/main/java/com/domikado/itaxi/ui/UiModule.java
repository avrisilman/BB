package com.domikado.itaxi.ui;

import com.domikado.itaxi.ui.ads.AdsModule;
import com.domikado.itaxi.ui.news.NewsModule;

import dagger.Module;

@Module(includes = {
    AdsModule.class,
    NewsModule.class
})
public class UiModule {
}
