package com.miraz.helloju.response;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class TicketBookRP implements Serializable {

    @SerializedName("status")
    private String status;

    @SerializedName("message")
    private String message;

    @SerializedName("msg")
    private String msg;

    @SerializedName("success")
    private String success;

    @SerializedName("person_wise_ticket")
    private int person_wise_ticket;

    public String getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }

    public String getMsg() {
        return msg;
    }

    public String getSuccess() {
        return success;
    }

    public int getPerson_wise_ticket() {
        return person_wise_ticket;
    }
}
