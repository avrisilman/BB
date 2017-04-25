package com.domikado.itaxi.data.service.analytics;

import android.content.Context;

import com.domikado.itaxi.data.entity.ads.Ads;
import com.domikado.itaxi.data.entity.analytics.AdsActionMetric;
import com.domikado.itaxi.data.repository.AnalyticsRepository;

import rx.Observable;
import rx.schedulers.Schedulers;

public class AnalyticsAgent {

    private final Context mContext;

    public AnalyticsAgent(Context mContext) {
        this.mContext = mContext;
    }

    public void reportScreen(String screen) {
        reportScreen(screen, true);
    }

    public void reportScreen(String screen, boolean isVisible) {
        AnalyticsRepository.recordScreenMetric(mContext, screen, isVisible);
    }

    public void reportNewsClick(long newsId) {
        AnalyticsRepository.recordNewsMetric(mContext, newsId);
    }

    public void reportCTAClick(long adsId) {
        AnalyticsRepository.recordCTAMetric(mContext, AdsActionMetric.ACTION_CLICK, adsId);
    }

    public void reportCTABounce(long adsId) {
        AnalyticsRepository.recordCTAMetric(mContext, AdsActionMetric.ACTION_BOUNCE, adsId);
    }

    public void reportCTASubmit(long adsId) {
        AnalyticsRepository.recordCTAMetric(mContext, AdsActionMetric.ACTION_SUBMIT, adsId);
    }

//    public Observable<Long> reportAdsStart(Ads ads) {
//        return AnalyticsRepository.recordAdsMetric(mContext, ads.getServerId(), ads.getPlacement(), ads.getType())
//            .subscribeOn(Schedulers.computation());
//    }
    public Observable<Long> reportAdsStart(Ads ads) {
        return AnalyticsRepository.recordAdsMetric(mContext, ads.getServerId(), ads.getPlacement(), ads.getType())
                .subscribeOn(Schedulers.computation());
    }

    public void reportAdsFinished(long id) {
        AnalyticsRepository.completeAdsMetric(id);
    }
}
