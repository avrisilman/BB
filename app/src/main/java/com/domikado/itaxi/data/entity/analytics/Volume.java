package com.domikado.itaxi.data.entity.analytics;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

@Table(name = "volume_brightness")
public class Volume extends Model {

    @Column(name = "session_id")
    private long sessionId;

    @Column(name = "time")
    private String datetime;

    @Column(name = "action")
    private String action;


    public void setSessionId(long sessionId) {
        this.sessionId = sessionId;
    }

    public void setDatetime(String datetime) {
        this.datetime = datetime;
    }

    public void setAction(String action) {
        this.action = action;
    }

}
