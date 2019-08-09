package com.example.livevideobroadcastersdk;

import android.graphics.SurfaceTexture;
import android.os.Handler;
import android.os.Message;
import android.util.Log;


import java.lang.ref.WeakReference;
/**
 * Created by yujongheyon on 2018-08-10.
 */

public class CameraHandler extends Handler {
    private static final String TAG = CameraHandler.class.getSimpleName();

    public static final int MSG_SET_SURFACE_TEXTURE = 0;


    public interface ICameraViewer {

        void handleSetSurfaceTexture(SurfaceTexture st);
    }


    // Weak reference to the Activity; only access this from the UI thread.
    private WeakReference<ICameraViewer> cameraViewerWeakReference;

    public CameraHandler(ICameraViewer cameraViewer) {
        cameraViewerWeakReference = new WeakReference<ICameraViewer>(cameraViewer);
    }

    /**
     * Drop the reference to the activity.  Useful as a paranoid measure to ensure that
     * attempts to access a stale Activity through a handler are caught.
     */
    public void invalidateHandler() {
        cameraViewerWeakReference.clear();
    }

    @Override  // runs on UI thread
    public void handleMessage(Message inputMessage) {
        int what = inputMessage.what;
        Log.d(TAG, "CameraHandler [" + this + "]: what=" + what);

        ICameraViewer cameraViewer = cameraViewerWeakReference.get();
        if (cameraViewer == null) {
            Log.w(TAG, "CameraHandler.handleMessage: cameraViewer is null");
            return;
        }

        switch (what) {
            case MSG_SET_SURFACE_TEXTURE:
                cameraViewer.handleSetSurfaceTexture((SurfaceTexture) inputMessage.obj);
                break;
            default:
                throw new RuntimeException("unknown msg " + what);
        }
    }


}
