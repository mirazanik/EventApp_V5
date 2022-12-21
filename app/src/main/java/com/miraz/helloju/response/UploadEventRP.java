package com.miraz.helloju.response;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class UploadEventRP implements Serializable {

    @SerializedName("status")
    private String status;

    @SerializedName("message")
    private String message;

    @SerializedName("success")
    private String success;

    @SerializedName("msg")
    private String msg;

    @SerializedName("event_title")
    private String event_title;

    @SerializedName("event_date")
    private String event_date;

    @SerializedName("event_banner_thumb")
    private String event_banner_thumb;

    @SerializedName("event_address")
    private String event_address;

    public String getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }

    public String getSuccess() {
        return success;
    }

    public String getMsg() {
        return msg;
    }

    public String getEvent_title() {
        return event_title;
    }

    public String getEvent_date() {
        return event_date;
    }

    public String getEvent_banner_thumb() {
        return event_banner_thumb;
    }

    public String getEvent_address() {
        return event_address;
    }
}
