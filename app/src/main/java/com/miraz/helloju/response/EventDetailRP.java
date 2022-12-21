package com.miraz.helloju.response;

import com.miraz.helloju.item.GalleryList;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class EventDetailRP implements Serializable {

    @SerializedName("status")
    private String status;

    @SerializedName("message")
    private String message;

    @SerializedName("success")
    private String success;

    @SerializedName("msg")
    private String msg;

    @SerializedName("id")
    private String id;

    @SerializedName("is_fav")
    private boolean is_fav;

    @SerializedName("booking_id")
    private String booking_id;

    @SerializedName("cat_id")
    private String cat_id;

    @SerializedName("event_logo")
    private String event_logo;

    @SerializedName("event_logo_thumb")
    private String event_logo_thumb;

    @SerializedName("event_banner")
    private String event_banner;

    @SerializedName("event_banner_thumb")
    private String event_banner_thumb;

    @SerializedName("event_title")
    private String event_title;

    @SerializedName("event_address")
    private String event_address;

    @SerializedName("event_date_time")
    private String event_date_time;

    @SerializedName("event_registration_date_time")
    private String event_registration_date_time;

    @SerializedName("event_email")
    private String event_email;

    @SerializedName("event_phone")
    private String event_phone;

    @SerializedName("event_website")
    private String event_website;

    @SerializedName("event_description")
    private String event_description;

    @SerializedName("event_map_latitude")
    private String event_map_latitude;

    @SerializedName("event_map_longitude")
    private String event_map_longitude;

    @SerializedName("event_ticket")
    private String event_ticket;

    @SerializedName("ticket_price")
    private String ticket_price;

    @SerializedName("remain_tickets")
    private String remain_tickets;

    @SerializedName("is_booking")
    private boolean is_booking;

    @SerializedName("is_userList")
    private boolean is_userList;

    @SerializedName("share_link")
    private String share_link;

    @SerializedName("cover_images")
    private List<GalleryList> galleryLists;

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

    public String getId() {
        return id;
    }

    public boolean isIs_fav() {
        return is_fav;
    }

    public String getBooking_id() {
        return booking_id;
    }

    public String getCat_id() {
        return cat_id;
    }

    public String getEvent_logo() {
        return event_logo;
    }

    public String getEvent_logo_thumb() {
        return event_logo_thumb;
    }

    public String getEvent_banner() {
        return event_banner;
    }

    public String getEvent_banner_thumb() {
        return event_banner_thumb;
    }

    public String getEvent_title() {
        return event_title;
    }

    public String getEvent_address() {
        return event_address;
    }

    public String getEvent_date_time() {
        return event_date_time;
    }

    public String getEvent_registration_date_time() {
        return event_registration_date_time;
    }

    public String getEvent_email() {
        return event_email;
    }

    public String getEvent_phone() {
        return event_phone;
    }

    public String getEvent_website() {
        return event_website;
    }

    public String getEvent_description() {
        return event_description;
    }

    public String getEvent_map_latitude() {
        return event_map_latitude;
    }

    public String getEvent_map_longitude() {
        return event_map_longitude;
    }

    public String getEvent_ticket() {
        return event_ticket;
    }

    public String getTicket_price() {
        return ticket_price;
    }

    public String getRemain_tickets() {
        return remain_tickets;
    }

    public boolean isIs_booking() {
        return is_booking;
    }

    public boolean isIs_userList() {
        return is_userList;
    }

    public String getShare_link() {
        return share_link;
    }

    public List<GalleryList> getGalleryLists() {
        return galleryLists;
    }
}
