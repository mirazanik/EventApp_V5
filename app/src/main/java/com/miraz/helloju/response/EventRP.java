package com.miraz.helloju.response;

import com.miraz.helloju.item.EventList;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class EventRP implements Serializable {

    @SerializedName("status")
    private String status;

    @SerializedName("message")
    private String message;

    @SerializedName("EVENT_APP")
    private List<EventList> eventLists;

    public String getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }

    public List<EventList> getEventLists() {
        return eventLists;
    }
}
