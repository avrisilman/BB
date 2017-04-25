package com.domikado.itaxi.data.entity.analytics;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

@Table(name = "metric_screen")
public class ScreenMetric extends Model {

    @Column(name = "session_id")
    private long sessionId;

    @Column(name = "is_visible")
    private boolean visible;

    @Column(name = "unix_time")
    private long timestamp;

    @Column(name= "screen")
    private String screen;

    public void setSessionId(long sessionId) {
        this.sessionId = sessionId;
    }
    public void setVisible(boolean visible) {
        this.visible = visible;
    }
    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
    public void setScreen(String screen) {
        this.screen = screen;
    }
}
