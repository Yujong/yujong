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

public class LoginRequest extends StringRequest
{

    final static private String URL = "http://ec2-18-188-84-158.us-east-2.compute.amazonaws.com/login.php";

    //에뮬레이터 값저장 확인완료, 모바일 어플리케이션 구동안됨 -> 디바이스 ip확인후 문제해결

    private Map<String, String> paremeters;

    public LoginRequest(String login_email, String login_password, Response.Listener<String> listener)
    {
        super(Method.POST,URL,listener,null);
        paremeters = new HashMap<>();
        paremeters.put("login_email",login_email);
        paremeters.put("login_password",login_password);



        Log.d("volley","요청 성공");


        Log.d("php connection",login_email);
        Log.d("php connection",login_password);

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
