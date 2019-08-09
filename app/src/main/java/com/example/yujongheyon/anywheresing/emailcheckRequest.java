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

public class emailcheckRequest extends StringRequest
{

    final static private String URL = "http://ec2-18-188-84-158.us-east-2.compute.amazonaws.com/emailcheck.php";

    //에뮬레이터 값저장 확인완료, 모바일 어플리케이션 구동안됨

    private Map<String, String> paremeters;

    public emailcheckRequest(String emailcheck,Response.Listener<String> listener)
    {
        super(Method.POST,URL,listener,null);
        paremeters = new HashMap<>();
        paremeters.put("emailcheck",emailcheck);
        Log.d("volley","이메일 중복확인");


        Log.d("php connection",emailcheck);

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
