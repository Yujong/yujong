package com.example.yujongheyon.anywheresing;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.hardware.display.DisplayManager;
import android.hardware.display.VirtualDisplay;
import android.media.Image;
import android.media.ImageReader;
import android.media.projection.MediaProjection;
import android.media.projection.MediaProjectionManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.OrientationEventListener;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;
import java.nio.ByteBuffer;

/**
 * Created by yujongheyon on 2018-08-11.
 */

public class broad extends AppCompatActivity
{
// PLEASE WRITE RTMP BASE URL of the your RTMP SERVER.

    //public static final String RTMP_BASE_URL = "rtmp://10.2.41.95/LiveApp/";

    TextView infoip, msg;


    private static final String TAG = "buffffer";
    private static final int REQUEST_CODE = 100;
    private static String STORE_DIRECTORY;
    private static int IMAGES_PRODUCED = 0;
    private static final String SCREENCAP_NAME = "screencap";
    private static final int VIRTUAL_DISPLAY_FLAGS = DisplayManager.VIRTUAL_DISPLAY_FLAG_OWN_CONTENT_ONLY | DisplayManager.VIRTUAL_DISPLAY_FLAG_PUBLIC;
    private static MediaProjection sMediaProjection;
    private MediaProjectionManager projectionManager;
    private ImageReader mImageReader;
    private Handler mHandler;
    private Display mDisplay;
    private VirtualDisplay mVirtualDisplay;
    private int mDensity;
    private int mWidth;
    private int mHeight;
    private int mRotation;
    private OrientationChangeCallback mOrientationChangeCallback;
    byte[] bytes;
    byte[] bit_byte;
    Bitmap bitmap;
    String image_string;

    private static final int socketServerPORT = 8015;
    private static final String serverIP = "127.0.0.1";
    private static final String serverIP1 = "172.30.1.22";
    private static final String serverIP2 = "172.30.1.52";



    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.broad);

                projectionManager = (MediaProjectionManager)
                getSystemService(Context.MEDIA_PROJECTION_SERVICE);

               // startProjection();


       // Thread send = new Thread(new sendImage());
       // send.start();


        Thread send = new Thread(new TCPtest());
        send.start();


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

    }


    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private class ImageAvailableListener implements ImageReader.OnImageAvailableListener {
        @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
        @Override
        public void onImageAvailable(ImageReader reader) {
            Image image = null;
            FileOutputStream fos = null;
            bitmap = null;

            try {
                image = reader.acquireLatestImage();
                if (image != null) {
                    Image.Plane[] planes = image.getPlanes();
                    ByteBuffer buffer = planes[0].getBuffer();
                    int pixelStride = planes[0].getPixelStride();
                    int rowStride = planes[0].getRowStride();
                    int rowPadding = rowStride - pixelStride * mWidth;
                    // create bitmap
                    bitmap = Bitmap.createBitmap(mWidth + rowPadding / pixelStride, mHeight, Bitmap.Config.ARGB_8888);
                    bitmap.copyPixelsFromBuffer(buffer);
                    // write bitmap to a file
                    fos = new FileOutputStream(STORE_DIRECTORY + "/myscreen_" + IMAGES_PRODUCED + ".png");
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 5, fos);
                    createImage(bitmap,IMAGES_PRODUCED);
                    Log.d(TAG, String.valueOf(bitmap));
                    bitmapTobyte(bitmap);
                    Log.d(TAG, String.valueOf(bitmap));
                    Log.d(TAG, String.valueOf(bytes));
                    encodeToBase64(bitmap);




                    IMAGES_PRODUCED++;
                    Log.d(TAG, "captured image: " + IMAGES_PRODUCED);

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

                if (bitmap != null) {
                    bitmap.recycle();
                }

                if (image != null) {
                    image.close();
                }
            }

        }
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public byte[] bitmapTobyte(Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 5, stream);
        bit_byte = stream.toByteArray();
        Log.d(TAG, String.valueOf(bit_byte));

        return bit_byte;
    }
    public void encodeToBase64(Bitmap bitmap)
    {
        ByteArrayOutputStream byteArrayOS = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG,5, byteArrayOS);
        image_string = Base64.encodeToString(byteArrayOS.toByteArray(),Base64.DEFAULT);

    }

    private class OrientationChangeCallback extends OrientationEventListener {

        OrientationChangeCallback(Context context) {
            super(context);
        }

        @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
        @Override
        public void onOrientationChanged(int orientation) {
            final int rotation = mDisplay.getRotation();
            if (rotation != mRotation) {
                mRotation = rotation;
                try {
                    // clean up
                    if (mVirtualDisplay != null) mVirtualDisplay.release();
                    if (mImageReader != null) mImageReader.setOnImageAvailableListener(null, null);

                    // re-create virtual display depending on device width / height
                    createVirtualDisplay();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private class MediaProjectionStopCallback extends MediaProjection.Callback {
        @Override
        public void onStop() {
            Log.e("ScreenCapture", "stopping projection.");
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    if (mVirtualDisplay != null) mVirtualDisplay.release();
                    if (mImageReader != null) mImageReader.setOnImageAvailableListener(null, null);
                    if (mOrientationChangeCallback != null) mOrientationChangeCallback.disable();
                    sMediaProjection.unregisterCallback(MediaProjectionStopCallback.this);
                }
            });
        }
    }


    @TargetApi(Build.VERSION_CODES.M)
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE) {
            sMediaProjection = projectionManager.getMediaProjection(resultCode, data);

            if (sMediaProjection != null) {
                File externalFilesDir = broad.this.getExternalFilesDir(null);
                if (externalFilesDir != null) {
                    STORE_DIRECTORY = externalFilesDir.getAbsolutePath() + "/screenshots/";
                    File storeDirectory = new File(STORE_DIRECTORY);
                    if (!storeDirectory.exists()) {
                        boolean success = storeDirectory.mkdirs();
                        if (!success) {
                            Log.e(TAG, "failed to create file storage directory.");
                            return;
                        }
                    }
                } else {
                    Log.e(TAG, "failed to create file storage directory, getExternalFilesDir is null.");
                    return;
                }

                // display metrics
                DisplayMetrics metrics = getResources().getDisplayMetrics();
                mDensity = metrics.densityDpi;
                mDisplay = broad.this.getWindowManager().getDefaultDisplay();

                // create virtual display depending on device width / height
                createVirtualDisplay();

                // register orientation change callback
                mOrientationChangeCallback = new OrientationChangeCallback(broad.this);
                if (mOrientationChangeCallback.canDetectOrientation()) {
                    mOrientationChangeCallback.enable();
                }

                // register media projection stop callback
                sMediaProjection.registerCallback(new MediaProjectionStopCallback(), mHandler);
            }
        }


    }

    /****************************************** UI Widget Callbacks *******************************/
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void startProjection() {
        startActivityForResult(projectionManager.createScreenCaptureIntent(), REQUEST_CODE);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void stopProjection() {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                if (sMediaProjection != null) {

                    sMediaProjection.stop();
                }
            }
        });
    }


    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void createVirtualDisplay() {
        // get width and height
        Point size = new Point();
        mDisplay.getSize(size);
        mWidth = size.x;
        mHeight = size.y;

        // start capture reader
        mImageReader = ImageReader.newInstance(mWidth, mHeight, PixelFormat.RGBA_8888, 2);
        mVirtualDisplay = sMediaProjection.createVirtualDisplay(SCREENCAP_NAME, mWidth, mHeight, mDensity, VIRTUAL_DISPLAY_FLAGS, mImageReader.getSurface(), null, mHandler);
        mImageReader.setOnImageAvailableListener(new ImageAvailableListener(), mHandler);
    }


    public byte[] getbyte() {
        Log.d(TAG, String.valueOf(bytes));

        return bytes;
    }

    public void createImage(Bitmap bmp,int i) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 10, bytes);

        File file1 = new File(Environment.getExternalStorageDirectory() +"/captures");
        file1.mkdir();

        File file = new File(Environment.getExternalStorageDirectory() +
                "/captures/capturedscreenandroid"+i+".jpg");
        try
        {
            file.createNewFile();
            FileOutputStream outputStream = new FileOutputStream(file);
            outputStream.write(bytes.toByteArray());
            outputStream.close();
            //Toast.makeText(getApplicationContext(),"success",Toast.LENGTH_SHORT).show();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }




    private class sendImage extends Thread
    {


        DatagramPacket packet;
        @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
        @Override
        public void run()
        {
            try {

                for (int i = 0; i < 1000; i ++)
                {
                    String test = "12345678910 : " + i;
                    bytes = test.getBytes();
                /*String test = "test";
                bytes = test.getBytes();*/

                    InetAddress serverAddr = InetAddress.getByName(serverIP);
                    DatagramSocket socket = new DatagramSocket();
                    Log.d("x111111", String.valueOf(serverAddr));

                    //bytes = bitmapTobyte(loadImageFromStorage(Environment.getExternalStorageDirectory()+"/captures/"));;
                    Log.d("x55555", String.valueOf(bytes));
                    packet = new DatagramPacket(bytes, bytes.length,serverAddr,socketServerPORT);
                    //packet.setData(new byte[1024]);
                    //socket.setBroadcast(true);
                    socket.send(packet);
                }





              /*  InetAddress serverAddr1 = InetAddress.getByName(serverIP1);
                packet = new DatagramPacket(bytes,bytes.length,serverAddr1,socketServerPORT);
                socket.send(packet);

                InetAddress serverAddr2 = InetAddress.getByName(serverIP2);
                packet = new DatagramPacket(bytes,bytes.length,serverAddr2,socketServerPORT);
                socket.send(packet);*/


            }
            //자체피드백
            catch (IOException e)
            {
                e.printStackTrace();
            }

        }
    }
    private class TCPtest extends Thread
    {

        rudpcl.ReliableServerSocket serversocket = null;
        BufferedReader br = null;
        BufferedWriter bw = null;
        Socket socket = null;


        @Override
        public void run()
        {
            try
            {

                serversocket = new rudpcl.ReliableServerSocket(8890);
                System.out.println("시작");
                while(true)
                {
                    socket = serversocket.accept();

                    br = new BufferedReader(new InputStreamReader(socket.getInputStream()));

                    String receive_message = br.readLine();

                    System.out.println(receive_message);

                    bw = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));

                    bw.write(receive_message + "\n");
                    bw.flush();


                }

            }
            catch(IOException e)
            {
                e.printStackTrace();
            }
        }


    }






}
