package com.miraz.helloju.response;

import com.miraz.helloju.item.CategoryList;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class CategoryRP implements Serializable {

    @SerializedName("status")
    private String status;

    @SerializedName("message")
    private String message;

    @SerializedName("EVENT_APP")
    private List<CategoryList> categoryLists;

    public String getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }

    public List<CategoryList> getCategoryLists() {
        return categoryLists;
    }
}
