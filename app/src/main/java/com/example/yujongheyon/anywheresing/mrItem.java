package com.example.yujongheyon.anywheresing;

import android.widget.SeekBar;

/**
 * Created by yujongheyon on 2018-07-10.
 */

public class mrItem {

private int coverImage;
private String mr_title;
private String artist;




    public mrItem(int coverImage, String mr_title, String artist)
    {
        this.coverImage = coverImage;
        this.mr_title = mr_title;
        this.artist = artist;

    }



    public int getCoverImage() {
        return coverImage;
    }

    public void setCoverImage(int coverImage) {
        this.coverImage = coverImage;
    }

    public String getMr_title() {
        return mr_title;
    }

    public void setMr_title(String title) {
        this.mr_title = title;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }
}
