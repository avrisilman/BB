package com.domikado.itaxi.data.api.json;

import com.google.gson.annotations.SerializedName;

public class NewsJson {

    @SerializedName("id")
    private int serverId;

    @SerializedName("picture")
    private String imageUrl;

    @SerializedName("title")
    private String title;

    @SerializedName("summary")
    private String summary;

    @SerializedName("content")
    private String content;

    @SerializedName("date")
    private String publishedDate;

    @SerializedName("source")
    private String source;

    @SerializedName("fingerprint")
    private String fingerprint;

    public int getServerId() {
        return serverId;
    }
    public String getTitle() {
        return title;
    }
    public String getSummary() {
        return summary;
    }
    public String getContent() {
        return content;
    }
    public String getImageUrl() {
        return imageUrl;
    }
    public String getPublishedDate() {
        return publishedDate;
    }
    public String getSource() {
        return source;
    }
    public String getFingerprint() {
        return fingerprint;
    }
}
