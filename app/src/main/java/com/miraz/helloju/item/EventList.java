package com.miraz.helloju.item;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class EventList implements Serializable {

    @SerializedName("id")
    private String id;

    @SerializedName("cat_id")
    private String cat_id;

    @SerializedName("is_fav")
    private boolean is_fav;

    @SerializedName("is_reviewed")
    private boolean is_reviewed;

    @SerializedName("event_title")
    private String event_title;

    @SerializedName("event_date")
    private String event_date;

    @SerializedName("event_address")
    private String event_address;

    @SerializedName("event_banner_thumb")
    private String event_banner_thumb;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCat_id() {
        return cat_id;
    }

    public void setCat_id(String cat_id) {
        this.cat_id = cat_id;
    }

    public boolean isIs_fav() {
        return is_fav;
    }

    public void setIs_fav(boolean is_fav) {
        this.is_fav = is_fav;
    }

    public boolean isIs_reviewed() {
        return is_reviewed;
    }

    public String getEvent_title() {
        return event_title;
    }

    public void setEvent_title(String event_title) {
        this.event_title = event_title;
    }

    public String getEvent_date() {
        return event_date;
    }

    public void setEvent_date(String event_date) {
        this.event_date = event_date;
    }

    public String getEvent_address() {
        return event_address;
    }

    public void setEvent_address(String event_address) {
        this.event_address = event_address;
    }

    public String getEvent_banner_thumb() {
        return event_banner_thumb;
    }

    public void setEvent_banner_thumb(String event_banner_thumb) {
        this.event_banner_thumb = event_banner_thumb;
    }
}
