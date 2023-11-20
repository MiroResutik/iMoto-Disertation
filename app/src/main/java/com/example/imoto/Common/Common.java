package com.example.imoto.Common;

import android.support.annotation.NonNull;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Common {
    public static String API_KEY = "0ec6d0deb6ac1907a613c8489fdd48dc";
    public static String API_LINK = "https://api.openweathermap.org/data/2.5/weather"
;
    //Method to build api request query string
    @NonNull
    public static String apiRequest(String lat, String lng){
        StringBuilder sb = new StringBuilder(API_LINK);
        sb.append(String.format("?lat=%s&lon=%s&APPID=%s&units=metric", lat, lng, API_KEY));
        return sb.toString();

    }

    public static String unixTimeStampToDateTime(double unixTimeStamp){
        DateFormat dateFormat = new SimpleDateFormat("HH:mm");
        Date date = new Date();
        date.setTime((long)unixTimeStamp*1000);
        return dateFormat.format(date);

    }

    //Method to get weather image
    @NonNull
    public static String getImage(String icon){
        return String.format("https://api.openweathermap.org/img/w/%s.png", icon);

    }

    public static String getDateNow(){
        DateFormat dateFormat = new SimpleDateFormat(" dd  MMMM yyyy HH:mm");
        Date date = new Date();
        return dateFormat.format(date);
    }
}
