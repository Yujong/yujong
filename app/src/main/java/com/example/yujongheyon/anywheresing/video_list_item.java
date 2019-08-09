package com.example.yujongheyon.anywheresing;

/**
 * Created by yujongheyon on 2018-09-28.
 */

public class video_list_item {

    private String video_title;
    private String video_creater_id;
    private String video_create_time;
    private String video_name;


    public video_list_item(String video_title,String video_creater_id,String video_create_time,String video_name)
    {
        this.video_title = video_title;
        this.video_creater_id = video_creater_id;
        this.video_create_time = video_create_time;
        this.video_name = video_name;
    }

    public String getVideo_title() {
        return video_title;
    }

    public void setVideo_title(String video_title) {
        this.video_title = video_title;
    }

    public String getVideo_creater_id() {
        return video_creater_id;
    }

    public void setVideo_creater_id(String video_creater_id) {
        this.video_creater_id = video_creater_id;
    }

    public String getVideo_create_time() {
        return video_create_time;
    }

    public void setVideo_create_time(String video_create_time) {
        this.video_create_time = video_create_time;
    }

    public String getVideo_name() {
        return video_name;
    }

    public void setVideo_name(String video_name) {
        this.video_name = video_name;
    }
}
