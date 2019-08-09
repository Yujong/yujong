package com.example.yujongheyon.anywheresing;

import android.Manifest;
import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.hardware.display.DisplayManager;
import android.hardware.display.VirtualDisplay;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.media.projection.MediaProjection;
import android.media.projection.MediaProjectionManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.SparseIntArray;
import android.view.LayoutInflater;
import android.view.Surface;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.yujongheyon.anywheresing.rtmp.ConnectCheckerRtmp;

import org.json.JSONArray;
import org.json.JSONObject;
import org.webrtc.RendererCommon;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import cn.zhaiyifan.lyric.LyricUtils;
import cn.zhaiyifan.lyric.widget.LyricView;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by yujongheyon on 2018-07-21.
 */

public class CallFragment extends Fragment implements ConnectCheckerRtmp {
    private View controlView;

    private static Socket socket;
    private static BufferedReader bufferedReader;
    private static final int SERVERPORT = 8055;
    private static final String SERVER_IP = "192.168.43.202";
    DataInputStream dataInputStream;
    PrintWriter printWriter;

    TextView viewMSG;

    Button submit;
    EditText insertmsg;
    DataOutputStream dataOutputStream;
    String sessionId;
    SharedPreferences pref;

    Button back_btn;

    RecyclerView chatlist;
    ArrayList<chatitem> chatitem;
    RecyclerView.Adapter chatAdapter;
    String editmessage;


    String contactName;

    private TextView contactView;
    private ImageButton disconnectButton;
    private ImageButton cameraSwitchButton;
    private ImageButton videoScalingButton;
    private ImageButton toggleMuteButton;
    private TextView captureFormatText;
    private SeekBar captureFormatSlider;
    private OnCallEvents callEvents;
    private RendererCommon.ScalingType scalingType;
    private boolean videoCallEnabled = true;





    private static final int REQUEST_CODE = 1000;
    private static final int REQUEST_PERMISSION = 1001;
    private static final SparseIntArray ORIENTATIONS = new SparseIntArray();
    static
    {
        ORIENTATIONS.append(Surface.ROTATION_0, 90);
        ORIENTATIONS.append(Surface.ROTATION_90, 0);
        ORIENTATIONS.append(Surface.ROTATION_180, 270);
        ORIENTATIONS.append(Surface.ROTATION_270, 180);
    }
    private ToggleButton toggle;
    private String VideoUrl = "";
    private RelativeLayout root_view;


    private MediaProjectionManager mediaProjectionManager;
    private MediaProjection mediaProjection;
    private VirtualDisplay virtualDisplay;
    private MediaProjectionCallback mediaProjectionCallback;
    private MediaRecorder mediaRecorder;


    private int ScreenDensity;
    private static int DISPLAY_WIDTH = 720;
    private static int DISPLAY_HEIGHT = 1280;


    private Button udp_image_streaming;
    private Bitmap udp_bitmap;
    private static int IMAGES_PRODUCED = 0;
    String udp_image_string;
    byte[] bytes;
    private static final int socketServerPORT = 8013;
    private static final String serverIP = "192.168.0.162";
    private static final String serverIP1 = "192.168.0.14";
    private static final String serverIP2 = "192.168.0.188";


    private String selectedPath;
    private static final int SELECT_VIDEO = 3;
    String path;



    //lyric_part
    LyricView lyric_view;
    String lyric;


    Button song_start;
    Button song_stop;
    TextView song_title;
    SharedPreferences lyric_save;
    SharedPreferences isplay;
    String islyric;


    String lyric_isplaying;




    private Handler lyric_handler = new Handler();
    boolean delay = false;

    A_islyric_update_thread a_islyric_update_thread = new A_islyric_update_thread();

    int room_person;
    int delay_num;

     RequestQueue queue;
    MediaPlayer lyric_play;
    @Override
    public void onConnectionSuccessRtmp() {

    }

    @Override
    public void onConnectionFailedRtmp(String s) {

    }

    @Override
    public void onDisconnectRtmp() {

    }

    @Override
    public void onAuthErrorRtmp() {

    }

    @Override
    public void onAuthSuccessRtmp() {

    }

    /**
     * Call control interface for container activity.
     */
    public interface OnCallEvents {
        void onCallHangUp();
        void onCameraSwitch();
        void onVideoScalingSwitch(RendererCommon.ScalingType scalingType);
        void onCaptureFormatChange(int width, int height, int framerate);
        boolean onToggleMic();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        //Log.d("resultCODE",lyric);

    }
    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public View onCreateView(
            final LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        controlView = inflater.inflate(R.layout.fragment_call, container, false);
        pref = getActivity().getSharedPreferences("session",MODE_PRIVATE);
        sessionId = pref.getString("session","");
        viewMSG = (TextView)controlView.findViewById(R.id.viewMSG);
        insertmsg = (EditText)controlView.findViewById(R.id.insertmsg);
        submit = (Button)controlView.findViewById(R.id.submit);
        back_btn = (Button)controlView.findViewById(R.id.back_btn);

        //가사 불러오기,재생하기


        song_start = (Button)controlView.findViewById(R.id.song_start);
        song_stop = (Button)controlView.findViewById(R.id.sond_stop);
        song_title = (TextView)controlView.findViewById(R.id.song_title);


        lyric_view = (LyricView)controlView.findViewById(R.id.lyric_view);



        song_start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                islyric_update();

            }
        });


        back_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                lyric_play.stop();
                Intent intent = new Intent(getActivity(),anywheresing_mainActivity.class);
                startActivity(intent);
            }
        });


       a_islyric_update_thread.execute();



        ChatOperator chatOperator = new ChatOperator();
        chatOperator.execute();

        song_stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                song_title.setText("");
                lyric_view.stop();
                lyric_view.clearComposingText();
                lyric_save.edit().clear().commit();
            }
        });

        chatlist = (RecyclerView)controlView.findViewById(R.id.chatlist);
        chatitem = new ArrayList<>();
        chatlist.setHasFixedSize(true);





        LinearLayoutManager chatlist_layoutManager = new LinearLayoutManager(getActivity());
        chatlist_layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        chatlist.setLayoutManager(chatlist_layoutManager);
        chatlist.setItemAnimator(new DefaultItemAnimator());
        chatAdapter = new chatAdapter(chatitem);
        chatlist.setAdapter(chatAdapter);








      // Create UI controls.
        contactView = (TextView) controlView.findViewById(R.id.contact_name_call);
        disconnectButton = (ImageButton) controlView.findViewById(R.id.button_call_disconnect);
        cameraSwitchButton = (ImageButton) controlView.findViewById(R.id.button_call_switch_camera);
        videoScalingButton = (ImageButton) controlView.findViewById(R.id.button_call_scaling_mode);
        toggleMuteButton = (ImageButton) controlView.findViewById(R.id.button_call_toggle_mic);
        captureFormatText = (TextView) controlView.findViewById(R.id.capture_format_text_call);
        captureFormatSlider = (SeekBar) controlView.findViewById(R.id.capture_format_slider_call);

        // Add buttons click events.
        disconnectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                callEvents.onCallHangUp();
            }
        });

        cameraSwitchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                callEvents.onCameraSwitch();
            }
        });

        videoScalingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (scalingType == RendererCommon.ScalingType.SCALE_ASPECT_FILL) {

                    scalingType = RendererCommon.ScalingType.SCALE_ASPECT_FIT;
                } else {

                    scalingType = RendererCommon.ScalingType.SCALE_ASPECT_FILL;
                }
                callEvents.onVideoScalingSwitch(scalingType);
            }
        });
        scalingType = RendererCommon.ScalingType.SCALE_ASPECT_FILL;

        toggleMuteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                boolean enabled = callEvents.onToggleMic();
                toggleMuteButton.setAlpha(enabled ? 1.0f : 0.3f);
            }
        });


        //방송 시작 부분
        DisplayMetrics metrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(metrics);
        ScreenDensity = metrics.densityDpi;


        DISPLAY_HEIGHT = metrics.heightPixels;
        DISPLAY_WIDTH = metrics.widthPixels;

        mediaRecorder = new MediaRecorder();
        mediaProjectionManager = (MediaProjectionManager)getActivity().getSystemService(Context.MEDIA_PROJECTION_SERVICE);
        toggle = (ToggleButton)controlView.findViewById(R.id.toggle);
        root_view = (RelativeLayout)controlView.findViewById(R.id.root_view);

        toggle.setOnClickListener(new View.OnClickListener()
        {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE) + ContextCompat.checkSelfPermission(getActivity(),Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED)
                {
                    if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),Manifest.permission.WRITE_EXTERNAL_STORAGE) || ActivityCompat.shouldShowRequestPermissionRationale(getActivity()
                            ,Manifest.permission.RECORD_AUDIO))
                    {
                        toggle.setChecked(false);
                        Snackbar.make(root_view,"권한",Snackbar.LENGTH_INDEFINITE)
                                .setAction("ENABLE", new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {

                                        ActivityCompat.requestPermissions(getActivity(),new String[]{
                                                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                                Manifest.permission.RECORD_AUDIO

                                        },REQUEST_PERMISSION);

                                    }
                                }).show();

                    }
                    else
                    {
                        ActivityCompat.requestPermissions(getActivity(),new String[]{
                                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                Manifest.permission.RECORD_AUDIO

                        },REQUEST_PERMISSION);

                    }

                }
                else
                {
                    toggleScreenShare(v);
                }
            }
        });


     /*\Button upload_start = (Button)controlView.findViewById(R.id.upload_start);
        upload_start.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                        Toast.makeText(getActivity(),"upload_success",Toast.LENGTH_LONG).show();
                        UploadVideo uv = new UploadVideo();
                        uv.execute();
            }
        });*/

        final Button choise_vedio = (Button)controlView.findViewById(R.id.choise_vedio);
        choise_vedio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                chooseVideo();

            }
        });

        Button mirroring = (Button)controlView.findViewById(R.id.mirroring);
        mirroring.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(),broad.class);
                startActivity(intent);
            }
        });

        Button sond_add = (Button)controlView.findViewById(R.id.sond_add);
        sond_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(),lyric_test.class);
                startActivity(intent);
            }
        });




        //is_lyric_person.run();
       /* lyric_ChatOperator lyric_chatOperator = new lyric_ChatOperator();
        lyric_chatOperator.execute();*/

        return controlView;
    }

    @Override
    public void onResume()
    {
        super.onResume();
        lyric_save = getActivity().getSharedPreferences("lyric", MODE_PRIVATE);
        lyric = lyric_save.getString("lyric", null);

        isplay = getActivity().getSharedPreferences("isplay",MODE_PRIVATE);
        islyric = isplay.getString("isplay",null);


        /*delay_lyric delay_lyric = new delay_lyric();
        delay_lyric.run();*/


    }

    /*
        public class delay_lyric extends Thread
        {

            @Override
            public void run()
            {
               get_person_isplay();

               if (delay_num == 2)
               {
                   lyric_view_play();
               }


            }
        }*/

    public class A_islyric_update_thread extends AsyncTask<Void,Void,Void>
    {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... voids)
        {
            while(true)
            {
                Log.d("whyyyyy","whyyyyy");
                get_person_isplay();

                if(delay_num == 2)
                {
                    break;
                }

            }

            return null;
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);


        }


        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            if(delay_num == 2)
            {
                Log.d("whyyyyy","nostart");
                lyric_view_play();

                if (delay == true)
                {
                    a_islyric_update_thread = null;
                }
                if (lyric_play.isPlaying())
                {
                    Log.d("isplaying","isplaying");
                }
                else
                {
                    Log.d("isplaying","notisplaying");
                }
            }

        }
    }



    public void lyric_view_play()
    {
                delay = true;
                song_title.setText("어디에도");


        String url = "http://ec2-18-188-84-158.us-east-2.compute.amazonaws.com/Mrfile/어디에도.wav";
      /*  try
        {
            // your URL here MediaPlayer
            lyric_play= new MediaPlayer();
            lyric_play.setAudioStreamType(AudioManager.STREAM_MUSIC);
            lyric_play.setDataSource(url);
            //lyric_play = MediaPlayer.create(getActivity(),R.raw.mrfirst);
            lyric_play.prepare();
            lyric_play.start();

        } catch (IOException e)
        {

        }
        */
        lyric_play= new MediaPlayer();
        lyric_play.setAudioStreamType(AudioManager.STREAM_MUSIC);
        lyric_play = MediaPlayer.create(getActivity(),R.raw.mrfirst);
        lyric_play.start();


                lyric_view.setLyric(LyricUtils.parseLyric(getResources().openRawResource(R.raw.anywhere),"UTF-8"));
                lyric_view.setLyricIndex(0);
                lyric_view.play();
    }




    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void toggleScreenShare(View v)
    {
        if(((ToggleButton)v).isChecked())
        {
            initRecoder();
            recodeScreen();
        }
        else
        {
            try
            {
                mediaRecorder.stop();
                mediaRecorder.reset();
            }
            catch (RuntimeException e)
            {

            }
            stopRecordScreen();

        }
    }
    private void initRecoder()
    {
        try
        {


            mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            mediaRecorder.setVideoSource(MediaRecorder.VideoSource.SURFACE);
            mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
            VideoUrl = Environment.getExternalStoragePublicDirectory(String.valueOf(Environment.getExternalStorageDirectory()))
                    + new StringBuilder("/").append(contactName).append(".mp4").toString();
            mediaRecorder.setOutputFile(VideoUrl);
            mediaRecorder.setVideoSize(DISPLAY_WIDTH, DISPLAY_HEIGHT);
            mediaRecorder.setVideoEncoder(MediaRecorder.VideoEncoder.H264);
            mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
            mediaRecorder.setVideoEncodingBitRate(512 * 1000);
            mediaRecorder.setVideoFrameRate(30);
            int rotation = getActivity().getWindowManager().getDefaultDisplay().getRotation();
            int orientation = ORIENTATIONS.get(rotation + 90);
            mediaRecorder.setOrientationHint(orientation);
            mediaRecorder.prepare();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void recodeScreen()
    {
        if (mediaProjection == null) {
            startActivityForResult(mediaProjectionManager.createScreenCaptureIntent(), REQUEST_CODE);
            return;
        }
        virtualDisplay = createVirtualDisplay();
        mediaRecorder.start();
    }
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private VirtualDisplay createVirtualDisplay()
    {

        return mediaProjection.createVirtualDisplay("MainActivity",
                DISPLAY_WIDTH, DISPLAY_HEIGHT, ScreenDensity,
                DisplayManager.VIRTUAL_DISPLAY_FLAG_AUTO_MIRROR,
                mediaRecorder.getSurface(), null /*Callbacks*/, null
                /*Handler*/);
    }
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {


        if (requestCode == SELECT_VIDEO)
        {
            System.out.println("SELECT_VIDEO");
            //destroyMediaprojection();
            Uri selectedImageUri = data.getData();
            selectedPath = getPath(selectedImageUri);
        }
        if (requestCode == REQUEST_CODE)
        {

            mediaProjectionCallback = new MediaProjectionCallback();
            mediaProjection = mediaProjectionManager.getMediaProjection(resultCode,data);
            mediaProjection.registerCallback(mediaProjectionCallback,null);
            virtualDisplay = createVirtualDisplay();
        /*    try
            {
                mediaRecorder.prepare();
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }*/
            mediaRecorder.start();
        }
    }


    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private class MediaProjectionCallback extends MediaProjection.Callback
    {
        public MediaProjectionCallback() {
            super();
        }

        @Override
        public void onStop() {
            if (toggle.isChecked())
            {
                toggle.setChecked(false);
                mediaRecorder.stop();
                mediaRecorder.reset();
            }
            mediaProjection = null;
            stopRecordScreen();
            super.onStop();
        }
    }
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void  stopRecordScreen()
    {
        if (virtualDisplay == null)

            return;
        virtualDisplay.release();
        destroyMediaprojection();
    }
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void destroyMediaprojection()
    {
        if (mediaProjection != null)
        {
            mediaProjection.unregisterCallback(mediaProjectionCallback);
            mediaProjection.stop();
            mediaProjection = null;

        }
    }
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[],
                                           @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_PERMISSION: {
                if ((grantResults.length > 0) && (grantResults[0] +
                        grantResults[1]) == PackageManager.PERMISSION_GRANTED) {
                    toggleScreenShare(toggle);
                } else {
                    toggle.setChecked(false);
                    Snackbar.make(controlView.findViewById(android.R.id.content),"권한",
                            Snackbar.LENGTH_INDEFINITE).setAction("ENABLE",
                            new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Intent intent = new Intent();
                                    intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                    intent.addCategory(Intent.CATEGORY_DEFAULT);
                                    intent.setData(Uri.parse("package:" + getActivity().getPackageName()));
                                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
                                    startActivity(intent);
                                }
                            }).show();
                }
                return;
            }
        }
    }

    public void restart()
    {
        mediaRecorder.reset();


    }









    private void chooseVideo() {
        Intent intent = new Intent();
        intent.setType("video/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select a Video "), SELECT_VIDEO);
    }

    public String getPath(Uri uri) {
        Cursor cursor =getActivity().getContentResolver().query(uri, null, null, null, null);
        cursor.moveToFirst();
        String document_id = cursor.getString(0);
        document_id = document_id.substring(document_id.lastIndexOf(":") + 1);
        cursor.close();

        cursor = getActivity().getContentResolver().query(
                android.provider.MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
                null, MediaStore.Images.Media._ID + " = ? ", new String[]{document_id}, null);
        cursor.moveToFirst();
        //String path = cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.DATA));
        if( cursor != null && cursor.moveToFirst() ){
            path = cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.DATA));
            cursor.close();
        }
        return path;
    }





    public void  get_person_isplay()
    {
        queue = Volley.newRequestQueue(getActivity());
        String url = "http://ec2-18-188-84-158.us-east-2.compute.amazonaws.com/roominfo.php";
        StringRequest stringRequest = new StringRequest(Request.Method.POST,url,new Response.Listener<String>()
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
                    JSONArray jsonarray = jsonobject.getJSONArray("room");

                    for (int i = 0; i < jsonarray.length(); i++)
                    {
                        //getting the json object of the particular index inside the array
                        JSONObject data = jsonarray.getJSONObject(i);
                        //room_person  = data.getInt("room_person");
                        delay_num =  data.getInt("room_isplaying");

                        //Log.d("peeeeeerson111", String.valueOf(room_person));
                        Log.d("peeeeeerson", String.valueOf(delay_num));
                    }
                } catch (Exception e) {
                    e.printStackTrace();

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

                parameters.put("room_number",contactName);


                return parameters;
            }
        };
        queue.add(stringRequest);
}


    public void islyric_update()
    {
        queue = Volley.newRequestQueue(getActivity());
        String url = "http://ec2-18-188-84-158.us-east-2.compute.amazonaws.com/room_lyric_update.php";

        StringRequest stringRequest = new  StringRequest(Request.Method.POST,url,new Response.Listener<String>()
        {

            @Override
            public void onResponse(String response) {
                try {

                    Log.d("peeeeersonaaaaaasss","safasffa");
                    JSONObject jsonobject = new JSONObject(response);
                    JSONArray jsonarray = jsonobject.getJSONArray("room");

                    for (int i = 0; i < jsonarray.length(); i++)
                    {
                        //getting the json object of the particular index inside the array
                        JSONObject data = jsonarray.getJSONObject(i);
                        //delay_num = data.getInt("room_isplaying");

                        //Log.d("peeeeersonaaaaaa", String.valueOf(delay_num));

                    }
                } catch (Exception e) {
                    e.printStackTrace();

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

                parameters.put("room_number",contactName);
                return parameters;
            }
        };
        queue.add(stringRequest);

    }




























    @Override
    public void onStart() {
        super.onStart();

        boolean captureSliderEnabled = false;
        Bundle args = getArguments();
        if (args != null) {
            contactName = args.getString(CallActivity.EXTRA_ROOMID);
            contactView.setText(contactName);
            videoCallEnabled = args.getBoolean(CallActivity.EXTRA_VIDEO_CALL, true);
            captureSliderEnabled = videoCallEnabled
                    && args.getBoolean(CallActivity.EXTRA_VIDEO_CAPTUREQUALITYSLIDER_ENABLED, false);
        }
        if (!videoCallEnabled) {
            cameraSwitchButton.setVisibility(View.INVISIBLE);
        }
        if (captureSliderEnabled) {
            captureFormatSlider.setOnSeekBarChangeListener
                    (
                    new CaptureQualityController(captureFormatText, callEvents));
        } else {
            captureFormatText.setVisibility(View.GONE);
            captureFormatSlider.setVisibility(View.GONE);
        }
    }

    // TODO(sakal): Replace with onAttach(Context) once we only support API level 23+.
    @SuppressWarnings("deprecation")
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        callEvents = (OnCallEvents) activity;
    }

    private class ChatOperator extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... arg0) {
            try {
                socket = new Socket(SERVER_IP,SERVERPORT); // Creating the server socket.

                if (socket != null) {
                    printWriter = new PrintWriter(socket.getOutputStream(), true);
                    InputStreamReader inputStreamReader = new InputStreamReader(socket.getInputStream());
                    //dataOutputStream = new DataOutputStream(socket.getOutputStream());

                    bufferedReader = new BufferedReader(inputStreamReader);
                    dataInputStream = new DataInputStream(socket.getInputStream());
                    dataOutputStream = new DataOutputStream(socket.getOutputStream());


                   // dataOutputStream.writeUTF(sessionId);
                    dataOutputStream.writeUTF(sessionId+"&"+contactName);
                    dataOutputStream.flush();
                  //  Log.d("connnn",contactName);
                } else {

                }
            } catch (UnknownHostException e) {

                e.printStackTrace();
            } catch (IOException e) {

                e.printStackTrace();
            }

            Receiver receiver = new Receiver(); // Initialize chat receiver AsyncTask.
            receiver.execute();

            return null;
        }

        /**
         * Following method is executed at the end of doInBackground method.
         */
        @Override
        protected void onPostExecute(Void result) {
            submit.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    final Sender messageSender = new Sender(); // Initialize chat sender AsyncTask.
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
                    {
                        messageSender.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                    }
                    else
                    {
                        messageSender.execute();
                    }

                    insertmsg.setText("");
                }
            });




        }

    }

    /**
     * This AsyncTask continuously reads the input buffer and show the chat
     * message if a message is availble.
     */
    private class Receiver extends AsyncTask<Void, Void, Void> {

        private String message;

        @Override
        protected Void doInBackground(Void... params) {
            while (true) {
                try {

                    if (bufferedReader.ready())
                    {
                        message = bufferedReader.readLine();
                        publishProgress(null);
                    }



                } catch (UnknownHostException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                try {
                    Thread.sleep(500);
                } catch (InterruptedException ie) {
                }
            }
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            Log.d("connnn", message);
            editmessage = message + "\n";
            chatitem.add(new chatitem(editmessage));
            chatAdapter.notifyDataSetChanged();
            if (editmessage.startsWith(sessionId))
            {

            }


            //viewMSG.setText(message);
        }

    }

    /**
     * This AsyncTask sends the chat message through the output stream.
     */
    private class Sender extends AsyncTask<Void, Void, Void> {

        private String message;

        @Override
        protected Void doInBackground(Void... params)
        {
            message = insertmsg.getText().toString();



                //dataOutputStream.close();
            printWriter.write(message +"\n");
            printWriter.flush();



            InetAddress ip = socket.getInetAddress();

            /*message = insertmsg.getText().toString();*/

           /*try
            {
                dataOutputStream.writeUTF(String.valueOf(ip));
                dataOutputStream.flush();

                Log.d("connnnnn", sessionId +" : " + message);
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
           */



            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            Log.d("connnnnnnn",message);
        }
    }

    @Override
    public void onStop() {
        super.onStop();

    }




    class chatAdapter extends RecyclerView.Adapter<chatAdapter.RecycleViewHolder> {
        ArrayList<chatitem> chatitem;
        Context context;


        public chatAdapter(ArrayList<chatitem> chatitem) {
            this.chatitem = chatitem;
        }


        @NonNull
        @Override
        public RecycleViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.chatitem, parent, false);
            context = parent.getContext();
            RecycleViewHolder holder = new RecycleViewHolder(view);


            return holder;
        }

        @Override
        public void onBindViewHolder(@NonNull RecycleViewHolder holder, int position) {


            holder.chat_content.setText(chatitem.get(position).getChat_content());


        }

        @Override
        public int getItemCount() {
            return chatitem.size();
        }

        public class RecycleViewHolder extends RecyclerView.ViewHolder {
            TextView chat_content;

            public RecycleViewHolder(View view) {
                super(view);

                chat_content = (TextView) view.findViewById(R.id.chat_content);
            }

        }


    }

/*private void init_recoder()
{
    broad_record = new MediaRecorder();
    broad_record.setAudioSource(MediaRecorder.AudioSource.MIC);
    broad_record.setVideoSource(MediaRecorder.VideoSource.SURFACE);
    broad_record.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
    broad_record.setVideoEncoder(MediaRecorder.VideoEncoder.H264);
    broad_record.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
    broad_record.setVideoEncodingBitRate(512 * 1000);
    broad_record.setVideoFrameRate(30);
    broad_record.setVideoSize(mWidth,mHeight);
   //broad_record.setOutputFile();
}
private void prepareRecoder()
{
    try
    {

        broad_record = new MediaRecorder();
        broad_record.setAudioSource(MediaRecorder.AudioSource.MIC);
        broad_record.setVideoSource(MediaRecorder.VideoSource.SURFACE);
        broad_record.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
        broad_record.setVideoEncoder(MediaRecorder.VideoEncoder.H264);
        broad_record.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
        broad_record.setVideoEncodingBitRate(512 * 1000);
        broad_record.setVideoFrameRate(30);
        broad_record.setVideoSize(mWidth,mHeight);
        broad_record.setProfile(CamcorderProfile.get(Camera.CameraInfo.CAMERA_FACING_FRONT, CamcorderProfile.QUALITY_HIGH));
        broad_record.setProfile(CamcorderProfile.get(CamcorderProfile.QUALITY_HIGH));
        broad_record.prepare();
    }
    catch (IllegalStateException | IOException e)
    {
        e.printStackTrace();
    }
}*/
//Thread = 일정시간마다 영상 계속 서버에 올리는 쓰레드
    public class upload_streaming extends Thread
    {
        @Override
        public void run()
        {

        }
    }




//udp image submit 다른부분에서 시연
  /*  @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private class ImageAvailableListener implements ImageReader.OnImageAvailableListener {
        @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
        @Override
        public void onImageAvailable(ImageReader reader) {
            Image image = null;
            FileOutputStream fos = null;
            udp_bitmap = null;

            try {
                image = reader.acquireLatestImage();
                if (image != null) {
                    Image.Plane[] planes = image.getPlanes();
                    ByteBuffer buffer = planes[0].getBuffer();
                    int pixelStride = planes[0].getPixelStride();
                    int rowStride = planes[0].getRowStride();
                    int rowPadding = rowStride - pixelStride * DISPLAY_WIDTH;
                    // create bitmap
                    udp_bitmap = Bitmap.createBitmap(DISPLAY_WIDTH + rowPadding / pixelStride, DISPLAY_HEIGHT, Bitmap.Config.ARGB_8888);
                    udp_bitmap.copyPixelsFromBuffer(buffer);
                    // write bitmap to a file
                    //fos = new FileOutputStream(getActivity().this.getExternalFilesDir.getAbsolutePath() + "/myscreen_" + IMAGES_PRODUCED + ".png");
                    udp_bitmap.compress(Bitmap.CompressFormat.JPEG, 5, fos);
                    //createImage(udp_bitmap,IMAGES_PRODUCED);
                    encodeToBase64(udp_bitmap);
                    Thread send = new Thread(new sendImage());
                    send.start();



                    IMAGES_PRODUCED++;
                    Log.d("ASDASD", "captured image: " + IMAGES_PRODUCED);

                }

            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (fos != null) {
                    try {
                        fos.close();
                    } catch (IOException ioe) {
                        ioe.printStackTrace();
                    }
                }

                if (udp_bitmap != null) {
                    udp_bitmap.recycle();
                }

                if (image != null) {
                    image.close();
                }
            }

        }
    }


    public void encodeToBase64(Bitmap bitmap)
    {
        ByteArrayOutputStream byteArrayOS = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG,5, byteArrayOS);
        udp_image_string = Base64.encodeToString(byteArrayOS.toByteArray(),Base64.DEFAULT);

    }

    private class sendImage extends Thread {


        DatagramPacket packet;
        @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
        @Override
        public void run()
        {
            try {

                bytes = udp_image_string.getBytes();
                *//*String test = "test";
                bytes = test.getBytes();*//*



                InetAddress serverAddr = InetAddress.getByName(serverIP);
                DatagramSocket socket = new DatagramSocket();
                Log.d("x111111", String.valueOf(serverAddr));

                //bytes = bitmapTobyte(loadImageFromStorage(Environment.getExternalStorageDirectory()+"/captures/"));;
                Log.d("x55555", String.valueOf(bytes));
                packet = new DatagramPacket(bytes, bytes.length, serverAddr, socketServerPORT);
                packet.setData(new byte[1024]);
                socket.setBroadcast(true);
                socket.send(packet);
                Log.d("x44444", String.valueOf(bytes));


                InetAddress serverAddr1 = InetAddress.getByName(serverIP1);
                packet = new DatagramPacket(bytes,bytes.length,serverAddr1,socketServerPORT);
                socket.send(packet);

                InetAddress serverAddr2 = InetAddress.getByName(serverIP2);
                packet = new DatagramPacket(bytes,bytes.length,serverAddr2,socketServerPORT);
                socket.send(packet);


            }
            //자체피드백
            catch (IOException e)
            {
                e.printStackTrace();
            }

        }
    }
*/

}
