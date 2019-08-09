package com.example.yujongheyon.anywheresing;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.widget.MediaController;
import android.widget.VideoView;

/**
 * Created by yujongheyon on 2018-09-28.
 */

public class anywheresing_duet_videoplay extends Activity
{
    VideoView video_play;
    /*RecyclerView video_reple_list;
    EditText video_reple_content;
    Button video_reple_upload;
*/

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        setContentView(R.layout.anywheresing_duet_videoplay);
        super.onCreate(savedInstanceState);

        video_play = (VideoView)findViewById(R.id.video_play);
       /* video_reple_list = (RecyclerView)findViewById(R.id.video_reple_list);
        video_reple_content = (EditText)findViewById(R.id.video_reple_content);
        video_reple_upload = (Button)findViewById(R.id.video_reple_upload);*/


        Intent intent = getIntent();
        String video_name = intent.getStringExtra("video_name");
        String video_path = "http://ec2-18-188-84-158.us-east-2.compute.amazonaws.com/video" + "/" + video_name;
        Uri video_uri_video_path = Uri.parse(video_path);

        Bitmap thumbnail = ThumbnailUtils.createVideoThumbnail(video_path, MediaStore.Images.Thumbnails.MINI_KIND);
        final MediaController mediaController = new MediaController(this);
        BitmapDrawable bitmapDrawable = new BitmapDrawable(thumbnail);
        video_play.setBackgroundDrawable(bitmapDrawable);
        video_play.setMediaController(mediaController);
        video_play.setVideoURI(video_uri_video_path);
        video_play.requestFocus();
        video_play.postDelayed(new Runnable() {
            @Override
            public void run() {
                mediaController.show(0);
                video_play.pause();
            }
        }, 100);
        video_play.start();


    }
}
