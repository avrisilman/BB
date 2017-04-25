package com.domikado.itaxi.ui.ads.banner;

import com.domikado.itaxi.data.entity.ads.Ads;
import com.hannesdorfmann.mosby.mvp.MvpView;

import java.util.List;

interface BannerAdsView extends MvpView {

    void setAds(List<Ads> ads);
    void onSlide();
}
