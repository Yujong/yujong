package com.example.yujongheyon.anywheresing;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;

import com.roughike.bottombar.BottomBar;
import com.roughike.bottombar.OnTabSelectListener;

import java.util.Map;

/**
 * Created by yujongheyon on 2018-06-28.
 */

public class anywheresing_mainActivity extends FragmentActivity {

    private Map<String, String> paremeters;
   String json_url = "http://ec2-18-220-206-87.us-east-2.compute.amazonaws.com/userimageload.php";
   String usernickname;
   String userimage;


   private anywheresing_single_voice anywheresing_single_voice;
   private anywheresing_duet_voice anywheresing_duet_voice;
   private anywheresing_mypage anywheresing_mypage;
   private anywheresing_duet_video anywheresing_duet_video;
/*
   FragmentManager manager = getSupportFragmentManager();
   final FragmentTransaction fragmentTransaction = manager.beginTransaction();*/



String profil_openCVimage;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.anywhere_sing_main);

            /*
        ImageView user_profilimage = (ImageView)findViewById(R.id.user_profilimage);
        Glide.with(this).load(userimage).apply(RequestOptions.overrideOf(650,650).circleCrop()).into(user_profilimage);

        TextView user_nickname = (TextView)findViewById(R.id.user_nickname);
        user_nickname.setText(usernickname);*/
        //sendRequest();
        //saveDateRead();


        Intent intent = getIntent();
        final String request = intent.getStringExtra("useremail");
        final int requestcode = intent.getIntExtra("discrimination",0);

        int openCVimageCode = intent.getIntExtra("opencv_code",0);

        int success_code = intent.getIntExtra("success_code",0);

        //profil_openCVimage = intent.getStringExtra("opencv_image");

        //Log.d("ashasfashsdhsdh",request);
        anywheresing_single_voice  = new anywheresing_single_voice();

        anywheresing_duet_voice = new anywheresing_duet_voice();

        anywheresing_mypage = new anywheresing_mypage();

        anywheresing_duet_video = new anywheresing_duet_video();


        BottomBar bottomBar = (BottomBar)findViewById(R.id.bottomBar);


        SharedPreferences pref = getSharedPreferences("session", MODE_PRIVATE);
       final String autologin = pref.getString("session", null);


        bottomBar.setOnTabSelectListener(new OnTabSelectListener() {
            @Override
            public void onTabSelected(int tabId) {
              FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

              if (tabId == R.id.single_vocie)
                {
                   transaction.replace(R.id.menucontent,anywheresing_single_voice).commit();
                }
               else if (tabId == R.id.duet_voice)
                {



                    Bundle bundle = new Bundle(); // 파라미터는 전달할 데이터 개수
                    bundle.putString("useremail", autologin); // key , value
                    bundle.putInt("requestcode",requestcode);
                    Log.d("fikjfdsihgjfdsigjg", String.valueOf(requestcode));
                    anywheresing_duet_voice.setArguments(bundle);
                    transaction.replace(R.id.menucontent,anywheresing_duet_voice).commit();
                }

                else if (tabId == R.id.voice_list)
                {
                    transaction.replace(R.id.menucontent,anywheresing_duet_video).commit();
                }
                else if (tabId == R.id.myinfo)
                {



                    //transaction.replace(R.id.menucontent,anywheresing_mypage).commit();
                    // Fragment 생성
                    Bundle bundle = new Bundle(); // 파라미터는 전달할 데이터 개수
                    bundle.putString("useremail", autologin); // key , value
                    bundle.putInt("requestcode",requestcode);
                    Log.d("fikjfdsihgjfdsigjg", String.valueOf(requestcode));
                    anywheresing_mypage.setArguments(bundle);
                    /*
                    fragmentTransaction.add(R.id.menucontent,anywheresing_mypage);
                    fragmentTransaction.commit();*/
                    transaction.replace(R.id.menucontent,anywheresing_mypage).commit();

                    //getSupportFragmentManager().beginTransaction().replace(R.id.menucontent,anywheresing_mypage).commit();
                     //transaction.replace(R.id.menucontent,anywheresing_mypage).commit();




                }


            }
        });



        String openCV_image = intent.getStringExtra("opencv_image");

        if (openCVimageCode == 22)
        {

            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            Bundle bundle = new Bundle(3);// 파라미터는 전달할 데이터 개수
            bundle.putString("opencv_image",openCV_image);
            bundle.putString("useremail", autologin); // key , value
            bundle.putInt("requestcode",requestcode);
            anywheresing_mypage.setArguments(bundle);
            transaction.replace(R.id.menucontent,anywheresing_mypage).commit();

        }
        if (success_code == 20)
        {
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.menucontent,anywheresing_duet_video).commit();
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 2)
        {

        }
    }


/*

   public void saveDateRead()
    {


        Intent intent = getIntent();
        final String request = intent.getStringExtra("useremail");//이메일받기
        Log.d("useremail",request);


        Response.Listener<JSONObject> responseListener = new Response.Listener<JSONObject>()
                    {
                        @Override
                        public void onResponse(JSONObject response)
                        {

                        try {

                            JSONObject jsonObject = new JSONObject();
                            boolean success = jsonObject.getBoolean("success");
                            Log.d("순서가?","순서가?????");
                              if (success)
                              {



                                  ImageView user_profilimage = (ImageView)findViewById(R.id.user_profilimage);

                                  Glide.with(anywheresing_mainActivity.this).load(jsonObject.getString("userimage")).apply(RequestOptions.overrideOf(650,650).circleCrop()).into(user_profilimage);

                                  TextView user_nickname = (TextView)findViewById(R.id.user_nickname);

                                  user_nickname.setText(jsonObject.getString("nickname"));
                                  Log.d("님",jsonObject.getString("nickname"));

                              }

                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    };
                    Log.d("로그인이메일",request);

                    jsonRequest jsonRequest = new jsonRequest(request,paremeters,responseListener,null);
                    RequestQueue queue = Volley.newRequestQueue(anywheresing_mainActivity.this);
                    queue.add(jsonRequest);


    }




   public void sendRequest(){
        // RequestQueue를 새로 만들어준다.
        RequestQueue queue = Volley.newRequestQueue(this);
        // Request를 요청 할 URL
       String url = "http://ec2-18-220-208-130.us-east-2.compute.amazonaws.com/userimageload.php";


       Intent intent = getIntent();
       final String request = intent.getStringExtra("useremail");//이메일받기
       Log.d("useremail",request);


       JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url,null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            paremeters.put("request_email",request);

                            String json_nickname = response.getString("nickname");
                            String json_userimage = response.getString("userimage");

                            ImageView user_profilimage = (ImageView)findViewById(R.id.user_profilimage);

                            Glide.with(anywheresing_mainActivity.this).load(response.getString("userimage")).apply(RequestOptions.overrideOf(650,650).circleCrop()).into(user_profilimage);

                            TextView user_nickname = (TextView)findViewById(R.id.user_nickname);

                            user_nickname.setText(response.getString("nickname"));
                            Log.d("님",response.getString("nickname"));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("error","errorrrrrrrrrrrr");
            }
        });
        // queue에 Request를 추가해준다.
        queue.add(jsonObjectRequest);
    }*/






}
