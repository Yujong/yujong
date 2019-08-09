package com.example.yujongheyon.anywheresing;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.content.Context.MODE_PRIVATE;

/*
import com.google.android.youtube.player.YouTubeBaseActivity;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerView;*/


/**
 * Created by yujongheyon on 2018-07-04.
 */

public class anywheresing_mypage extends Fragment{

    TextView myinfo_email;
    TextView myinfo_nickname;
    TextView myinfo_gender;
    ImageView myinfo_image;
    String userId;
    String nickname;
    String userimage;
    String gender;
    Button logout;


    String sessionId;

    int requestCode;

    String opencv_image;

    List<JSONObject> objects = new ArrayList<JSONObject>();


    //String openCV_image;
    public anywheresing_mypage()
    {

    }
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


            Bundle bundle = getArguments();

            userId = bundle.getString("useremail");

            requestCode = bundle.getInt("requestcode");
            Log.d("sogmasogsho",userId);

            Log.d("sdgshashahawrhhwwh","sdgshashahawrhhwwh");

            Log.d("resultCODE", String.valueOf(requestCode));


            opencv_image  = bundle.getString("opencv_image");
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.mypage, container, false);


        //email,닉네임,성별,좋아요누른글,내글,친구목록,
        myinfo_email = (TextView) view.findViewById(R.id.myinfo_email);

        myinfo_nickname = (TextView) view.findViewById(R.id.myinfo_nickname);

        myinfo_gender = (TextView) view.findViewById(R.id.myinfo_gender);

        myinfo_image= (ImageView)view.findViewById(R.id.myinfo_image);

        myinfo_email.setText(userId);

        myinfo_nickname.setText("ert");
        myinfo_gender.setText("남성");





        myinfo_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    Intent intent = new Intent(getActivity(),anywhereopenCV.class);
                    startActivity(intent);

            }
        });





    if (opencv_image != null)
    {

        byte [] encodeByte= Base64.decode(opencv_image,Base64.DEFAULT);
        Bitmap open_bitmap= BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.length);

        Glide.with(anywheresing_mypage.this).load(open_bitmap).apply(RequestOptions.overrideOf(650,650).circleCrop()).into(myinfo_image);


    }
    else
    {
        imageRequest();
    }



        if (requestCode == 1) //자체 회원가입 정보
        {
            myloginrequest();
        }
        else if (requestCode == 2) //facebook
        {
            facebookRequest();
        }
        else if (requestCode == 3) // google
        {
            googleloginRequest();
        }
        else  if (requestCode ==4) //kakao
        {
           kakaologinRequest();
        }

    //       imageRequest();
//getArguments.getString("userEmail");

      /*  Bundle bundle = getArguments();

        String userId = bundle.getString("useremail");*/
/*
        String userId = getArguments().getString("useremail");

        myinfo_email.setText(userId);

        Log.d("dgsdgaiwrjgwrih",userId);*/

      /*

        Bundle bundle = getArguments();
        onCreate(bundle);
        if (bundle != null)
        {
            userId = bundle.getString("useremail");
            Log.d("프래그먼트아이디",userId);

        }
        if (bundle == null)
        {

            Log.d("sdagadgadsgasgd","Adsgasdgasdgasdgasg");
        }

*/
        Context context = getActivity();
        SharedPreferences pref = context.getSharedPreferences("session", MODE_PRIVATE);

        sessionId = pref.getString("session","");

        logout = (Button)view.findViewById(R.id.logout);
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                Context context = getActivity();
                SharedPreferences pref = context.getSharedPreferences("session", MODE_PRIVATE);
                SharedPreferences.Editor editor = pref.edit();
                editor.remove("session");
                editor.commit();

                Intent intent = new Intent(getActivity(),anywheresing_loginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY );
                startActivity(intent);
                getActivity().finish();
            }
        });



        return view;
    }


    public void  myloginrequest()
{
    final RequestQueue queue = Volley.newRequestQueue(getActivity());
    String url = "http://ec2-18-188-84-158.us-east-2.compute.amazonaws.com/userinfo.php";
    StringRequest stringRequest = new  StringRequest(Request.Method.POST,url,new Response.Listener<String>()
    {

        @Override
        public void onResponse(String response)
        {
            try {

               /* String json = response;
                json = json.replace("\\\"","'");

                json.substring(json.indexOf("{"), json.lastIndexOf("}") + 1);*/

                /*JsonParser parser = new JsonParser();
                String retVal = parser.parse(response).getAsString();*/


          // JSONObject jsonObject = new JSONObject(response);


                JSONObject jsonobject = new JSONObject(response);
                JSONArray jsonarray = jsonobject.getJSONArray("member");

                for (int i = 0; i < jsonarray.length(); i++) {
                    //getting the json object of the particular index inside the array
                    JSONObject data = jsonarray.getJSONObject(i);

                    nickname  = data.getString("nickname");

                    Log.d("dsagsdgwriajg",nickname);

                    myinfo_nickname.setText(nickname);

                    gender = data.getString("gender");

                    myinfo_gender.setText(gender);

                    objects.add(data);
                }
        } catch (Exception e) {
        e.printStackTrace();
        Log.d("tnstngks tkfladl","fdgaddsagasdgasdgasdgsgsdg");
    }
        }
    }, new Response.ErrorListener() {
        @Override
        public void onErrorResponse(VolleyError error) {
            error.printStackTrace();
        }
    })
    {
        @Override
        protected Map<String,String> getParams() throws AuthFailureError {
            Map<String,String> parameters = new HashMap<String,String>();

            parameters.put("request_email",sessionId);

            Log.d("jr19jf",myinfo_email.getText().toString());
            return parameters;
        }
    };
    queue.add(stringRequest);

}
public void facebookRequest()
{
    final RequestQueue queue = Volley.newRequestQueue(getActivity());
    String url = "http://ec2-18-188-84-158.us-east-2.compute.amazonaws.com/facebookinfo.php";
    StringRequest stringRequest = new  StringRequest(Request.Method.POST,url,new Response.Listener<String>()
    {

        @Override
        public void onResponse(String response) {
            try {

               /* String json = response;
                json = json.replace("\\\"","'");

                json.substring(json.indexOf("{"), json.lastIndexOf("}") + 1);*/

                /*JsonParser parser = new JsonParser();
                String retVal = parser.parse(response).getAsString();*/


                // JSONObject jsonObject = new JSONObject(response);


                JSONObject jsonobject = new JSONObject(response);
                JSONArray jsonarray = jsonobject.getJSONArray("member");

                for (int i = 0; i < jsonarray.length(); i++) {
                    //getting the json object of the particular index inside the array
                    JSONObject data = jsonarray.getJSONObject(i);

                    nickname  = data.getString("nickname");

                    Log.d("dsagsdgwriajg",nickname);

                    myinfo_nickname.setText(nickname);

                    objects.add(data);
                }
            } catch (Exception e) {
                e.printStackTrace();
                Log.d("tnstngks tkfladl","fdgaddsagasdgasdgasdgsgsdg");
            }
        }
    }, new Response.ErrorListener() {
        @Override
        public void onErrorResponse(VolleyError error) {
            error.printStackTrace();
        }
    })
    {
        @Override
        protected Map<String,String> getParams() throws AuthFailureError {
            Map<String,String> parameters = new HashMap<String,String>();

            parameters.put("request_email",sessionId);

            Log.d("jr19jf",myinfo_email.getText().toString());
            return parameters;
        }
    };
    queue.add(stringRequest);

}
public void googleloginRequest()
{
    final RequestQueue queue = Volley.newRequestQueue(getActivity());
    String url = "http://ec2-18-188-84-158.us-east-2.compute.amazonaws.com/googleinfo.php";
    StringRequest stringRequest = new  StringRequest(Request.Method.POST,url,new Response.Listener<String>()
    {

        @Override
        public void onResponse(String response) {
            try {

               /* String json = response;
                json = json.replace("\\\"","'");

                json.substring(json.indexOf("{"), json.lastIndexOf("}") + 1);*/

                /*JsonParser parser = new JsonParser();
                String retVal = parser.parse(response).getAsString();*/


                // JSONObject jsonObject = new JSONObject(response);


                JSONObject jsonobject = new JSONObject(response);
                JSONArray jsonarray = jsonobject.getJSONArray("member");

                for (int i = 0; i < jsonarray.length(); i++) {
                    //getting the json object of the particular index inside the array
                    JSONObject data = jsonarray.getJSONObject(i);

                    nickname  = data.getString("nickname");

                    Log.d("dsagsdgwriajg",nickname);

                    myinfo_nickname.setText(nickname);

                    objects.add(data);
                }
            } catch (Exception e) {
                e.printStackTrace();
                Log.d("tnstngks tkfladl","fdgaddsagasdgasdgasdgsgsdg");
            }
        }
    }, new Response.ErrorListener() {
        @Override
        public void onErrorResponse(VolleyError error) {
            error.printStackTrace();
        }
    })
    {
        @Override
        protected Map<String,String> getParams() throws AuthFailureError {
            Map<String,String> parameters = new HashMap<String,String>();

            parameters.put("request_email",sessionId);

            Log.d("jr19jf",myinfo_email.getText().toString());
            return parameters;
        }
    };
    queue.add(stringRequest);

}
public void kakaologinRequest()
{
    final RequestQueue queue = Volley.newRequestQueue(getActivity());
    String url = "http://ec2-18-188-84-158.us-east-2.compute.amazonaws.com/kakaoinfo.php";
    StringRequest stringRequest = new  StringRequest(Request.Method.POST,url,new Response.Listener<String>()
    {

        @Override
        public void onResponse(String response) {
            try {

               /* String json = response;
                json = json.replace("\\\"","'");

                json.substring(json.indexOf("{"), json.lastIndexOf("}") + 1);*/

                /*JsonParser parser = new JsonParser();
                String retVal = parser.parse(response).getAsString();*/


                // JSONObject jsonObject = new JSONObject(response);


                JSONObject jsonobject = new JSONObject(response);
                JSONArray jsonarray = jsonobject.getJSONArray("member");

                for (int i = 0; i < jsonarray.length(); i++) {
                    //getting the json object of the particular index inside the array
                    JSONObject data = jsonarray.getJSONObject(i);

                    nickname  = data.getString("nickname");

                    Log.d("dsagsdgwriajg",nickname);

                    myinfo_nickname.setText(nickname);

                    objects.add(data);
                }
            } catch (Exception e) {
                e.printStackTrace();
                Log.d("tnstngks tkfladl","fdgaddsagasdgasdgasdgsgsdg");
            }
        }
    }, new Response.ErrorListener() {
        @Override
        public void onErrorResponse(VolleyError error) {
            error.printStackTrace();
        }
    })
    {
        @Override
        protected Map<String,String> getParams() throws AuthFailureError {
            Map<String,String> parameters = new HashMap<String,String>();

            parameters.put("request_email",sessionId);

            Log.d("jr19jf",myinfo_email.getText().toString());
            return parameters;
        }
    };
    queue.add(stringRequest);

}


public void imageRequest()
{

    RequestQueue queue = Volley.newRequestQueue(getActivity());
    final String url = "http://ec2-18-188-84-158.us-east-2.compute.amazonaws.com/imageloader.php";

    StringRequest stringRequest  = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
       @Override
       public void onResponse(String response) {

           Log.d("agdaedhahhh",response);
           try
           {
               //String temp = URLDecoder.decode(response,"utf-8");

               byte [] encodeByte= Base64.decode(response,Base64.DEFAULT);
               Bitmap bitmap= BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.length);


               Log.d("bitbibitbi", String.valueOf(bitmap));


               myinfo_image.setImageBitmap(bitmap);


               Glide.with(anywheresing_mypage.this).load(bitmap).apply(RequestOptions.overrideOf(650,650).circleCrop()).into(myinfo_image);


               if (bitmap == null)
               {
                   Glide.with(anywheresing_mypage.this).load(R.drawable.kakao_2).apply(RequestOptions.overrideOf(650,650).circleCrop()).into(myinfo_image);
               }
           }
           catch (Exception e)
           {
               e.printStackTrace();
           }


       }
   }, new Response.ErrorListener() {
       @Override
       public void onErrorResponse(VolleyError error) {

       }
   })
   {

       @Override
       protected Map<String,String> getParams() throws AuthFailureError {
           Map<String,String> parameters = new HashMap<String,String>();

           parameters.put("request_email",sessionId);

           Log.d("jr19jf",myinfo_email.getText().toString());
           return parameters;
       }
   };
    queue.add(stringRequest);

}
}


