package com.domikado.itaxi.data.api.json;

import com.domikado.itaxi.data.entity.ads.Placement;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class AdsPlaylistJson {

    @SerializedName(Placement.ZONE_MAIN)
    private List<AdsJson> middleboard;

    @SerializedName(Placement.ZONE_PREMIUM)
    private List<AdsJson> premium;

    @SerializedName(Placement.ZONE_TOP)
    private List<AdsJson> top;

    @SerializedName(Placement.ZONE_BANNER_LEFT)
    private List<AdsJson> bannerLeft;

    @SerializedName(Placement.ZONE_BANNER_RIGHT)
    private List<AdsJson> bannerRight;

    @SerializedName(Placement.ZONE_SPLASH)
    private List<AdsJson> splash;

    @SerializedName(Placement.ZONE_POPUP)
    private List<AdsJson> zonePopup;

    public List<AdsJson> getSplash() {
        return splash == null ? emptyList() : splash;
    }
    public List<AdsJson> getBannerLeft() {
        return bannerLeft == null ? emptyList() : bannerLeft;
    }
    public List<AdsJson> getBannerRight() {
        return bannerRight == null ? emptyList() : bannerRight;
    }
    public List<AdsJson> getMain() {
        return middleboard == null ? emptyList() : middleboard;
    }
    public List<AdsJson> getTop() {
        return top == null ? emptyList() : top;
    }
    public List<AdsJson> getPremium() {
        return premium == null ? emptyList() : premium;
    }
    public List<AdsJson> getZonePopup(){
        return zonePopup == null ? emptyList() : zonePopup;
    }
    public List<AdsJson> emptyList(){
        return new ArrayList<>();
    }
}
