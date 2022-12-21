package com.miraz.helloju.response;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class AppRP implements Serializable {

    @SerializedName("status")
    private String status;

    @SerializedName("message")
    private String message;

    @SerializedName("publisher_id")
    private String publisher_id;

    @SerializedName("banner_ad")
    private boolean banner_ad;

    @SerializedName("banner_ad_id")
    private String banner_ad_id;

    @SerializedName("banner_ad_type")
    private String banner_ad_type;

    @SerializedName("interstitial_ad")
    private boolean interstitial_ad;

    @SerializedName("interstitial_ad_id")
    private String interstitial_ad_id;

    @SerializedName("interstitial_ad_click")
    private String interstitial_ad_click;

    @SerializedName("interstitial_ad_type")
    private String interstitial_ad_type ;

    @SerializedName("nativ_ad")
    private boolean native_ad;

    @SerializedName("nativ_ad_id")
    private String native_ad_id;

    @SerializedName("nativ_ad_position")
    private String native_ad_position;

    @SerializedName("nativ_ad_type")
    private String native_ad_type ;

    @SerializedName("startapp_app_id")
    private String startapp_app_id ;

    @SerializedName("privacy_policy_link")
    private String privacy_policy_link;

    @SerializedName("app_update_status")
    private String app_update_status ;

    @SerializedName("app_new_version")
    private int app_new_version ;

    @SerializedName("app_update_desc")
    private String app_update_desc ;

    @SerializedName("app_redirect_url")
    private String app_redirect_url ;

    @SerializedName("cancel_update_status")
    private String cancel_update_status ;

    public String getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }

    public String getPublisher_id() {
        return publisher_id;
    }

    public boolean isBanner_ad() {
        return banner_ad;
    }

    public String getBanner_ad_id() {
        return banner_ad_id;
    }

    public String getBanner_ad_type() {
        return banner_ad_type;
    }

    public boolean isInterstitial_ad() {
        return interstitial_ad;
    }

    public String getInterstitial_ad_id() {
        return interstitial_ad_id;
    }

    public String getInterstitial_ad_click() {
        return interstitial_ad_click;
    }

    public String getInterstitial_ad_type() {
        return interstitial_ad_type;
    }

    public String getPrivacy_policy_link() {
        return privacy_policy_link;
    }

    public String getApp_update_status() {
        return app_update_status;
    }

    public int getApp_new_version() {
        return app_new_version;
    }

    public String getApp_update_desc() {
        return app_update_desc;
    }

    public String getApp_redirect_url() {
        return app_redirect_url;
    }

    public String getCancel_update_status() {
        return cancel_update_status;
    }

    public boolean isNative_ad() {
        return native_ad;
    }

    public String getNative_ad_id() {
        return native_ad_id;
    }

    public String getNative_ad_position() {
        return native_ad_position;
    }

    public String getNative_ad_type() {
        return native_ad_type;
    }

    public String getStartapp_app_id() {
        return startapp_app_id;
    }
}
