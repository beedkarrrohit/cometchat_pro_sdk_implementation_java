package com.example.cometchatprojava.utils;

import com.cometchat.pro.core.AppSettings;

public class Constants {
    public static String app_id = "3311511a27b441a";
    public static String region = "us";
    public static String auth_key = "6a4db0efc4e09f67fe6a6ae27599659385ddd7ce";
    /*public static String app_id = "32117cf666b9454";
    public static String region = "us";
    public static String auth_key = "b3c172e90d839d00e43f3d8037d8188f9b5fcba9";*/
    public static AppSettings appSettings = new AppSettings.AppSettingsBuilder().subscribePresenceForAllUsers().setRegion(region).build();
}
