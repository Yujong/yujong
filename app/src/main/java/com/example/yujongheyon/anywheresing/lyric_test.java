package com.example.yujongheyon.anywheresing;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.util.ArrayList;

/**
 * Created by yujongheyon on 2018-09-18.
 */

public class lyric_test extends Activity {


    RecyclerView lyric_list;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.lyric_test);

        lyric_list = (RecyclerView) findViewById(R.id.lyric_list);
        lyric_list.setHasFixedSize(true);
        /*setContentView(R.layout.lyric_test);
        //http://ec2-18-188-84-158.us-east-2.compute.amazonaws.com/lyricfile/어디에도.lrc
        String path = "AnywhereSing/app/src/main/res/raw/anywhere.lrc";
        File file = new File (path);
        Log.d("fillllle", String.valueOf(file));


        File testfile = new File(String.valueOf(R.raw.anywhere));
        LyricView lyric_view = (LyricView)findViewById(R.id.lyric_view);
        lyric_view.setLyricFile(testfile);
        lyric_view.setCurrentTimeMillis(1000);
        lyric_view.setOnPlayerClickListener(new LyricView.OnPlayerClickListener() {
            @Override
            public void onPlayerClicked(long l, String s) {

            }
        });*/


        LinearLayoutManager MRlist_layoutManager = new LinearLayoutManager(lyric_test.this);
        MRlist_layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        ArrayList<mrItem> items = new ArrayList<>();

        items.add(new mrItem(R.drawable.nomatterwhere,"어디에도","MC THE MAX"));

        lyric_list.setLayoutManager(MRlist_layoutManager);
        lyric_list.setItemAnimator(new DefaultItemAnimator());
        lyric_list_adpter lyric_list_adpter= new lyric_list_adpter(items);
        lyric_list.setAdapter(lyric_list_adpter);



    }

    class lyric_list_adpter extends RecyclerView.Adapter<lyric_list_adpter.RecycleViewHolder>
    {
        ArrayList<mrItem> mrItems;
        Context context;

        public lyric_list_adpter(ArrayList<mrItem> items)
        {
            this.mrItems = items;
        }


        @NonNull
        @Override
        public RecycleViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.lyric_item,parent,false);
            context = parent.getContext();
            lyric_list_adpter.RecycleViewHolder holder = new lyric_list_adpter.RecycleViewHolder(view);
            return holder;
        }

        @Override
        public void onBindViewHolder(@NonNull RecycleViewHolder holder, int position)
        {

            Glide.with(lyric_test.this).load(mrItems.get(position).getCoverImage()).apply(RequestOptions.overrideOf(200,200).circleCrop()).into(holder.MRlist_image);
            holder.MRlist_title.setText(mrItems.get(position).getMr_title());
            holder.Mrlist_artist.setText(mrItems.get(position).getArtist());


            final int itemposition = holder.getLayoutPosition();

            final Intent intent = new Intent(lyric_test.this,CallActivity.class);

            holder.MRlayout.setOnClickListener(new View.OnClickListener() {
                @SuppressLint("ResourceType")
                @Override
                public void onClick(View v) {
                    if (itemposition == 0)
                    {
                            String where = "어디에도";
                        /* intent.putExtra("lyric",ly);
                        startActivityForResult(intent,50);
                        setResult(50);
                        finish();*/
                       /* CallFragment callFragment = new CallFragment();
                        Bundle bundle = new Bundle(); // 파라미터는 전달할 데이터 개수
                        bundle.putString("lyric",ly); // key , value
                        callFragment.setArguments(bundle);*/

                        SharedPreferences pref = getSharedPreferences("lyric", MODE_PRIVATE);
                        SharedPreferences.Editor editor = pref.edit();
                        editor.putString("lyric",where);
                        Log.d("lyricccccc",where);
                        editor.commit();


                     /* android.app.FragmentManager fragmentManager = getFragmentManager();
                        android.app.FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                        fragmentTransaction.replace(R.layout.fragment_call,callFragment);
                        fragmentTransaction.commit();*/
                        finish();



                    }


                }
            });


        }

        @Override
        public int getItemCount()
        {
            return mrItems.size();
        }

        public class RecycleViewHolder extends RecyclerView.ViewHolder
        {
            ImageView MRlist_image;
            TextView MRlist_title;
            TextView Mrlist_artist;
            LinearLayout MRlayout;




            //SeekBar Mr_bar;

            //SeekBar Mr_bar;


            public RecycleViewHolder(View itemView)
            {
                super(itemView);
                MRlist_image = (ImageView)itemView.findViewById(R.id.MRlist_image);
                MRlist_title = (TextView)itemView.findViewById(R.id.MRlist_title);
                Mrlist_artist = (TextView)itemView.findViewById(R.id.MRlist_artist);
                MRlayout = (LinearLayout)itemView.findViewById(R.id.MrLayout);

                // play_time = (TextView)itemView.findViewById(R.id.play_time);
                //MRplay_time = (TextView)itemView.findViewById(R.id.MRplay_time);
                //Mr_bar = (SeekBar)itemView.findViewById(R.id.Mr_bar);
            }



        }
    }
}
