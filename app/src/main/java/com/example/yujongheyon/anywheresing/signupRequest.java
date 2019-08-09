package com.example.yujongheyon.anywheresing;

import android.util.Log;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by yujongheyon on 2018-06-25.
 */

public class signupRequest extends StringRequest
{

    final static private String URL = "http://ec2-18-188-84-158.us-east-2.compute.amazonaws.com/signup.php";

    //에뮬레이터 값저장 확인완료, 모바일 어플리케이션 구동안됨

    private Map<String, String> paremeters;

    public signupRequest(String signup_email, String signup_nickname, String signup_password,String signup_gender,String userImage, Response.Listener<String> listener)
    {
        super(Method.POST,URL,listener,null);
        paremeters = new HashMap<>();
        paremeters.put("singup_email",signup_email);
        paremeters.put("singup_nickname",signup_nickname);
        paremeters.put("singup_password",signup_password);
        paremeters.put("signup_gender",signup_gender);
        paremeters.put("signup_image",userImage);





        Log.d("volley","요청 성공");


        Log.d("php connection",signup_email);
        Log.d("php connection",signup_nickname);
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
