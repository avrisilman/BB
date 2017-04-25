package com.domikado.itaxi.data.taximeter;

import com.domikado.itaxi.TaxiApplication;

import org.greenrobot.eventbus.EventBus;

public class Taximeter {

    public final static String GIGATAX_2519 = "Gigatax2519";
    public final static String GIGATAX_25F9 = "Gigatax25F9";

    private Gigatax gigatax2519 = new Gigatax2519(EventBus.getDefault());
    private Gigatax gigatax25F9 = new Gigatax25F9(EventBus.getDefault());

    public void process(byte[] data) {
        switch (TaxiApplication.getSettings().getTaximeter()) {
            case GIGATAX_2519:
                gigatax2519.call(data);
                break;
            case GIGATAX_25F9:
                gigatax25F9.call(data);
                break;
            default:
                gigatax25F9.call(data);
                break;
        }
    }
}
