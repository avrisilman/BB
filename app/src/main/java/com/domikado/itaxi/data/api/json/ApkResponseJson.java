package com.domikado.itaxi.data.api.json;

import com.google.gson.annotations.SerializedName;

public class ApkResponseJson {

    @SerializedName("url")
    private String url;

    @SerializedName("version")
    private String version;

    @SerializedName("fingerprint")
    private String fingerprint;

    public String getUrl() {
        return url;
    }
    public String getVersion() { return version; }
    public String getFingerprint() {
        return fingerprint;
    }
}
