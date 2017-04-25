package com.domikado.itaxi.ui.ads;

import android.content.Context;
import android.content.Intent;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.v4.view.ViewCompat;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import com.domikado.itaxi.R;
import com.domikado.itaxi.data.entity.ads.Ads;
import com.domikado.itaxi.data.entity.ads.Placement;
import com.domikado.itaxi.data.repository.AdsRepository;
import com.domikado.itaxi.data.repository.PlacementRepository;
import com.domikado.itaxi.events.EventCallToAction;
import com.domikado.itaxi.events.EventInvisible;
import com.domikado.itaxi.events.EventPause;
import com.domikado.itaxi.events.EventPopupQuiz;
import com.domikado.itaxi.events.EventPopupQuizDisappear;
import com.domikado.itaxi.events.EventStop;
import com.domikado.itaxi.events.EventUnpause;
import com.domikado.itaxi.events.EventVisible;
import com.domikado.itaxi.ui.ads.cta.CTAFragment;
import com.domikado.itaxi.ui.ads.popup.PopupPresenter;
import com.domikado.itaxi.ui.ads.popup.PopupQuiz;
import com.domikado.itaxi.ui.base.BaseFragment;
import com.domikado.itaxi.ui.base.KioskActivity;
import com.domikado.itaxi.ui.common.ControlFragment;
import com.domikado.itaxi.ui.menu.MenuFragment;
import com.domikado.itaxi.ui.pin.PinDialogListener;
import com.domikado.itaxi.utils.CommonUtils;
import com.domikado.itaxi.utils.EBus;
import com.domikado.itaxi.utils.KioskUtils;
import org.greenrobot.eventbus.Subscribe;
import java.util.concurrent.TimeUnit;
import javax.inject.Inject;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;

public class MainActivity extends KioskActivity
    implements PinDialogListener, BaseFragment.OnHomeUpSelectedListener {

    @BindView(R.id.main_container)
    View mainContainer;

    @BindView(R.id.side_container)
    View sideContainer;

    @BindView(R.id.bottom_container)
    View bottomContainer;

    @BindView(R.id.hide_container)
    View mOverlay;

    @Inject
    PopupPresenter presenter;

    private View mRootView;
    private int currentHeight;
    Point point;

    private Subscription subscription;

    public static void start(Context context) {
        Intent intent = new Intent(context, MainActivity.class).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getComponent().inject(this);

        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        initPlacement();
        initLayout();

        point = new Point();
        ((WindowManager) getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getSize(point);

        mRootView = ((ViewGroup) findViewById(android.R.id.content)).getChildAt(0);
        mRootView.getViewTreeObserver().addOnGlobalLayoutListener(() -> {
            hideSystemUI();

            Rect r = new Rect();
            mRootView.getWindowVisibleDisplayFrame(r);

            int heightDiff = mRootView.getRootView().getHeight() - (r.bottom - r.top);
            if (heightDiff > 200 * 1.5) {
                currentHeight = point.y - heightDiff;
            } else {
                currentHeight = point.y - bottomContainer.getHeight();
            }

            if (sideContainer.getLayoutParams().height != currentHeight) {
                sideContainer.getLayoutParams().height = currentHeight;
                sideContainer.requestLayout();
            }
        });

        KioskUtils.setBrightness(getWindow(), KioskUtils.getCurrentBrightnessSetting());
        logScreen();


    }

    @Override
    protected void onResume() {
        super.onResume();
        EBus.register(this);
        startPopupObserver();
    }

    @Override
    protected void onPause() {
        EBus.unregister(this);
        super.onPause();
        if (subscription != null)
            subscription.unsubscribe();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        CommonUtils.hideSoftKeyboard(this);
        return super.onTouchEvent(event);
    }

    @Override
    public void onPinOpened() {
        finish();
    }

    @Override
    public void onHomeUpSelected() {
        getSupportFragmentManager().popBackStack();
    }

    @Subscribe
    public void onEvent(EventCallToAction e) {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.side_container, CTAFragment.newInstance(e.getModel()))
                .addToBackStack(null)
                .commit();
    }

    @Subscribe
    public void onEvent(EventInvisible e) {
        KioskUtils.setBrightness(getWindow(), 0);

        analyticsAgent.reportScreen(getClass().getSimpleName(), false);
        ViewCompat.animate(mOverlay)
                .alpha(1f)
                .withEndAction(() -> {
                    mOverlay.setVisibility(View.VISIBLE);
                    EBus.post(new EventPause());
                });
    }

    @Subscribe
    public void onEvent(EventVisible e) {
        KioskUtils.setBrightness(getWindow(), KioskUtils.getCurrentBrightnessSetting());

        analyticsAgent.reportScreen(getClass().getSimpleName());
        ViewCompat.animate(mOverlay)
                .alpha(0f)
                .withEndAction(() -> {
                    mOverlay.setVisibility(View.GONE);
                    EBus.post(new EventUnpause());
                });
    }

    @Subscribe
    public void onEvent(EventPopupQuiz e) {
        presenter.reportAdsStart(e.getAds());
        PopupQuiz.newInstance(e.getAds()).show(getSupportFragmentManager(), PopupQuiz.TAG);
        EBus.post(new EventPause());
    }

    @Subscribe
    public void onEvent(EventPopupQuizDisappear e) {
        presenter.reportAdsFinish();
        EBus.post(new EventUnpause());
    }
    
    @Subscribe
    public void onEvent(EventStop e) {
        navigateToFare(e.getFare());
    }

    @OnClick(R.id.hide_container)
    public void onOverlayClick() {
        EBus.post(new EventVisible());
    }

    private void initLayout() {
        mainContainer.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                mainContainer.getViewTreeObserver().removeOnPreDrawListener(this);

                Placement placement = PlacementRepository.findByName(Placement.ZONE_MAIN);

                if (placement != null) {
                    int mainWidth = placement.getWidth();
                    mainContainer.getLayoutParams().width = mainWidth;
                    mainContainer.requestLayout();

                    int sideWidth = point.x - mainWidth;
                    sideContainer.getLayoutParams().width = sideWidth;
                    sideContainer.getLayoutParams().height = point.y - bottomContainer.getHeight();
                    sideContainer.requestLayout();

                    bottomContainer.getLayoutParams().width = sideWidth;
                    bottomContainer.requestLayout();

                    LeftAdsFragment leftAdsFragment = (LeftAdsFragment) getSupportFragmentManager()
                            .findFragmentById(R.id.main_container);
                    leftAdsFragment.setResolution(PlacementRepository.findAll());
                }
                return false;
            }
        });
    }

    private void initPlacement() {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.main_container, LeftAdsFragment.newInstance())
                .replace(R.id.side_container, MenuFragment.newInstance())
                .replace(R.id.bottom_container, ControlFragment.newInstance(ControlFragment.FULL))
                .commit();
    }



    private void startPopupObserver() {
        subscription = Observable
                .interval(1, TimeUnit.SECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::triggerPopup);
    }

    public void triggerPopup(long tick) {
        Log.d("TICK === ", String.valueOf(tick));
        for(int i = 0; i < AdsRepository.getAdsPopup().size(); i++){
            if(tick == AdsRepository.getAdsPopup().get(i).getTime()){
                Ads ads = AdsRepository.getAdsPopup().get(i);
                EBus.post(new EventPopupQuiz(ads));
            }
        }
    }

}