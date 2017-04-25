package com.domikado.itaxi.data.api.json;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class NewsResponseJson {

    @SerializedName("news")
    private List<NewsJson> news;

    @SerializedName("version")
    private String version;

    public List<NewsJson> getNews() {
        return news;
    }
    public String getVersion() {
        return version;
    }
}
