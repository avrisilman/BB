package com.domikado.itaxi.ui.ads.banner;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import com.domikado.itaxi.R;
import com.domikado.itaxi.TaxiApplication;
import com.domikado.itaxi.data.entity.ads.Ads;
import com.domikado.itaxi.injection.component.UiComponent;
import com.hannesdorfmann.mosby.mvp.layout.MvpFrameLayout;

import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

public class BannerAds extends MvpFrameLayout<BannerAdsView, BannerAdsPresenter>
        implements BannerAdsView, ViewPager.OnPageChangeListener {

    @BindView(R.id.taxi_ads_viewpager)
    LoopViewPager mViewPager;

    @Inject
    BannerAdsPresenter mPresenter;

    private BannerAdsAdapter mBannerAdsAdapter;

    public BannerAds(Context context) {
        this(context, null);
    }

    public BannerAds(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BannerAds(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        if (isInEditMode())
            return;

        inflate(getContext(), R.layout.view_banner_ads, this);
        ButterKnife.bind(this);

        UiComponent.Initializer
                .init(TaxiApplication.getComponent())
                .inject(this);

        mBannerAdsAdapter = new BannerAdsAdapter(getContext());
        mBannerAdsAdapter.setOnClickItemListener(mPresenter::clickAds);

        mViewPager.setAdapter(mBannerAdsAdapter);
        mViewPager.setBoundaryCaching(true);
        mViewPager.addOnPageChangeListener(this);
        mViewPager.setPageTransformer(false, (view, position) -> {
            mPresenter.transformPage(view, position);
        });
    }

    @NonNull
    @Override
    public BannerAdsPresenter createPresenter() {
        return mPresenter;
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        mPresenter.registerBus();
        mPresenter.startTimer();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        mPresenter.unregisterBus();
        mPresenter.stopTimer();
    }

    @Override
    public void setAds(List<Ads> ads) {
        if(ads.size() == 1){
            ads.add(1, ads.get(0));
        }

        mBannerAdsAdapter.setAdses(ads);
        mViewPager.getAdapter().notifyDataSetChanged();
        onPageSelected(0);
    }

    @Override
    public void onSlide() {
        if (mBannerAdsAdapter == null) return;
        mViewPager.setCurrentItem(mViewPager.getCurrentItem() + 1, true);
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
    }

    @Override
    public void onPageSelected(int position) {
        Ads ads = mBannerAdsAdapter.getItem(position);
        mPresenter.reportAds(ads);
    }

    @Override
    public void onPageScrollStateChanged(int state) {
        if (state == ViewPager.SCROLL_STATE_IDLE) {
            mPresenter.startTimer();
        } else {
            mPresenter.stopTimer();
        }
    }
}
