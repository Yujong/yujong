package com.example.yujongheyon.anywheresing;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
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
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.LoggingBehavior;
import com.facebook.Profile;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.kakao.auth.AuthType;
import com.kakao.auth.ISessionCallback;
import com.kakao.auth.Session;
import com.kakao.network.ErrorResult;
import com.kakao.usermgmt.UserManagement;
import com.kakao.usermgmt.callback.MeResponseCallback;
import com.kakao.usermgmt.response.model.UserProfile;
import com.kakao.util.exception.KakaoException;

import org.json.JSONException;
import org.json.JSONObject;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

public class anywheresing_loginActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener{
    ImageView mic_image;

    SessionCallback callback;
    GoogleApiClient googleApiClient;
    CallbackManager callbackManager;
    private int googlelogincode = 12;
    private FirebaseAuth fAUTH;
    String Facebookemail;
    private Bundle savedInstanceState;

    //my = 1 face = 2 google = 3 kakao = 4 ->식별값id = discrimination

    int my = 1;
    int face = 2;
    int google = 3;
    int kakao = 4;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.anywheresing_login);

            SharedPreferences pref = getSharedPreferences("session", MODE_PRIVATE);
           String autologin = pref.getString("session", null);

       if (autologin != null)
        {
            Intent intent = new Intent(anywheresing_loginActivity.this,anywheresing_mainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY );
            startActivity(intent);
            finish();
        }

        this.savedInstanceState = savedInstanceState;


        FacebookSdk.sdkInitialize(getApplicationContext());
        AppEventsLogger.activateApp(anywheresing_loginActivity.this);
        callbackManager = CallbackManager.Factory.create();
        LoginManager.getInstance().logInWithReadPermissions(anywheresing_loginActivity.this,Arrays.asList("public_profile","email"));

        mic_image = (ImageView)findViewById(R.id.single_mic_image);
        Glide.with(this).load(R.drawable.mic_icon).apply(RequestOptions.overrideOf(650,650)).into(mic_image);




        final EditText login_email = (EditText)findViewById(R.id.login_email);
        final EditText login_password = (EditText)findViewById(R.id.login_password);
        final Button signup = (Button)findViewById(R.id.signupmove);
        final Button loginok = (Button)findViewById(R.id.loginok);
        Button goolglelogin = (Button)findViewById(R.id.goolglelogin);//구글로그인버튼
        Button faceboocklogin = (Button)findViewById(R.id.faceboocklogin);//페이스북//
        Button kakaologin = (Button)findViewById(R.id.kakaologin);//카카오//





        setGoogleLogin();

        kakaologin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                callback = new SessionCallback();
                Session.getCurrentSession().addCallback(callback);

                Session session = Session.getCurrentSession();
                session.addCallback(new SessionCallback());
                session.open(AuthType.KAKAO_LOGIN_ALL,anywheresing_loginActivity.this);

                requstMe();
            }
        });

        goolglelogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loginGoogle();
            }
        });



        faceboocklogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                LoginManager.getInstance().logInWithReadPermissions(anywheresing_loginActivity.this,Arrays.asList("public_profile", "user_friends"));
                LoginManager.getInstance().registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult)
                    {
                        Log.d("getAccessToken()", String.valueOf(loginResult.getAccessToken()));
                        Log.d("getId()", String.valueOf(Profile.getCurrentProfile().getId())); //아이디
                        Log.d("getName()", String.valueOf(Profile.getCurrentProfile().getName())); // 이름
                        Log.d("getProfilePictureUri", String.valueOf(Profile.getCurrentProfile().getProfilePictureUri(200, 200)));//프로필 사진
                        getUserEmail(loginResult);


                    }

                    @Override
                    public void onCancel() {

                    }

                    @Override
                    public void onError(FacebookException error) {

                    }
                });

            }
        });




            //로그인btn
            loginok.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                     final String userEmail = login_email.getText().toString();
                     String userPassword = login_password.getText().toString();


                    Response.Listener<String> responseListener = new Response.Listener<String>()
                    {
                        @Override
                        public void onResponse(String response)
                        {

                            try
                            {
                                JSONObject jsonObject = new JSONObject(response);
                                boolean success = jsonObject.getBoolean("success");
                                Log.d("순서가?","순서가?????");

                                if (success)
                                {

                                    AlertDialog.Builder builder = new AlertDialog.Builder(anywheresing_loginActivity.this);
                                    builder.setMessage("로그인 성공")
                                            .create()
                                            .dismiss();

                                    Intent intent = new Intent(anywheresing_loginActivity.this,anywheresing_mainActivity.class);
                                    intent.putExtra("useremail",userEmail);
                                    intent.putExtra("discrimination",my);


                                    SharedPreferences pref = getSharedPreferences("session", MODE_PRIVATE);
                                    SharedPreferences.Editor editor = pref.edit();
                                    editor.putString("session",userEmail);
                                    editor.commit();


                                    Log.d("abcdefg",userEmail);


                                    Fragment fragment = new Fragment(); // Fragment 생성
                                    Bundle bundle = new Bundle(1); // 파라미터는 전달할 데이터 개수
                                    bundle.putString("useremail", userEmail); // key , value
                                    bundle.putInt("discrimination",my);
                                    Log.d("mikhgsedfmhish", String.valueOf(my));
                                    fragment.setArguments(bundle);

                                    startActivity(intent);
                                    finish();

                                }
                                else
                                {
                                    AlertDialog.Builder builder = new AlertDialog.Builder(anywheresing_loginActivity.this);
                                    builder.setMessage("계정을 다시 확인해주세요")
                                            .create()
                                            .show();
                                }
                            }
                            catch (Exception e)
                            {

                            }
                        }
                    };
                    Log.d("로그인이메일",userEmail);
                    Log.d("비비ㅣ비비",userPassword);
                    LoginRequest loginRequest = new LoginRequest(userEmail,userPassword,responseListener);
                    RequestQueue queue = Volley.newRequestQueue(anywheresing_loginActivity.this);
                    queue.add(loginRequest);

                }
            });
         //회원가입이동
        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(anywheresing_loginActivity.this,anywheresing_signupActivity.class);

                startActivity(intent);

            }
        });



   //해시키발급
        printkh();
    }
    //facebook로그인 유지

    protected void autoLogin()
    {
        FacebookSdk.addLoggingBehavior(LoggingBehavior.INCLUDE_ACCESS_TOKENS);
    }





    //페이스북에 로그인한 회원정보 가져오기
    public void getUserEmail(LoginResult loginResult){



        GraphRequest request = GraphRequest.newMeRequest(
                loginResult.getAccessToken(),
                new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(JSONObject object, GraphResponse response)
                    {

                    if (response.getError() != null)
                    {
                        Log.d("sfg4321`123123","ifdgj2i4g2tg");
                    }
                    else
                    {
                        try
                        {

                            String Facebookemail = object.getString("email");//이메일가져오기

                            String name = object.getString("name");
                            //String gender = object.getString("gender");

                            Log.d("email", Facebookemail);

                            Log.d("name", name);

                          //  Log.d("gender", gender);
                         Response.Listener<String> responseListener = new Response.Listener<String>() {
                                @Override
                                public void onResponse(String response)
                                {

                                }
                            };

                            facebookRequest facebookRequest = new facebookRequest(Facebookemail,name,responseListener);
                            RequestQueue queue = Volley.newRequestQueue(anywheresing_loginActivity.this);
                            queue.add(facebookRequest);
                            Intent intent = new Intent(anywheresing_loginActivity.this,anywheresing_mainActivity.class);
                            intent.putExtra("useremail",Facebookemail);
                            intent.putExtra("discrimination",face);
                            Log.d("abcdefg",Facebookemail);
                            Fragment fragment = new Fragment(); // Fragment 생성
                            Bundle bundle = new Bundle(1);// 파라미터는 전달할 데이터 개수
                            bundle.putString("useremail",Facebookemail); // key , value
                            bundle.putInt("discrimination",face);
                            fragment.setArguments(bundle);


                            SharedPreferences pref = getSharedPreferences("session", MODE_PRIVATE);
                            SharedPreferences.Editor editor = pref.edit();
                            editor.putString("session",Facebookemail);
                            editor.commit();


                            startActivity(intent);
                            finish();

                        }
                        catch (JSONException e)
                        {
                            e.printStackTrace();
                            Log.d("dasgaigj24t9g412t","ifdgj2i4g2tg");
                        }
                    }


                    }

                });
        Bundle para = new Bundle();
        para.putString("fields","email,name,gender,id,birthday,link");
        request.setParameters(para);
        request.executeAsync();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);



        callbackManager.onActivityResult(requestCode, resultCode, data);

       if (requestCode == googlelogincode)
        {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if (result != null)
            {
                if (result.isSuccess())//구글 회원정보 가저오기
                {
                    GoogleSignInAccount account = result.getSignInAccount();

                    String googleName = account.getDisplayName();
                    String googleEmail = account.getEmail();
                    /*String googleId  = account.getId();
                    String googleTokenKey =account.getServerAuthCode();*/



                    Response.Listener<String> responseListener = new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response)
                        {

                        }
                    };
                    googleRequest googleRequest = new googleRequest(googleEmail,googleName,responseListener);
                    RequestQueue queue = Volley.newRequestQueue(anywheresing_loginActivity.this);
                    queue.add(googleRequest);
                    Intent intent = new Intent(anywheresing_loginActivity.this,anywheresing_mainActivity.class);
                    intent.putExtra("useremail",googleEmail);
                    intent.putExtra("discrimination",google);
                    Log.d("abcdefg",googleEmail);
                    Fragment fragment = new Fragment(); // Fragment 생성
                    Bundle bundle = new Bundle(1);// 파라미터는 전달할 데이터 개수
                    bundle.putString("useremail",googleEmail); // key , value
                    bundle.putInt("discrimination",google);
                    fragment.setArguments(bundle);


                    SharedPreferences pref = getSharedPreferences("session", MODE_PRIVATE);
                    SharedPreferences.Editor editor = pref.edit();
                    editor.putString("session",googleEmail);
                    editor.commit();

                    startActivity(intent);
                    finish();


                 /*   Intent intent = new Intent(anywheresing_loginActivity.this,anywheresing_mainActivity.class);
                    startActivity(intent);*/
                    FireBaseAuthWithGoogle(account);

                    googleApiClient.disconnect();
                }
            }
        }
        if (Session.getCurrentSession().handleActivityResult(requestCode,resultCode,data))
        {
            return;
        }

    }
    private void FireBaseAuthWithGoogle (GoogleSignInAccount acct)
    {
        fAUTH = FirebaseAuth.getInstance();

        Log.d("googleerror","googleerrrrrrror");
        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(),null);
        fAUTH.signInWithCredential(credential)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {

                            if (!task.isSuccessful())
                            {
                                    Log.d("googleerror","googleerrrrrrror");
                            }
                            else
                            {
                                FirebaseUser firebaseUser = fAUTH.getCurrentUser();
                            }

                        }
                    });



    }

    //카카오톡 세션콜백
    private class SessionCallback implements ISessionCallback
    {
        //로그인을 성공했을때
        @Override
        public void onSessionOpened()
        {
            UserManagement.getInstance().requestMe(new MeResponseCallback() {
                @Override
                public void onSessionClosed(ErrorResult errorResult) {

                }

                @Override
                public void onNotSignedUp() {

                }

                @Override
                public void onSuccess(UserProfile result) {


                    Log.e("UserProfile", result.toString());
                    Log.e("UserProfile", result.getId() + "");

                   /* String kakoemail = result.getEmail();
                    String kakaonickname = result.getNickname();

                    Response.Listener<String> responseListener = new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response)
                        {

                        }
                    };
                    kakaoRequest kakaoRequest = new kakaoRequest(kakoemail,kakaonickname,responseListener);
                    RequestQueue queue = Volley.newRequestQueue(anywheresing_loginActivity.this);
                    queue.add(kakaoRequest);
                    Intent intent = new Intent(anywheresing_loginActivity.this,anywheresing_mainActivity.class);
                    intent.putExtra("useremail",kakoemail);
                    intent.putExtra("discrimination",kakao);
                    Log.d("abcdefg",kakoemail);
                    Fragment fragment = new Fragment(); // Fragment 생성
                    Bundle bundle = new Bundle(1);// 파라미터는 전달할 데이터 개수
                    bundle.putString("useremail",kakoemail); // key , value
                    bundle.putInt("discrimination",kakao);
                    fragment.setArguments(bundle);


                    startActivity(intent);
                    finish();*/

                    /*
                    Intent intent = new Intent(anywheresing_loginActivity.this,anywheresing_mainActivity.class);
                    startActivity(intent);*/
                }
            });
        }

        @Override
        public void onSessionOpenFailed(KakaoException exception) {

        }
    }
    public void requstMe()//카카오톡 로그인 사용자 정보요청
    {


       UserManagement.getInstance().requestMe(new MeResponseCallback() {
            @Override
            public void onSessionClosed(ErrorResult errorResult) {

            }

            @Override
            public void onNotSignedUp() {

            }

            @Override
            public void onSuccess(UserProfile result) {

                String kakoemail = result.getEmail();
                String kakaonickname = result.getNickname();

                Response.Listener<String> responseListener = new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response)
                    {

                    }
                };
                kakaoRequest kakaoRequest = new kakaoRequest(kakoemail,kakaonickname,responseListener);
                RequestQueue queue = Volley.newRequestQueue(anywheresing_loginActivity.this);
                queue.add(kakaoRequest);
                Intent intent = new Intent(anywheresing_loginActivity.this,anywheresing_mainActivity.class);
                intent.putExtra("useremail",kakoemail);
                intent.putExtra("discrimination",kakao);
                Log.d("abcdefg",kakoemail);
                Log.d("fhuedfh8787",kakaonickname);
                Fragment fragment = new Fragment(); // Fragment 생성
                Bundle bundle = new Bundle(1);// 파라미터는 전달할 데이터 개수
                bundle.putString("useremail",kakoemail); // key , value
                bundle.putInt("discrimination",kakao);
                fragment.setArguments(bundle);

                SharedPreferences pref = getSharedPreferences("session", MODE_PRIVATE);
                SharedPreferences.Editor editor = pref.edit();
                editor.putString("session",kakoemail);
                editor.commit();


                startActivity(intent);
                finish();
            }
        });
    }




    private void  setGoogleLogin()//구글로그인셋
    {
        FirebaseAuth.getInstance().signOut();

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestIdToken("160604369361-a9qt4f2u5ohevt22hi736mkg56sn7aab.apps.googleusercontent.com")
                .requestEmail().build();

        googleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, new GoogleApiClient.OnConnectionFailedListener() {
                    @Override
                    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

                    }
                })
                .addApi(Auth.GOOGLE_SIGN_IN_API,gso)
                .build();
    }
    private void loginGoogle()//구글로그인
    {
        Intent googlelogin = Auth.GoogleSignInApi.getSignInIntent(googleApiClient);
        startActivityForResult(googlelogin,googlelogincode);
    }



    private void printkh()//API로그인 해시키발급코드 잊어먹지말자
    {
        try
        {
            PackageInfo info = getPackageManager().getPackageInfo(getPackageName().toString(), PackageManager.GET_SIGNATURES);

            for (Signature signature : info.signatures)
            {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.d("json keyHash :", Base64.encodeToString(md.digest(),Base64.DEFAULT));
            }
        }
        catch (PackageManager.NameNotFoundException e)
        {
            Log.e("KeyHash",e.toString());
        }
        catch (NoSuchAlgorithmException e)
        {
            Log.e("KeyHash",e.toString());
        }
    }


    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.d("구글실패","구글실패");
    }


}


