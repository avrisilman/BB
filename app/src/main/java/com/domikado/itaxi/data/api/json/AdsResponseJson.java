package com.domikado.itaxi.data.api.json;

import com.google.gson.annotations.SerializedName;

public class AdsResponseJson {

    @SerializedName("playlists")
    private AdsPlaylistJson playlists;

    @SerializedName("version")
    private String version;

    public AdsPlaylistJson getPlaylists() {
        return playlists;
    }
    public String getVersion() {
        return version;
    }
}