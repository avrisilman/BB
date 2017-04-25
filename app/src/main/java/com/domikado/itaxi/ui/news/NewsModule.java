package com.domikado.itaxi.ui.news;

import android.content.Context;

import com.domikado.itaxi.injection.scope.PerView;

import dagger.Module;
import dagger.Provides;

@Module
public class NewsModule {

    @PerView
    @Provides
    NewsListPresenter provideNewsPresenter() {
        return new NewsListPresenter();
    }

    @PerView
    @Provides
    NewsListAdapter provideNewsListAdapter(Context context) {
        return new NewsListAdapter(context);
    }
}
