package com.example.yujongheyon.anywheresing;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by yujongheyon on 2018-08-25.
 */

public class stream_roomwrite extends Activity
    {
    EditText stream_room_title;
    Button stream_room_create_btn;
    String stream;
    String isplaying;
        private SharedPreferences sharedPref;
        int room_isplaying;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.stream_roomwrite);
        stream_room_create_btn = (Button)findViewById(R.id.stream_room_create_btn);

        stream_room_title = (EditText)findViewById(R.id.stream_room_title);
        SharedPreferences pref = getSharedPreferences("session", MODE_PRIVATE);
        final String createrId = pref.getString("session", null);


        long now = System.currentTimeMillis();

        Date date = new Date(now);

        final SimpleDateFormat record_number = new SimpleDateFormat("YYYY-MM-dd : HH-mm");
        //어떻게?
        final String room_create_time = record_number.format(date);
        stream = "stream";
        isplaying = "false";
        room_isplaying =1;
        stream_room_create_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                Response.Listener<String> responseListener = new Response.Listener<String>()
                {
                    @Override
                    public void onResponse(String response)
                    {
                        Log.d("respon",response);
                    }
                };
                roomRequest roomRequest = new roomRequest(stream_room_title.getText().toString(),createrId,room_create_time,stream,responseListener);
                RequestQueue queue = Volley.newRequestQueue(stream_roomwrite.this);
                queue.add(roomRequest);




                Log.d("roooooooom",stream_room_title.getText().toString());


                /*

                생성된 그 당시의 방번호를 받아야 하는데
                그 시점을 어떻게?????

               shared에 저장을하고? 무슨키값으로?

               각항목 변수에 number값을 넣어준다

               근데 어떻게 불러오냐 포지션+number?


               */
                //생성완료버튼 클릭시 바로커넥트(방)으로 들어가게한다

                //Log.d("lconnnoooooooom", String.valueOf(room_number));



                DialogInterface.OnClickListener check = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Intent intent = new Intent(stream_roomwrite.this,duet_stream_screan.class);
                        intent.putExtra("room_title",stream_room_title.getText().toString());
                        Log.d("streaaaaaam",stream_room_title.getText().toString());
                        startActivity(intent);


                    }
                };


                AlertDialog.Builder builder = new AlertDialog.Builder(stream_roomwrite.this);

                builder.setMessage("잠시 기다려 주세요..")
                        .setNeutralButton("ok..", check)
                        .create()
                        .show();




                //call_duet();
               /* Intent intent = new Intent(roomwrite.this,ConnectAcivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY );
                startActivityForResult(intent,1);
                finish();*/
            }
        });
    }


}
