package com.example.yujongheyon.anywheresing;

/**
 * Created by yujongheyon on 2018-07-13.
 */

public class Utility {

    public static String convertDuration(long duration)
    {

        long minutes = (duration / 1000) / 60;
        long seconds = (duration / 1000) % 60;

        String converted = String.format("%d:%02d", minutes, seconds);
        return converted;


    }
}