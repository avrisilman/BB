package com.domikado.itaxi.data.entity.ads;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.domikado.itaxi.data.api.json.AdsJson;

@Table(name = "ads")
public class Ads extends Model {

    public static final String VIDEO = "video";
    public static final String IMAGE = "banner";
    public static final String FILES = "file";

    @Column(name = "server_id")
    private long serverId;

    @Column(name = "server_media_url")
    private String serverMediaUrl;

    @Column(name = "cta_id")
    private long callToActionId;

    @Column(name = "placement")
    private String placement;

    @Column(name = "type")
    private String type;

    @Column(name = "filepath")
    private String filepath;

    @Column(name = "bannerpath")
    private String bannerpath;

    @Column(name = "fingerprint")
    private String fingerprint;

    @Column(name = "tags")
    private String tags;

    @Column(name = "banner_url")
    private String bannerUrl;

    @Column(name = "version")
    private String version;

    @Column(name = "duration")
    private long duration;

    @Column(name = "time")
    private long time;

    public Ads() {
        super();
    }

    public Ads(AdsJson ads) {
        super();
        this.serverId = ads.getId();
        this.serverMediaUrl = ads.getUrl();
        this.fingerprint = ads.getFingerprint();
    }

    public void setCallToActionId(long callToActionId) {
        this.callToActionId = callToActionId;
    }

    public void setServerId(long serverId) {
        this.serverId = serverId;
    }

    public void setPlacement(String placement) {
        this.placement = placement;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setServerMediaUrl(String serverMediaUrl) {
        this.serverMediaUrl = serverMediaUrl;
    }

    public void setFilepath(String filepath) {
        this.filepath = filepath;
    }

    public void setBannerpath(String bannerpath) {
        this.bannerpath = bannerpath;
    }

    public void setFingerprint(String fingerprint) {
        this.fingerprint = fingerprint;
    }

    public void setTags(String tags) {
        this.tags = tags;
    }

    public void setBannerUrl(String bannerUrl) {
        this.bannerUrl = bannerUrl;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public long getCallToActionId() {
        return callToActionId;
    }

    public long getServerId() {
        return serverId;
    }

    public String getPlacement() {
        return placement;
    }

    public String getType() {
        return type;
    }

    public String getFilepath() {
        return filepath;
    }

    public String getBannerpath() {
        return bannerpath;
    }

    public String getFingerprint() {
        return fingerprint;
    }

    public String getTags() {
        return tags;
    }

    public String getLayout(){
        String layout = "";
        String[] tags = this.getTags().split(",");

        for(String tag: tags){
            if(tag.contains("layout:")){
                layout = tag;
            }
        }

        return layout;
    }

    public String getBannerUrl() {
        return bannerUrl;
    }

    public String getVersion() {
        return version;
    }

    public String getServerMediaUrl() {
        return serverMediaUrl;
    }

    public long getDuration() {
        return duration;
    }

    public long getTime() {
        return time;
    }

    public boolean haveCallToAction() {
        return callToActionId > 0;
    }
}