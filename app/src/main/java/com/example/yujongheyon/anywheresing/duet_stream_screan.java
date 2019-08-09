package com.example.yujongheyon.anywheresing;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.hardware.display.VirtualDisplay;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Surface;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.yujongheyon.anywheresing.model.stream_config;
import com.example.yujongheyon.anywheresing.rtmp.ConnectCheckerRtmp;
import com.example.yujongheyon.anywheresing.rtmp.RtmpCamera1;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;



/**
 * Created
 *by yujongheyon on 2018-08-25.
 */

public class duet_stream_screan extends Activity implements ConnectCheckerRtmp {


    SurfaceView duet_stream_screan_view;
    Surface surface;
    RtmpCamera1 rtmpCamera1;



    Button duet_stream_screan_submit;
    EditText duet_stream_screan_msg;
    DataOutputStream dataOutputStream;
    String sessionId;
    SharedPreferences pref;

    RecyclerView duet_stream_screan_chatlist;
    ArrayList<chatitem> chatitem;
    RecyclerView.Adapter chatAdapter;
    String editmessage;


    private static Socket socket;
    private static BufferedReader bufferedReader;
    private static final int SERVERPORT = 9590;
    private static final String SERVER_IP = "192.168.1.149";
    DataInputStream dataInputStream;
    PrintWriter printWriter;


    String stream_title;


    VirtualDisplay virtualDisplay;
    //screen capture



    @TargetApi(Build.VERSION_CODES.KITKAT_WATCH)
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);           // 상태바 툴바 제거
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);    // 화면꺼짐 방지
        setContentView(R.layout.duet_stream_screan);

         duet_stream_screan_view = (SurfaceView)findViewById(R.id.duet_stream_screan);

         rtmpCamera1 = new RtmpCamera1(duet_stream_screan_view,this);



        Intent intent = getIntent();
        stream_title = intent.getStringExtra("room_title");
        duet_stream_screan_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                rtmpCamera1.setAuthorization("Yujong", "whdgus12");
                rtmpCamera1.setVideoBitrateOnFly(stream_config.bitrate);
                rtmpCamera1.prepareAudio(128 * 1024, stream_config.samplerate, true, false, false);
                rtmpCamera1.prepareVideo(stream_config.width, stream_config.height, stream_config.fps, stream_config.bitrate, false, 90);
                rtmpCamera1.getResolutionsBack().get(8);
                rtmpCamera1.enableVideo();
                rtmpCamera1.enableAudio();
                rtmpCamera1.startStream("rtmp://18.220.161.122:1935/mobile/"+stream_title);
            }
        });



        pref = getSharedPreferences("session",MODE_PRIVATE);
        sessionId = pref.getString("session","");

        duet_stream_screan_submit = (Button)findViewById(R.id.duet_stream_screan_submit);
        duet_stream_screan_msg = (EditText)findViewById(R.id.duet_stream_screan_msg);
        ChatOperator chatOperator = new ChatOperator();
        chatOperator.execute();

        duet_stream_screan_chatlist = (RecyclerView)findViewById(R.id.duet_stream_screan_chatlist);
        chatitem = new ArrayList<>();
        duet_stream_screan_chatlist.setHasFixedSize(true);

        LinearLayoutManager chatlist_layoutManager = new LinearLayoutManager(this);
        chatlist_layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        duet_stream_screan_chatlist.setLayoutManager(chatlist_layoutManager);
        duet_stream_screan_chatlist.setItemAnimator(new DefaultItemAnimator());
        chatAdapter = new chatAdapter(chatitem);
        duet_stream_screan_chatlist.setAdapter(chatAdapter);
    }

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
                    dataOutputStream.writeUTF(sessionId+"&"+stream_title);
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
            duet_stream_screan_submit.setOnClickListener(new View.OnClickListener() {
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

                    duet_stream_screan_msg.setText("");
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
            message = duet_stream_screan_msg.getText().toString();



            //dataOutputStream.close();
            printWriter.write(message +"\n");
            printWriter.flush();



            // InetAddress ip = socket.getInetAddress();

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
        try
        {
            socket.close();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }




    class chatAdapter extends RecyclerView.Adapter<chatAdapter.RecycleViewHolder>
    {
        ArrayList<chatitem> chatitem;
        Context context;


        public  chatAdapter(ArrayList<chatitem> chatitem)
        {
            this.chatitem = chatitem;
        }


        @NonNull
        @Override
        public RecycleViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
        {

            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.chatitem,parent,false);
            context = parent.getContext();
            chatAdapter.RecycleViewHolder holder = new chatAdapter.RecycleViewHolder(view);


            return holder;
        }

        @Override
        public void onBindViewHolder(@NonNull chatAdapter.RecycleViewHolder holder, int position)
        {


            holder.chat_content.setText(chatitem.get(position).getChat_content());


        }

        @Override
        public int getItemCount() {
            return chatitem.size();
        }

        public class RecycleViewHolder extends RecyclerView.ViewHolder
        {
            TextView chat_content;

            public RecycleViewHolder(View view)
            {
                super(view);

                chat_content = (TextView)view.findViewById(R.id.chat_content);
            }

        }

    }
}
