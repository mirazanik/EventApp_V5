package com.miraz.helloju.util;

import android.os.Environment;

import com.miraz.helloju.item.GalleryList;
import com.miraz.helloju.response.AppRP;

import java.util.ArrayList;
import java.util.List;

public class Constant {

    //storage folder path
    public static String appStorage = Environment.getExternalStorageDirectory() + "/" + Environment.DIRECTORY_DOWNLOADS;

    //Change WebView text color light and dark mode
    public static String webViewText = "#8b8b8b;";
    public static String webViewTextDark = "#FFFFFF;";

    //Change WebView link color light and dark mode
    public static String webViewLink = "#0782C1;";
    public static String webViewLinkDark = "#a9793b;";

    public static final String AD_TYPE_ADMOB = "admob";
    public static final String AD_TYPE_FACEBOOK = "facebook";
    public static final String AD_TYPE_STARTAPP = "startapp";
    public static final String AD_TYPE_APPLOVIN = "applovins";

    public static int AD_COUNT = 0;
    public static int AD_COUNT_SHOW = 0;

    public static String stringLatitude = "";
    public static String stringLongitude = "";

    public static AppRP appRP;

    public static List<GalleryList> galleryLists = new ArrayList<>();
}