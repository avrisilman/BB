package com.domikado.itaxi.data.api.json;

import com.domikado.itaxi.data.taximeter.Taximeter;
import com.google.gson.annotations.SerializedName;

public class ApplicationSettingJson {

    @SerializedName("call_to_action_show")
    private int callToActionShow;

    @SerializedName("call_to_action_dismiss")
    private int callToActionDismiss;

    @SerializedName("ping_interval")
    private int pingInterval;

    @SerializedName("visibility_off")
    private int visibilityOff;

    @SerializedName("day_brightness")
    private int dayBrightness;

    @SerializedName("night_brightness")
    private int nightBrightness;

    @SerializedName("initial_volume")
    private int initialVolume;

    @SerializedName("update_interval")
    private int locationUpdateInterval;

    @SerializedName("splash_screen_duration")
    private int splashScreenDuration;

    @SerializedName("banner_slide_interval")
    private int bannerSlideInterval;

    @SerializedName("news_auto_update_interval")
    private int newsAutoUpdateInterval;

    @SerializedName("ads_auto_update_interval")
    private int adsAutoUpdateInterval;

    @SerializedName("setting_auto_update_interval")
    private int settingAutoUpdateInterval;

    @SerializedName("analytics_auto_upload_interval")
    private int analyticsAutoUploadInterval;

    @SerializedName("log_auto_upload_interval")
    private int logAutoUploadInterval;

    @SerializedName("apk_check_interval")
    private int apkCheckInterval;

    @SerializedName("idle_time_before_shutdown")
    private int idleTimeBeforeShutdown;

    @SerializedName("taximeter")
    private String taximeter;

    @SerializedName("lock_pass")
    private String lockPass;

    @SerializedName("overlay_log_level")
    private int overlayLogLevel;

    @SerializedName("application_settings_url")
    private String applicationSettingsUrl;

    @SerializedName("playlists_url")
    private String playlistsUrl;

    @SerializedName("news_url")
    private String newsUrl;

    @SerializedName("apk_url")
    private String apkUrl;

    @SerializedName("upload_report_url")
    private String uploadAnalyticsUrl;

    @SerializedName("upload_log_url")
    private String uploadLogUrl;

    @SerializedName("menu_url")
    private String menuUrl;

    @SerializedName("placement_resolutions")
    private PlacementJson placementResolutions;

    public int getCallToActionShow() {
        return callToActionShow;
    }
    public int getCallToActionDismiss() {
        return callToActionDismiss;
    }
    public int getPingInterval() {
        return pingInterval;
    }
    public int getVisibilityOff() {
        return visibilityOff;
    }
    public int getDayBrightness() {
        return dayBrightness;
    }
    public int getNightBrightness() {
        return nightBrightness;
    }
    public int getInitialVolume() {
        return initialVolume;
    }
    public int getLocationUpdateInterval() {
        return locationUpdateInterval;
    }
    public int getSplashScreenDuration() {
        return splashScreenDuration;
    }
    public int getBannerSlideInterval() {
        return bannerSlideInterval;
    }
    public PlacementJson getPlacementResolutions() {
        return placementResolutions;
    }
    public String getApplicationSettingsUrl() {
        return applicationSettingsUrl;
    }
    public String getApkUrl() {
        return apkUrl;
    }
    public String getPlaylistsUrl() {
        return playlistsUrl;
    }
    public String getNewsUrl() {
        return newsUrl;
    }
    public String getUploadAnalyticsUrl() {
        return uploadAnalyticsUrl;
    }
    public String getUploadLogUrl() {
        return uploadLogUrl;
    }
    public String getMenuUrl() {
        return menuUrl;
    }
    public int getNewsAutoUpdateInterval() {
        return newsAutoUpdateInterval;
    }
    public int getAdsAutoUpdateInterval() {
        return adsAutoUpdateInterval;
    }
    public int getSettingAutoUpdateInterval() {
        return settingAutoUpdateInterval;
    }
    public int getAnalyticsAutoUploadInterval() {
        return analyticsAutoUploadInterval;
    }
    public int getLogAutoUploadInterval() {
        return logAutoUploadInterval;
    }
    public int getApkCheckInterval() {
        return apkCheckInterval;
    }
    public int getIdleTimeBeforeShutdown() {
        return idleTimeBeforeShutdown;
    }
    public String getLockPass() {
        return lockPass;
    }
    public String getTaximeter() {
        return taximeter;
    }
    public int getOverlayLogLevel() {
        return overlayLogLevel;
    }

    public static ApplicationSettingJson defaultSetting() {
        ApplicationSettingJson setting = new ApplicationSettingJson();
        setting.pingInterval = 30;
        setting.locationUpdateInterval = 30;
        setting.callToActionShow = 3;
        setting.callToActionDismiss = 8;
        setting.dayBrightness = 100;
        setting.nightBrightness = 100;
        setting.visibilityOff = 10;
        setting.splashScreenDuration = 5;
        setting.initialVolume = 50;
        setting.bannerSlideInterval = 15;
        setting.newsAutoUpdateInterval = 60;
        setting.adsAutoUpdateInterval = 60;
        setting.settingAutoUpdateInterval = 60;
        setting.analyticsAutoUploadInterval = 60;
        setting.logAutoUploadInterval = 60;
        setting.apkCheckInterval = 60;
        setting.idleTimeBeforeShutdown = 300;
        setting.overlayLogLevel = 10;
        setting.taximeter = Taximeter.GIGATAX_25F9;
        return setting;
    }
}
