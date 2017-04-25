package com.domikado.itaxi.data.repository;

import com.activeandroid.query.Delete;
import com.activeandroid.query.Select;
import com.domikado.itaxi.Constant;
import com.domikado.itaxi.data.entity.ads.Ads;
import com.orhanobut.hawk.Hawk;

import java.util.Arrays;
import java.util.List;

public class AdsRepository {

    public static List<Ads> getAdsWithPlacement(String placement) {
        String version = Hawk.get(Constant.DataStore.ADS_CURRENT_VERSION);
        return new Select()
            .from(Ads.class)
            .where("placement = ?", placement)
            .where("version = ?", version)
            .execute();
    }

    public static List<Ads> getAdsWithPlacementAds(String placement) {
        String version = Hawk.get(Constant.DataStore.ADS_CURRENT_VERSION);
        String tag = "%category:nonads%";
        return new Select()
                .from(Ads.class)
                .where("placement = ?", placement)
                .where("version = ?", version)
                .where("tags NOT LIKE ?", tag)
                .execute();
    }

    public static List<Ads> getAdsWithPlacementNonAds(String placement) {
        String version = Hawk.get(Constant.DataStore.ADS_CURRENT_VERSION);
        String tag = "%category:nonads%";
        return new Select()
                .from(Ads.class)
                .where("placement = ?", placement)
                .where("version = ?", version)
                .where("tags LIKE ?", tag)
                .execute();
    }

    public static List<Ads> getAdsPopup() {
        String version = Hawk.get(Constant.DataStore.ADS_CURRENT_VERSION);
        return new Select()
                .from(Ads.class)
                .where("placement = ?", "popup")
                .where("version = ?", version)
                .execute();
    }

    public static Ads getInstance(long id) {
        Ads ads = new Select()
            .from(Ads.class)
            .where("id = ?", id)
            .executeSingle();

        if (ads == null)
            return null;
        else return ads;
    }

    public static boolean isAnyAds(String placement) {
        String version = Hawk.get(Constant.DataStore.ADS_CURRENT_VERSION);
        if (version == null)
            return false;

        return new Select()
            .from(Ads.class)
            .where("placement = ?", placement)
            .where("version = ?", version)
            .count() > 0;
    }

    public static Ads findByFingerprint(String fingerprint) {
        return new Select()
            .from(Ads.class)
            .where("fingerprint = ?", fingerprint)
            .executeSingle();
    }

    public static void deleteThisVersion(String version) {
        new Delete()
            .from(Ads.class)
            .where("version = ?", version)
            .execute();
    }

    public static void deleteObsoleteAds() {
        String before = Hawk.get(Constant.DataStore.ADS_BEFORE_VERSION);
        String current = Hawk.get(Constant.DataStore.ADS_CURRENT_VERSION);

        if (before == null || current == null) return;
        new Delete()
            .from(Ads.class)
            .where("version NOT IN (?, ?)", new String[] {before, current})
            .execute();
    }
}
