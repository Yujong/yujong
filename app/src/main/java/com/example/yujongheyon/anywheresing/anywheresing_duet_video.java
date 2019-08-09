package com.example.yujongheyon.anywheresing;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by yujongheyon on 2018-09-28.
 */

public class anywheresing_duet_video extends Fragment
{


    Button upload;
    Button video_list_refresh;
    RecyclerView video_list;
    ArrayList<video_list_item> video_list_item;
    video_list_Adapter video_list_Adapter;

    String video_list_title;
    String video_list_creater;
    String video_list_createtime;
    String video_name;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.anywheresing_duet_video,container,false);

        upload = (Button)view.findViewById(R.id.video_upload);
        video_list = (RecyclerView)view.findViewById(R.id.video_list);
        video_list_refresh = (Button)view.findViewById(R.id.video_list_refresh);

        video_list_item = new ArrayList<>();
        video_list();
        video_list.setHasFixedSize(true);



        video_list_refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                video_list_Adapter.notifyDataSetChanged();
            }
        });


        upload.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent intent = new Intent(getActivity(),anywheresing_duet_video_write.class);
                startActivity(intent);
            }
        });





        return view;
    }


    class video_list_Adapter extends RecyclerView.Adapter<video_list_Adapter.RecycleViewHolder>
    {

        ArrayList<video_list_item> video_list_item;
        Context context;
        //private SeekBarUpdater seekBarUpdater;
        private RecycleViewHolder playingHolder;
        private int lastPosition ;


        public video_list_Adapter(ArrayList<video_list_item> video_list_item)
        {
            this.video_list_item = video_list_item;
            this.lastPosition = -1;
            //seekBarUpdater = new SeekBarUpdater();
        }

        @NonNull
        @Override
        public video_list_Adapter.RecycleViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
        {

            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.video_list_item,parent,false);
            context = parent.getContext();
            video_list_Adapter.RecycleViewHolder holder = new video_list_Adapter.RecycleViewHolder(view);


            return holder;
        }

        @Override
        public void onBindViewHolder(@NonNull final video_list_Adapter.RecycleViewHolder holder, int position) {
            holder.video_title_item.setText(video_list_item.get(position).getVideo_title());
            holder.video_creater_id_item.setText(video_list_item.get(position).getVideo_creater_id());
            holder.video_create_time_item.setText(video_list_item.get(position).getVideo_create_time());

            video_list.addOnItemTouchListener(new video_listclick(getActivity(),video_list, new video_listclick.OnItemClickListener() {
                @Override
                public void onItemClick(View view, int position) {
                    Intent intent = new Intent(getActivity(),anywheresing_duet_videoplay.class);
                    intent.putExtra("video_name",video_list_item.get(position).getVideo_name());
                    startActivity(intent);
                }

                @Override
                public void onLongItemClick(View view, int position) {

                }
            }));

        }




        @Override
        public int getItemCount()
        {
            return video_list_item.size();
        }


        public class RecycleViewHolder extends RecyclerView.ViewHolder
        {
            TextView video_title_item;
            TextView video_creater_id_item;
            TextView video_create_time_item;


            public RecycleViewHolder(View itemView)
            {
                super(itemView);
                video_title_item = (TextView)itemView.findViewById(R.id.video_title_item);
                video_creater_id_item = (TextView)itemView.findViewById(R.id.video_creater_id_item);
                video_create_time_item = (TextView)itemView.findViewById(R.id.video_create_time_item);

                itemView.setTag(getAdapterPosition());


            }


        }


    }




    private void video_list()
    {
        final RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
        String url = "http://ec2-18-188-84-158.us-east-2.compute.amazonaws.com/videolist.php";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try

                {
                    JSONObject jsonObject = new JSONObject(response);
                    JSONArray jsonArray = jsonObject.getJSONArray("room");
                    for (int i=0; i< jsonArray.length(); i++)
                    {
                        JSONObject data = jsonArray.getJSONObject(i);
                        video_list_title =  data.getString("video_title");
                        video_list_creater = (data.getString("video_creater_Id"));
                        video_list_createtime = (data.getString("video_create_time"));
                        video_name = data.getString("video_name");
                        video_list_item.add(new video_list_item(video_list_title,video_list_creater,video_list_createtime,video_name));
                    }

                }

                catch (Exception e)
                {

                }


                LinearLayoutManager room_list_layoutManager = new LinearLayoutManager(getActivity());
                room_list_layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
                video_list.setLayoutManager(room_list_layoutManager);
                video_list.setItemAnimator(new DefaultItemAnimator());
                video_list_Adapter = new video_list_Adapter(video_list_item);
                video_list_Adapter.notifyDataSetChanged();
                video_list.setAdapter(video_list_Adapter);


            }
        }, new Response.ErrorListener()
        {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        });
        requestQueue.add(stringRequest);

    }

    public static class video_listclick implements RecyclerView.OnItemTouchListener
    {
        private video_listclick.OnItemClickListener mListener;

        public interface OnItemClickListener
        {
            public void onItemClick(View view, int position);

            public void onLongItemClick(View view, int position);
        }

        GestureDetector mGestureDetector;

        public video_listclick(Context context, final RecyclerView recyclerView,video_listclick.OnItemClickListener listener) {
            mListener = listener;
            mGestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {
                @Override
                public boolean onSingleTapUp(MotionEvent e) {
                    return true;
                }

                @Override
                public void onLongPress(MotionEvent e) {
                    View child = recyclerView.findChildViewUnder(e.getX(), e.getY());
                    if (child != null && mListener != null) {
                        mListener.onLongItemClick(child, recyclerView.getChildAdapterPosition(child));
                    }
                }
            });
        }

        @Override public boolean onInterceptTouchEvent(RecyclerView view, MotionEvent e) {
            View childView = view.findChildViewUnder(e.getX(), e.getY());
            if (childView != null && mListener != null && mGestureDetector.onTouchEvent(e)) {
                mListener.onItemClick(childView, view.getChildAdapterPosition(childView));
                return true;
            }
            return false;
        }

        @Override public void onTouchEvent(RecyclerView view, MotionEvent motionEvent) { }

        @Override
        public void onRequestDisallowInterceptTouchEvent (boolean disallowIntercept){}

    }
}
