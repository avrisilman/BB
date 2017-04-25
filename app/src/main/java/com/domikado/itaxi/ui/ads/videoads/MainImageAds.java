package com.domikado.itaxi.ui.ads.videoads;

import android.content.Context;
import android.widget.ImageView;
import android.widget.LinearLayout;
import com.bumptech.glide.Glide;
import com.domikado.itaxi.R;
import com.domikado.itaxi.TaxiApplication;
import com.domikado.itaxi.data.entity.ads.Ads;
import com.domikado.itaxi.data.repository.AdsRepository;
import com.domikado.itaxi.data.service.analytics.AnalyticsAgent;
import com.domikado.itaxi.events.EventImageAds;
import com.domikado.itaxi.events.EventVideoAdsCompletion;
import com.domikado.itaxi.injection.component.UiComponent;
import com.domikado.itaxi.utils.EBus;
import org.greenrobot.eventbus.Subscribe;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import timber.log.Timber;

/**
 * Created by domikado on 3/8/17.
 */

public class MainImageAds extends LinearLayout{

    @BindView(R.id.imageAds)
    ImageView imageAds;
    private Unbinder unbinder;
    private Ads ads;
    private Subscription subscription;
    @Inject
    AnalyticsAgent mAnalyticsAgent;
    private long metricsId = -1;


    public MainImageAds(Context context) {
        super(context);
        inflate(context, R.layout.view_category_video_image, this);

        UiComponent.Initializer
                .init(TaxiApplication.getComponent())
                .inject(this);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        EBus.register(this);
        unbinder = ButterKnife.bind(this);
        startTimer();
        mAnalyticsAgent.reportAdsStart(ads)
                .subscribe(aLong -> {
                    metricsId = aLong;
                });

        String url = "file://" + AdsRepository.getInstance(ads.getId()).getFilepath();
        Glide.with(getContext())
                .load(url)
                .crossFade()
                .into(imageAds);

    }

    @Override
    protected void onDetachedFromWindow() {
        unbinder.unbind();
        EBus.unregister(this);
        super.onDetachedFromWindow();
        if (subscription != null)
            subscription.unsubscribe();
    }

    public void setAds(Ads ads) {
        this.ads = ads;
    }

    @Subscribe
    public void onComplete(long tick) {
        mAnalyticsAgent.reportAdsFinished(metricsId);
        long duration = ads.getDuration();
        long timer = duration - tick;
        Timber.i(String.valueOf(timer));
        if(tick == ads.getDuration()) {
            EBus.post(new EventVideoAdsCompletion());
        }
    }

    private void startTimer(){
        subscription = Observable
                .interval(1, TimeUnit.SECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::onComplete);
    }


}
