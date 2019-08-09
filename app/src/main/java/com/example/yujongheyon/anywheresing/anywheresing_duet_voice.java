package com.example.yujongheyon.anywheresing;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.URLUtil;
import android.widget.Button;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 * Created by yujongheyon on 2018-06-29.
 */



public class anywheresing_duet_voice extends Fragment
{
    /*
    방생성버튼

    방만들수있는 액티비티 -> 커넥트 액티비티

    수정본 -> 방만드는창 따로생성




    방만들기 완료시 바로 콜액티비티

    duet_room_list 리사이클뷰 Id

    duet_room_make_btn 방생성버튼


    while문 돌려서 리스트뷰전채 가져오기


    값불러와서 item.add
    while

    삭제 즉 방파기는 어떻게할것인가 현재세션Id와 저장된
    */

    private SharedPreferences sharedPref;
    private static boolean commandLineRun = false;
    private String keyprefRoomServerUrl;
    private String keyprefResolution;
    private String keyprefFps;
    private String keyprefVideoBitrateType;
    private String keyprefVideoBitrateValue;
    private String keyprefAudioBitrateType;
    private String keyprefAudioBitrateValue;





    RecyclerView duet_room_list;
    Button duet_room_make_btn;
    Button stream_start_btn;
    String userId;
    String stream_or_duet;
    String title;
    String creater;
    String time;
    int room_number;
    String room_person;
    ArrayList<roomItem> roomItem;

    RecyclerView.Adapter roomAdapter;

    String roomID;







    private View.OnClickListener onClickListener;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        Bundle bundle = getArguments();

        userId = bundle.getString("useremail");


        Log.d("sogmasogsho",userId);

        Log.d("sdgshashahawrhhwwh","sdgshashahawrhhwwh");

    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.duet_voice,container,false);


        duet_room_list = (RecyclerView)view.findViewById(R.id.duet_room_list);

        roomItem = new ArrayList<>();
        roomlist();
        duet_room_list.setHasFixedSize(true);





        duet_room_make_btn = (Button)view.findViewById(R.id.duet_room_make_btn);

        duet_room_make_btn.setOnClickListener(new View.OnClickListener()
        {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(getActivity(),roomwrite.class);
            startActivity(intent);
        }
        });
        stream_start_btn = (Button)view.findViewById(R.id.stream_start_btn);
        stream_start_btn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                Intent intent =new Intent(getActivity(),stream_roomwrite.class);
                startActivity(intent);
            }
        });





        PreferenceManager.setDefaultValues(getActivity(), R.xml.preferences, false);
        sharedPref = PreferenceManager.getDefaultSharedPreferences(getActivity());
        keyprefVideoBitrateType = getString(R.string.pref_maxvideobitrate_key);
        keyprefVideoBitrateValue = getString(R.string.pref_maxvideobitratevalue_key);
        keyprefResolution = getString(R.string.pref_resolution_key);
        keyprefFps =

                getString(R.string.pref_fps_key);
        keyprefRoomServerUrl = getString(R.string.pref_room_server_url_key);
        keyprefAudioBitrateType = getString(R.string.pref_startaudiobitrate_key);
        keyprefAudioBitrateValue = getString(R.string.pref_startaudiobitratevalue_key);

        return view;
 }


/*

7월 25일


다음날 이주석을 보는즉시 노트북닫고

다이소가서 스케치북이든 공책이든 살것

코드를 쓰는거보다 어떤식으로 해야할지를 생각하는 시점이다

막연하게 하다보니 진행이 전혀되지않고 아이디어가 떠오르지 않는다

7시에 소모임가야하니 빨리움직이자
*/


    class roomAdapter extends RecyclerView.Adapter<roomAdapter.RecycleViewHolder>
    {



        ArrayList<roomItem> roomItem;
        Context context;


        //private SeekBarUpdater seekBarUpdater;
        private RecycleViewHolder playingHolder;
        private int lastPosition ;


        public roomAdapter(ArrayList<roomItem> roomItem)
        {
            this.roomItem = roomItem;
            this.lastPosition = -1;
            //seekBarUpdater = new SeekBarUpdater();
        }

        @NonNull
        @Override
        public RecycleViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
        {

            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.roomlist_item,parent,false);
            context = parent.getContext();
            RecycleViewHolder holder = new RecycleViewHolder(view);


            return holder;
        }

        @Override
        public void onBindViewHolder(@NonNull final RecycleViewHolder holder, int position)
        {
            /*
            2018 7 25


            db에서 값 차례대로 받아오기 아니 전부 받아오기 서버코드 Select All

            WhERE 문  필요X

            레이아웃 ID 달고 클릭이벤트 처리 클릭시 어디로? -> connectActivity

            왜 테이블을 분리했을까 카톡페북구글 어캐할까


        table에 칼럼 하나 추가하고 기본값0 방생성 할때 +1

        방을 클릭했을때 +1

        닫기 또는 뒤로가기 버튼 생성뒤 나갈때마다 -1

        0일됬을때 db에서 리스트삭제
            */

            holder.room_title.setText(roomItem.get(position).getRoom_title());
            holder.room_create_nick.setText(roomItem.get(position).getRoom_creater_nick());
            holder.room_create_time.setText(roomItem.get(position).getRoom_create_time());
            holder.room_person.setText(roomItem.get(position).getRoom_person());

            duet_room_list.addOnItemTouchListener(new listclick(getActivity(),duet_room_list, new listclick.OnItemClickListener() {
                @Override
                public void onItemClick(View view, int position) {

                    roomID = String.valueOf(roomItem.get(position).getRoom_number());
                    room_person();
                    //islyric_update();
                    roomAdapter.notifyDataSetChanged();
                    int room_full  = Integer.parseInt(room_person);

                    if(stream_or_duet.equals("duet"))
                    {

                        if (room_full <= 2)
                        {
                         /*   String where = "play";
                            SharedPreferences pref = getActivity().getSharedPreferences("isplay", MODE_PRIVATE);
                            SharedPreferences.Editor editor = pref.edit();
                            editor.putString("isplay",where);
                            Log.d("lyricccccc",where);
                            editor.commit();*/

                            connectToRoom(roomID, false, false, false, 0);

                        }
                        else if(room_full > 2)
                        {

                        Intent intent = new Intent(getActivity(),opencv_image.class);
                        startActivity(intent);
                        }




                    }
                    else if (stream_or_duet.equals("stream"))
                    {
                            String stream_title = roomItem.get(position).getRoom_title();
                            Intent intent = new Intent(getContext(),duet_stream_player.class);
                            intent.putExtra("room_title",stream_title);
                            startActivity(intent);
                    }


                    }

                @Override
                public void onLongItemClick(View view, int position)
                {

                }
            }));
        }




        @Override
        public int getItemCount()
        {
            return roomItem.size();
        }


        public class RecycleViewHolder extends RecyclerView.ViewHolder
        {
            TextView room_title;
            TextView room_create_nick;
            TextView room_create_time;
            TextView room_number;
            TextView room_person;

            public RecycleViewHolder(View itemView)
            {
                super(itemView);
                room_title = (TextView)itemView.findViewById(R.id.room_title);
                room_create_nick = (TextView)itemView.findViewById(R.id.room_create_nick);
                room_create_time = (TextView)itemView.findViewById(R.id.room_create_time);
                room_number = (TextView)itemView.findViewById(R.id.room_number);
                room_person= (TextView)itemView.findViewById(R.id.room_person);
                itemView.setTag(getAdapterPosition());


            }


        }


    }
/*
방나누기가 되었다 태그값으로 방을 나누자?

영상통화가능 +

채팅기능 추가

3개붙이면 설계 1개완료
*/

    public void islyric_update()
    {
        final RequestQueue queue = Volley.newRequestQueue(getActivity());
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

                parameters.put("room_number",roomID);
                return parameters;
            }
        };
        queue.add(stringRequest);

    }

    public void room_person()
    {
        final RequestQueue queue = Volley.newRequestQueue(getActivity());
        String url = "http://ec2-18-188-84-158.us-east-2.compute.amazonaws.com/room_person.php";
        StringRequest stringRequest = new  StringRequest(Request.Method.POST,url,new Response.Listener<String>()
        {

            @Override
            public void onResponse(String response) {
                try {


                    JSONObject jsonobject = new JSONObject(response);
                    JSONArray jsonarray = jsonobject.getJSONArray("room");

                    for (int i = 0; i < jsonarray.length(); i++)
                    {
                        //getting the json object of the particular index inside the array
                        JSONObject data = jsonarray.getJSONObject(i);
                        room_person = String.valueOf(data.getInt("room_person"));

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

                parameters.put("roomID",roomID);
                return parameters;
            }
        };
        queue.add(stringRequest);

    }


private void roomlist()
{
    final RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
    String url = "http://ec2-18-188-84-158.us-east-2.compute.amazonaws.com/roomlist.php";
    StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
        @Override
        public void onResponse(String response) {
            try

            {

                JSONObject jsonObject = new JSONObject(response);
                JSONArray jsonArray = jsonObject.getJSONArray("room");

                for (int i=0; i< jsonArray.length(); i++)
                {
                    JSONObject data = jsonArray.getJSONObject(i);

                    title =  data.getString("room_title");
                    creater = (data.getString("room_creater"));
                    time = (data.getString("room_create_time"));
                    room_number = data.getInt("room_number");
                    room_person = String.valueOf(data.getInt("room_person"));
                    stream_or_duet = data.getString("stream_or_duet");


                    roomItem.add(new roomItem(title,creater,time,room_number,room_person));

                    Log.d("hoooooooom",data.getString("room_title"));
                    Log.d("hoooooooom",data.getString("room_creater"));
                    Log.d("hoooooooom",data.getString("room_create_time"));
                    Log.d("hoooooooom",stream_or_duet);

                }

            }

            catch (Exception e)
            {

            }


            LinearLayoutManager room_list_layoutManager = new LinearLayoutManager(getActivity());
            room_list_layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
            duet_room_list.setLayoutManager(room_list_layoutManager);
            duet_room_list.setItemAnimator(new DefaultItemAnimator());
            roomAdapter = new roomAdapter(roomItem);
            roomAdapter.notifyDataSetChanged();
            duet_room_list.setAdapter(roomAdapter);


        }
    }, new Response.ErrorListener()
    {
        @Override
        public void onErrorResponse(VolleyError error) {
            error.printStackTrace();
        }
    });
    requestQueue.add(stringRequest);

}


    private void connectToRoom(String roomId, boolean commandLineRun, boolean loopback,
                               boolean useValuesFromIntent, int runTimeMs) {
        this.commandLineRun = commandLineRun;

        // roomId is random for loopback.
        if (loopback) {
            roomId = Integer.toString((new Random()).nextInt(100000000));
        }

        String roomUrl = sharedPref.getString(
                keyprefRoomServerUrl, getString(R.string.pref_room_server_url_default));

        // Video call enabled flag.
        boolean videoCallEnabled = sharedPrefGetBoolean(R.string.pref_videocall_key,
                CallActivity.EXTRA_VIDEO_CALL, R.string.pref_videocall_default, useValuesFromIntent);

        // Use screencapture option.
        boolean useScreencapture = sharedPrefGetBoolean(R.string.pref_screencapture_key,
                CallActivity.EXTRA_SCREENCAPTURE, R.string.pref_screencapture_default, useValuesFromIntent);

        // Use Camera2 option.
        boolean useCamera2 = sharedPrefGetBoolean(R.string.pref_camera2_key, CallActivity.EXTRA_CAMERA2,
                R.string.pref_camera2_default, useValuesFromIntent);

        // Get default codecs.
        String videoCodec = sharedPrefGetString(R.string.pref_videocodec_key,
                CallActivity.EXTRA_VIDEOCODEC, R.string.pref_videocodec_default, useValuesFromIntent);
        String audioCodec = sharedPrefGetString(R.string.pref_audiocodec_key,
                CallActivity.EXTRA_AUDIOCODEC, R.string.pref_audiocodec_default, useValuesFromIntent);

        // Check HW codec flag.
        boolean hwCodec = sharedPrefGetBoolean(R.string.pref_hwcodec_key,
                CallActivity.EXTRA_HWCODEC_ENABLED, R.string.pref_hwcodec_default, useValuesFromIntent);

        // Check Capture to texture.
        boolean captureToTexture = sharedPrefGetBoolean(R.string.pref_capturetotexture_key,
                CallActivity.EXTRA_CAPTURETOTEXTURE_ENABLED, R.string.pref_capturetotexture_default,
                useValuesFromIntent);

        // Check FlexFEC.
        boolean flexfecEnabled = sharedPrefGetBoolean(R.string.pref_flexfec_key,
                CallActivity.EXTRA_FLEXFEC_ENABLED, R.string.pref_flexfec_default, useValuesFromIntent);

        // Check Disable Audio Processing flag.
        boolean noAudioProcessing = sharedPrefGetBoolean(R.string.pref_noaudioprocessing_key,
                CallActivity.EXTRA_NOAUDIOPROCESSING_ENABLED, R.string.pref_noaudioprocessing_default,
                useValuesFromIntent);

        // Check Disable Audio Processing flag.
        boolean aecDump = sharedPrefGetBoolean(R.string.pref_aecdump_key,
                CallActivity.EXTRA_AECDUMP_ENABLED, R.string.pref_aecdump_default, useValuesFromIntent);

        // Check OpenSL ES enabled flag.
        boolean useOpenSLES = sharedPrefGetBoolean(R.string.pref_opensles_key,
                CallActivity.EXTRA_OPENSLES_ENABLED, R.string.pref_opensles_default, useValuesFromIntent);

        // Check Disable built-in AEC flag.
        boolean disableBuiltInAEC = sharedPrefGetBoolean(R.string.pref_disable_built_in_aec_key,
                CallActivity.EXTRA_DISABLE_BUILT_IN_AEC, R.string.pref_disable_built_in_aec_default,
                useValuesFromIntent);

        // Check Disable built-in AGC flag.
        boolean disableBuiltInAGC = sharedPrefGetBoolean(R.string.pref_disable_built_in_agc_key,
                CallActivity.EXTRA_DISABLE_BUILT_IN_AGC, R.string.pref_disable_built_in_agc_default,
                useValuesFromIntent);

        // Check Disable built-in NS flag.
        boolean disableBuiltInNS = sharedPrefGetBoolean(R.string.pref_disable_built_in_ns_key,
                CallActivity.EXTRA_DISABLE_BUILT_IN_NS, R.string.pref_disable_built_in_ns_default,
                useValuesFromIntent);

        // Check Enable level control.
        boolean enableLevelControl = sharedPrefGetBoolean(R.string.pref_enable_level_control_key,
                CallActivity.EXTRA_ENABLE_LEVEL_CONTROL, R.string.pref_enable_level_control_key,
                useValuesFromIntent);

        // Check Disable gain control
        boolean disableWebRtcAGCAndHPF = sharedPrefGetBoolean(
                R.string.pref_disable_webrtc_agc_and_hpf_key, CallActivity.EXTRA_DISABLE_WEBRTC_AGC_AND_HPF,
                R.string.pref_disable_webrtc_agc_and_hpf_key, useValuesFromIntent);

        // Get video resolution from settings.
        int videoWidth = 0;
        int videoHeight = 0;
        if (useValuesFromIntent) {
            videoWidth = getActivity().getIntent().getIntExtra(CallActivity.EXTRA_VIDEO_WIDTH, 0);
            videoHeight = getActivity().getIntent().getIntExtra(CallActivity.EXTRA_VIDEO_HEIGHT, 0);
        }
        if (videoWidth == 0 && videoHeight == 0) {
            String resolution =
                    sharedPref.getString(keyprefResolution, getString(R.string.pref_resolution_default));
            String[] dimensions = resolution.split("[ x]+");
            if (dimensions.length == 2) {
                try {
                    videoWidth = Integer.parseInt(dimensions[0]);
                    videoHeight = Integer.parseInt(dimensions[1]);
                } catch (NumberFormatException e) {
                    videoWidth = 0;
                    videoHeight = 0;

                }
            }
        }

        // Get camera fps from settings.
        int cameraFps = 0;
        if (useValuesFromIntent) {
            cameraFps = getActivity().getIntent().getIntExtra(CallActivity.EXTRA_VIDEO_FPS, 0);
        }
        if (cameraFps == 0) {
            String fps = sharedPref.getString(keyprefFps, getString(R.string.pref_fps_default));
            String[] fpsValues = fps.split("[x]+");
            if (fpsValues.length == 2) {
                try {
                    cameraFps = Integer.parseInt(fpsValues[0]);
                } catch (NumberFormatException e) {
                    cameraFps = 0;

                }
            }
        }

        // Check capture quality slider flag.
        boolean captureQualitySlider = sharedPrefGetBoolean(R.string.pref_capturequalityslider_key,
                CallActivity.EXTRA_VIDEO_CAPTUREQUALITYSLIDER_ENABLED,
                R.string.pref_capturequalityslider_default, useValuesFromIntent);

        // Get video and audio start bitrate.
        int videoStartBitrate = 0;
        if (useValuesFromIntent) {
            videoStartBitrate = getActivity().getIntent().getIntExtra(CallActivity.EXTRA_VIDEO_BITRATE, 0);
        }
        if (videoStartBitrate == 0) {
            String bitrateTypeDefault = getString(R.string.pref_maxvideobitrate_default);
            String bitrateType = sharedPref.getString(keyprefVideoBitrateType, bitrateTypeDefault);
            if (!bitrateType.equals(bitrateTypeDefault)) {
                String bitrateValue = sharedPref.getString(
                        keyprefVideoBitrateValue, getString(R.string.pref_maxvideobitratevalue_default));
                videoStartBitrate = Integer.parseInt(bitrateValue);
            }
        }

        int audioStartBitrate = 0;
        if (useValuesFromIntent) {
            audioStartBitrate = getActivity().getIntent().getIntExtra(CallActivity.EXTRA_AUDIO_BITRATE, 0);
        }
        if (audioStartBitrate == 0) {
            String bitrateTypeDefault = getString(R.string.pref_startaudiobitrate_default);
            String bitrateType = sharedPref.getString(keyprefAudioBitrateType, bitrateTypeDefault);
            if (!bitrateType.equals(bitrateTypeDefault)) {
                String bitrateValue = sharedPref.getString(
                        keyprefAudioBitrateValue, getString(R.string.pref_startaudiobitratevalue_default));
                audioStartBitrate = Integer.parseInt(bitrateValue);
            }
        }

        // Check statistics display option.
        boolean displayHud = sharedPrefGetBoolean(R.string.pref_displayhud_key,
                CallActivity.EXTRA_DISPLAY_HUD, R.string.pref_displayhud_default, useValuesFromIntent);

        boolean tracing = sharedPrefGetBoolean(R.string.pref_tracing_key, CallActivity.EXTRA_TRACING,
                R.string.pref_tracing_default, useValuesFromIntent);

        // Get datachannel options
        boolean dataChannelEnabled = sharedPrefGetBoolean(R.string.pref_enable_datachannel_key,
                CallActivity.EXTRA_DATA_CHANNEL_ENABLED, R.string.pref_enable_datachannel_default,
                useValuesFromIntent);
        boolean ordered = sharedPrefGetBoolean(R.string.pref_ordered_key, CallActivity.EXTRA_ORDERED,
                R.string.pref_ordered_default, useValuesFromIntent);
        boolean negotiated = sharedPrefGetBoolean(R.string.pref_negotiated_key,
                CallActivity.EXTRA_NEGOTIATED, R.string.pref_negotiated_default, useValuesFromIntent);
        int maxRetrMs = sharedPrefGetInteger(R.string.pref_max_retransmit_time_ms_key,
                CallActivity.EXTRA_MAX_RETRANSMITS_MS, R.string.pref_max_retransmit_time_ms_default,
                useValuesFromIntent);
        int maxRetr =
                sharedPrefGetInteger(R.string.pref_max_retransmits_key, CallActivity.EXTRA_MAX_RETRANSMITS,
                        R.string.pref_max_retransmits_default, useValuesFromIntent);
        int id = sharedPrefGetInteger(R.string.pref_data_id_key, CallActivity.EXTRA_ID,
                R.string.pref_data_id_default, useValuesFromIntent);
        String protocol = sharedPrefGetString(R.string.pref_data_protocol_key,
                CallActivity.EXTRA_PROTOCOL, R.string.pref_data_protocol_default, useValuesFromIntent);

        // Start AppRTCMobile activity.
        if (validateUrl(roomUrl)) {
            Uri uri = Uri.parse(roomUrl);
            Intent intent = new Intent(getActivity(),CallActivity.class);
            intent.setData(uri);
            intent.putExtra(CallActivity.EXTRA_ROOMID, roomId);
            intent.putExtra(CallActivity.EXTRA_LOOPBACK, loopback);
            intent.putExtra(CallActivity.EXTRA_VIDEO_CALL, videoCallEnabled);
            intent.putExtra(CallActivity.EXTRA_SCREENCAPTURE, useScreencapture);
            intent.putExtra(CallActivity.EXTRA_CAMERA2, useCamera2);
            intent.putExtra(CallActivity.EXTRA_VIDEO_WIDTH, videoWidth);
            intent.putExtra(CallActivity.EXTRA_VIDEO_HEIGHT, videoHeight);
            intent.putExtra(CallActivity.EXTRA_VIDEO_FPS, cameraFps);
            intent.putExtra(CallActivity.EXTRA_VIDEO_CAPTUREQUALITYSLIDER_ENABLED, captureQualitySlider);
            intent.putExtra(CallActivity.EXTRA_VIDEO_BITRATE, videoStartBitrate);
            intent.putExtra(CallActivity.EXTRA_VIDEOCODEC, videoCodec);
            intent.putExtra(CallActivity.EXTRA_HWCODEC_ENABLED, hwCodec);
            intent.putExtra(CallActivity.EXTRA_CAPTURETOTEXTURE_ENABLED, captureToTexture);
            intent.putExtra(CallActivity.EXTRA_FLEXFEC_ENABLED, flexfecEnabled);
            intent.putExtra(CallActivity.EXTRA_NOAUDIOPROCESSING_ENABLED, noAudioProcessing);
            intent.putExtra(CallActivity.EXTRA_AECDUMP_ENABLED, aecDump);
            intent.putExtra(CallActivity.EXTRA_OPENSLES_ENABLED, useOpenSLES);
            intent.putExtra(CallActivity.EXTRA_DISABLE_BUILT_IN_AEC, disableBuiltInAEC);
            intent.putExtra(CallActivity.EXTRA_DISABLE_BUILT_IN_AGC, disableBuiltInAGC);
            intent.putExtra(CallActivity.EXTRA_DISABLE_BUILT_IN_NS, disableBuiltInNS);
            intent.putExtra(CallActivity.EXTRA_ENABLE_LEVEL_CONTROL, enableLevelControl);
            intent.putExtra(CallActivity.EXTRA_DISABLE_WEBRTC_AGC_AND_HPF, disableWebRtcAGCAndHPF);
            intent.putExtra(CallActivity.EXTRA_AUDIO_BITRATE, audioStartBitrate);
            intent.putExtra(CallActivity.EXTRA_AUDIOCODEC, audioCodec);
            intent.putExtra(CallActivity.EXTRA_DISPLAY_HUD, displayHud);
            intent.putExtra(CallActivity.EXTRA_TRACING, tracing);
            intent.putExtra(CallActivity.EXTRA_CMDLINE, commandLineRun);
            intent.putExtra(CallActivity.EXTRA_RUNTIME, runTimeMs);

            intent.putExtra(CallActivity.EXTRA_DATA_CHANNEL_ENABLED, dataChannelEnabled);

            if (dataChannelEnabled) {
                intent.putExtra(CallActivity.EXTRA_ORDERED, ordered);
                intent.putExtra(CallActivity.EXTRA_MAX_RETRANSMITS_MS, maxRetrMs);
                intent.putExtra(CallActivity.EXTRA_MAX_RETRANSMITS, maxRetr);
                intent.putExtra(CallActivity.EXTRA_PROTOCOL, protocol);
                intent.putExtra(CallActivity.EXTRA_NEGOTIATED, negotiated);
                intent.putExtra(CallActivity.EXTRA_ID, id);
            }

            if (useValuesFromIntent) {
                if (getActivity().getIntent().hasExtra(CallActivity.EXTRA_VIDEO_FILE_AS_CAMERA)) {
                    String videoFileAsCamera =
                            getActivity().getIntent().getStringExtra(CallActivity.EXTRA_VIDEO_FILE_AS_CAMERA);
                    intent.putExtra(CallActivity.EXTRA_VIDEO_FILE_AS_CAMERA, videoFileAsCamera);
                }

                if (getActivity().getIntent().hasExtra(CallActivity.EXTRA_SAVE_REMOTE_VIDEO_TO_FILE)) {
                    String saveRemoteVideoToFile =
                            getActivity().getIntent().getStringExtra(CallActivity.EXTRA_SAVE_REMOTE_VIDEO_TO_FILE);
                    intent.putExtra(CallActivity.EXTRA_SAVE_REMOTE_VIDEO_TO_FILE, saveRemoteVideoToFile);
                }

                if (getActivity().getIntent().hasExtra(CallActivity.EXTRA_SAVE_REMOTE_VIDEO_TO_FILE_WIDTH)) {
                    int videoOutWidth =
                            getActivity().getIntent().getIntExtra(CallActivity.EXTRA_SAVE_REMOTE_VIDEO_TO_FILE_WIDTH, 0);
                    intent.putExtra(CallActivity.EXTRA_SAVE_REMOTE_VIDEO_TO_FILE_WIDTH, videoOutWidth);
                }

                if (getActivity().getIntent().hasExtra(CallActivity.EXTRA_SAVE_REMOTE_VIDEO_TO_FILE_HEIGHT)) {
                    int videoOutHeight =
                            getActivity().getIntent().getIntExtra(CallActivity.EXTRA_SAVE_REMOTE_VIDEO_TO_FILE_HEIGHT, 0);
                    intent.putExtra(CallActivity.EXTRA_SAVE_REMOTE_VIDEO_TO_FILE_HEIGHT, videoOutHeight);
                }
            }

            startActivityForResult(intent, 1);
        }
    }
    private String sharedPrefGetString(
            int attributeId, String intentName, int defaultId, boolean useFromIntent) {
        String defaultValue = getString(defaultId);
        if (useFromIntent) {
            String value = getActivity().getIntent().getStringExtra(intentName);
            if (value != null) {
                return value;
            }
            return defaultValue;
        } else {
            String attributeName = getString(attributeId);
            return sharedPref.getString(attributeName, defaultValue);
        }
    }

    /**
     * Get a value from the shared preference or from the intent, if it does not
     * exist the default is used.
     */
    private boolean sharedPrefGetBoolean(
            int attributeId, String intentName, int defaultId, boolean useFromIntent) {
        boolean defaultValue = Boolean.valueOf(getString(defaultId));
        if (useFromIntent) {
            return getActivity().getIntent().getBooleanExtra(intentName, defaultValue);
        } else {
            String attributeName = getString(attributeId);
            return sharedPref.getBoolean(attributeName, defaultValue);
        }
    }

    /**
     * Get a value from the shared preference or from the intent, if it does not
     * exist the default is used.
     */
    private int sharedPrefGetInteger(
            int attributeId, String intentName, int defaultId, boolean useFromIntent)
    {
        String defaultString = getString(defaultId);
        int defaultValue = Integer.parseInt(defaultString);
        if (useFromIntent) {
            return getActivity().getIntent().getIntExtra(intentName, defaultValue);
        } else {
            String attributeName = getString(attributeId);
            String value = sharedPref.getString(attributeName, defaultString);
            try {
                return Integer.parseInt(value);
            } catch (NumberFormatException e) {
                return defaultValue;
            }
        }
    }

    private boolean validateUrl(String url) {
        if (URLUtil.isHttpsUrl(url) || URLUtil.isHttpUrl(url)) {
            return true;
        }

        new AlertDialog.Builder(getActivity())
                .setTitle(getText(R.string.invalid_url_title))
                .setMessage(getString(R.string.invalid_url_text, url))
                .setCancelable(false)
                .setNeutralButton(R.string.ok,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        })
                .create()
                .show();
        return false;
    }

public static class listclick implements RecyclerView.OnItemTouchListener
    {
        private OnItemClickListener mListener;

        public interface OnItemClickListener
        {
            public void onItemClick(View view, int position);

            public void onLongItemClick(View view, int position);
        }

        GestureDetector mGestureDetector;

        public listclick(Context context, final RecyclerView recyclerView, OnItemClickListener listener) {
            mListener = listener;
            mGestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {
                @Override
                public boolean onSingleTapUp(MotionEvent e) {
                    return true;
                }

                @Override
                public void onLongPress(MotionEvent e) {
                    View child = recyclerView.findChildViewUnder(e.getX(), e.getY());
                    if (child != null && mListener != null) {
                        mListener.onLongItemClick(child, recyclerView.getChildAdapterPosition(child));
                    }
                }
            });
        }

        @Override public boolean onInterceptTouchEvent(RecyclerView view, MotionEvent e) {
            View childView = view.findChildViewUnder(e.getX(), e.getY());
            if (childView != null && mListener != null && mGestureDetector.onTouchEvent(e)) {
                mListener.onItemClick(childView, view.getChildAdapterPosition(childView));
                return true;
            }
            return false;
        }

        @Override public void onTouchEvent(RecyclerView view, MotionEvent motionEvent) { }

        @Override
        public void onRequestDisallowInterceptTouchEvent (boolean disallowIntercept){}

    }
}
