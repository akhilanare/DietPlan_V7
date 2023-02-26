package com.example.dietplan.util;

import com.example.dietplan.BuildConfig;
import com.example.dietplan.item.DietPlanList;
import com.example.dietplan.response.AppRP;

import java.util.ArrayList;
import java.util.List;

public class Constant {

    //Change WebView text color light and dark mode
    public static String webViewText = "#8b8b8b;";
    public static String webViewTextDark = "#FFFFFF;";

    //Change WebView link color light and dark mode
    public static String webViewLink = "#0782C1;";
    public static String webViewLinkDark = "#5387ED;";

    public static final String AD_TYPE_ADMOB = "admob";
    public static final String AD_TYPE_FACEBOOK = "facebook";
    public static final String AD_TYPE_STARTAPP = "startapp";
    public static final String AD_TYPE_APPLOVIN = "applovins";

    //youtube api key
    public static String YOUR_DEVELOPER_KEY = BuildConfig.My_youtube_api_key;

    public static final String YOUTUBE_IMAGE_FRONT = "http://img.youtube.com/vi/";
    public static final String YOUTUBE_SMALL_IMAGE_BACK = "/hqdefault.jpg";

    public static int AD_COUNT = 0, interstitialAdShow = 0, nativeAdPos = 5;

    public static AppRP appRP;

}
