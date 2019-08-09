package com.example.yujongheyon.anywheresing;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;


/**
 * Created by yujongheyon on 2018-07-30.
 */

public class Sender extends Activity
{
    private ServerSocket serverSocket;
    Handler UPHandler;

    Thread serverThread = null;

    TextView test;

    public static final int SERVERPORT = 9000;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        //setContentView(R.layout.server);

       // test = (TextView)findViewById(R.id.test);

        UPHandler = new Handler();

       /* this.serverThread = new Thread(new ServerThread());
        this.serverThread.start();
    */}

    @Override
    protected void onStop() {
        super.onStop();

        try
        {
            serverSocket.close();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }
    class ServerThread implements Runnable
    {


        @Override
        public void run()
        {
            Socket socket = null;

            try
            {
                serverSocket = new ServerSocket(SERVERPORT);
            }
            catch (IOException e)
            {

            }


            while(!Thread.currentThread().isInterrupted())
            {
                try
                {
                    socket = serverSocket.accept();

                    CommunicationThread thread = new CommunicationThread(socket);
                    new Thread(thread).start();
                }
                catch (IOException e)
                {

                }
            }
        }
    }

    class CommunicationThread implements Runnable
    {
        private Socket clientsocket;
        private BufferedReader bufferedReader;

        public CommunicationThread(Socket clientsocket)
        {
            this.clientsocket = clientsocket;
            try
            {
                this.bufferedReader = new BufferedReader(new InputStreamReader(this.clientsocket.getInputStream()));
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }

        @Override
        public void run()
        {
            while (!Thread.currentThread().isInterrupted())
            {
                try
                {
                    String read = bufferedReader.readLine();

                    UPHandler.post(new TextThread(read));
                }
                catch (IOException e)
                {

                }
            }
        }
    }
    class TextThread implements Runnable
    {
        private String msg;

        public TextThread(String str)
        {
            this.msg = str;
        }

        @Override
        public void run()
        {
            SharedPreferences pref = getSharedPreferences("session", MODE_PRIVATE);

            String sessionId = pref.getString("session","");


            Log.d("23156316",sessionId);

            Log.d("23156316",msg);


        }
    }
}
