package com.domikado.itaxi.data.entity.analytics;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

@Table(name = "uploads")
public class Upload extends Model {

    @Column(name = "path")
    String path;

    @Column(name = "unix_time")
    long timestamp;

    public void setPath(String path) {
        this.path = path;
    }
    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}
