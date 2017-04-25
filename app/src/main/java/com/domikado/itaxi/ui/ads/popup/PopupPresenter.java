package com.domikado.itaxi.ui.ads.popup;

import com.domikado.itaxi.TaxiApplication;
import com.domikado.itaxi.data.entity.ads.Ads;
import com.domikado.itaxi.data.service.analytics.AnalyticsAgent;

public class PopupPresenter {

    private AnalyticsAgent mAnalyticsAgent;

    private long mLastReportId = -1;

    public PopupPresenter(AnalyticsAgent analyticsAgent) {
        mAnalyticsAgent = analyticsAgent;
    }

    public void reportAdsFinish() {
        if (mLastReportId > 0)
            mAnalyticsAgent.reportAdsFinished(mLastReportId);
    }

    public void reportAdsStart(Ads ads) {
        mAnalyticsAgent.reportAdsStart(ads)
                .subscribe(aLong -> {
                    mLastReportId = aLong;
                });
    }

}
