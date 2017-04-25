package com.domikado.itaxi.data.entity.analytics;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

@Table(name = "metric_ads_action")
public class AdsActionMetric extends Model {

    public static final String ACTION_CLICK = "ACTION_CLICK";
    public static final String ACTION_BOUNCE = "ACTION_BOUNCE";
    public static final String ACTION_SUBMIT = "ACTION_SUBMIT";
    public static final String ACTION_AUTO = "ACTION_AUTO";

    @Column(name = "unix_time")
    long timestamp;

    @Column(name = "session_id")
    long sessionId;

    @Column(name = "action")
    String action;

    @Column(name = "ads_id")
    long adsId;

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
    public void setSessionId(long sessionId) {
        this.sessionId = sessionId;
    }
    public void setAction(String action) {
        this.action = action;
    }
    public void setAdsId(long adsId) {
        this.adsId = adsId;
    }
}
