package com.example.yujongheyon.anywheresing.rtsp;

/**
 * Created by pedro on 20/02/17.
 */

public interface ConnectCheckerRtsp {

  void onConnectionSuccessRtsp();

  void onConnectionFailedRtsp(String reason);

  void onDisconnectRtsp();

  void onAuthErrorRtsp();

  void onAuthSuccessRtsp();
}
