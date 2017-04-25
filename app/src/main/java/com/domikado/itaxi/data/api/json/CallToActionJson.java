package com.domikado.itaxi.data.api.json;

import com.google.gson.annotations.SerializedName;

public class CallToActionJson {

    @SerializedName("id")
    private int id;

    @SerializedName("url")
    private String url;

    @SerializedName("name")
    private String name;

    @SerializedName("size")
    private String size;

    @SerializedName("button_image")
    private String buttonImage;

    @SerializedName("fingerprint")
    private String fingerprint;

    public int getId() {
        return id;
    }
    public String getButtonImage() {
        return buttonImage;
    }
    public String getUrl() {
        return url;
    }
    public String getName() {
        return name;
    }
    public String getSize() {
        return size;
    }
    public String getFingerprint() {
        return fingerprint;
    }
}
