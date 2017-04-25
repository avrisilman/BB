package com.domikado.itaxi.ui.ads;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.ViewTreeObserver;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.domikado.itaxi.R;
import com.domikado.itaxi.TaxiApplication;
import com.domikado.itaxi.data.entity.ads.Placement;
import com.domikado.itaxi.data.repository.AdsRepository;
import com.domikado.itaxi.data.repository.PlacementRepository;
import com.domikado.itaxi.data.SPSession;
import com.domikado.itaxi.data.entity.ads.Ads;
import com.domikado.itaxi.data.repository.SessionRepository;
import com.domikado.itaxi.ui.base.KioskActivity;
import com.domikado.itaxi.utils.KioskUtils;
import com.domikado.itaxi.utils.TokenGenerator;

import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import rx.Observable;
import rx.subscriptions.CompositeSubscription;
import timber.log.Timber;

public class SplashActivity extends KioskActivity {

    @BindView(R.id.imgContent)
    ImageView imgContent;

    @Inject
    TokenGenerator mToken;

    private CompositeSubscription compositeSubscription = new CompositeSubscription();
    private Placement placement;

    public static void start(Context context) {
        context.startActivity(new Intent(context, SplashActivity.class));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getComponent().inject(this);

        placement = PlacementRepository.findByName(Placement.ZONE_SPLASH);
        if (placement == null || !AdsRepository.isAnyAds(Placement.ZONE_SPLASH)) {
            navigateToPremium();
            return;
        }

        setContentView(R.layout.activity_splash);
        ButterKnife.bind(this);

        createSession();
        initContent();
        KioskUtils.setBrightness(getWindow(), KioskUtils.getCurrentBrightnessSetting());
        logScreen();
    }

    @Override
    protected void onDestroy() {
        compositeSubscription.unsubscribe();
        super.onDestroy();
    }

    private void initContent() {
        imgContent.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                imgContent.getViewTreeObserver().removeOnPreDrawListener(this);
                imgContent.getLayoutParams().width = placement.getWidth();
                imgContent.getLayoutParams().height = placement.getHeight();
                return false;
            }
        });

        List<Ads> adses = AdsRepository.getAdsWithPlacement(Placement.ZONE_SPLASH);
        if (!adses.isEmpty()) {
            Ads ads = adses.get(0);
            Glide.with(this)
                .load(ads.getFilepath())
                .into(imgContent);

            int splashTimer = TaxiApplication.getSettings().getSplashScreenDuration();
            compositeSubscription.add(Observable.timer(splashTimer, TimeUnit.SECONDS)
                .subscribe(aLong -> {
                    navigateToPremium();
                }, Timber::e));
        }
    }

    private void createSession() {
        SPSession.setSessionToken(this, mToken.nextString());
        compositeSubscription.add(SessionRepository.startSession(SPSession.getSessionToken(this))
            .subscribe(aLong -> {
                SPSession.setSessionId(this, aLong);
            }));
    }
}
