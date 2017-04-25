package com.domikado.itaxi.data.entity.ads;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

@Table(name = "ads_result")
public class AdsResult extends Model {

    @Column(name = "session_id")
    private long sessionId;

    @Column(name = "ads_id")
    private long adsId;

    @Column(name = "data")
    private String data;

    @Column(name = "time")
    private String datetime;

    public void setSessionId(long sessionId) {
        this.sessionId = sessionId;
    }
    public void setAdsId(long adsId) {
        this.adsId = adsId;
    }
    public void setData(String data) {
        this.data = data;
    }
    public void setDatetime(String datetime){
        this.datetime = datetime;
    }
}
