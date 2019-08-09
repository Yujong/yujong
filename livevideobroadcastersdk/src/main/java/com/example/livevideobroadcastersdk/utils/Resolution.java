package com.example.livevideobroadcastersdk.utils;

import java.io.Serializable;

/**
 * Created by yujongheyon on 2018-08-11.
 */

public class Resolution implements Serializable
{
    public final int width;
    public final int height;

    public Resolution(int width, int height)
    {
        this.width = width;
        this.height = height;
    }
}
