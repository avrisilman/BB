package com.domikado.itaxi.ui.activities.kiosk;

import android.content.Context;
import android.content.Intent;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.v4.view.ViewCompat;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;

import com.domikado.itaxi.R;
import com.domikado.itaxi.TaxiApp;
import com.domikado.itaxi.data.entity.ads.ResolutionModel;
import com.domikado.itaxi.data.entity.content.News;
import com.domikado.itaxi.events.EventCallToAction;
import com.domikado.itaxi.events.EventInvisible;
import com.domikado.itaxi.events.EventPause;
import com.domikado.itaxi.events.EventStop;
import com.domikado.itaxi.events.EventUnpause;
import com.domikado.itaxi.events.EventVisible;
import com.domikado.itaxi.injection.HasComponent;
import com.domikado.itaxi.injection.component.ActivityComponent;
import com.domikado.itaxi.ui.control.BaseFragment;
import com.domikado.itaxi.ui.control.CTAFragment;
import com.domikado.itaxi.ui.control.ControlFragment;
import com.domikado.itaxi.ui.control.LeftAdsFragment;
import com.domikado.itaxi.ui.control.NewsFragment;
import com.domikado.itaxi.ui.control.NewsListFragment;
import com.domikado.itaxi.ui.pin.PinDialogListener;
import com.domikado.itaxi.ui.views.news.NewsListListener;
import com.domikado.itaxi.utils.CommonUtils;
import com.domikado.itaxi.utils.EBus;
import com.domikado.itaxi.utils.Screens;

import org.greenrobot.eventbus.Subscribe;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import timber.log.Timber;

public class MainActivity extends KioskActivity
    implements PinDialogListener, NewsListListener, BaseFragment.OnHomeUpSelectedListener,
    HasComponent<ActivityComponent> {

    @BindView(R.id.main_container)
    View mainContainer;

    @BindView(R.id.side_container)
    View sideContainer;

    @BindView(R.id.bottom_container)
    View bottomContainer;

    @BindView(R.id.hide_container)
    View mOverlay;

    private View mRootView;
    private int currentHeight;

    public static void start(Context context) {
        Intent intent = new Intent(context, MainActivity.class).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TaxiApp.getComponent().inject(this);

        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        initPlacement();
        initLayout();

        Point point = Screens.getDisplaySize(MainActivity.this);

        mRootView = ((ViewGroup) findViewById(android.R.id.content)).getChildAt(0);
        mRootView.getViewTreeObserver().addOnGlobalLayoutListener(() -> {
            Rect r = new Rect();
            mRootView.getWindowVisibleDisplayFrame(r);

            int heightDiff = mRootView.getRootView().getHeight() - (r.bottom - r.top);
            Timber.i("===");
            Timber.i("diff: %d", heightDiff);
            if (heightDiff > 100) {
                Timber.i("y: %d", point.y);
                currentHeight = point.y - 520;
            } else {
                Timber.i("yy: %d", point.y);
                currentHeight = point.y - 24;
            }

            if (sideContainer.getLayoutParams().height != currentHeight) {
                sideContainer.getLayoutParams().height = currentHeight;
                sideContainer.requestLayout();

            }
        });

        logScreen();
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
    public void onNewsSelected(News news) {
        replaceFragment(NewsFragment.newInstance(news));
    }

    @Override
    public void onHomeUpSelected() {
        getSupportFragmentManager().popBackStack();
    }

    @Override
    public ActivityComponent getComponent() {
        return ActivityComponent.Initializer.inject(TaxiApp.getComponent());
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
        analyticsAgent.reportScreen(getClass().getSimpleName());
        ViewCompat.animate(mOverlay)
            .alpha(0f)
            .withEndAction(() -> {
                mOverlay.setVisibility(View.GONE);
                EBus.post(new EventUnpause());
            });
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

                List<ResolutionModel> resolutions = ResolutionModel.getAll();
                if (resolutions != null && !resolutions.isEmpty()) {
                    ResolutionModel resolutionModel = resolutions.get(0);
                    Point point = Screens.getDisplaySize(MainActivity.this);

                    Timber.d("Size:" + point.x);
                    Timber.d("Size:" + resolutionModel.toString());

                    int resolutionWidth = resolutionModel.getWidth();
                    int sideWidth = point.x - resolutionWidth;

                    bottomContainer.getLayoutParams().width = sideWidth;
                    bottomContainer.requestLayout();

                    sideContainer.getLayoutParams().width = sideWidth;
                    sideContainer.requestLayout();

                    mainContainer.getLayoutParams().width = resolutionWidth;
                    mainContainer.requestLayout();

                    LeftAdsFragment leftAdsFragment = (LeftAdsFragment) getSupportFragmentManager()
                        .findFragmentById(R.id.main_container);
                    leftAdsFragment.setResolution(resolutions);
                }
                return false;
            }
        });
    }

    private void initPlacement() {
        getSupportFragmentManager().beginTransaction()
            .replace(R.id.main_container, LeftAdsFragment.newInstance())
            .replace(R.id.side_container, NewsListFragment.newInstance())
            .replace(R.id.bottom_container, ControlFragment.newInstance(ControlFragment.FULL))
            .commit();
    }

    private void replaceFragment(BaseFragment baseFragment) {
        getSupportFragmentManager().beginTransaction()
            .replace(R.id.side_container, baseFragment)
            .addToBackStack(null)
            .commit();
    }
}
