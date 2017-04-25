package com.domikado.itaxi.data.entity.analytics;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

@Table(name = "rating")
public class Rating extends Model{

    @Column(name = "session_id")
    private long sessionId;

    @Column(name = "time")
    private String datetime;

    @Column(name = "rate")
    private String rate;

    public void setSessionId(long sessionId) {
        this.sessionId = sessionId;
    }

    public void setDatetime(String datetime) {
        this.datetime = datetime;
    }

    public void setRate(String rate) {
        this.rate = rate;
    }
}
