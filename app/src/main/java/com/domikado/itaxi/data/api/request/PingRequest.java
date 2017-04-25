package com.domikado.itaxi.data.api.request;

import com.google.gson.annotations.SerializedName;

public class PingRequest {

    @SerializedName("latitude")
    private double lat;

    @SerializedName("longitude")
    private double lng;

    @SerializedName("playlist_version")
    private String playlistVersion;

    @SerializedName("battery_level")
    private float batteryLevel;

    @SerializedName("gsm_signal_strength")
    private int gsmSignalStrength;

    @SerializedName("network_type")
    private String networkType;

    @SerializedName("session_token")
    private String sessionToken;

    private PingRequest(Builder builder) {
        this.lat = builder.lat;
        this.lng = builder.lng;
        this.playlistVersion = builder.playlistVersion;
        this.batteryLevel = builder.batteryLevel;
        this.gsmSignalStrength = builder.gsmSignalStrength;
        this.networkType = builder.networkType;
        this.sessionToken = builder.sessionToken;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {

        private double lat;
        private double lng;
        private String playlistVersion;
        private float batteryLevel;
        private int gsmSignalStrength;
        private String networkType;
        private String sessionToken;

        private Builder() {
        }

        public Builder setLatitude(double lat) {
            this.lat = lat;
            return this;
        }

        public Builder setLongitude(double lng) {
            this.lng = lng;
            return this;
        }

        public Builder setPlaylistVersion(String version) {
            this.playlistVersion = version;
            return this;
        }

        public Builder setGsmSignalStrength(int strength) {
            this.gsmSignalStrength = strength;
            return this;
        }

        public Builder setBatteryLevel(float level) {
            this.batteryLevel = level;
            return this;
        }

        public Builder setNetworkType(String type) {
            this.networkType = type;
            return this;
        }

        public Builder setSessionToken(String token) {
            this.sessionToken = token;
            return this;
        }

        public PingRequest build() { return new PingRequest(this); }
    }
}
