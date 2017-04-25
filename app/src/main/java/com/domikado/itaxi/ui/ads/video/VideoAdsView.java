package com.domikado.itaxi.ui.ads.video;

import com.domikado.itaxi.data.entity.ads.Ads;
import com.hannesdorfmann.mosby.mvp.MvpView;

import java.util.List;

interface VideoAdsView extends MvpView {

    void setAds(List<Ads> ads);
    void showLoading(boolean isShow);
    void initLayout(int width, int height);
    void releaseSurface();

    boolean isLoop();
    void onVideoComplete();
    void setVideoViewListener(VideoViewListener listener);
    void setCallToActionImage(String imagePath);
    void showCallToActionPrompt(int durationLeft);
}
