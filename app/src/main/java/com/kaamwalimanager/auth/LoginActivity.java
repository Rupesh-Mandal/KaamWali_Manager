package com.kaamwalimanager.auth;

import static com.kaamwalimanager.utils.Constant.login;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.kaamwalimanager.MainActivity;
import com.kaamwalimanager.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {

    Button login_btn_signin;
    EditText email,password;
    SharedPreferences sharedpreferences;
    SharedPreferences.Editor editor;

    TextView signup;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        login_btn_signin=findViewById(R.id.login_btn_signin);
        email=findViewById(R.id.email);
        password=findViewById(R.id.password);
        signup=findViewById(R.id.signup);
        sharedpreferences = getSharedPreferences("MyPREFERENCES", MODE_PRIVATE);
        editor = sharedpreferences.edit();

        login_btn_signin.setOnClickListener(view -> {
            if (isValid()){
                checkLogin(email.getText().toString().trim(),password.getText().toString().trim());
            }
        });
        signup.setOnClickListener(view -> {
            startActivity(new Intent(this,SignUpActivity.class));
            finish();
        });
    }

    private void checkLogin(String e, String p) {
        RequestQueue queue = Volley.newRequestQueue(this);
        String Url=login;

        ProgressDialog progressDialog=new ProgressDialog(this);
        progressDialog.setMessage("Please Waite");
        progressDialog.show();

        StringRequest sr = new StringRequest(Request.Method.POST, Url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.e("abcd",response);
                        progressDialog.dismiss();

                        try {
                            JSONObject jsonObject=new JSONObject(response);
                            if (jsonObject.getString("result").equals("1")){
                                Toast.makeText(LoginActivity.this, jsonObject.getString("message"), Toast.LENGTH_SHORT).show();
                                editor.putString("data",jsonObject.getJSONObject("data").toString());
                                editor.commit();
                                startActivity(new Intent(LoginActivity.this, MainActivity.class));
                                finish();
                            }else {
                                Toast.makeText(LoginActivity.this, jsonObject.getString("message"), Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException jsonException) {
                            jsonException.printStackTrace();
                            Log.e("abcd",jsonException.toString());
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("abcd",error.getMessage());
                        progressDialog.dismiss();
                        Toast.makeText(LoginActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();

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

    boolean isValid(){
        if (email.getText().toString().trim().isEmpty()){
            email.setError("Please provide email");
            email.requestFocus();
            return false;
        }else {
            if (password.getText().toString().trim().isEmpty()){
                password.setError("Please provide Password");
                password.requestFocus();
                return false;
            }else {
                return true;
            }
        }
    }
}