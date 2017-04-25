package com.domikado.itaxi.data.entity.analytics;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

@Table(name = "metric_ads")
public class AdsMetric extends Model {

    @Column(name = "ads_id")
    private long adsId;

    @Column(name = "ads_type")
    private String adsType;

    @Column(name = "placement")
    private String placement;

    @Column(name = "session_id")
    private long sessionId;

    @Column(name = "started_at")
    private long startedAt;

    @Column(name = "finished_at")
    private long finishedAt;

    public void setAdsId(long adsId) {
        this.adsId = adsId;
    }
    public void setAdsType(String adsType) {
        this.adsType = adsType;
    }
    public void setPlacement(String placement) {
        this.placement = placement;
    }
    public void setSessionDevice(long sessionDevice) {
        this.sessionId = sessionDevice;
    }
    public void setStartedAt(long startedAt) {
        this.startedAt = startedAt;
    }
    public void setFinishedAt(long finishedAt) {
        this.finishedAt = finishedAt;
    }
}
