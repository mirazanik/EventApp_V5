package com.miraz.helloju.response;

import com.miraz.helloju.item.CategoryList;
import com.miraz.helloju.item.GalleryList;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class EditEventRP implements Serializable {

    @SerializedName("status")
    private String status;

    @SerializedName("message")
    private String message;

    @SerializedName("is_event")
    private boolean is_event;

    @SerializedName("event_title")
    private String event_title;

    @SerializedName("id")
    private String id;

    @SerializedName("cat_id")
    private String cat_id;

    @SerializedName("event_email")
    private String event_email;

    @SerializedName("event_phone")
    private String event_phone;

    @SerializedName("event_website")
    private String event_website;

    @SerializedName("event_ticket")
    private String event_ticket;

    @SerializedName("person_wise_ticket")
    private String person_wise_ticket;

    @SerializedName("ticket_price")
    private String ticket_price;

    @SerializedName("event_description")
    private String event_description;

    @SerializedName("event_address")
    private String event_address;

    @SerializedName("event_map_latitude")
    private String event_map_latitude;

    @SerializedName("event_map_longitude")
    private String event_map_longitude;

    @SerializedName("EVENT_APP")
    private List<CategoryList> categoryLists;

    @SerializedName("event_logo")
    private String event_logo;

    @SerializedName("event_logo_thumb")
    private String event_logo_thumb;

    @SerializedName("event_banner")
    private String event_banner;

    @SerializedName("event_banner_thumb")
    private String event_banner_thumb;

    @SerializedName("cover_images")
    private List<GalleryList> galleryLists;

    @SerializedName("event_start_date")
    private String event_start_date;

    @SerializedName("event_start_time")
    private String event_start_time;

    @SerializedName("event_end_date")
    private String event_end_date;

    @SerializedName("event_end_time")
    private String event_end_time;

    @SerializedName("registration_start_date")
    private String registration_start_date;

    @SerializedName("registration_start_time")
    private String registration_start_time;

    @SerializedName("registration_end_date")
    private String registration_end_date;

    @SerializedName("registration_end_time")
    private String registration_end_time;

    @SerializedName("edit_event_start_date")
    private String edit_event_start_date;

    @SerializedName("edit_event_start_time")
    private String edit_event_start_time;

    @SerializedName("edit_event_end_date")
    private String edit_event_end_date;

    @SerializedName("edit_event_end_time")
    private String edit_event_end_time;

    @SerializedName("edit_registration_start_date")
    private String edit_registration_start_date;

    @SerializedName("edit_registration_start_time")
    private String edit_registration_start_time;

    @SerializedName("edit_registration_end_date")
    private String edit_registration_end_date;

    @SerializedName("edit_registration_end_time")
    private String edit_registration_end_time;

    public String getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }

    public boolean isIs_event() {
        return is_event;
    }

    public String getEvent_title() {
        return event_title;
    }

    public String getId() {
        return id;
    }

    public String getCat_id() {
        return cat_id;
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

    public String getEvent_ticket() {
        return event_ticket;
    }

    public String getPerson_wise_ticket() {
        return person_wise_ticket;
    }

    public String getTicket_price() {
        return ticket_price;
    }

    public String getEvent_description() {
        return event_description;
    }

    public String getEvent_address() {
        return event_address;
    }

    public String getEvent_map_latitude() {
        return event_map_latitude;
    }

    public String getEvent_map_longitude() {
        return event_map_longitude;
    }

    public List<CategoryList> getCategoryLists() {
        return categoryLists;
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

    public List<GalleryList> getGalleryLists() {
        return galleryLists;
    }

    public String getEvent_start_date() {
        return event_start_date;
    }

    public String getEvent_start_time() {
        return event_start_time;
    }

    public String getEvent_end_date() {
        return event_end_date;
    }

    public String getEvent_end_time() {
        return event_end_time;
    }

    public String getRegistration_start_date() {
        return registration_start_date;
    }

    public String getRegistration_start_time() {
        return registration_start_time;
    }

    public String getRegistration_end_date() {
        return registration_end_date;
    }

    public String getRegistration_end_time() {
        return registration_end_time;
    }

    public String getEdit_event_start_date() {
        return edit_event_start_date;
    }

    public String getEdit_event_start_time() {
        return edit_event_start_time;
    }

    public String getEdit_event_end_date() {
        return edit_event_end_date;
    }

    public String getEdit_event_end_time() {
        return edit_event_end_time;
    }

    public String getEdit_registration_start_date() {
        return edit_registration_start_date;
    }

    public String getEdit_registration_start_time() {
        return edit_registration_start_time;
    }

    public String getEdit_registration_end_date() {
        return edit_registration_end_date;
    }

    public String getEdit_registration_end_time() {
        return edit_registration_end_time;
    }
}
