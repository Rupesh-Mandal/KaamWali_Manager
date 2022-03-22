package com.kaamwalimanager.activity;


import static com.kaamwalimanager.utils.Constant.delete_worker;
import static com.kaamwalimanager.utils.Constant.showData;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
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

public class DetaitlWorkerActivity extends AppCompatActivity {

    TextView name, gender, job, salary, start_date, end_date, advance, absent_days, previous_due;
    JSONObject workerObject = new JSONObject();
    String owner_id, worker_id;
    SharedPreferences sharedpreferences;

    Button delete;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detaitl_worker);
        sharedpreferences = getSharedPreferences("MyPREFERENCES", MODE_PRIVATE);
        String object = sharedpreferences.getString("data", "");
        String wObject = getIntent().getStringExtra("JSONObject");
        try {
            workerObject = new JSONObject(wObject);
            worker_id = workerObject.getString("worker_id");

            JSONObject jsonObject = new JSONObject(object);
            owner_id = jsonObject.getString("id");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        initView();
    }

    private void initView() {
        name = findViewById(R.id.name);
        gender = findViewById(R.id.gender);
        job = findViewById(R.id.job);
        salary = findViewById(R.id.salary);
        start_date = findViewById(R.id.start_date);
        end_date = findViewById(R.id.end_date);
        delete = findViewById(R.id.delete);

        advance = findViewById(R.id.advance);
        absent_days = findViewById(R.id.absent_days);
        previous_due = findViewById(R.id.previous_due);

        delete.setOnClickListener(view -> {
            deleteWorker();
        });

        try {
            name.setText(workerObject.getString("name"));
            gender.setText(workerObject.getString("gender"));
            job.setText(workerObject.getString("job"));
            salary.setText(workerObject.getString("salary"));
            start_date.setText(workerObject.getString("start_date"));
            end_date.setText(workerObject.getString("end_date"));

            loadData();
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    private void loadData() {
        RequestQueue queue = Volley.newRequestQueue(this);
        String Url = showData;

        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Please Waite");
        progressDialog.show();

        StringRequest sr = new StringRequest(Request.Method.POST, Url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.e("abcd", response);
                        progressDialog.dismiss();

                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            if (jsonObject.getString("result").equals("1")) {

                                JSONObject dataObject=jsonObject.getJSONObject("data");
                                advance.setText(dataObject.getString("advance"));
                                absent_days.setText(dataObject.getString("absent_days"));
                                previous_due.setText(dataObject.getString("previous_due"));

                            } else {
                                Toast.makeText(DetaitlWorkerActivity.this, jsonObject.getString("message"), Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException jsonException) {
                            jsonException.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("abcd", error.getMessage());
                        progressDialog.dismiss();
                        Toast.makeText(DetaitlWorkerActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();

                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("owner_id", owner_id);
                params.put("worker_id", worker_id);
                return params;
            }
        };
        queue.add(sr);

    }

    private void deleteWorker() {
        RequestQueue queue = Volley.newRequestQueue(this);
        String Url = delete_worker;

        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Please Waite");
        progressDialog.show();

        StringRequest sr = new StringRequest(Request.Method.POST, Url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.e("abcd", response);
                        progressDialog.dismiss();

                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            if (jsonObject.getString("result").equals("1")) {
                                Toast.makeText(DetaitlWorkerActivity.this, jsonObject.getString("message"), Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(DetaitlWorkerActivity.this,
                                        MainActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
                            } else {
                                Toast.makeText(DetaitlWorkerActivity.this, jsonObject.getString("message"), Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException jsonException) {
                            jsonException.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("abcd", error.getMessage());
                        progressDialog.dismiss();
                        Toast.makeText(DetaitlWorkerActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();

                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("owner_id", owner_id);
                params.put("worker_id", worker_id);
                return params;
            }
        };
        queue.add(sr);

    }
}