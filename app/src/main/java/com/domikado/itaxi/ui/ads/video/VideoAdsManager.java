package com.domikado.itaxi.ui.ads.video;

import com.domikado.itaxi.data.entity.ads.Ads;

import java.util.ArrayList;
import java.util.List;

public class VideoAdsManager {

    private List<Ads> adses = new ArrayList<>();

    private int mCurrentPosition = 0;
    private boolean isLastVideo = false;

    Ads getNextAds() {
        if (adses == null || adses.isEmpty())
            return null;

        Ads ads = adses.get(mCurrentPosition);

        mCurrentPosition++;
        if (mCurrentPosition == adses.size()) {
            mCurrentPosition = 0;
            isLastVideo = true;
        } else {
            isLastVideo = false;
        }

        return ads;
    }

    boolean isLastVideo() {
        return adses == null || adses.isEmpty() || isLastVideo;
    }

    public Ads getCurrentAds() {
        int position = mCurrentPosition == 0 ? adses.size() - 1 : mCurrentPosition - 1;
        return adses.get(position);
    }

    void setAdses(List<Ads> adses) {
        this.adses = adses;
    }

    boolean isEmpty() {
        return adses.isEmpty();
    }
}
