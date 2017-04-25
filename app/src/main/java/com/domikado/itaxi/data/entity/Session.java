package com.domikado.itaxi.data.entity;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

@Table(name = "sessions")
public class Session extends Model {

    @Column(name = "token")
    private String token;

    @Column(name = "started_at")
    private long startedAt;

    @Column(name = "finished_at")
    private long finishedAt;

    @Column(name = "fare")
    private int fare;

    public void setToken(String token) {
        this.token = token;
    }
    public void setStartedAt(long startedAt) {
        this.startedAt = startedAt;
    }
    public void setFinishedAt(long finishedAt) {
        this.finishedAt = finishedAt;
    }
    public void setFare(int fare) {
        this.fare = fare;
    }
}
