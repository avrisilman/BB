package com.domikado.itaxi.data.api.json;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class AdsJson {

    @SerializedName("id")
    private int id;

    @SerializedName("url")
    private String url;

    @SerializedName("fingerprint")
    private String fingerprint;

    @SerializedName("tags")
    private ArrayList<String> tags;

    @SerializedName("banner_url")
    private String bannerUrl;

    @SerializedName("call_to_action")
    private CallToActionJson callToAction;

    @SerializedName("duration")
    private long Duration;

    @SerializedName("time")
    private long time;

    @SerializedName("type")
    private String type;

    public int getId() {
        return id;
    }

    public String getUrl() {
        return url;
    }

    public String getFingerprint() {
        return fingerprint;
    }

    public CallToActionJson getCallToAction() {
        return callToAction;
    }

    public long getDuration() {
        return Duration;
    }

    public long getTime() {
        return time;
    }

    public String getBannerUrl() {
        return bannerUrl;
    }

    public ArrayList<String> getTags() {
        return tags;
    }

    public String getType() {
        return type;
    }
}