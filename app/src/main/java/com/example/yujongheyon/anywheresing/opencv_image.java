package com.example.yujongheyon.anywheresing;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.annotation.Nullable;
import android.util.Base64;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;

/**
 * Created by yujongheyon on 2018-09-15.
 */

public class opencv_image extends Activity{


    private String stringData = null;
    private static final int socketServerPORT = 8013;
    private static final String serverIP = "192.168.1.70";
    DatagramSocket ds;
    DatagramPacket dp;
    ImageView mirror;
    Bitmap bitmap;
    TextView mirror_text;


    MClient mclient = new MClient();
    AMClient amClient = new AMClient();
    AsncClient asncClient = new AsncClient();

    private boolean receive_boolean;
    byte[] msg;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.opencv_image);


        mirror = (ImageView)findViewById(R.id.mirroring);
        // mirror_text = (TextView)findViewById(R.id.mirror_text);
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        WifiManager.WifiLock lock = ((WifiManager)this.getApplicationContext().getSystemService(Context.WIFI_SERVICE)).createWifiLock("MyWifiLock");

  /*      try
        {
            ds = new DatagramSocket(socketServerPORT);

        }
        catch (IOException e)
        {

        }*/


        //client.run();
        /*AsncClient asncClient = new AsncClient();
        asncClient.execute();*/

        //mclient.run();


    /*    if(ds.isClosed())
        {
            amClient.run();
            Log.d("TAGGGGG","againRun");
        }*/

        receive_boolean = true;
        // mclient.run();
        asncClient.execute();


        //amClient.run();

        /*
        if (receive_boolean == false)
        {
                receive_boolean = true;
                AsncClient asncClient  = new AsncClient();
                asncClient.execute();
        }*/
    }


    public void receive_restart()
    {
      /* if (mirror != null && receive_boolean == true)
       {
           receive_boolean = false;
           //mclient.stop();
           //mclient.interrupt();
           if (ds.isClosed() && receive_boolean == false)
           {

               receive_boolean = true;
               mclient.run();
           }
       }*/
        if(ds.isClosed())
        {

            /*asncClient.cancel(true);
          if (asncClient.isCancelled())
          {
            AsncClient asncClient = new AsncClient();
            asncClient.execute();
          }*/
            //receive_boolean = true;
            //mclient.run();
        }
     /* if (ds.isClosed())
      {
       asncClient.cancel(true);
       if(asncClient.isCancelled())
       {
           AsncClient asncClient  = new AsncClient();
           asncClient.execute();
       }

      }*/

        if (receive_boolean == false)
        {


            asncClient.cancel(true);
            if(asncClient.isCancelled())
            {
                receive_boolean = true;
                AsncClient asncClient  = new AsncClient();
                asncClient.execute();
            }

        }

    }
    @Override
    protected void onResume() {
        super.onResume();


        //amClient.run();
        //receive_restart();



        /*if(mirror != null)
        {
            ds.close();
            ds.disconnect();
            if(ds.isClosed())
            {
                mclient.interrupt();
                if (mclient.isInterrupted())
                {

                    receive_boolean = true;
                    mclient.run();
                }

                Log.d("TAGGGGG","againRun");
            }

        }*/


    }

    public class MClient extends Thread
    {


        @Override
        public void run() {

            try {

                ds = new DatagramSocket(socketServerPORT);
                byte[] msg;

                Log.d("TAGGGG","time");
                ds.setSoTimeout(1000);
                //ds.connect(InetAddress.getByName(serverIP),socketServerPORT);
                Log.d("TAGGGG","ti");
                while(receive_boolean)
                {


                    //send,receive fail?
                    msg = new byte[65535];

                    dp = new DatagramPacket(msg,msg.length);

                    Log.d("x111111","timeout");
                    //here ssival

                    ds.receive(dp);
                    Log.d("TAGGGG","tm");
                    stringData = new String(msg,0, dp.getLength());
                    Log.d("TAGGGG",stringData);

                   /* StringToBitMap(stringData);
                    mirror.setImageBitmap(bitmap);
                    if(mirror != null)
                    {
                        mirror.invalidate();
                    }*/





                    /*
                    InetAddress serverAddr = InetAddress.getByName(serverIP);
                    DatagramSocket socket = new DatagramSocket();
                    Log.d("x111111", String.valueOf(serverAddr));
                    byte[] test = stringData.getBytes();

                    bytes = bitmapTobyte(loadImageFromStorage(Environment.getExternalStorageDirectory()+"/captures/"));;
                    Log.d("x55555", String.valueOf(test));
                    dp = new DatagramPacket(test, test.length, serverAddr, socketServerPORT);
                    packet.setData(new byte[1024]);
                    socket.setBroadcast(true);
                    socket.send(dp);*/

                 /*   mirror_text.setText(stringData);
                    if (mirror_text != null)
                    {
                        ds.close();
                        msg = new byte[65535];
                        dp = new DatagramPacket(msg,msg.length);

                        mclient.run();
                    }
                */
                }


            }

            catch (SocketTimeoutException e)
            {

                e.printStackTrace();
                Log.d("ttttime", String.valueOf(e));
                ds.close();

                //ds = null;
                //mclient.interrupt();

               /* if (ds.isClosed())
                {
                    mclient.run();
                }
            */
            }
            catch (UnknownHostException e)
            {
                e.printStackTrace();
                Log.d("tttime", String.valueOf(e));
            }
            catch (SocketException e)
            {
                e.printStackTrace();
                Log.d("ttime", String.valueOf(e));
            }
            catch (IOException e)
            {
                e.printStackTrace();
                Log.d("time", String.valueOf(e));
            }
            //ds.close();
        }

    }

    public class AMClient extends Thread
    {
        @Override
        public void run()
        {
            if (receive_boolean == false)
            {
                receive_boolean = true;
                AsncClient asncClient  = new AsncClient();
                asncClient.execute();
            }


        }
    }


    public class AsncClient extends AsyncTask<Void,Void,Void>
    {


        @Override
        protected Void doInBackground(Void... voids)
        {
            Log.d("TAGGGGAGAF","ressstart");
            msg = new byte[65535];


            try {

                ds = new DatagramSocket(socketServerPORT);
                dp = new DatagramPacket(msg,msg.length);
                Log.d("TAGGGG","time");
                ds.setSoTimeout(500);
                //ds.connect(InetAddress.getByName(serverIP),socketServerPORT);*//*
                Log.d("TAGGGG","ti");
                while(true)
                {

                    //send,receive fail?

                    Log.d("x111111","timeout");
                    //here ssival
                    ds.receive(dp);
                    Log.d("TAGGGG","tm");
                    stringData = new String(msg,0, dp.getLength());
                    Log.d("TAGGGG",stringData);

                    /* StringToBitMap(stringData);
                    mirror.setImageBitmap(bitmap);
                    if(mirror != null)
                    {
                        mirror.invalidate();
                    }
                    continue;*/



                    //mirror_text.setText(stringData);
                }


            }
            catch (SocketTimeoutException e)
            {
                ds.close();
                //ds = null;
                e.printStackTrace();
                Log.d("ttttime",String.valueOf(e));
                receive_boolean = false;
                Log.d("false", String.valueOf(receive_boolean));
                if (ds.isClosed())
                {
                    Log.d("TAGGGG","뒤졌냐?");
                    amClient.run();
                }
            }
            catch (UnknownHostException e)
            {
                e.printStackTrace();
                Log.d("tttime", String.valueOf(e));
            }
            catch (SocketException e)
            {
                e.printStackTrace();
                Log.d("ttime", String.valueOf(e));
            }
            catch (IOException e)
            {
                e.printStackTrace();
                Log.d("time", String.valueOf(e));
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            StringToBitMap(stringData);
            mirror.setImageBitmap(bitmap);
            //Glide.with(opencv_image.this).load(bitmap).into(mirror);
            if(mirror != null)
            {
                mirror.invalidate();
            }
            Log.d("TAGGGG","ㅆㅁㅎㅎㅎ");

        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);


        }

    }



    public Bitmap StringToBitMap(String encodedString)
    {
        try{
            byte [] encodeByte= Base64.decode(encodedString,Base64.DEFAULT);
            bitmap=BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.length);
            return bitmap;
        }catch(Exception e){
            e.getMessage();
            return null;
        }
    }
}
