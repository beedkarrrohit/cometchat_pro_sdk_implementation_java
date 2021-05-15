package com.example.cometchatprojava.utils;

import android.content.Context;
import android.content.pm.PackageManager;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import java.util.*;
import java.text.SimpleDateFormat;

public class Helper {
    public static String convertTimeStamp(long timeStamp){
        String time = new SimpleDateFormat("h:mm a").format(new Date(timeStamp * 1000));
        return time;
    }

    public static boolean permissions(Context context, String permissions){
        if(ActivityCompat.checkSelfPermission(context,permissions)!= PackageManager.PERMISSION_GRANTED){
            return false;
        }else {
            return true;
        }
    }

    public static String getTimeDate(long timeStamp){
        String time = new SimpleDateFormat("h:mm a").format(new Date(timeStamp * 1000));
        String date = new SimpleDateFormat("dd/mm/yyyy").format(new Date(timeStamp * 1000));
        String week = new SimpleDateFormat("EEE").format(new Date(timeStamp * 1000));
        long compareTime = System.currentTimeMillis()- timeStamp*1000;
        if(compareTime < 24 * 60 * 60 * 1000){
            return time;
        }else if(compareTime < 48*60*60*1000){
            return "Yesterday";
        }else if(compareTime < 7*24*60*60*1000){
            return week;
        }else{
            return date;
        }
    }
}
