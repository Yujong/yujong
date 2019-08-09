package com.example.yujongheyon.anywheresing;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URLEncoder;

/**
 * Created by yujongheyon on 2018-06-25.
 */

public class  anywheresing_signupActivity extends Activity
{

    //String filepath;
    Bitmap bitmap;
    private String userGender;
    private int REQUEST_IMAGE_CAPTURE =1;
    private int REQUEST_IMAGE_ALBUM = 2;
    String imageString; //카메라촬영으로 얻은 이미지 스트링으로 인코딩한값
    String temp;
    ImageView profilimageadd;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.anywheresing_signup);


        final EditText signup_email = (EditText)findViewById(R.id.signup_email);
        final EditText signup_nickname = (EditText)findViewById(R.id.signup_nickname);
        final EditText signup_password = (EditText)findViewById(R.id.signup_password);
        final EditText signup_passwordconfirm = (EditText)findViewById(R.id.signup_passwordconfirm);
        final TextView passwordconfirmmsg = (TextView)findViewById(R.id.passwordconfirmmsg);
        final TextView emailspace = (TextView)findViewById(R.id.emailspace);
          profilimageadd = (ImageView)findViewById(R.id.profilimageadd);

        Button signup_ok = (Button)findViewById(R.id.signup_ok);
        final Button emailcheck = (Button)findViewById(R.id.emailcheck);
        Button nicknamechaeck = (Button)findViewById(R.id.nicknamechek);
        final Button back  = (Button)findViewById(R.id.signup_close);

        Glide.with(this).load(R.drawable.kakao_2).apply(RequestOptions.overrideOf(650,650).circleCrop()).into(profilimageadd);


        //성별 데이터 가져오기
        RadioGroup genderGroup = (RadioGroup)findViewById(R.id.genderGroup);
        int genderGroupID = genderGroup.getCheckedRadioButtonId();
        userGender = ((RadioButton)findViewById(genderGroupID)).getText().toString();

        genderGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                RadioButton genderButton = (RadioButton)findViewById(i);
                userGender = genderButton.getText().toString();
            }
        });

        //프로필 이미지 생성,  이미지선택시 다이얼로그생성(사진촬영,갤러리,취소)
        profilimageadd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //다이얼로그 카메라버튼 클릭리스너
                DialogInterface.OnClickListener camera = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                            takePicture();
                    }
                };
                // 앨범불러오기버튼 리스너
                DialogInterface.OnClickListener album = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        AlbumAction();
                    }
                };
                //취소버튼
                DialogInterface.OnClickListener close = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();//다이얼로그 닫기
                    }
                };


                new AlertDialog.Builder(anywheresing_signupActivity.this)
                        .setPositiveButton("사진 촬영",camera)
                        .setNegativeButton("갤러리",album)
                        .setNeutralButton("취소",close)
                        .show();
            }
        });



        //email값 넘겨서 가입되어있는지 아닌지 판단
        emailcheck.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view) {
                String Vemailchek = signup_email.getText().toString();

                Response.Listener<String> responseListner = new Response.Listener<String>()
                {
                    @Override
                    public void onResponse(String response)
                    {
                        try
                        {
                            JSONObject jsonResponse = new JSONObject(response);
                            boolean success = jsonResponse.getBoolean("success");
                            Log.d("var","중복체크");

                            if (success)
                            {
                                AlertDialog.Builder builder = new AlertDialog.Builder(anywheresing_signupActivity.this);

                                   builder.setMessage("가입 하실 수 있는 이메일입니다")
                                           .create()
                                           .show();
                            }
                            else
                            {
                                AlertDialog.Builder builder = new AlertDialog.Builder(anywheresing_signupActivity.this);

                                builder.setMessage("이미 등록된 회원입니다")
                                        .create()
                                        .show();
                            }
                        }
                        catch (JSONException e)
                        {
                            e.printStackTrace();
                        }


                    }
                };

                emailcheckRequest emailcheckRequest = new emailcheckRequest(Vemailchek,responseListner);
                RequestQueue queue = Volley.newRequestQueue(anywheresing_signupActivity.this);
                queue.add(emailcheckRequest);

            }
        });


        //닉네임 중복일치
        nicknamechaeck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String Vnicknamechek = signup_nickname.getText().toString();

                Response.Listener<String> responseListner = new Response.Listener<String>()
                {
                    @Override
                    public void onResponse(String response)
                    {
                        try
                        {
                            JSONObject jsonResponse = new JSONObject(response);
                            boolean success = jsonResponse.getBoolean("success");
                            Log.d("nick","중복체크");

                            if (success)
                            {
                                AlertDialog.Builder builder = new AlertDialog.Builder(anywheresing_signupActivity.this);

                                builder.setMessage("사용가능한 닉네임 입니다")
                                        .create()
                                        .show();
                            }
                            else
                            {
                                AlertDialog.Builder builder = new AlertDialog.Builder(anywheresing_signupActivity.this);

                                builder.setMessage("이미 사용중인 닉네임 입니다")
                                        .create()
                                        .show();
                            }
                        }
                        catch (JSONException e)
                        {
                            e.printStackTrace();
                        }


                    }
                };

                nicknamecheckRequest nicknamecheckRequest = new nicknamecheckRequest(Vnicknamechek,responseListner);
                RequestQueue queue = Volley.newRequestQueue(anywheresing_signupActivity.this);
                queue.add(nicknamecheckRequest);

        }
        });


        //회원가입 완료
        signup_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String signup_save_email = signup_email.getText().toString();
                String signup_save_nickname = signup_nickname.getText().toString();
                String signup_save_password = signup_password.getText().toString();




                Response.Listener<String> responseListener = new Response.Listener<String>()
                {

                    @Override
                    public void onResponse(String response)
                {
                    Log.d("respon",response);
                }
             };



               //    Log.d("php connection",temp);


                signupRequest signupRequest = new signupRequest(signup_save_email,signup_save_nickname,signup_save_password,userGender,imageString,responseListener);
                RequestQueue queue = Volley.newRequestQueue(anywheresing_signupActivity.this);
                queue.add(signupRequest);

                Log.d("php connection",signup_save_email);
                Log.d("php connection",signup_save_nickname);
                Log.d("php connection",signup_save_password);


                AlertDialog.Builder builder = new AlertDialog.Builder(anywheresing_signupActivity.this);//서버로 값전달
                builder.setMessage("회원 가입이 완료 되었습니다")
                        .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Intent intent = new Intent(anywheresing_signupActivity.this,anywheresing_loginActivity.class);
                        intent.putExtra("login_email",signup_save_email);//간편로그인을 위한 이메일텍스트 전송
                        startActivity(intent);
                        Log.d("php connection","접근성공");
                     }
                    })
                        .create()
                        .show();
                        //회원가입 성공시 다이얼로그 생성 확인버튼 누르면 로그인페이지(메인)로 이동



            }
        });
        //email공백 여부 확인

        signup_nickname.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                //공백일 경우
                if(signup_email.getText().toString().equals(""))
                {
                    emailspace.setText("이메일은 공백이 될 수 없습니다");
                }
                else
                {
                    emailspace.setText(null);
                }

                if (!Patterns.EMAIL_ADDRESS.matcher(signup_email.getText().toString()).matches())
                {
                    emailspace.setText("올바른 email 형식이 아닙니다");
                }
                else
                {
                    emailspace.setText(null);
                }

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });




        //비밀번호 일치,불일치 여부
        signup_passwordconfirm.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                //비밀번호 확인 입력창 입력시 일치하지 않으면 텍스트 출력(else)문
                if(signup_password.getText().toString().equals(signup_passwordconfirm.getText().toString()))
                {
                    passwordconfirmmsg.setText(null);
                }
                else
                {
                    passwordconfirmmsg.setText("비밀번호가 일치하지 않습니다");
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });



        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                finish();
            }
        });
    }

    //카메라 촬영
    public void takePicture ()
    {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent,REQUEST_IMAGE_CAPTURE);
    }
    //앨범불러오기
    public void AlbumAction()
    {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setData(android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        startActivityForResult(intent,REQUEST_IMAGE_ALBUM);

    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {

        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK)
        {

            Bundle extras = data.getExtras();
            bitmap = (Bitmap) extras.get("data");

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 10, baos);
            byte[] imageBytes = baos.toByteArray();
            imageString = Base64.encodeToString(imageBytes, Base64.DEFAULT);

            try
            {
                temp=URLEncoder.encode(imageString,"utf-8");
            }catch (Exception e)
            {
                Log.e("exception",e.toString());
            }

            Log.d("jpg",temp);

            //카메라로 찍은 사진 이미지뷰에 출력
            ImageView profilimageadd = (ImageView)findViewById(R.id.profilimageadd);
            Glide.with(this).load(bitmap).apply(RequestOptions.overrideOf(600,600).circleCrop()).into(profilimageadd);


            /*

             Uri imagefath = data.getData(); //이미지 주소 얻어오기

            try
            {
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(),imagefath); //이미지 비트맵으로 변환

                final ImageView profilimageadd = (ImageView)findViewById(R.id.profilimageadd);            //카메라로 찍은 사진 이미지뷰에 출력

                Glide.with(this).load(bitmap).apply(RequestOptions.overrideOf(600,600).circleCrop()).into(profilimageadd);
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }

            */
        }
        if (requestCode == REQUEST_IMAGE_ALBUM && resultCode == RESULT_OK)
        {
            String imagePath = getRealPathFromURI(data.getData()); //이미지 데이터를 비트맵으로 받아온다.

            try
            {
                Bitmap image_bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), data.getData());

                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                image_bitmap.compress(Bitmap.CompressFormat.JPEG, 10, baos);
                byte[] imageBytes = baos.toByteArray();
                imageString = Base64.encodeToString(imageBytes, Base64.DEFAULT);

               /* try{
                    temp= URLEncoder.encode(imageString,"utf-8");
                }catch (Exception e){
                    Log.e("exception",e.toString());
                }

*/

                Glide.with(this).load(image_bitmap).apply(RequestOptions.overrideOf(600,600).circleCrop()).into(profilimageadd);

            } catch (IOException e) {
                e.printStackTrace();
            }




        }


    }
    public void ALBUM()
    {

    }




    private String getRealPathFromURI(Uri contentUri) {
        int column_index=0;
        String[] proj = {MediaStore.Images.Media.DATA};
        Cursor cursor = getContentResolver().query(contentUri, proj, null, null, null);
        if(cursor.moveToFirst()){
            column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        }

        return cursor.getString(column_index);
    }


    // bitmap image 를  base64인코딩하여 스트링으로 변환

  /*public static String saveBitmapToJpeg(Context context, Bitmap bitmap, String name){

        File storage = context.getCacheDir(); //임시파일 저장 경로

        String fileName = name + ".jpg";  // 파일이름

        File tempFile = new File(storage,fileName);

        try{
            tempFile.createNewFile();  // 파일을 생성해주고

            FileOutputStream out = new FileOutputStream(tempFile);

            bitmap.compress(Bitmap.CompressFormat.JPEG, 90 , out);  // 비트맵파일 jpg로 변환

            Log.d("비트맵","파일변환");
            Log.d("jpg",fileName);

            out.close();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return tempFile.getAbsolutePath();
    }*/


}
