package com.hoangnguyen.onlinenewspapers.commom;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import org.ocpsoft.prettytime.PrettyTime;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Random;

public class Utils {
    public static ColorDrawable[] vibrantLightColorList =
            {
                    new ColorDrawable(Color.parseColor("#ffeead")),
                    new ColorDrawable(Color.parseColor("#93cfb3")),
                    new ColorDrawable(Color.parseColor("#fd7a7a")),
                    new ColorDrawable(Color.parseColor("#faca5f")),
                    new ColorDrawable(Color.parseColor("#1ba798")),
                    new ColorDrawable(Color.parseColor("#6aa9ae")),
                    new ColorDrawable(Color.parseColor("#ffbf27")),
                    new ColorDrawable(Color.parseColor("#d93947"))
            };

    public static ColorDrawable getRandomDrawbleColor() {
        int idx = new Random().nextInt(vibrantLightColorList.length);
        return vibrantLightColorList[idx];
    }

    public static String DateToTimeFormat(String oldstringDate) {
        PrettyTime p = new PrettyTime(new Locale(getCountry()));
        //PrettyTime p = new PrettyTime();
        String isTime = null;
        try {
            // SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.ENGLISH);
            SimpleDateFormat sdf = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss Z",Locale.ENGLISH );
            Date date = sdf.parse(oldstringDate);
            isTime = p.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return isTime;
    }
    public static String DateFormatTuoiTre(String oldstringDate) {
        String newDate;
        //SimpleDateFormat dateFormat = new SimpleDateFormat("E, d MMM yyyy", new Locale(getCountry()));
        SimpleDateFormat dateFormat = new SimpleDateFormat("E, d MMM yyyy", new Locale(getCountry()));
        try {
            // Date date = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'").parse(oldstringDate); //"2020-08-24T08:57:05Z"
            SimpleDateFormat sdf = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss 'GMT'Z",Locale.ENGLISH);
            Date date = sdf.parse(oldstringDate);
            newDate = dateFormat.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
            newDate = oldstringDate;
        }

        return newDate;
    }
    public static String DateFormat(String oldstringDate) {
        String newDate;
        //SimpleDateFormat dateFormat = new SimpleDateFormat("E, d MMM yyyy", new Locale(getCountry()));
        SimpleDateFormat dateFormat = new SimpleDateFormat("E, d MMM yyyy", new Locale(getCountry()));
        try {
            // Date date = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'").parse(oldstringDate); //"2020-08-24T08:57:05Z"
            SimpleDateFormat sdf = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss Z",Locale.ENGLISH);
            Date date = sdf.parse(oldstringDate);
            newDate = dateFormat.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
            newDate = oldstringDate;
        }

        return newDate;
    }

    public static String getCountry() {
        Locale locale = Locale.getDefault();
        String country = String.valueOf(locale.getCountry());
        return country.toLowerCase();
    }

    public static String getLanguage() {
        Locale locale = Locale.getDefault();
        String country = String.valueOf(locale.getLanguage());
        return country.toLowerCase();
    }

    public static Boolean isOnline(Context mContext) {
        ConnectivityManager cm = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo ni = cm.getActiveNetworkInfo();
        if (ni != null && ni.isConnected()) {
            return true;
        }
        return false;
    }

    public static String getLinkImage(String s) {
        int indexStartUrl = s.indexOf("url=");
        int indexStartSrc = s.indexOf("src=");
        int indexEndJpg = s.indexOf(".jpg");
        int indexEndJPG = s.indexOf(".JPG");
        int indexEndJpeg = s.indexOf(".jpeg");
        int indexEndPng = s.indexOf(".png");
        int indexEndGIF = s.indexOf(".gif");
        if (indexStartUrl != -1 && indexEndJpg != -1) {
            return s.substring(indexStartUrl + 5, indexEndJpg + 4);
        } else if (indexStartUrl != -1 && indexEndJPG != -1) {
            return s.substring(indexStartUrl + 5, indexEndJPG + 4);
        } else if (indexStartUrl != -1 && indexEndJpeg != -1) {
            return s.substring(indexStartUrl + 5, indexEndJpeg + 5);
        } else if (indexStartUrl != -1 && indexEndPng != -1) {
            return s.substring(indexStartUrl + 5, indexEndPng + 4);
        } else if (indexStartUrl != -1 && indexEndGIF != -1) {
            return s.substring(indexStartUrl + 5, indexEndGIF + 4);
        } else if (indexStartSrc != -1 && indexEndJpg != -1) {
            return s.substring(indexStartSrc + 5, indexEndJpg + 4);
        } else if (indexStartSrc != -1 && indexEndJPG != -1) {
            return s.substring(indexStartSrc + 5, indexEndJPG + 4);
        } else if (indexStartSrc != -1 && indexEndJpeg != -1) {
            return s.substring(indexStartSrc + 5, indexEndJpeg + 5);
        } else if (indexStartSrc != -1 && indexEndPng != -1) {
            return s.substring(indexStartSrc + 5, indexEndPng + 4);
        } else if (indexStartSrc != -1 && indexEndGIF != -1) {
            return s.substring(indexStartSrc + 5, indexEndGIF + 4);
        } else {
            return "";
        }
    }
}
