package com.domikado.itaxi.ui.ads;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewCompat;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterViewFlipper;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SlidingDrawer;
import android.widget.Toast;

import com.domikado.itaxi.R;
import com.domikado.itaxi.data.entity.ads.Ads;
import com.domikado.itaxi.data.entity.ads.Placement;
import com.domikado.itaxi.data.repository.AdsRepository;
import com.domikado.itaxi.data.service.analytics.AnalyticsAgent;
import com.domikado.itaxi.events.EventInvisible;
import com.domikado.itaxi.events.EventPause;
import com.domikado.itaxi.events.EventStop;
import com.domikado.itaxi.events.EventUnpause;
import com.domikado.itaxi.events.EventVideoCompletion;
import com.domikado.itaxi.events.EventVisible;
import com.domikado.itaxi.ui.ads.premium.VideoAdsAdapter;
import com.domikado.itaxi.ui.base.KioskActivity;
import com.domikado.itaxi.ui.common.ControlFragmentPremium;
import com.domikado.itaxi.utils.EBus;
import com.domikado.itaxi.utils.KioskUtils;

import org.greenrobot.eventbus.Subscribe;

import java.util.Collections;
import java.util.List;
import java.util.Random;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class PremiumActivity extends KioskActivity {

    @BindView(R.id.video_flipper)
    AdapterViewFlipper mVideoFlipper;

    @BindView(R.id.hide_container)
    View mOverlay;

    @BindView(R.id.bottom_control)
    View bottomcontrol;

    @BindView(R.id.slide)
    SlidingDrawer slide;

    @BindView(R.id.handle)
    ImageView handle;

    @Inject
    AnalyticsAgent mAnalyticsAgent;

    VideoAdsAdapter adapter;

    public static void start(Context context) {
        context.startActivity(new Intent(context, PremiumActivity.class));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getComponent().inject(this);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_premium);
        ButterKnife.bind(this);

        slide.setOnDrawerOpenListener(this::onNextOpen);
        slide.setOnDrawerCloseListener(this::onNextClose);
        handle.setBackgroundResource(R.drawable.left);

        KioskUtils.setVolumeDefault(getApplicationContext());

        setControlBar();
        initContent();
        KioskUtils.setBrightness(getWindow(), KioskUtils.getCurrentBrightnessSetting());
        logScreen();
    }

    private void onNextOpen() {
        handle.setBackgroundResource(R.drawable.right);
    }

    private void onNextClose() {
        handle.setBackgroundResource(R.drawable.left);
    }

    @Override
    protected void onResume() {
        super.onResume();
        EBus.register(this);
    }

    @Override
    protected void onPause() {
        EBus.unregister(this);
        super.onPause();
    }

    @Subscribe
    public void onEvent(EventStop e) {
        navigateToFare(e.getFare());
    }

    private void initContent() {
        if (!AdsRepository.isAnyAds(Placement.ZONE_PREMIUM)) {
            navigateToMain();
            return;
        }

        List<Ads> ads = AdsRepository.getAdsWithPlacement(Placement.ZONE_PREMIUM);
        Collections.shuffle(ads, new Random(System.nanoTime()));
        if (!ads.isEmpty()) {
            adapter = new VideoAdsAdapter(this);
            adapter.setAdses(ads);
            mVideoFlipper.setAdapter(adapter);
        }
    }

    @Subscribe
    public void onVideoCompletion(EventVideoCompletion e) {
        if (mVideoFlipper.getDisplayedChild() == adapter.getCount() - 1) {
            navigateToMain();
        } else {
            mVideoFlipper.showNext();
        }
    }

    @Subscribe
    public void onEvent(EventInvisible e) {
        mAnalyticsAgent.reportScreen(getClass().getSimpleName(), false);
        ViewCompat.animate(mOverlay)
            .alpha(1f)
            .withEndAction(() -> {
                mOverlay.setVisibility(View.VISIBLE);
                EBus.post(new EventPause());
            });
    }

    @Subscribe
    public void onEvent(EventVisible e) {
        mAnalyticsAgent.reportScreen(getClass().getSimpleName());
        ViewCompat.animate(mOverlay)
            .alpha(0f)
            .withEndAction(() -> {
                mOverlay.setVisibility(View.GONE);
                EBus.post(new EventUnpause());
            });
    }

    @OnClick(R.id.hide_container)
    public void onOverlayClick() {
        EBus.post(new EventVisible());
    }

    private void setControlBar() {
        getSupportFragmentManager().beginTransaction()
            .replace(R.id.bottom_control, ControlFragmentPremium.newInstance(ControlFragmentPremium.FULL))
            .commit();
    }
}
