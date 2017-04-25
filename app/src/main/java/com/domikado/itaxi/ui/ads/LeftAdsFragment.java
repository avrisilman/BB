package com.domikado.itaxi.ui.ads;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterViewFlipper;

import com.domikado.itaxi.R;
import com.domikado.itaxi.data.entity.ads.Ads;
import com.domikado.itaxi.data.entity.ads.Placement;
import com.domikado.itaxi.data.repository.AdsRepository;
import com.domikado.itaxi.events.EventImageAds;
import com.domikado.itaxi.events.EventVideoAdsCompletion;
import com.domikado.itaxi.ui.ads.banner.BannerAds;
import com.domikado.itaxi.ui.ads.videoads.MainVideoAdsAdapter;
import com.domikado.itaxi.ui.base.BaseMvpFragment;
import com.domikado.itaxi.utils.EBus;
import com.hannesdorfmann.mosby.mvp.MvpBasePresenter;
import com.hannesdorfmann.mosby.mvp.MvpView;
import org.greenrobot.eventbus.Subscribe;
import java.util.ArrayList;
import java.util.List;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import timber.log.Timber;

public class LeftAdsFragment extends BaseMvpFragment<MvpView, MvpBasePresenter<MvpView>> implements MvpView {

//    @BindView(R.id.adsVideoView)
//    VideoAds videoAds;

    @BindView(R.id.ads_top)
    BannerAds adsTop;

    @BindView(R.id.ads_banner_left)
    BannerAds adsBannerLeft;

    @BindView(R.id.ads_banner_right)
    BannerAds adsBannerRight;

    @BindView(R.id.video_flipper)
    AdapterViewFlipper mVideoFlipper;

    private Unbinder unbinder;
    private MainVideoAdsAdapter adapter;

    public static LeftAdsFragment newInstance() {
        return new LeftAdsFragment();
    }

    @Override
    protected int getResId() {
        return R.layout.fragment_left_ads;
    }

    @NonNull
    @Override
    public MvpBasePresenter<MvpView> createPresenter() {
        return new MvpBasePresenter<>();
    }

    @Override
    protected void onSetupView(View view, Bundle savedInstanceState) {

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        ButterKnife.bind(getActivity());

        adsTop.setAds(AdsRepository.getAdsWithPlacement(Placement.ZONE_TOP));
//        Log.d("BANNERS === ", String.valueOf(AdsRepository.getAdsWithPlacement(Placement.ZONE_TOP)));
        adsBannerLeft.setAds(AdsRepository.getAdsWithPlacement(Placement.ZONE_BANNER_LEFT));
        adsBannerRight.setAds(AdsRepository.getAdsWithPlacement(Placement.ZONE_BANNER_RIGHT));
//        videoAds.setAds(AdsRepository.getAdsWithPlacement(Placement.ZONE_MAIN));
//        videoAds.setLoop(true);
        adapter = new MainVideoAdsAdapter(getActivity());
       // adapter.setAdses(AdsRepository.getAdsWithPlacement(Placement.ZONE_MAIN));
        adapter.setAdses(getAdsNonAds());
        mVideoFlipper.setAdapter(adapter);

    }

    @Override
    public void onResume() {
        super.onResume();
        EBus.register(this);
    }

    @Override
    public void onPause() {
        EBus.unregister(this);
        super.onPause();
    }

    @Subscribe
    public void onVideoCompletion(EventVideoAdsCompletion e) {
        mVideoFlipper.showNext();
    }

    @Subscribe
    public void onImageAdsComplettion(EventImageAds e) {
        mVideoFlipper.showNext();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    public void setResolution(List<Placement> resolutions) {
        int topHeight = 0;
        int bottomHeight = 0;

        for (Placement resolution : resolutions) {
            switch (resolution.getName()) {
                case Placement.ZONE_MAIN:
//                    setResolution(mVideoFlipper, resolution);
                    break;
                case Placement.ZONE_TOP:
                    topHeight = resolution.getHeight();
                    setResolution(adsTop, resolution);
                    break;
                case Placement.ZONE_BANNER_RIGHT:
                    bottomHeight = resolution.getHeight();
                    setResolution(adsBannerRight, resolution);
                    break;
                case Placement.ZONE_BANNER_LEFT:
                    setResolution(adsBannerLeft, resolution);
                    break;
            }
        }

        int heightDiff = topHeight - bottomHeight;
        if(heightDiff >= 0) {
            mVideoFlipper.setPadding(0, Math.abs(heightDiff), 0, 0);
        }else{
            mVideoFlipper.setPadding(0, 0, 0, Math.abs(heightDiff));
        }
        mVideoFlipper.requestLayout();
    }

    private void setResolution(View view, Placement placement) {
        view.getLayoutParams().width = placement.getWidth();
        view.getLayoutParams().height = placement.getHeight();
        view.requestLayout();
    }

    public List<Ads> getAdsNonAds(){
        List<Ads> ads = AdsRepository.getAdsWithPlacementAds(Placement.ZONE_MAIN);
        List<Ads> nonAds = AdsRepository.getAdsWithPlacementNonAds(Placement.ZONE_MAIN);
        List<Ads> adsShuffle = new ArrayList<>();

        int maxLen = Math.max(ads.size(), nonAds.size());
        for(int i = 0; i < maxLen; i++){
            if(ads.size() > i){
                Timber.i(String.valueOf(ads.get(i).getTags()));
                adsShuffle.add(ads.get(i));
            }
            if(nonAds.size() > i){
                Timber.i(String.valueOf(nonAds.get(i).getTags()));
                adsShuffle.add(nonAds.get(i));
            }
        }

        // 2 same ads if only 1 ads
        if(adsShuffle.size() == 1){
            if(ads.size() >= 1) adsShuffle.add(ads.get(0));
            if(nonAds.size() >= 1) adsShuffle.add(nonAds.get(0));
        }

        Log.i("AdsShuffle", String.valueOf(adsShuffle.size()));
        return adsShuffle;
    }

}
