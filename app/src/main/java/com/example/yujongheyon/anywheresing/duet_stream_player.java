package com.example.yujongheyon.anywheresing;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.videolan.libvlc.IVLCVout;
import org.videolan.libvlc.LibVLC;
import org.videolan.libvlc.Media;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.lang.ref.WeakReference;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;

import static com.example.livevideobroadcastersdk.encoder.gles.GlUtil.TAG;

/**
 * Created by yujongheyon on 2018-08-25.
 */

public class duet_stream_player extends Activity implements IVLCVout.Callback{

    private SurfaceView duet_stream_play;
    Uri uri;
    private SurfaceHolder holder;
    private LibVLC libvlc;
    private org.videolan.libvlc.MediaPlayer mMediaPlayer = null;
    private int mVideoWidth;
    private int mVideoHeight;





    Button duet_stream_play_submit;
    EditText duet_stream_play_msg;
    DataOutputStream dataOutputStream;
    String sessionId;
    SharedPreferences pref;

    RecyclerView duet_stream_play_chatlist;
    ArrayList<chatitem> chatitem;
    RecyclerView.Adapter chatAdapter;
    String editmessage;


    private static Socket socket;
    private static BufferedReader bufferedReader;
    private static final int SERVERPORT = 9590;
    private static final String SERVER_IP = "192.168.1.149";
    DataInputStream dataInputStream;
    PrintWriter printWriter;


    String stream_name;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.duet_stream_player);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);    // 화면꺼짐 방지

        Intent intent = getIntent();
        stream_name =  intent.getStringExtra("room_title");

        duet_stream_play = (SurfaceView)findViewById(R.id.duet_stream_play);

        String streamUrl = "rtmp://18.220.161.122:1935/mobile/"+stream_name;
        uri = Uri.parse(streamUrl);

        holder = duet_stream_play.getHolder();
        //holder.lockCanvas().rotate(90);
        createPlayer(streamUrl);


        pref = getSharedPreferences("session",MODE_PRIVATE);
        sessionId = pref.getString("session","");

        duet_stream_play_submit = (Button)findViewById(R.id.duet_stream_play_submit);
        duet_stream_play_msg = (EditText)findViewById(R.id.duet_stream_play_msg);
        ChatOperator chatOperator = new ChatOperator();
        chatOperator.execute();

        duet_stream_play_chatlist = (RecyclerView)findViewById(R.id.duet_stream_play_chatlist);
        chatitem = new ArrayList<>();
        duet_stream_play_chatlist.setHasFixedSize(true);

        LinearLayoutManager chatlist_layoutManager = new LinearLayoutManager(this);
        chatlist_layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        duet_stream_play_chatlist.setLayoutManager(chatlist_layoutManager);
        duet_stream_play_chatlist.setItemAnimator(new DefaultItemAnimator());
        chatAdapter = new chatAdapter(chatitem);
        duet_stream_play_chatlist.setAdapter(chatAdapter);

    }

    private org.videolan.libvlc.MediaPlayer.EventListener mPlayerListener = new MyPlayerListener(this);

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        setSize(mVideoWidth, mVideoHeight);
    }


    @Override
    public void onNewLayout(IVLCVout vlcVout, int width, int height, int visibleWidth, int visibleHeight, int sarNum, int sarDen) {
        if (width * height == 0)
            return;

        // store video size
        mVideoWidth = width;
        mVideoHeight = height;

    }

    @Override
    public void onSurfacesCreated(IVLCVout vlcVout) {

    }

    @Override
    public void onSurfacesDestroyed(IVLCVout vlcVout) {

    }

    @Override
    public void onHardwareAccelerationError(IVLCVout vlcVout) {
        Log.e(TAG, "Error with hardware acceleration");
        this.releasePlayer();
        Toast.makeText(this, "Error with hardware acceleration", Toast.LENGTH_LONG).show();
    }




    private void setSize(int width, int height) {
        mVideoWidth = width;
        mVideoHeight = height;
        if (mVideoWidth * mVideoHeight <= 1)
            return;

        if (holder == null || duet_stream_play == null)
            return;

        int w = getWindow().getDecorView().getWidth();
        int h = getWindow().getDecorView().getHeight();
        boolean isPortrait = getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT;
        if (w > h && isPortrait || w < h && !isPortrait) {
            int i = w;
            w = h;
            h = i;
        }

        float videoAR = (float) mVideoWidth / (float) mVideoHeight;
        float screenAR = (float) w / (float) h;

        if (screenAR < videoAR)
            h = (int) (w / videoAR);
        else
            w = (int) (h * videoAR);

        holder.setFixedSize(mVideoWidth, mVideoHeight);
        ViewGroup.LayoutParams lp = duet_stream_play.getLayoutParams();
        lp.width = w;
        lp.height = h;
        duet_stream_play.setLayoutParams(lp);
        duet_stream_play.invalidate();
    }

    private void createPlayer(String uri)
    {
        try {
            if (uri.length() > 0) {
                Toast toast = Toast.makeText(this,uri, Toast.LENGTH_LONG);
                toast.setGravity(Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0,
                        0);
                toast.show();
            }

            // Create LibVLC
            // TODO: make this more robust, and sync with audio demo
            ArrayList<String> options = new ArrayList<String>();
            //options.add("--subsdec-encoding <encoding>");
            options.add("--aout=opensles");
            options.add("--audio-time-stretch"); // time stretching
            options.add("-vvv"); // verbosity
            libvlc = new LibVLC(this, options);
            holder.setKeepScreenOn(true);

            // Creating media player
            mMediaPlayer = new org.videolan.libvlc.MediaPlayer(libvlc);
            mMediaPlayer.setEventListener(mPlayerListener);

            // Seting up video output
            final IVLCVout vout = mMediaPlayer.getVLCVout();
            vout.setVideoView(duet_stream_play);
            //vout.setSubtitlesView(mSurfaceSubtitles);
            vout.addCallback(this);
            vout.attachViews();

            Media m = new Media(libvlc, Uri.parse(uri));
            mMediaPlayer.setMedia(m);
            // duet_stream_play.setRotation(45);

            mMediaPlayer.play();
             //duet_stream_play.setRotationY(90);
            // duet_stream_play.setRotation(90);
            //setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        } catch (Exception e) {
            Toast.makeText(this, "Error in creating player!", Toast
                    .LENGTH_LONG).show();
        }
    }
    private void releasePlayer() {
        if (libvlc == null)
            return;
        mMediaPlayer.stop();
        final IVLCVout vout = mMediaPlayer.getVLCVout();
        vout.removeCallback(this);
        vout.detachViews();
        holder = null;
        libvlc.release();
        libvlc = null;

        mVideoWidth = 0;
        mVideoHeight = 0;
    }


    private static class MyPlayerListener implements org.videolan.libvlc.MediaPlayer.EventListener {
        private WeakReference<duet_stream_player> mOwner;

        public MyPlayerListener(duet_stream_player owner) {
            mOwner = new WeakReference<duet_stream_player>(owner);
        }

        @Override
        public void onEvent(org.videolan.libvlc.MediaPlayer.Event event) {
            duet_stream_player player = mOwner.get();

            switch (event.type) {
                case org.videolan.libvlc.MediaPlayer.Event.EndReached:
                    Log.d(TAG, "MediaPlayerEndReached");
                    player.releasePlayer();
                    break;
                case org.videolan.libvlc.MediaPlayer.Event.Playing:
                case org.videolan.libvlc.MediaPlayer.Event.Paused:
                case org.videolan.libvlc.MediaPlayer.Event.Stopped:
                default:
                    break;
            }
        }
    }










    private class ChatOperator extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... arg0) {
            try {
                socket = new Socket(SERVER_IP,SERVERPORT); // Creating the server socket.

                DatagramSocket datagramSocket = new DatagramSocket();
                String updMessage = "GG";
                InetAddress ia = InetAddress.getByName("127.0.0.1");//39.7.19.226 /192.168.43.202
                DatagramPacket datagramPacket = new DatagramPacket(updMessage.getBytes(),updMessage.length(),ia,SERVERPORT);
                datagramSocket.send(datagramPacket);


                if (socket != null) {
                    printWriter = new PrintWriter(socket.getOutputStream(), true);
                    InputStreamReader inputStreamReader = new InputStreamReader(socket.getInputStream());
                    //dataOutputStream = new DataOutputStream(socket.getOutputStream());

                    bufferedReader = new BufferedReader(inputStreamReader);
                    dataInputStream = new DataInputStream(socket.getInputStream());
                    dataOutputStream = new DataOutputStream(socket.getOutputStream());


                    // dataOutputStream.writeUTF(sessionId);
                    dataOutputStream.writeUTF(sessionId+"&"+stream_name);
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
            duet_stream_play_submit.setOnClickListener(new View.OnClickListener() {
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

                    duet_stream_play_msg.setText("");
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
            message = duet_stream_play_msg.getText().toString();



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