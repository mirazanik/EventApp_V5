package com.miraz.helloju.response;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class TicketViewRP implements Serializable {

    @SerializedName("status")
    private String status;

    @SerializedName("message")
    private String message;

    @SerializedName("success")
    private String success;

    @SerializedName("msg")
    private String msg;

    @SerializedName("url")
    private String url;

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

    public String getUrl() {
        return url;
    }
}
