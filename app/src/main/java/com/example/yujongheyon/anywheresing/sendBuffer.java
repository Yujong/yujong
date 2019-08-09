package com.example.yujongheyon.anywheresing;

import android.util.Log;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

/**
 * Created by yujongheyon on 2018-09-05.
 */

public class sendBuffer
{
    private static final String serverIP = "192.168.1.46"; //serverIP를 추가합니다.
    private static final int port = 7000; //서버에서 설정한 UDP 포트번호를 추가합니다.
    private byte[] buffer;
    private String return_msg;

    public sendBuffer(byte[] buffer) {
        this.buffer = buffer;
    }
    public String run() {
        try {
            DatagramSocket socket = new DatagramSocket();

            InetAddress serverAddr = InetAddress.getByName(serverIP);

            byte[] buf = buffer;
            DatagramPacket Packet = new DatagramPacket(buf, buf.length,serverAddr,port);
            Log.d("UDP", "sendpacket.... " + new String(buf));
            socket.send(Packet);
            Log.d("UDP", "send....");
            Log.d("UDP", "Done.");

            socket.receive(Packet);
            Log.d("UDP", "Receive" + new String(Packet.getData()));

            //데이터를 받아와 return_msg에 복사합니다.
            //return_msg = new String(Packet.getData());

        } catch (Exception ex) {
            Log.d("UDP", "C: Error", ex);
        }
        return String.valueOf(buffer);
    }

}
