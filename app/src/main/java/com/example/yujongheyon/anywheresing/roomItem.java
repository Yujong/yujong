package com.example.yujongheyon.anywheresing;

/**
 * Created by yujongheyon on 2018-07-25.
 */

public class roomItem {


    private String room_title;
    private String room_creater_nick;
    private String room_create_time;
    private int room_number;
    private String room_person;




    public roomItem(String room_title, String room_creater_nick,String room_create_time,int room_number,String room_person)
    {
        this.room_title = room_title;
        this.room_creater_nick = room_creater_nick;
        this.room_create_time = room_create_time;
        this.room_number = room_number;
        this.room_person = room_person;

    }

    public int getRoom_number() {
        return room_number;
    }

    public void setRoom_number(int room_number) {
        this.room_number = room_number;
    }

    public String getRoom_title() {
        return room_title;
    }

    public void setRoom_title(String room_title) {
        this.room_title = room_title;
    }

    public String getRoom_creater_nick() {
        return room_creater_nick;
    }

    public void setRoom_creater_nick(String room_creater_nick) {
        this.room_creater_nick = room_creater_nick;
    }

    public String getRoom_create_time() {
        return room_create_time;
    }

    public void setRoom_create_time(String room_create_time) {
        this.room_create_time = room_create_time;
    }

    public String getRoom_person() {
        return room_person;
    }

    public void setRoom_person(String room_person) {
        this.room_person = room_person;
    }
}
