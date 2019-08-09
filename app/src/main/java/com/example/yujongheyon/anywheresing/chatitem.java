package com.example.yujongheyon.anywheresing;

/**
 * Created by yujongheyon on 2018-07-30.
 */

public class chatitem {

    String chat_Id;
    String chat_content;

    public chatitem(String chat_content)
    {

        this.chat_content = chat_content;
    }


    public String getChat_content() {
        return chat_content;
    }

    public void setChat_content(String chat_content) {
        this.chat_content = chat_content;
    }
}
