package com.miraz.helloju.response;

import com.miraz.helloju.item.CategoryList;
import com.miraz.helloju.item.EventList;
import com.miraz.helloju.item.SliderList;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class HomeRP implements Serializable {

    @SerializedName("status")
    private String status;

    @SerializedName("message")
    private String message;

    @SerializedName("event_slider")
    private List<SliderList> sliderLists;

    @SerializedName("cat_list")
    private List<CategoryList> categoryLists;

    @SerializedName("recent_views")
    private List<EventList> recentViewLists;

    @SerializedName("nearby_home")
    private List<EventList> nearByLists;

    public String getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }

    public List<SliderList> getSliderLists() {
        return sliderLists;
    }

    public List<EventList> getRecentViewLists() {
        return recentViewLists;
    }

    public List<CategoryList> getCategoryLists() {
        return categoryLists;
    }

    public List<EventList> getNearByLists() {
        return nearByLists;
    }
}
