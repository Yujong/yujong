package com.example.yujongheyon.anywheresing;

import android.util.Log;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by yujongheyon on 2018-07-23.
 */

public class roomRequest extends StringRequest
{


    final static private String URL = "http://ec2-18-188-84-158.us-east-2.compute.amazonaws.com/roomCreate.php";

    //에뮬레이터 값저장 확인완료, 모바일 어플리케이션 구동안됨

    private Map<String, String> paremeters;

    /*

    방아이템 ->방제,방만든시간,방생성자 닉네임,사람 full인지 아닌지 여부,
 full인지 아닌지 여부,???


    방에 더이상못들어가거나 듀엣꽉차면

   setenable = false

   새로고침버튼 만들기 (스타 게임방같은개념)
    4
    */
    public roomRequest(String room_title, String room_creater_Id, String room_create_time,String stream_or_duet,Response.Listener<String> listener)
    {
        super(Request.Method.POST,URL,listener,null);
        paremeters = new HashMap<>();
        paremeters.put("room_title",room_title);
        paremeters.put("room_creater_Id",room_creater_Id);
        paremeters.put("room_create_time",room_create_time);
        paremeters.put("stream_or_duet",stream_or_duet);
        //paremeters.put("room_isplaying", String.valueOf(isplaying));


        Log.d("volley","요청 성공");

        Log.d("loooooooom",room_title);

        Log.d("loooooooom",room_creater_Id);

        Log.d("loooooooom",room_create_time);



//        Log.d("php connection",signup_password);
        //       Log.d("php signup_image",userImage);
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
