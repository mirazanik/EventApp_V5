package com.miraz.helloju.item;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class CategoryList implements Serializable {

    @SerializedName("cid")
    private String cid;

    @SerializedName("category_name")
    private String category_name;

    @SerializedName("category_image")
    private String category_image;

    @SerializedName("category_image_thumb")
    private String category_image_thumb;

    @SerializedName("category_icon")
    private String category_icon;

    @SerializedName("category_bg")
    private String category_bg;

    @SerializedName("cat_count")
    private String cat_count;

    public CategoryList(String cid, String category_name, String category_image, String category_image_thumb, String category_icon, String category_bg, String cat_count) {
        this.cid = cid;
        this.category_name = category_name;
        this.category_image = category_image;
        this.category_image_thumb = category_image_thumb;
        this.category_icon = category_icon;
        this.category_bg = category_bg;
        this.cat_count = cat_count;
    }

    public String getCid() {
        return cid;
    }

    public String getCategory_name() {
        return category_name;
    }

    public String getCategory_image() {
        return category_image;
    }

    public String getCategory_image_thumb() {
        return category_image_thumb;
    }

    public String getCategory_icon() {
        return category_icon;
    }

    public String getCategory_bg() {
        return category_bg;
    }

    public String getCat_count() {
        return cat_count;
    }
}
