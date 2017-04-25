package com.domikado.itaxi.data.entity.analytics;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

@Table(name = "metric_systems")
public class SystemMetric extends Model {

    @Column(name = "signal_strengh")
    String signalStrength;

    @Column(name = "battery_level")
    String batteryLevel;

    @Column(name = "network_type")
    String networkType;

    @Column(name = "unix_time")
    long timestamp;

    @Column(name = "session_id")
    long sessionId;

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
    public void setSignalStrength(String signalStrength) {
        this.signalStrength = signalStrength;
    }
    public void setBatteryLevel(String batteryLevel) {
        this.batteryLevel = batteryLevel;
    }
    public void setNetworkType(String networkType) {
        this.networkType = networkType;
    }
    public void setSessionId(long sessionId) {
        this.sessionId = sessionId;
    }
}
