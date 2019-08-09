package com.example.yujongheyon.anywheresing;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.wifi.WpsInfo;
import android.net.wifi.aware.WifiAwareManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;

/**
 * Created by yujongheyon on 2018-07-29.
 */

public class samplechatclient extends Activity
{

    EditText insertmsg;

    private static Socket socket;
    private static BufferedReader bufferedReader;
    private static final int SERVERPORT = 9790;
    private static final String SERVER_IP = "192.168.43.202";

    DataInputStream dataInputStream;
    PrintWriter printWriter;
    private Handler handler = new Handler();
    Button submit;

    Handler UPHandler;
    DataOutputStream dataOutputStream;
    private static ServerSocket serverSocket;
    private static InputStreamReader inputStreamReader;


    RecyclerView chatlist;
    ArrayList<chatitem> chatitem;
    RecyclerView.Adapter chatAdapter;




    String sessionId;
    SharedPreferences pref;
    TextView viewMSG;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.clientchat);
        viewMSG = (TextView)findViewById(R.id.viewMSG);
        insertmsg = (EditText)findViewById(R.id.insertmsg);
        submit = (Button)findViewById(R.id.submit);


        chatlist = (RecyclerView)findViewById(R.id.chatlist);
        chatitem = new ArrayList<>();
        chatlist.setHasFixedSize(true);


        pref = getSharedPreferences("session",MODE_PRIVATE);
        sessionId = pref.getString("session","");

        ChatOperator chatOperator = new ChatOperator();
        chatOperator.execute();



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


                        dataOutputStream.writeUTF(sessionId);
                        dataOutputStream.flush();

                } else {

                }
            } catch (UnknownHostException e) {

                e.printStackTrace();
            } catch (IOException e) {

                e.printStackTrace();
            }
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
                    } else {
                        messageSender.execute();
                    }

                    insertmsg.setText("");
                }
            });

            Receiver receiver = new Receiver(); // Initialize chat receiver AsyncTask.
            receiver.execute();

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
           Log.d("connnn",message);
           viewMSG.setText(message);


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
                //printWriter.println(message);
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














/*

            SharedPreferences pref = getSharedPreferences("session", MODE_PRIVATE);

            String sessionId = pref.getString("session","");


            Log.d("23156316",sessionId);

            Log.d("23156316",msg);

            chatitem.add(new chatitem(sessionId,msg));

            LinearLayoutManager chatlist_layoutManager = new LinearLayoutManager(samplechatclient.this);
            chatlist_layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
            chatlist.setLayoutManager(chatlist_layoutManager);
            chatlist.setItemAnimator(new DefaultItemAnimator());
            chatAdapter = new chatAdapter(chatitem);
            chatlist.setAdapter(chatAdapter);
*/





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
        RecycleViewHolder holder = new RecycleViewHolder(view);


        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull RecycleViewHolder holder, int position)
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
