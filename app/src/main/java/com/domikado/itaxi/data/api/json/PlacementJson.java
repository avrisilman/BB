package com.domikado.itaxi.data.api.json;

import com.domikado.itaxi.data.entity.ads.Placement;
import com.google.gson.annotations.SerializedName;

public class PlacementJson {

    @SerializedName(Placement.ZONE_MAIN)
    private ResolutionJson middleboard;

    @SerializedName(Placement.ZONE_PREMIUM)
    private ResolutionJson premium;

    @SerializedName(Placement.ZONE_TOP)
    private ResolutionJson top;

    @SerializedName(Placement.ZONE_BANNER_LEFT)
    private ResolutionJson bannerLeft;

    @SerializedName(Placement.ZONE_BANNER_RIGHT)
    private ResolutionJson bannerRight;

    @SerializedName(Placement.ZONE_SPLASH)
    private ResolutionJson splash;

    public ResolutionJson getSplash() {
        return splash;
    }
    public ResolutionJson getPremium() {
        return premium;
    }
    public ResolutionJson getTop() {
        return top;
    }
    public ResolutionJson getMain() {
        return middleboard;
    }
    public ResolutionJson getBannerLeft() {
        return bannerLeft;
    }
    public ResolutionJson getBannerRight() {
        return bannerRight;
    }
}