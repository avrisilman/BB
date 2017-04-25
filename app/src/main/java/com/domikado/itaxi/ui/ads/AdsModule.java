package com.domikado.itaxi.ui.ads;

import android.content.Context;

import com.domikado.itaxi.data.service.analytics.AnalyticsAgent;
import com.domikado.itaxi.injection.scope.PerView;
import com.domikado.itaxi.ui.ads.banner.BannerAdsPresenter;
import com.domikado.itaxi.ui.ads.cta.CTAPresenter;
import com.domikado.itaxi.ui.ads.popup.PopupPresenter;
import com.domikado.itaxi.ui.ads.video.VideoAdsManager;
import com.domikado.itaxi.ui.ads.video.VideoAdsPresenter;

import dagger.Module;
import dagger.Provides;

@Module
public class AdsModule {

    @PerView
    @Provides
    VideoAdsManager provideAdsVideoManager() {
        return new VideoAdsManager();
    }

    @PerView
    @Provides
    VideoAdsPresenter provideVideoAdsPresenter(Context context, VideoAdsManager manager, AnalyticsAgent analyticsAgent) {
        return new VideoAdsPresenter(context, manager, analyticsAgent);
    }

    @PerView
    @Provides
    BannerAdsPresenter provideBannerAdsPresenter(AnalyticsAgent analyticsAgent) {
        return new BannerAdsPresenter(analyticsAgent);
    }

    @PerView
    @Provides
    CTAPresenter provideCallToActionPresenter(AnalyticsAgent analyticsAgent) {
        return new CTAPresenter(analyticsAgent);
    }

    @PerView
    @Provides
    PopupPresenter providePopupPresenter(AnalyticsAgent analyticsAgent) {
        return new PopupPresenter(analyticsAgent);
    }
}

