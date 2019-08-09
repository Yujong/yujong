package com.example.yujongheyon.anywheresing;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by yujongheyon on 2018-09-28.
 */

public class anywheresing_duet_video_write extends Activity
{

    ImageView video_choose;
    EditText video_title;
    Button video_upload;
    Button upload_success;


    String mediaPath;
    String[] mediaColumns = {MediaStore.Video.Media._ID};
    String videoPath;
    String createrId;
    SharedPreferences pref;
    String video_create_time;
    String video_name;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.anywhere_duet_video_writre);

        video_choose = (ImageView)findViewById(R.id.video_choose);
        video_title = (EditText)findViewById(R.id.video_title);
        video_upload = (Button)findViewById(R.id.video_upload);
        upload_success = (Button)findViewById(R.id.upload_success);



        video_choose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                Intent videointent = new Intent(Intent.ACTION_PICK, MediaStore.Video.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(videointent,1);
            }
        });

        Glide.with(this).load(R.drawable.plus).apply(RequestOptions.overrideOf(700,700)).into(video_choose);

        pref = getSharedPreferences("session", MODE_PRIVATE);
        createrId = pref.getString("session", null);


          long now = System.currentTimeMillis();
          Date date = new Date(now);
          SimpleDateFormat record_number = new SimpleDateFormat("YYYY-MM-dd : HH-mm");
          video_create_time = record_number.format(date);



        video_upload.setText("upload");
        upload_success.setTextColor(Color.BLACK);
        upload_success.setEnabled(false);
        video_upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadVideo();


                Response.Listener<String> responseListener = new Response.Listener<String>()
                {
                    @Override
                    public void onResponse(String response)
                    {
                        Log.d("respon",response);
                    }
                };
                video_requset video_requset = new video_requset(video_title.getText().toString(),createrId,video_create_time,video_name,responseListener);
                RequestQueue queue = Volley.newRequestQueue(anywheresing_duet_video_write.this);
                queue.add(video_requset);

            }
        });
        upload_success.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {

                Intent intent = new Intent(anywheresing_duet_video_write.this,anywheresing_mainActivity.class);
                intent.putExtra("success_code",20);
                startActivity(intent);
            }
        });



    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1 && resultCode == RESULT_OK && null != data)
        {


            Uri selectVideo = data.getData();
            String[] filePathColumn = {MediaStore.Video.Media.DATA};
            videoPath = getPath(selectVideo);
            Log.d("videowrite",videoPath);

            video_name = videoPath.substring(29);
            Log.d("videowrite",video_name);

            Cursor cursor = getContentResolver().query(selectVideo,filePathColumn,null,null,null);
            assert cursor != null;
            cursor.moveToFirst();

            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            mediaPath = cursor.getString(columnIndex);
            Log.d("mediawrite",mediaPath);
            Glide.with(this).load(getThumbnailPathForLocalFile(anywheresing_duet_video_write.this,selectVideo)).apply(RequestOptions.overrideOf(700,700)).into(video_choose);

        }

    }

    public Bitmap getThumbnailPathForLocalFile(Activity activity,Uri fileuri)
    {
        long fileid = getFileId(activity,fileuri);

        return MediaStore.Video.Thumbnails.getThumbnail(activity.getContentResolver(),fileid,MediaStore.Video.Thumbnails.MICRO_KIND,null);

    }
    public long getFileId(Activity activity,Uri uri)
    {
        Cursor cursor = activity.managedQuery(uri,mediaColumns,null,null,null);
        if (cursor.moveToFirst())
        {
            int columnindex = cursor.getColumnIndexOrThrow(MediaStore.Video.Media._ID);
            return cursor.getInt(columnindex);
        }
        return 0;
    }

    public String getPath(Uri uri)
    {
        Cursor cursor = getContentResolver().query(uri, null, null, null, null);
        cursor.moveToFirst();
        String document_id = cursor.getString(0);
        document_id = document_id.substring(document_id.lastIndexOf(":") + 1);
        cursor.close();

        cursor = getContentResolver().query(
                android.provider.MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
                null, MediaStore.Images.Media._ID + " = ? ", new String[]{document_id}, null);
        cursor.moveToFirst();
        String path = cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.DATA));
        cursor.close();

        return path;
    }


    private void uploadVideo() {
        class UploadVideo extends AsyncTask<Void, Void, String> {

            ProgressDialog uploading;

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                uploading = ProgressDialog.show(anywheresing_duet_video_write.this, "Uploading File", "Please wait...", false, false);
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                uploading.dismiss();
                upload_success.setEnabled(true);
                upload_success.setTextColor(Color.WHITE);
                video_title.setText("");
                Glide.with(anywheresing_duet_video_write.this).load(R.drawable.plus).apply(RequestOptions.overrideOf(700,700)).into(video_choose);

            }

            @Override
            protected String doInBackground(Void... params) {
                upload_video u = new upload_video();
                String msg = u.uploadVideo(videoPath);
                return msg;
            }
        }
        UploadVideo uv = new UploadVideo();
        uv.execute();
    }
}
