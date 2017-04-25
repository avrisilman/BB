package com.domikado.itaxi.ui.ads.banner;

import android.view.View;

import com.domikado.itaxi.TaxiApplication;
import com.domikado.itaxi.data.entity.ads.Ads;
import com.domikado.itaxi.data.entity.ads.Placement;
import com.domikado.itaxi.data.repository.AdsRepository;
import com.domikado.itaxi.data.service.analytics.AnalyticsAgent;
import com.domikado.itaxi.events.EventCallToAction;
import com.domikado.itaxi.events.EventPause;
import com.domikado.itaxi.events.EventUnpause;
import com.domikado.itaxi.utils.EBus;
import com.hannesdorfmann.mosby.mvp.MvpBasePresenter;
import org.greenrobot.eventbus.Subscribe;

import java.util.List;
import java.util.concurrent.TimeUnit;

public class BannerAdsPresenter extends MvpBasePresenter<BannerAdsView>
    implements CancelableTimerTask.TickListener {

    private AnalyticsAgent mAnalyticsAgent;
    private CancelableTimerTask mTimerTask;

    private long mLastReportId = -1;

    public BannerAdsPresenter(AnalyticsAgent analyticsAgent) {
        mAnalyticsAgent = analyticsAgent;

        int interval = TaxiApplication.getSettings().getBannerSlideInterval();
        mTimerTask = new CancelableTimerTask((int) TimeUnit.SECONDS.toMillis(interval), true);
        mTimerTask.setListener(this);
       // mTimerTask.start();
    }

    void registerBus() {
        EBus.register(this);
    }

    void unregisterBus() {
        EBus.unregister(this);
    }

    void startTimer() {
        if (mTimerTask != null)
            mTimerTask.start();
    }

    void stopTimer() {
        if (mTimerTask != null)
            mTimerTask.stop();
    }

    void transformPage(View view, float position) {
        if (position == 0.0F) {
            view.setAlpha(1.0F);
            view.setVisibility(View.VISIBLE);
        } else {
            view.setAlpha(1.0F - Math.abs(position));
            view.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onTick() {
        if (isViewAttached()) {
            getView().onSlide();
        }
    }

    private void reportAdsFinish() {
        if (mLastReportId > 0)
            mAnalyticsAgent.reportAdsFinished(mLastReportId);
    }

    private void reportAdsStart(Ads ads) {
        mAnalyticsAgent.reportAdsStart(ads)
            .subscribe(aLong -> {
                mLastReportId = aLong;
            });
    }

    void reportAds(Ads ads) {
        reportAdsFinish();
        reportAdsStart(ads);
    }

    void clickAds(Ads ads) {
        if (ads.haveCallToAction()) {
            EBus.post(new EventCallToAction(ads));
            mAnalyticsAgent.reportCTAClick(ads.getServerId());
        }
    }

    @Subscribe
    public void onEvent(EventPause e) {
        stopTimer();
    }

    @Subscribe
    public void onEvent(EventUnpause e) {
        startTimer();
    }
}
