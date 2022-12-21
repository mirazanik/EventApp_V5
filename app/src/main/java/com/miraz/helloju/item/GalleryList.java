package com.miraz.helloju.item;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class GalleryList implements Serializable {

    @SerializedName("cover_id")
    private String cover_id;

    @SerializedName("cover_image")
    private String cover_image;

    public GalleryList(String cover_id, String cover_image) {
        this.cover_id = cover_id;
        this.cover_image = cover_image;
    }

    public String getCover_id() {
        return cover_id;
    }

    public String getCover_image() {
        return cover_image;
    }
}
