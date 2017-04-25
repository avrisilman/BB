package com.domikado.itaxi.data.api.json;

import com.google.gson.annotations.SerializedName;

public class ResolutionJson {

    @SerializedName("height")
    private int height;

    @SerializedName("width")
    private int width;

    public int getHeight() {
        return height;
    }
    public int getWidth() {
        return width;
    }
}
