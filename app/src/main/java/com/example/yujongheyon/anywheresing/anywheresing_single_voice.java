package com.example.yujongheyon.anywheresing;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioRecord;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.support.v7.widget.AppCompatDrawableManager;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/*
import com.google.android.youtube.player.YouTubeBaseActivity;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerView;*/

/**
 * Created by yujongheyon on 2018-06-29.
 */

public class anywheresing_single_voice  extends Fragment{

    private static final long START_TIME_IN_MILLIS = 600000;

    private static final int RECODER_SAMPLERATE =8000;
    private static final int RECODER_AUDIO_ENCODING = AudioFormat.ENCODING_PCM_16BIT;
    private static final int RECODER_CHANNELS = AudioFormat.CHANNEL_IN_MONO;
    private AudioRecord recorder = null;
    private Thread recordingThread = null;
    int count=0;

    private long timeleft = START_TIME_IN_MILLIS;

     MediaRecorder mediaRecorder;
     MediaPlayer mediaPlayer;
     String outputFile ;
     boolean isRecording = false;
    Button record_start;
    Button record_stop;
    Button record_play;
    Button record_save;
    int cnt;

    MediaPlayer MRplayer = new MediaPlayer();


    TextView record_timer;
    WebView webMR;


    ListView search_list;

    RecyclerView MRlist;
    RecyclerView.Adapter MRlist_Adapter;
    //RecyclerView.LayoutManager MRlist_layoutManager;


    boolean Mrplaying = true;
    boolean MrplayingStats;


    private static final int SAMPLE_RATE = 8000;
    private static final int CHANNEL_CONFIG = AudioFormat.CHANNEL_CONFIGURATION_MONO;
    private static final int AUDIO_ENCODING = AudioFormat.ENCODING_PCM_16BIT;
    private ProgressBar progressBar;
    TextView record_subject;



    String record_save_name;


    TextView play_time;
    TextView mrplay_time;
    SeekBar Mr_bar;



    @SuppressLint("RestrictedApi")
    AppCompatDrawableManager drawableManager = new AppCompatDrawableManager();
    EditText mrsubjectsearch;
    AsyncTask<?, ?, ?> searchTask;

    Button mrsubjectsearchbtn;

    Handler handler;


    //final String serverKey="서버키를 넣으세요";


    long CurrentSongLength;

  /*  int play_time;
    int play_full_time;*/

    @Nullable
    @Override
    public View onCreateView(final LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.single_voice,container,false);

        MRlist = (RecyclerView)view.findViewById(R.id.MRlist);
        MRlist.setHasFixedSize(true);
      // MRlist_layoutManager = new StaggeredGridLayoutManager(2,StaggeredGridLayoutManager.VERTICAL);
        LinearLayoutManager MRlist_layoutManager = new LinearLayoutManager(getActivity());
        MRlist_layoutManager.setOrientation(LinearLayoutManager.VERTICAL);


        ArrayList<mrItem> items = new ArrayList<>();



        items.add(new mrItem(R.drawable.nomatterwhere,"어디에도","MC THE MAX"));
        items.add(new mrItem(R.drawable.libsay,"입술의 말","MC THE MAX"));
        items.add(new mrItem(R.drawable.loved,"사랑했지만","김광석"));
        items.add(new mrItem(R.drawable.paulkimevery,"모든날 모든순간","폴 킴"));
        items.add(new mrItem(R.drawable.lasthug,"마지막으로 안아도 될까","허 각"));
        items.add(new mrItem(R.drawable.thank,"감사","김동률"));
        items.add(new mrItem(R.drawable.isuway,"MyWay","이수"));
        items.add(new mrItem(R.drawable.brown,"Love Ballad","Brwon eyed Soul"));
        items.add(new mrItem(R.drawable.lovewhy,"그대를 사랑하는 10가지 이유","이석훈"));

        MRlist.setLayoutManager(MRlist_layoutManager);
        MRlist.setItemAnimator(new DefaultItemAnimator());
        MRlist_Adapter = new MrAdapter(items);
        MRlist.setAdapter(MRlist_Adapter);









       /* progressBar = (ProgressBar)view.findViewById(R.id.progressBar);
        LinearLayout sw = (LinearLayout)view.findViewById(R.id.SoundWave);
        soundWave = new SoundWave(getContext());
        sw.addView(soundWave);*/



        //int bufferSize = AudioRecord.getMinBufferSize(RECODER_SAMPLERATE,RECODER_CHANNELS,RECODER_AUDIO_ENCODING);
     /* search_list = (ListView)view.findViewById(R.id.search_list);
        mrsubjectsearch = (EditText)view.findViewById(R.id.mrsubjectsearch);
        mrsubjectsearchbtn = (Button)view.findViewById(R.id.mrsubjectsearchbtn);


        mrsubjectsearchbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                searchTask = new SearchTask().execute();
            }
        });*/





             //  outputFile = Environment.getExternalStorageDirectory().getAbsolutePath()+".wav" ;

        // File audioFile = Environment.getExternalStorageDirectory();


          record_start = (Button)view.findViewById(R.id.record_start);
          record_stop = (Button)view.findViewById(R.id.record_stop);
          //record_play = (Button)view.findViewById(R.id.record_play);
        record_save = (Button)view.findViewById(R.id.record_save);

        record_stop.setEnabled(false);
        // record_play.setEnabled(false);
        record_save.setEnabled(false);
/*

Glide.with(this).load(R.drawable.baseline_play_arrow_black_24dp).apply(RequestOptions.overrideOf(150,150)).into(record_play);

Glide.with(this).load(R.drawable.baseline_stop_black_18dp).apply(RequestOptions.overrideOf(150,150)).into(record_stop);
Glide.with(this).load(R.drawable.baseline_pause_black_18dp).apply(RequestOptions.overrideOf(150,150)).into(record_start);
*/

        record_timer = (TextView)view.findViewById(R.id.record_timer);

/*
        record_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                requestPermissions(new String[]{Manifest.permission.RECORD_AUDIO, Manifest.permission.WRITE_EXTERNAL_STORAGE},0);
                mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
                mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
                mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
                mediaRecorder.setOutputFile(outputFile);
            }
        });*/


        long now = System.currentTimeMillis();

        Date date = new Date(now);

        final SimpleDateFormat record_number = new SimpleDateFormat("YYYY-MM-dd : HH-mm");

        String record_subject_time = record_number.format(date);

        record_save_name = record_subject_time +".wav" ;

        outputFile =Environment.getExternalStorageDirectory().getAbsolutePath()+"/AnywhereSing"+"/"+record_save_name;

        record_subject = (TextView)view.findViewById(R.id.record_subject);
        record_subject.setText(record_save_name);



        requestPermissions(new String[]{Manifest.permission.RECORD_AUDIO, Manifest.permission.WRITE_EXTERNAL_STORAGE},0);


        mediaRecorder = new MediaRecorder();
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
        mediaRecorder.setOutputFile(outputFile);



        final File recoder_path = new File(Environment.getExternalStorageDirectory().getAbsolutePath()+"/AnywhereSing");

        if (!recoder_path.exists())
        {
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            builder.setMessage("녹음파일 저장공간을 만들어주세요");
            builder.setNegativeButton("okay", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i)
                {
                    recoder_path.mkdir();
                    dialogInterface.cancel();
                }
            });
            builder.setNeutralButton("닫기", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which)
                {
                    Toast savepath_check = Toast.makeText(getActivity(), "녹음기능이 되지않습니다.", Toast.LENGTH_SHORT);
                    savepath_check.show();
                }
            });
            builder.create().show();
        }


        record_start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                    //countDownTimer.start();
                    record_start.setEnabled(false);
                    //record_play.setEnabled(false);
                    record_stop.setEnabled(true);
                    record_save.setEnabled(false);
                    record_subject.setEnabled(false);

                isRecording = true;
                Log.d("file","123");
                try
                {
                    Log.d("start_recoder","is_start");
                    mediaRecorder.prepare();
                    mediaRecorder.start();
                    MrTimer.start();
                }
                catch (IllegalStateException ise)
                {

                    Log.d("start_recoder",ise.getMessage());
                }
                catch (IOException ioe)
                {
                    Log.d("start_recoder",ioe.getMessage());
                }
                }
        });
        record_stop.setOnClickListener( new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View view)
            {

                record_start.setEnabled(true);
                record_stop.setEnabled(false);
                record_save.setEnabled(true);
                record_subject.setEnabled(true);
                mediaRecorder.pause();
                MrTimer.cancel();

            }
        });
        /*record_play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               record_start.setEnabled(false);
                record_play.setEnabled(false);
                record_stop.setEnabled(true);
               // record_save.setEnabled(false);

                mediaPlayer = new MediaPlayer();
                try
                {
                    mediaPlayer.setDataSource(outputFile);
                    mediaPlayer.prepare();
                    mediaPlayer.start();
                }
                catch (Exception e)
                {

                }

            }
        });*/

       /* webMR = (WebView)view.findViewById(R.id.webmr);

        WebSettings websetting = webMR.getSettings();
        websetting.setJavaScriptEnabled(true);
        webMR.setWebViewClient(new WebViewClient()
        {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view,String request) {
                view.loadUrl(request);
                return true;
            }
        });
        webMR.loadUrl("https://www.youtube.com/");


*/


        //미리 들어보기,닫기(진행중녹음파일날림,reset)
        Button chattest = (Button)view.findViewById(R.id.chattest);

        chattest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(),broad.class);
                startActivity(intent);
            }
        });

record_save.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View view)
    {
        record_save.setEnabled(false);
        record_start.setEnabled(true);
        record_stop.setEnabled(false);
        record_timer.setText(null);
        mediaRecorder.stop();
        mediaRecorder.release();
        mediaRecorder = null;
        isRecording = false;
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setMessage("저장완료");
        builder.setNegativeButton("닫기", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
            }
        });
        builder.create().show();
        //mediaRecorder.setOutputFile(outputFile);
        //mediaRecorder.reset();

    }
});

/*
mr 재생,중단과 녹음 중단,재생은 따로해야 한다-(노래방,사용자가 가지고있는다른반주 등)

저장을 하게되면 내저장소에서만 저장이되고(플레이버튼 삭제)
서버에 글을 올리진 않는다 - 글쓰기를 통하여 녹음파일을 따로 올릴수 있게한다

내가 녹음한 list는 서버에 올라간 녹음파일을 보여준다

*/
       // handleSeekbar();


        play_time = (TextView)view.findViewById(R.id.play_time);
        mrplay_time = (TextView)view.findViewById(R.id.mrplay_time);
        Mr_bar = (SeekBar)view.findViewById(R.id.Mr_bar);



        handler = new Handler();


        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {


                    mrplay_time.setText(Utility.convertDuration(MRplayer.getDuration()));
                    play_time.setText(Utility.convertDuration(MRplayer.getCurrentPosition()));

                    Mr_bar.setMax(MRplayer.getDuration());
                    Mr_bar.setProgress(MRplayer.getCurrentPosition());

                    handler.postDelayed(this,100);

            }
        });
        Mr_bar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int position, boolean fromuser) {
                if (MRplayer != null && fromuser)
                {
                    MRplayer.seekTo(position );
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        return view;
}



    private Runnable moveSeekbar = new Runnable() {
        @Override
        public void run() {


        }
    };
    private void handleSeekbar()
    {
        Mr_bar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                if (MRplayer != null && b)
                {
                    MRplayer.seekTo(i);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

    }

   /* public void duration()
    {


            String url = "http://ec2-18-220-208-130.us-east-2.compute.amazonaws.com/Mrfile/어디에도.wav";
            MediaPlayer  MRplayer = new MediaPlayer();
            try {
                MRplayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                MRplayer.setDataSource(url);
                MRplayer.prepare();
                play0 = MRplayer.getDuration();
            } catch (IOException e) {
                e.printStackTrace();
            }



            String url1 = "http://ec2-18-220-208-130.us-east-2.compute.amazonaws.com/Mrfile/입술의말.wav";
            MediaPlayer  MRplayer1 = new MediaPlayer();
            try {
                MRplayer1.setAudioStreamType(AudioManager.STREAM_MUSIC);
                MRplayer1.setDataSource(url1);
                MRplayer1.prepare();
                play1 = MRplayer1.getDuration();
            } catch (IOException e) {
                e.printStackTrace();
            }



            String url2 = "http://ec2-18-220-208-130.us-east-2.compute.amazonaws.com/Mrfile/사랑했지만.wav";
            MediaPlayer  MRplayer2 = new MediaPlayer();
            try {
                MRplayer2.setAudioStreamType(AudioManager.STREAM_MUSIC);
                MRplayer2.setDataSource(url2);
                MRplayer2.prepare();
                play2 = MRplayer2.getDuration();
            } catch (IOException e) {
                e.printStackTrace();
            }



            String url3 = "http://ec2-18-220-208-130.us-east-2.compute.amazonaws.com/Mrfile/모든날 모든순간.wav";
            MediaPlayer  MRplayer3 = new MediaPlayer();
            try {
                MRplayer3.setAudioStreamType(AudioManager.STREAM_MUSIC);
                MRplayer3.setDataSource(url3);
                MRplayer3.prepare();
                play3 = MRplayer3.getDuration();
            } catch (IOException e) {
                e.printStackTrace();
            }



            String url4 = "http://ec2-18-220-208-130.us-east-2.compute.amazonaws.com/Mrfile/마지막으로 안아도 될까.wav";
            MediaPlayer  MRplayer4 = new MediaPlayer();
            try {
                MRplayer4.setAudioStreamType(AudioManager.STREAM_MUSIC);
                MRplayer4.setDataSource(url4);
                MRplayer4.prepare();
                play4 = MRplayer4.getDuration();
            } catch (IOException e) {
                e.printStackTrace();
            }



            String url5 = "http://ec2-18-220-208-130.us-east-2.compute.amazonaws.com/Mrfile/감사.wav";
            MediaPlayer  MRplayer5 = new MediaPlayer();
            try {
                MRplayer5.setAudioStreamType(AudioManager.STREAM_MUSIC);
                MRplayer5.setDataSource(url5);
                MRplayer5.prepare();
                play5 = MRplayer5.getDuration();
            } catch (IOException e) {
                e.printStackTrace();
            }

            String url6 = "http://ec2-18-220-208-130.us-east-2.compute.amazonaws.com/Mrfile/My way.wav";
            MediaPlayer  MRplayer6 = new MediaPlayer();
            try {
                MRplayer6.setAudioStreamType(AudioManager.STREAM_MUSIC);
                MRplayer6.setDataSource(url6);
                MRplayer6.prepare();
                play6 = MRplayer6.getDuration();
            } catch (IOException e) {
                e.printStackTrace();
            }

            String url7 = "http://ec2-18-220-208-130.us-east-2.compute.amazonaws.com/Mrfile/Loveballad.wav";
            MediaPlayer  MRplayer7 = new MediaPlayer();
            try {
                MRplayer7.setAudioStreamType(AudioManager.STREAM_MUSIC);
                MRplayer7.setDataSource(url7);
                MRplayer7.prepare();
                play7 = MRplayer7.getDuration();
            } catch (IOException e) {
                e.printStackTrace();
            }




    }*/






class MrAdapter extends RecyclerView.Adapter<MrAdapter.RecycleViewHolder>
{

    ArrayList<mrItem> mrItems;
    Context context;


    //private SeekBarUpdater seekBarUpdater;
    private RecycleViewHolder playingHolder;
    private int lastPosition ;


    public MrAdapter(ArrayList<mrItem> mrItem)
    {
        this.mrItems = mrItem;
        this.lastPosition = -1;
        //seekBarUpdater = new SeekBarUpdater();
    }



    @NonNull
    @Override
    public RecycleViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.mrlist_item,parent,false);
        context = parent.getContext();
        RecycleViewHolder holder = new RecycleViewHolder(view);
        return holder;
    }




    @Override
    public void onBindViewHolder(@NonNull final RecycleViewHolder holder, final int position)
    {


        Glide.with(anywheresing_single_voice.this).load(mrItems.get(position).getCoverImage()).apply(RequestOptions.overrideOf(200,200).circleCrop()).into(holder.MRlist_image);
        holder.MRlist_title.setText(mrItems.get(position).getMr_title());
        holder.Mrlist_artist.setText(mrItems.get(position).getArtist());
        //holder.play_time.setText(mrItems.get(position).getPlay_time());
        holder.Mrlist_stopbtn.setEnabled(false);



      final int itemposition = holder.getLayoutPosition();

        holder.MRlayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                if (itemposition == 0)
                {

                    final AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

                    builder.setTitle("가사");
                    builder.setNegativeButton("닫기", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.cancel();
                        }
                    });
                    builder.setMessage(" " +
                            "[Verse 1]" +
                            "차가워진 눈빛을 바라보며\n" +
                            "이별의 말을 전해 들어요\n" +
                            "아무 의미 없던 노래 가사가\n" +
                            "아프게 귓가에 맴돌아요\n" +
                            "다시 겨울이 시작되듯이\n" +
                            "흩어지는 눈 사이로\n" +
                            "그대 내 맘에 쌓여만 가네\n" +
                            "\n" +
                            "[Pre-Chorus]\n" +
                            "떠나지 말라는\n" +
                            "그런 말도 하지 못하고\n" +
                            "고개를 떨구던 뒷모습만\n" +
                            "\n" +
                            "[Chorus]\n" +
                            "그대 내게 오지 말아요\n" +
                            "두 번 다시 이런 사랑하지 마요\n" +
                            "그댈 추억하기보단 기다리는 게\n" +
                            "부서진 내 맘이 더 아파 와\n" +
                            "다시 누군가를 만나서\n" +
                            "결국 우리 사랑 지워내도\n" +
                            "행복했던 것만 기억에 남아\n" +
                            "나를 천천히 잊어주기를\n" +
                            "\n" +
                            "[Verse 3]\n" +
                            "아무것도 마음대로 안 돼요\n" +
                            "아픔은 그저 나를 따라와\n" +
                            "밤새도록 커져 버린 그리움\n" +
                            "언제쯤 익숙해져 가나요\n" +
                            "많은 날들이 떠오르네요\n" +
                            "우리가 나눴던 날들\n" +
                            "애써 감추고 돌아서네요[Pre-Chorus]\n" +
                            "떠나지 말라는\n" +
                            "그런 말도 하지 못하고\n" +
                            "고개를 떨구던 뒷모습만\n" +
                            "\n" +
                            "[Chorus]\n" +
                            "그대 내게 오지 말아요\n" +
                            "두 번 다시 이런 사랑하지 마요\n" +
                            "그댈 추억하기보단 기다리는 게\n" +
                            "부서진 내 맘이 더 아파 와\n" +
                            "다시 누군가를 만나서\n" +
                            "결국 우리 사랑 지워내도\n" +
                            "행복했던 것만 기억에 남아\n" +
                            "나를 천천히 잊어주기를\n" +
                            "\n" +
                            "[Bridge]\n" +
                            "부를 수도 없이 멀어진 그대가\n" +
                            "\n" +
                            "[Outro]\n" +
                            "지나치는 바람에도 목이 메어와\n" +
                            "어디에도 그대가 살아서\n" +
                            "우린 사랑하면 안 돼요\n" +
                            "다가갈수록 미워지니까\n" +
                            "행복했던 것만 기억에 남아\n" +
                            "나를 천천히 잊어가기를");
                    builder.create();
                    builder.show();
                }

                if (itemposition == 1)
                {
                    final AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

                    builder.setTitle("가사");
                    builder.setNegativeButton("닫기", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.cancel();
                        }
                    });
                    builder.setMessage(" 아무 일 없듯이 굳게 잠긴 말들\n" +
                            "돌아서는 뒷모습에 들리지 않는 인사를\n" +
                            "\n" +
                            "시간이 지나가도 덜어내진 못할\n" +
                            "네 입술의 못된 말이 전부 다 너의 맘일까\n" +
                            "\n" +
                            "안녕 그대로 걸어가 우리 이제 다시 만나지 말아\n" +
                            "잊혀짐도 잊을 만큼 나를 지워가 돌아선 그대로\n" +
                            "\n" +
                            "남아 있는 나의 맘은 하루하루 모두 흩어짐으로\n" +
                            "결국에는 사랑만을 내게 말했던 네 고운 입술만\n" +
                            "\n" +
                            "시간이 지나가도 덜어내진 못할\n" +
                            "네 입술의 못된 말이 전부 다 너의 맘일까\n" +
                            "\n" +
                            "살아가는 동안에 이런 사랑 다시 온대도\n" +
                            "그 처음이 너였음을 잊지 않을게\n" +
                            "이루어질 수 없었던 우리 둘의 얘기는 여기까지인 거야\n" +
                            "\n" +
                            "안녕 그대로 걸어가 우리 이제 다시 만나지 말아\n" +
                            "잊혀짐도 잊을 만큼 나를 지워가 돌아선 그대로\n" +
                            "\n" +
                            "남아 있는 나의 맘은 하루하루 모두 흩어짐으로\n" +
                            "결국에는 사랑만을 내게 말했던 네 고운 입술만\n" +
                            "네 고운 입술 ");
                    builder.create();
                    builder.show();
                }
                if (itemposition == 2)
                {
                    final AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

                    builder.setTitle("가사");
                    builder.setNegativeButton("닫기", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.cancel();
                        }
                    });
                    builder.setMessage("어제는 하루 종일 \n" +
                            "비가 내렸어\n" +
                            "\n" +
                            "자욱하게 내려앉은 \n" +
                            "먼지 사이로 \n" +
                            "\n" +
                            "귓가에 은은하게 \n" +
                            "울려 퍼지는 \n" +
                            "\n" +
                            "그대 음성 빗속으로 \n" +
                            "사라져버려\n" +
                            "\n" +
                            "때론 눈물도 흐르겠지 \n" +
                            "그리움으로 \n" +
                            "\n" +
                            "때론 가슴도 저리겠지 \n" +
                            "외로움으로 \n" +
                            "\n" +
                            "사랑했지만 \n" +
                            "그대를 사랑했지만 \n" +
                            "\n" +
                            "그저 이렇게 멀리서 바라볼 뿐 \n" +
                            "다가설 수 없어\n" +
                            "\n" +
                            "지친 그대 곁에 머물고 싶지만 \n" +
                            "떠날 수밖에 \n" +
                            "\n" +
                            "그대를 \n" +
                            "사랑했지만 \n" +
                            "\n" +
                            "때론 눈물도 흐르겠지 \n" +
                            "그리움으로\n" +
                            "\n" +
                            "때론 가슴도 저리겠지 \n" +
                            "외로움으로 \n" +
                            "사랑했지만 \n" +
                            "그대를 사랑했지만 \n" +
                            "\n" +
                            "그저 이렇게 멀리서 바라볼 뿐 \n" +
                            "다가설 수 없어 \n" +
                            "\n" +
                            "지친 그대 곁에 \n" +
                            "머물고 싶지만 떠날 수밖에 \n" +
                            "\n" +
                            "그대를 사랑했지만 \n" +
                            "사랑했지만 \n" +
                            "그대를 사랑했지만 \n" +
                            "\n" +
                            "그저 이렇게 멀리서 바라볼 뿐 \n" +
                            "다가설 수 없어 \n" +
                            "\n" +
                            "그대를 사랑했지만");
                    builder.create();
                    builder.show();
                }
                if (itemposition == 3)
                {
                    final AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

                    builder.setTitle("가사");
                    builder.setNegativeButton("닫기", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.cancel();
                        }
                    });
                    builder.setMessage(" 네가 없이 웃을 수 있을까\n" +
                            "생각만 해도 눈물이나\n" +
                            "힘든 시간 날 지켜준 사람\n" +
                            "이제는 내가 그댈 지킬 테니\n" +
                            "\n" +
                            "너의 품은 항상 따뜻했어\n" +
                            "고단했던 나의 하루에 유일한 휴식처 \n" +
                            "\n" +
                            "나는 너 하나로 충분해\n" +
                            "긴 말 안 해도 눈빛으로 다 아니깐\n" +
                            "한 송이의 꽃이 피고 지는\n" +
                            "모든 날, 모든 순간 함께해\n" +
                            "\n" +
                            "햇살처럼 빛나고 있었지\n" +
                            "나를 보는 네 눈빛은\n" +
                            "꿈이라고 해도 좋을 만큼\n" +
                            "그 모든 순간은 눈부셨다\n" +
                            "\n" +
                            "불안했던 나의 고된 삶에\n" +
                            "한줄기 빛처럼 다가와 날 웃게 해준 너 \n" +
                            "\n" +
                            "나는 너 하나로 충분해\n" +
                            "긴 말 안 해도 눈빛으로 다 아니깐\n" +
                            "한 송이의 꽃이 피고 지는\n" +
                            "모든 날, 모든 순간 함께해\n" +
                            "\n" +
                            "알 수 없는 미래지만\n" +
                            "네 품속에 있는 지금 순간 순간이\n" +
                            "영원 했으면 해 \n" +
                            "\n" +
                            "갈게 바람이 좋은 날에\n" +
                            "햇살 눈부신 어떤 날에 너에게로\n" +
                            "처음 내게 왔던 그날처럼\n" +
                            "모든 날, 모든 순간 함께해");
                    builder.create();
                    builder.show();
                }
                if (itemposition == 4)
                {
                    final AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

                    builder.setTitle("가사");
                    builder.setNegativeButton("닫기", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.cancel();
                        }
                    });
                    builder.setMessage("마지막으로 안아도 될까\n" +
                            "마지막으로 입 맞춰도 될까\n" +
                            "마지막 한 번만 처음처럼\n" +
                            "우리 웃어볼까\n" +
                            "\n" +
                            "설레는 맘에 밤을 지새고\n" +
                            "두근대는 맘 요동을 치던\n" +
                            "어제 같은 우리 그때처럼\n" +
                            "\n" +
                            "마지막으로 안아도 될까\n" +
                            "마지막 네 온기 품어도 될까\n" +
                            "흩어지는 슬픈 향기 기억에 배도록\n" +
                            "꼭 끌어안은 젖은 그림자\n" +
                            "이 밤을 뒤로 안녕 잠시만 안녕\n" +
                            "돌아서기에는 더딘 발걸음\n" +
                            "\n" +
                            "짓궂은 시간 우릴 보채고\n" +
                            "약해진 마음 여미어 봐도\n" +
                            "그 말만은 쉬이 떼지질 않아\n" +
                            "\n" +
                            "마지막으로 안아도 될까\n" +
                            "마지막 네 온기 품어도 될까\n" +
                            "흩어지는 슬픈 향기 기억에 배도록\n" +
                            "꼭 끌어안은 젖은 그림자\n" +
                            "이 밤을 뒤로 안녕 잠시만 안녕\n" +
                            "돌아서기에는 더딘 발걸음\n" +
                            "\n" +
                            "우리 힘든 기약 대신\n" +
                            "말없이 믿어보기로 해\n" +
                            "반드시 한 뼘 더 자란 모습으로\n" +
                            "네 앞에 설 테니까\n" +
                            "\n" +
                            "그토록 봐왔던 얼굴\n" +
                            "오늘은 유난히 더 예쁘구나\n" +
                            "나보다 더 씩씩하게 웃어줘서 고마워\n" +
                            "두 점이 된 우리 그림자\n" +
                            "이 밤을 뒤로 안녕 잠시만 안녕\n" +
                            "다른 길로 향한 서툰 발걸음\n" +
                            "애써 재촉하는 낯선 발걸음");
                    builder.create();
                    builder.show();
                }
                if (itemposition == 5)
                {
                    final AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

                    builder.setTitle("가사");
                    builder.setNegativeButton("닫기", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i)
                        {
                            dialogInterface.cancel();
                        }
                    });
                    builder.setMessage("눈부신 햇살이 오늘도 나를 감싸면\n" +
                            "살아있음을 그대에게 난 감사해요\n" +
                            "부족한 내 마음이 누구에게 힘이 될 줄은\n" +
                            "그것만으로 그대에게 난 감사해요\n" +
                            "\n" +
                            "그 누구에게도 내 사람이란 게\n" +
                            "부끄럽지 않게 날 사랑할게요\n" +
                            "단 한순간에도 나의 사람이란 걸\n" +
                            "후회하지 않도록 그댈 사랑할게요\n" +
                            "\n" +
                            "이제야 나 태어난 그 이유를 알 것만 같아요\n" +
                            "그대를 만나 죽도록 사랑하는 게\n" +
                            "누군가 주신 나의 행복이죠\n" +
                            "\n" +
                            "그 어디에서도 나의 사람인걸\n" +
                            "잊을 수 없도록 늘 함께 할게요\n" +
                            "단 한순간에도 나의 사랑이란 걸\n" +
                            "아파하지 않도록 그댈 사랑할게요\n" +
                            "\n" +
                            "이제야 나 태어난 그 이유를 알 것만 같아요\n" +
                            "그대를 만나 죽도록 사랑하는 게\n" +
                            "누군가 주신 내 삶의 이유라면\n" +
                            "\n" +
                            "더 이상 나에겐 그 무엇도 바랄게 없어요\n" +
                            "지금처럼만 서로를 사랑하는 게\n" +
                            "누군가 주신 나의 행복이죠");
                    builder.create();
                    builder.show();
                }
                if (itemposition == 6)
                {
                    final AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

                    builder.setTitle("가사");
                    builder.setNegativeButton("닫기", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.cancel();
                        }
                    });
                    builder.setMessage("흐르는 물결이\n" +
                            "내 발목에 감겨 나가면\n" +
                            "깊은 곳에 숨겨둔 아픈 기억\n" +
                            "모두 살아나고\n" +
                            "\n" +
                            "어디에 기대 살아갈까\n" +
                            "나를 스쳐가는\n" +
                            "그 모든 것들이 상처인데\n" +
                            "\n" +
                            "그댈 그린 밤들이\n" +
                            "내게 욕심이란 걸\n" +
                            "맘 아프게 알아\n" +
                            "나를 택한 운명이\n" +
                            "행여 그댈 맴돌아\n" +
                            "붙잡지 못하게\n" +
                            "이제 그대 곁에서 떠나가\n" +
                            "\n" +
                            "내뱉는 숨마다\n" +
                            "가시 돋친 고통이어서\n" +
                            "깊은 곳에 숨겨둔 사랑 또한\n" +
                            "그저 지워 내고\n" +
                            "\n" +
                            "어디에 기대 살아갈까\n" +
                            "나를 스쳐가는\n" +
                            "그 모든 것들이 상처인데\n" +
                            "\n" +
                            "그댈 그린 밤들이\n" +
                            "내게 사치라는 걸\n" +
                            "맘 아프게 알아\n" +
                            "나를 택한 운명이\n" +
                            "행여 그댈 맴돌아\n" +
                            "붙잡지 못하게\n" +
                            "이제 그대 곁에서 떠나가\n" +
                            "\n" +
                            "언젠가 그대 곁에\n" +
                            "다시 돌아갈 수 있다면\n" +
                            "그때가 언제라도\n" +
                            "나를 잊지 않았다면\n" +
                            "\n" +
                            "그댈 그린 날들이\n" +
                            "내게 마지막 남은\n" +
                            "기쁨이었단 걸\n" +
                            "내가 택한 운명이\n" +
                            "다른 무엇이 아닌\n" +
                            "그대뿐이라는 걸\n" +
                            "\n" +
                            "이제 그대 곁에서 영원히");
                    builder.create();
                    builder.show();
                }
                if (itemposition == 7)
                {
                    final AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

                    builder.setTitle("가사");
                    builder.setNegativeButton("닫기", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.cancel();
                        }
                    });
                    builder.setMessage("늘 내 곁에만 그렇게 있어줘요\n" +
                            "처음 같지 않아도 괜찮아요\n" +
                            "그보다 더 그대를 아낄게요\n" +
                            "눈물도 없을 만큼 상처도 지울 만큼\n" +
                            "셀 수도 없을 만큼 사랑할게요\n" +
                            "\n" +
                            "난 숨길 수 없죠\n" +
                            "그대 눈 속에 난 많이도 웃네요\n" +
                            "내 맘을 말해줄게요\n" +
                            "떨리는 이 순간 빛나는 오늘밤\n" +
                            "\n" +
                            "많이 기다려온 그 말을 참아왔죠\n" +
                            "그대에게 모자란 나라서\n" +
                            "\n" +
                            "늘 내 곁에만 그렇게 있어줘요\n" +
                            "처음 같지 않아도 괜찮아요\n" +
                            "그보다 더 그대를 아낄게요\n" +
                            "눈물도 없을 만큼 상처도 지울 만큼\n" +
                            "셀 수도 없을 만큼 사랑할게요\n" +
                            "\n" +
                            "내 마음 언제부턴지\n" +
                            "알지도 못해요 누가 알겠어요\n" +
                            "자꾸만 또 나도 모르게\n" +
                            "그대로 내 맘은 물들었나 봐요\n" +
                            "\n" +
                            "외롭던 어느 날에 그대의 목소리는\n" +
                            "나를 불러 봄을 선물했죠\n" +
                            "\n" +
                            "늘 내 곁에만 그렇게 있어줘요\n" +
                            "처음 같지 않아도 괜찮아요\n" +
                            "그보다 더 그대를 아낄게요\n" +
                            "눈물도 없을 만큼 상처도 지울 만큼\n" +
                            "셀 수도 없을 만큼 사랑할게요\n" +
                            "\n" +
                            "가끔은 지쳐가겠죠 (OH~ I DO~)\n" +
                            "때로는 무뎌지겠죠\n" +
                            "오늘 이 밤 우리 tonight\n" +
                            "기억해요 It's all right\n" +
                            "그날의 소중한 우리들을\n" +
                            "\n" +
                            "(내게로 더 가까이 Tonight Love you tonight) \n" +
                            "(내게로 더 가까이 Tonight Love you tonight)");
                    builder.create();
                    builder.show();
                }

                if (itemposition == 8)
                {
                    final AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

                    builder.setTitle("가사");
                    builder.setNegativeButton("닫기", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.cancel();
                        }
                    });
                    builder.setMessage("그대 예쁜 목소리로 자장가를 불러줘요\n" +
                            "오늘 밤도 그대\n" +
                            "내 꿈 속에 나와 함께 살아요\n" +
                            "아이같은 그 미소로\n" +
                            "나를 보며 웃어주네요\n" +
                            "나는 그대라서 참 행복합니다\n" +
                            "내가 힘들때나 많이 아플때\n" +
                            "내 눈물을 고이 닦아주던\n" +
                            "그대란 사람 어찌 말을 해야 할까요\n" +
                            "첫번째 그대의 마음\n" +
                            "나무처럼 나를 쉬게해\n" +
                            "두번째 그대의 미소\n" +
                            "햇살처럼 나를 밝게 비추고\n" +
                            "세번째 그대의 손길\n" +
                            "내가 힘들때마다\n" +
                            "품에 안아준 그댈 사랑합니다\n" +
                            "내가 슬플 때나 외로워 할 때\n" +
                            "내 곁을 따스히 지켜주던\n" +
                            "그대란 사람 어찌 말을 해야 할까요\n" +
                            "네번째 그대의 온도\n" +
                            "가슴을 따스하게 해\n" +
                            "다섯째 그대의 눈물\n" +
                            "더 열심히 나를 살아가게 해\n" +
                            "여섯째 그대의 기도\n" +
                            "쳐진 어깨를 피게 만들어 주는\n" +
                            "그댈 사랑합니다\n" +
                            "그대여 더 이상 눈물은\n" +
                            "이젠 흘리지 말아요\n" +
                            "영원토록 그대를 지킬게요\n" +
                            "사랑하는 그댈 보면\n" +
                            "바라만 봐도 난 행복해\n" +
                            "일곱번째 그대 표정\n" +
                            "시무룩한 나를 웃게해\n" +
                            "여덟째 그대 목소리\n" +
                            "내게 힘을 주는 그대이니까\n" +
                            "아홉번째 그대 걸음\n" +
                            "못난 날 매일마다 찾아와 주네\n" +
                            "열번짼 그대란 선물\n" +
                            "그대라서 난 정말 행복합니다\n");
                    builder.create();
                    builder.show();
                }





            }
        });

        holder.Mrlist_startbtn.setText("start");

        //holder.Mr_bar.setVisibility(View.INVISIBLE);

        holder.Mrlist_startbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {





                if (MrplayingStats = false)
                {
                    MRplayer.seekTo(MRplayer.getCurrentPosition());
                    try {
                        MRplayer.prepare();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    MRplayer.start();
                }
                else
                {
                    Toast.makeText(getContext(),"잠시만 기다려주세요...",Toast.LENGTH_LONG).show();

                    MrStart(itemposition);
                }






                holder.Mrlist_startbtn.setEnabled(false);
                holder.Mrlist_stopbtn.setEnabled(true);

                //Mr_bar.setProgress(0);
                //holder.Mr_bar.setVisibility(View.VISIBLE);
                //holder.MRplay_time.setText(Utility.convertDuration(playMR(itemposition).getDuration()));
                //holder.play_time.setText(Utility.convertDuration(playMR(itemposition).getCurrentPosition()));



                //holder.Mr_bar.setMax(playMR(itemposition).getDuration());
                //updatePlayingView(holder,itemposition);

            }
        });









        //일시중단 버튼 생성해라,가사

        /*
         멈춤없이 일시중단만 할수있게 해놓아도
         나중에 bar생성해서 MR 맨처음으로 갈수있게
         음악 스트리밍을 생각해보자
        */
        holder.Mrlist_stopbtn.setText("pause");
        holder.Mrlist_stopbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                holder.Mrlist_startbtn.setEnabled(true);
                holder.Mrlist_stopbtn.setEnabled(false);
                MrplayingStats = false;
                MRplayer.pause();

            }
        });



    }
    /*
    private class  SeekBarUpdater implements Runnable
    {

        @Override
        public void run() {
           while (MRplayer.isPlaying())
            {

                playingHolder.Mr_bar.setProgress(MRplayer.getCurrentPosition());
                playingHolder.Mr_bar.postDelayed(this,100);
            }
        }
    }

*/

    /*
    @Override
   public void onViewRecycled(RecycleViewHolder holder)
    {
        super.onViewRecycled(holder);
        if (lastPosition == holder.getAdapterPosition())
        {
            updateNonPlayingView(playingHolder);
            playingHolder = null;
        }
    }
    private void updateNonPlayingView(RecycleViewHolder holder)
    {
        holder.Mr_bar.removeCallbacks(seekBarUpdater);
        holder.Mr_bar.setEnabled(false);
        holder.Mr_bar.setProgress(0);
    }
    private void updatePlayingView(RecycleViewHolder holder,int itemposition)
    {
        holder.Mr_bar.setMax(playMR(itemposition).getDuration());
        holder.Mr_bar.setProgress(playMR(itemposition).getCurrentPosition());
        holder.Mr_bar.setEnabled(true);
        if (MRplayer.isPlaying())
        {
            holder.Mr_bar.postDelayed(seekBarUpdater,100);
        }
        else
        {
            holder.Mr_bar.removeCallbacks(seekBarUpdater);
        }

    }*/


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
        Button Mrlist_startbtn;
        Button Mrlist_stopbtn;
        LinearLayout MRlayout;



        //SeekBar Mr_bar;

        //SeekBar Mr_bar;


        public RecycleViewHolder(View itemView)
        {
            super(itemView);
            MRlist_image = (ImageView)itemView.findViewById(R.id.MRlist_image);
            MRlist_title = (TextView)itemView.findViewById(R.id.MRlist_title);
            Mrlist_artist = (TextView)itemView.findViewById(R.id.MRlist_artist);
            Mrlist_startbtn = (Button)itemView.findViewById(R.id.MRlist_startbtn);
            Mrlist_stopbtn = (Button)itemView.findViewById(R.id.MRlist_stopbtn);
            MRlayout = (LinearLayout)itemView.findViewById(R.id.MrLayout);

            // play_time = (TextView)itemView.findViewById(R.id.play_time);
            //MRplay_time = (TextView)itemView.findViewById(R.id.MRplay_time);
            //Mr_bar = (SeekBar)itemView.findViewById(R.id.Mr_bar);
    }



    }


}



/*


*/




    public MediaPlayer playMR(int position)
    {

        //MediaPlayer MRplayer = null;
        if (position == 0)
        {
            String url = "http://ec2-52-15-174-85.us-east-2.compute.amazonaws.com/Mrfile/어디에도.wav";
             MRplayer = new MediaPlayer();
            try {
                MRplayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                MRplayer.setDataSource(url);
                MRplayer.prepare();

            } catch (IOException e) {
                e.printStackTrace();
            }

        }
        if (position == 1)
        {
            String url = "http://ec2-52-15-174-85.us-east-2.compute.amazonaws.com/Mrfile/입술의말.wav";
             MRplayer = new MediaPlayer();
            try {
                MRplayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                MRplayer.setDataSource(url);
                MRplayer.prepare();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (position == 2)
        {
            String url = "http://ec2-52-15-174-85.us-east-2.compute.amazonaws.com/Mrfile/사랑했지만.wav";
            MRplayer = new MediaPlayer();
            try {
                MRplayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                MRplayer.setDataSource(url);
                MRplayer.prepare();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (position == 3)
        {
            String url = "http://ec2-52-15-174-85.us-east-2.compute.amazonaws.com/Mrfile/모든날 모든순간.wav";
            MRplayer = new MediaPlayer();
            try {
                MRplayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                MRplayer.setDataSource(url);
                MRplayer.prepare();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (position == 4)
        {
            String url = "http://ec2-52-15-174-85.us-east-2.compute.amazonaws.com/Mrfile/마지막으로 안아도 될까.wav";
            MRplayer = new MediaPlayer();
            try {
                MRplayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                MRplayer.setDataSource(url);
                MRplayer.prepare();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (position == 5)
        {
            String url = "http://ec2-52-15-174-85.us-east-2.compute.amazonaws.com/Mrfile/감사.wav";
             MRplayer = new MediaPlayer();
            try {
                MRplayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                MRplayer.setDataSource(url);
                MRplayer.prepare();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (position == 6)
        {
            String url = "http://ec2-52-15-174-85.us-east-2.compute.amazonaws.com/Mrfile/My way.wav";
          MRplayer = new MediaPlayer();
            try {
                MRplayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                MRplayer.setDataSource(url);
                MRplayer.prepare();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (position == 7)
        {
            String url = "http://ec2-52-15-174-85.us-east-2.compute.amazonaws.com/Mrfile/Loveballad.wav";
              MRplayer = new MediaPlayer();
            try {
                MRplayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                MRplayer.setDataSource(url);
                MRplayer.prepare();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (position == 8)
        {
            String url = "http://ec2-52-15-174-85.us-east-2.compute.amazonaws.com/Mrfile/그대를 사랑하는 10가지 이유.wav";
            MRplayer = new MediaPlayer();
            try {
                MRplayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                MRplayer.setDataSource(url);
                MRplayer.prepare();

            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return MRplayer;
    }







/*


    public int currentposition(int position)
    {


        if (position == 0)
        {
            String url = "http://ec2-18-220-208-130.us-east-2.compute.amazonaws.com/Mrfile/어디에도.wav";
            final MediaPlayer  MRplayer = new MediaPlayer();
            try {
                MRplayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                MRplayer.setDataSource(url);
                MRplayer.prepare();
                play = MRplayer.getCurrentPosition();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
        if (position == 1)
        {
            String url = "http://ec2-18-220-208-130.us-east-2.compute.amazonaws.com/Mrfile/입술의말.wav";
            MediaPlayer  MRplayer = new MediaPlayer();
            try {
                MRplayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                MRplayer.setDataSource(url);
                MRplayer.prepare();
                play = MRplayer.getCurrentPosition();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (position == 2)
        {
            String url = "http://ec2-18-220-208-130.us-east-2.compute.amazonaws.com/Mrfile/사랑했지만.wav";
            MediaPlayer  MRplayer = new MediaPlayer();
            try {
                MRplayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                MRplayer.setDataSource(url);
                MRplayer.prepare();
                play = MRplayer.getCurrentPosition();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (position == 3)
        {
            String url = "http://ec2-18-220-208-130.us-east-2.compute.amazonaws.com/Mrfile/모든날 모든순간.wav";
            MediaPlayer  MRplayer = new MediaPlayer();
            try {
                MRplayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                MRplayer.setDataSource(url);
                MRplayer.prepare();
                play = MRplayer.getCurrentPosition();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (position == 4)
        {
            String url = "http://ec2-18-220-208-130.us-east-2.compute.amazonaws.com/Mrfile/마지막으로 안아도 될까.wav";
            MediaPlayer  MRplayer = new MediaPlayer();
            try {
                MRplayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                MRplayer.setDataSource(url);
                MRplayer.prepare();
                play = MRplayer.getCurrentPosition();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (position == 5)
        {
            String url = "http://ec2-18-220-208-130.us-east-2.compute.amazonaws.com/Mrfile/감사.wav";
            MediaPlayer  MRplayer = new MediaPlayer();
            try {
                MRplayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                MRplayer.setDataSource(url);
                MRplayer.prepare();
                play = MRplayer.getCurrentPosition();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (position == 6)
        {
            String url = "http://ec2-18-220-208-130.us-east-2.compute.amazonaws.com/Mrfile/My way.wav";
            MediaPlayer  MRplayer = new MediaPlayer();
            try {
                MRplayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                MRplayer.setDataSource(url);
                MRplayer.prepare();
                play = MRplayer.getCurrentPosition();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (position == 7)
        {
            String url = "http://ec2-18-220-208-130.us-east-2.compute.amazonaws.com/Mrfile/Loveballad.wav";
            MediaPlayer  MRplayer = new MediaPlayer();
            try {
                MRplayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                MRplayer.setDataSource(url);
                MRplayer.prepare();
                play = MRplayer.getCurrentPosition();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return play;
    }
 public int duration(int position)
    {


        if (position == 0)
        {
            String url = "http://ec2-18-220-208-130.us-east-2.compute.amazonaws.com/Mrfile/어디에도.wav";
            final MediaPlayer  MRplayer = new MediaPlayer();
            try {
                MRplayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                MRplayer.setDataSource(url);
                MRplayer.prepare();
                play = MRplayer.getDuration();


            } catch (IOException e) {
                e.printStackTrace();
            }

        }
        if (position == 1)
        {
            String url = "http://ec2-18-220-208-130.us-east-2.compute.amazonaws.com/Mrfile/입술의말.wav";
            MediaPlayer  MRplayer = new MediaPlayer();
            try {
                MRplayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                MRplayer.setDataSource(url);
                MRplayer.prepare();
                play = MRplayer.getDuration();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (position == 2)
        {
            String url = "http://ec2-18-220-208-130.us-east-2.compute.amazonaws.com/Mrfile/사랑했지만.wav";
            MediaPlayer  MRplayer = new MediaPlayer();
            try {
                MRplayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                MRplayer.setDataSource(url);
                MRplayer.prepare();
                play = MRplayer.getDuration();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (position == 3)
        {
            String url = "http://ec2-18-220-208-130.us-east-2.compute.amazonaws.com/Mrfile/모든날 모든순간.wav";
            MediaPlayer  MRplayer = new MediaPlayer();
            try {
                MRplayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                MRplayer.setDataSource(url);
                MRplayer.prepare();
                play = MRplayer.getDuration();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (position == 4)
        {
            String url = "http://ec2-18-220-208-130.us-east-2.compute.amazonaws.com/Mrfile/마지막으로 안아도 될까.wav";
            MediaPlayer MRplayer = new MediaPlayer();
            try {
                MRplayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                MRplayer.setDataSource(url);
                MRplayer.prepare();
                play = MRplayer.getDuration();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (position == 5)
        {
            String url = "http://ec2-18-220-208-130.us-east-2.compute.amazonaws.com/Mrfile/감사.wav";
            MediaPlayer  MRplayer = new MediaPlayer();
            try {
                MRplayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                MRplayer.setDataSource(url);
                MRplayer.prepare();
                play = MRplayer.getDuration();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (position == 6)
        {
            String url = "http://ec2-18-220-208-130.us-east-2.compute.amazonaws.com/Mrfile/My way.wav";
            MediaPlayer  MRplayer = new MediaPlayer();
            try {
                MRplayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                MRplayer.setDataSource(url);
                MRplayer.prepare();
                play = MRplayer.getDuration();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (position == 7)
        {
            String url = "http://ec2-18-220-208-130.us-east-2.compute.amazonaws.com/Mrfile/Loveballad.wav";
            MediaPlayer  MRplayer = new MediaPlayer();
            try {
                MRplayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                MRplayer.setDataSource(url);
                MRplayer.prepare();
                play = MRplayer.getDuration();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return play;
    }






*/

        public void MrStart(int position)
        {

      /* final Handler handler = new Handler();
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run()
            {
                    Mr_bar.setMax((int)CurrentSongLength/ 1000);
                    int CurrentPosition = MRplayer.getCurrentPosition() /1000;
                    Mr_bar.setProgress(CurrentPosition);
                    MRplay_time.setText(com.example.yujongheyon.anywheresing.Utility.convertDuration((long)MRplayer.getCurrentPosition()));
                    handler.postDelayed(this,1000);
            }
        });*/

          //  mediaPlayer = new MediaPlayer();
            // Mrplaying = true;
            if (position == 0)
            {


                String url = "http://ec2-18-188-84-158.us-east-2.compute.amazonaws.com/Mrfile/어디에도.wav";
                try {
                    // your URL here MediaPlayer
                    MRplayer = new MediaPlayer();
                    MRplayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                    MRplayer.setDataSource(url);
                    MRplayer.prepare();
                    MRplayer.start();

                   /* play_time = MRplayer.getCurrentPosition();
                    play_full_time = MRplayer.getDuration();*/

                } catch (IOException e) {

                }


            }

            if (position == 1) {
                String url = "http://ec2-18-188-84-158.us-east-2.compute.amazonaws.com/Mrfile/입술의말.wav";
                try {
                    // your URL here MediaPlayer
                    MRplayer = new MediaPlayer();
                    MRplayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                    MRplayer.setDataSource(url);
                    MRplayer.prepare();
                    MRplayer.start();
                } catch (IOException e) {

                }


            }

            if (position == 2) {
                String url = "http://ec2-18-188-84-158.us-east-2.compute.amazonaws.com/Mrfile/사랑했지만.wav";
                try {
                    // your URL here MediaPlayer
                    MRplayer = new MediaPlayer();
                    MRplayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                    MRplayer.setDataSource(url);
                    MRplayer.prepare();
                    MRplayer.start();
                } catch (IOException e) {

                }

            }

            if (position == 3) {
                String url = "http://ec2-18-188-84-158.us-east-2.compute.amazonaws.com/Mrfile/모든날 모든순간.wav";
                try {
                    // your URL here MediaPlayer
                    MRplayer = new MediaPlayer();
                    MRplayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                    MRplayer.setDataSource(url);
                    MRplayer.prepare();
                    MRplayer.start();
                } catch (IOException e) {

                }

            }
            if (position == 4) {
                String url = "http://ec2-18-188-84-158.us-east-2.compute.amazonaws.com/Mrfile/마지막으로 안아도 될까.wav";
                    try {
                    // your URL here MediaPlayer
                    MRplayer = new MediaPlayer();
                    MRplayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                    MRplayer.setDataSource(url);
                    MRplayer.prepare();
                    MRplayer.start();
                } catch (IOException e) {

                }

            }
            if (position == 5) {
                String url = "http://ec2-18-188-84-158.us-east-2.compute.amazonaws.com/Mrfile/감사.wav";
                try {
                    // your URL here MediaPlayer
                    MRplayer = new MediaPlayer();
                    MRplayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                    MRplayer.setDataSource(url);
                    MRplayer.prepare();
                    MRplayer.start();
                } catch (IOException e) {

                }

            }
            if (position == 6) {
                String url = "http://ec2-18-188-84-158.us-east-2.compute.amazonaws.com/Mrfile/My way.wav";
                try {
                    // your URL here MediaPlayer
                    MRplayer = new MediaPlayer();
                    MRplayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                    MRplayer.setDataSource(url);
                    MRplayer.prepare();
                    MRplayer.start();
                } catch (IOException e) {

                }


            }
            if (position == 7) {
                String url = "http://ec2-18-188-84-158.us-east-2.compute.amazonaws.com/Mrfile/Loveballad.wav";
                try {
                    // your URL here MediaPlayer
                    MRplayer = new MediaPlayer();
                    MRplayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                    MRplayer.setDataSource(url);
                    MRplayer.prepare();
                    MRplayer.start();
                } catch (IOException e) {

                }

            }
            if (position == 8) {
                String url = "http://ec2-18-188-84-158.us-east-2.compute.amazonaws.com/Mrfile/그대를 사랑하는 10가지 이유.wav";
                try {
                    // your URL here MediaPlayer
                    MRplayer = new MediaPlayer();
                    MRplayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                    MRplayer.setDataSource(url);
                    MRplayer.prepare();
                    MRplayer.start();
                } catch (IOException e) {

                }

            }
        }

        CountDownTimer MrTimer = new CountDownTimer(Long.MAX_VALUE,1000) {
            @Override
            public void onTick(long l) {

                cnt++;
                String time = new Integer(cnt).toString();

                long millis = cnt;
                int seconds = (int) (millis / 60);
                int minutes = seconds / 60;
                seconds     = seconds % 60;

                record_timer.setText(String.format("%d:%02d:%02d", minutes, seconds,millis));
            }

            @Override
            public void onFinish() {

            }
        };


    @Override
    public void onStop() {
        super.onStop();

        MrTimer.cancel();
    }

    /*public void MRstop()
    {

    MRplayer.stop();

    }*/
/*

    @Override
    public void onStop() {
        super.onStop();
        MRplayer.stop();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        MRplayer.stop();
    }
*/
   /* private boolean saveSoundFile(File savefile, boolean isWavFile) {
        byte[] data = soundWave.getAllWaveData();
        if (data.length == 0) {
            Log.w("doigji", "save data is not found.");
            return false;
        }

        try {
            savefile.createNewFile();

            FileOutputStream targetStream = new FileOutputStream(savefile);
            try {
                if (isWavFile) {
                    WaveFileHeaderCreator.pushWaveHeader(targetStream, SAMPLE_RATE, CHANNEL_CONFIG, AUDIO_ENCODING, data.length);
                }
                targetStream.write(data);
            } finally {
                if (targetStream != null) {
                    targetStream.close();
                }
            }
            return true;
        } catch (IOException ex) {
            Log.w("igjwroi", "Fail to save sound file.", ex);
            return false;
        }
    }*/
/*

    private void stopAll() {
        if (recordTask != null && recordTask.isRunning()) {
            stopRecording();
        }
        if (playTask != null && playTask.isRunning()) {
            stopPlaying();
        }
    }
    private boolean saveSoundFile(File savefile, boolean isWavFile) {
        byte[] data = soundWave.getAllWaveData();
        if (data.length == 0) {
            Log.w("fdsaf", "save data is not found.");
            return false;
        }

        try {
            savefile.createNewFile();

            FileOutputStream targetStream = new FileOutputStream(savefile);
            try {
                if (isWavFile) {
                    WaveFileHeaderCreator.pushWaveHeader(targetStream, SAMPLE_RATE, CHANNEL_CONFIG, AUDIO_ENCODING, data.length);
                }
                targetStream.write(data);
            } finally {
                if (targetStream != null) {
                    targetStream.close();
                }
            }
            return true;
        } catch (IOException ex) {
            Log.w("odsakfaoifk", "Fail to save sound file.", ex);
            return false;
        }
    }


    private void startRecording() {
        Log.i("sakifasifajsfi", "start recording.");
        setButtonEnable(true);
        try {
            recordTask = new MicRecordTask(progressBar, soundWave, SAMPLE_RATE, CHANNEL_CONFIG, AUDIO_ENCODING);
            recordTask.setMax(10 * getDataBytesPerSecond(SAMPLE_RATE, CHANNEL_CONFIG, AUDIO_ENCODING));
        } catch (IllegalArgumentException ex) {
            Log.w("iajfaisfjasfi", "Fail to create MicRecordTask.", ex);
        }
        recordTask.start();
        waitEndTask(recordTask);
    }

    private void stopRecording() {
        stopTask(recordTask);
        Log.i("faaifajsf", "stop recording.");
    }

    private void startPlaying() {
        Log.i("aojsfaisf", "start playing.");

        setButtonEnable(true);
        try {
            playTask = new AudioPlayTask(progressBar, soundWave, SAMPLE_RATE, CHANNEL_CONFIG, AUDIO_ENCODING);
        } catch (IllegalArgumentException ex) {
            Log.w("jmafsjof01", "Fail to create MicRecordTask.", ex);
        }
        playTask.start();
        waitEndTask(playTask);
    }

    private void stopPlaying() {
        stopTask(playTask);
        Log.i("kosak", "stop playing.");
    }

    private void stopTask(StopableTask task) {
        if (task.stopTask()) {
            try {
                task.join(1000);
            } catch (InterruptedException e) {
                Log.w("osoa", "Interrupted recoring thread stopping.");
            }
        }
        setButtonEnable(false);
    }



    private void setButtonEnable(boolean b) {



        record_start.setEnabled(!b);
        record_play.setEnabled(!b);
        record_stop.setEnabled(b);
        record_save.setEnabled(!b && hasSDCard());
    }


    private void waitEndTask(final Thread t) {
        final Handler handler = new Handler();
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    t.join();
                } catch (InterruptedException e) {
                }

                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        setButtonEnable(false);
                    }
                });
            }
        }).start();
    }


    private File getCacheFile() {
        return new File(getSavePath(), "cache.raw");
    }

    private File getSavePath() {
        if (hasSDCard()) {
            File path = new File(Environment.getExternalStorageDirectory(), "/VoiceChanger/");
            path.mkdirs();
            return path;
        } else {
            Log.i("asijdsaidasd", "SDCard is unuseable: " + Environment.getExternalStorageState());
            return getCacheFile();
        }
    }

    private boolean hasSDCard() {
        String state = Environment.getExternalStorageState();
        return state.equals(Environment.MEDIA_MOUNTED);
    }


    private int getDataBytesPerSecond(int sampleRate, int channelConfig, int audioEncoding) {
        boolean is8bit = audioEncoding == AudioFormat.ENCODING_PCM_8BIT;
        boolean isMonoChannel = channelConfig != AudioFormat.CHANNEL_CONFIGURATION_STEREO;
        return sampleRate * (isMonoChannel ? 1: 2) * (is8bit ? 1: 2);
    }


*/





/*  @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        boolean ret = super.onCreateOptionsMenu(menu);
        int index = Menu.FIRST;
        menu.add(Menu.NONE, index++, Menu.NONE, "データクリア");
        menu.add(Menu.NONE, index++, Menu.NONE, "ノイズを追加");
        menu.add(Menu.NONE, index++, Menu.NONE, "サインを追加");
        menu.add(Menu.NONE, index++, Menu.NONE, "矩形を追加");
        return ret;
    }*/

   /* @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        final int size = 8000;
        final int freq = 440;
        switch (item.getItemId()) {
            case Menu.FIRST:
                soundWave.clearWaveData();
                break;
            case Menu.FIRST + 1:
                soundWave.addWaveData(NormalizeWaveData.createNoiseData(size));
                break;
            case Menu.FIRST + 2:
                soundWave.addWaveData(NormalizeWaveData.createSineData(size, freq));
                break;
            case Menu.FIRST + 3:
                soundWave.addWaveData(NormalizeWaveData.createSquareData(size, freq));
                break;
        }
        return true;
    }*/


    /*
public class SearchTask extends AsyncTask<Void, Void, Void>
{

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected Void doInBackground(Void... params)
    {
        try
        {
            paringJsonData(getUtube());
        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid)
    {
        search_list

        super.onPostExecute(aVoid);
    }
}
*/






/*
private void recoderTime()
{
    int minute = (int) (timeleft/1000) /60;
    int seconds = (int) (timeleft/1000) % 60;

    String recordFormat = String.format(Locale.getDefault(),"%02d : %02d",minute,seconds);

    record_timer.setText(recordFormat);


}
CountDownTimer countDownTimer = new CountDownTimer(1800000,1000) {
    @Override
    public void onTick(long l) {

        int minute = (int) (timeleft/1000) /60;
        int seconds = (int) (timeleft/1000) % 60;
            minute++;
            if(minute == 60)
            {
                minute = 0;

                seconds = seconds+1;
            }
        String recordFormat = String.format(Locale.getDefault(),"%02d : %02d",minute,seconds);

        record_timer.setText(recordFormat);
    }

    @Override
    public void onFinish() {

    }
};


    Handler handler = new Handler()
    {
        public void handleMessage(Message msg)
        {
            if (msg.arg1 ==1)
            {
                int minute = (int) (timeleft/1000) /60;
                int seconds = (int) (timeleft/1000) % 60;

                String recordFormat = String.format(Locale.getDefault(),"%02d : %02d",minute,seconds);

                record_timer.setText(recordFormat);
            }
        }
    };


    public class myThread implements Runnable
    {

        @Override
        public void run()
        {
            while (true)
            {

                Message message = new Message();
                message.arg1 = 1;
                handler.sendMessage(message);
                return;
            }
        }
    }
*/
    /*
public void Record_image()
{

}*/


 /*   int BufferElements2Rec = 1024;
    int BytePerElement =2;

    private void startRecording()
    {
        recorder = new AudioRecord(MediaRecorder.AudioSource.MIC,RECODER_SAMPLERATE,RECODER_CHANNELS,RECODER_AUDIO_ENCODING,BufferElements2Rec * BytePerElement);

        recorder.startRecording();
        isRecording = true;
        recordingThread = new Thread(new Runnable() {
            @Override
            public void run() {
                writeAudioDataToFile();
            }
        },"오디오 녹음 쓰레드");
        recordingThread.start();
    }

    private byte[] short2byte(short[] sData) {
        int shortArrsize = sData.length;
        byte[] bytes = new byte[shortArrsize * 2];
        for (int i = 0; i < shortArrsize; i++) {
            bytes[i * 2] = (byte) (sData[i] & 0x00FF);
            bytes[(i * 2) + 1] = (byte) (sData[i] >> 8);
            sData[i] = 0;
        }
        return bytes;

    }
    private void writeAudioDataToFile() {
        // Write the output audio in byte

        String filePath = "/sdcard/voice8K16bitmono.pcm";
        short sData[] = new short[BufferElements2Rec];

        FileOutputStream os = null;
        try {
            os = new FileOutputStream(filePath);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        while (isRecording) {
            // gets the voice output from microphone to byte format

            recorder.read(sData, 0, BufferElements2Rec);
            System.out.println("Short wirting to file" + sData.toString());
            try {
                // // writes the data to file from buffer
                // // stores the voice buffer
                byte bData[] = short2byte(sData);
                os.write(bData, 0, BufferElements2Rec * BytePerElement);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        try {
            os.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void stopRecording() {
        // stops the recording activity
        if (null != recorder) {
            isRecording = false;
            recorder.stop();
            recorder.release();
            recorder = null;
            recordingThread = null;
        }
    }*/




}
