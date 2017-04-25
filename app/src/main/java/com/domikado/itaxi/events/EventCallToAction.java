package com.domikado.itaxi.events;

import com.domikado.itaxi.data.entity.ads.Ads;

public class EventCallToAction {

    private final Ads model;

    public EventCallToAction(Ads model) {
        this.model = model;
    }

    public Ads getModel() {
        return model;
    }
}
