package com.kaamwalimanager.splash;

import static com.kaamwalimanager.utils.Constant.login;

import androidx.appcompat.app.AppCompatActivity;

import android.animation.ObjectAnimator;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.WindowManager;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.kaamwalimanager.MainActivity;
import com.kaamwalimanager.R;
import com.kaamwalimanager.auth.LoginActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class SplashActivity extends AppCompatActivity {

    ProgressBar splashProgress;
    int SPLASH_TIME = 1500; //This is 3 seconds
    SharedPreferences sharedpreferences;
    SharedPreferences.Editor editor;

    String object;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_splash);
        sharedpreferences = getSharedPreferences("MyPREFERENCES", MODE_PRIVATE);
        editor = sharedpreferences.edit();
        object=sharedpreferences.getString("data","");

        //This is additional feature, used to run a progress bar
        splashProgress = findViewById(R.id.splashProgress);
        playProgress();

        //Code to start timer and take action after the timer ends
        new Handler(getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                if (object.trim().isEmpty()){
                    Intent mySuperIntent = new Intent(SplashActivity.this, LoginActivity.class);
                    startActivity(mySuperIntent);
                    finish();
                }else {
                    try {
                        JSONObject jsonObject=new JSONObject(object);
                        checkLogin(jsonObject.getString("email"),jsonObject.getString("password"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Intent mySuperIntent = new Intent(SplashActivity.this, LoginActivity.class);
                        startActivity(mySuperIntent);
                        finish();
                    }
                }
            }
        }, SPLASH_TIME);
    }

    private void checkLogin(String e, String p) {
        RequestQueue queue = Volley.newRequestQueue(this);
        String Url=login;

        StringRequest sr = new StringRequest(Request.Method.POST, Url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.e("abcd",response);

                        try {
                            JSONObject jsonObject=new JSONObject(response);
                            if (jsonObject.getString("result").equals("1")){
                                editor.putString("data",jsonObject.getJSONObject("data").toString());
                                editor.commit();
                                startActivity(new Intent(SplashActivity.this, MainActivity.class));
                                finish();
                            }else {
                                Toast.makeText(SplashActivity.this, jsonObject.getString("message"), Toast.LENGTH_SHORT).show();
                                Intent mySuperIntent = new Intent(SplashActivity.this, LoginActivity.class);
                                startActivity(mySuperIntent);
                                finish();
                            }
                        } catch (JSONException jsonException) {
                            jsonException.printStackTrace();
                            Intent mySuperIntent = new Intent(SplashActivity.this, LoginActivity.class);
                            startActivity(mySuperIntent);
                            finish();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("abcd",error.getMessage());
                        Toast.makeText(SplashActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();

                    }
                })
        {
            @Override
            protected Map<String,String> getParams(){
                Map<String,String> params = new HashMap<String, String>();
                params.put("email",e);
                params.put("password",p);
                return params;
            }
        };
        queue.add(sr);
    }


    private void playProgress() {
        ObjectAnimator.ofInt(splashProgress, "progress", 100)
                .setDuration(3000)
                .start();
    }
}