package com.domikado.itaxi.ui.ads.cta;

import com.domikado.itaxi.data.entity.ads.Ads;
import com.domikado.itaxi.data.repository.CTARepository;
import com.domikado.itaxi.data.service.analytics.AnalyticsAgent;
import com.hannesdorfmann.mosby.mvp.MvpNullObjectBasePresenter;

import javax.inject.Inject;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class CTAPresenter extends MvpNullObjectBasePresenter<CTAView> {

    private AnalyticsAgent mAnalyticsAgent;
    private Ads mAds;

    @Inject
    public CTAPresenter(AnalyticsAgent agent) {
        mAnalyticsAgent = agent;
    }

    void setAdsModel(Ads ads) {
        mAds = ads;
    }

    void recordCTABounce() {
        mAnalyticsAgent.reportCTABounce(mAds.getServerId());
    }

    void recordCTASubmit() {
        mAnalyticsAgent.reportCTASubmit(mAds.getServerId());
    }

    void loadCTA(Ads ads) {
        getView().showLoading(false);
        Observable.just(CTARepository.findCTAById(ads.getCallToActionId()))
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(callToActionModel -> {
                getView().setData(callToActionModel);
                getView().showContent();
            }, throwable -> getView().showError(throwable, false));
    }
}
