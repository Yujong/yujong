package com.example.yujongheyon.anywheresing;

import android.util.Log;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by yujongheyon on 2018-09-28.
 */

public class video_requset extends StringRequest
{

    final static private String URL = "http://ec2-18-188-84-158.us-east-2.compute.amazonaws.com/video_list_create.php";
    private Map<String, String> paremeters;

    public video_requset(String video_title,String video_creater_Id,String video_create_time,String video_name,Response.Listener<String> listener)
    {
        super(Request.Method.POST,URL,listener,null);
        paremeters = new HashMap<>();
        paremeters.put("video_title",video_title);
        paremeters.put("video_creater_Id",video_creater_Id);
        paremeters.put("video_create_time",video_create_time);
        paremeters.put("video_name",video_name);

        new Response.ErrorListener()
        {
            @Override
            public void onErrorResponse(VolleyError error)
            {
                Log.d("통신에러","통신에러");
            }
        };
    }




    @Override
    public Map<String, String>getParams()
    {
        return paremeters;
    }

}
