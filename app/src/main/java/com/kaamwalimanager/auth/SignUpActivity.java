package com.kaamwalimanager.auth;


import static com.kaamwalimanager.utils.Constant.signup;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
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

public class SignUpActivity extends AppCompatActivity {

    Button btn_signup;
    EditText email,password;

    TextView signIn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        btn_signup=findViewById(R.id.btn_signup);
        email=findViewById(R.id.email);
        password=findViewById(R.id.password);
        signIn=findViewById(R.id.signIn);

        btn_signup.setOnClickListener(view -> {
            signUp(email.getText().toString().trim(),password.getText().toString().trim());

        });
        signIn.setOnClickListener(view -> {
            startActivity(new Intent(this,LoginActivity.class));
            finish();
        });
    }

    private void signUp(String e, String p) {
        RequestQueue queue = Volley.newRequestQueue(this);
        String Url=signup;

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
                                Toast.makeText(SignUpActivity.this, jsonObject.getString("message"), Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(SignUpActivity.this, LoginActivity.class));
                                finish();
                            }else {
                                Toast.makeText(SignUpActivity.this, jsonObject.getString("message"), Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException jsonException) {
                            jsonException.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("abcd",error.getMessage());
                        progressDialog.dismiss();
                        Toast.makeText(SignUpActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();

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