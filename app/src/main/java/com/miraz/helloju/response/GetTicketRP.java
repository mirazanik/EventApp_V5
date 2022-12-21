package com.miraz.helloju.response;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class GetTicketRP implements Serializable {

    @SerializedName("status")
    private String status;

    @SerializedName("message")
    private String message;

    @SerializedName("name")
    private String name;

    @SerializedName("email")
    private String email;

    @SerializedName("phone")
    private String phone;

    @SerializedName("address")
    private String address;

    @SerializedName("person_wise_ticket")
    private int person_wise_ticket;

    public String getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getPhone() {
        return phone;
    }

    public String getAddress() {
        return address;
    }

    public int getPerson_wise_ticket() {
        return person_wise_ticket;
    }
}
