package com.domikado.itaxi.events;

import com.domikado.itaxi.data.entity.ads.Ads;

public class EventPopupQuiz {

    private Ads ads;

    public EventPopupQuiz(Ads ads) {
        this.ads = ads;
    }

    public Ads getAds() {
        return ads;
    }
}
