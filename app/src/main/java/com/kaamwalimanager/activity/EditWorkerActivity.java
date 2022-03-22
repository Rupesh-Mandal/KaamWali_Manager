package com.kaamwalimanager.activity;

import static com.kaamwalimanager.utils.Constant.editworker;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioGroup;
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

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class EditWorkerActivity extends AppCompatActivity {


    EditText name, salary;
    AutoCompleteTextView job;
    TextView start_date, end_date;
    String owner_id, worker_id;
    SharedPreferences sharedpreferences;

    String[] jobList = {"All Purpose", "Cooking", "Cleanning", "Washing Clothes", "Washing utensils", "Other Work"};

    Button update_worker;
    RadioGroup genderRadioG;

    JSONObject workerObject = new JSONObject();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_worker);
        sharedpreferences = getSharedPreferences("MyPREFERENCES", MODE_PRIVATE);
        String object = sharedpreferences.getString("data", "");
        String wObject = getIntent().getStringExtra("JSONObject");
        try {
            workerObject = new JSONObject(wObject);

            JSONObject jsonObject = new JSONObject(object);
            owner_id = jsonObject.getString("id");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        initView();
    }

    private void initView() {
        name = findViewById(R.id.name);
        salary = findViewById(R.id.salary);
        genderRadioG = findViewById(R.id.genderRadioG);
        job = findViewById(R.id.job);
        start_date = findViewById(R.id.start_date);
        end_date = findViewById(R.id.end_date);
        update_worker = findViewById(R.id.update_worker);


        try {
            worker_id = workerObject.getString("worker_id");
            name.setText(workerObject.getString("name"));
            salary.setText(workerObject.getString("salary"));
            job.setText(workerObject.getString("job"));
            start_date.setText(workerObject.getString("start_date"));
            end_date.setText(workerObject.getString("end_date"));

            if (workerObject.getString("gender").equals("Male")) {
                genderRadioG.check(R.id.Male);
            } else {
                genderRadioG.check(R.id.Female);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        update_worker.setOnClickListener(view -> {
            if (isValid()) {
                upDateWorker();
            }
        });
        // which hold the data as the list item.
        ArrayAdapter<String> jobListAdapter = new ArrayAdapter<String>(this, android.R.layout.select_dialog_item, jobList);

        job.setThreshold(1);
        job.setAdapter(jobListAdapter);
        job.setTextColor(Color.BLACK);

        start_date.setOnClickListener(view -> {
            Calendar cal = Calendar.getInstance();
            int year = cal.get(Calendar.YEAR);
            int month = cal.get(Calendar.MONTH);
            int day = cal.get(Calendar.DAY_OF_MONTH);

            int style = AlertDialog.THEME_HOLO_LIGHT;

            DatePickerDialog datePickerDialog = new DatePickerDialog(this, style, new DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker datePicker, int years, int months, int days) {
                    start_date.setText(years + "-" + months + "-" + days);

                }
            }, year, month, day);
            datePickerDialog.show();

        });

        end_date.setOnClickListener(view -> {
            Calendar cal = Calendar.getInstance();
            int year = cal.get(Calendar.YEAR);
            int month = cal.get(Calendar.MONTH) + 1;
            int day = cal.get(Calendar.DAY_OF_MONTH);

            int style = AlertDialog.THEME_HOLO_LIGHT;

            DatePickerDialog datePickerDialog = new DatePickerDialog(this, style, new DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker datePicker, int years, int months, int days) {
                    end_date.setText(years + "-" + months + "-" + days);
                }
            }, year, month, day);
            datePickerDialog.show();

        });

    }

    private void upDateWorker() {
        RequestQueue queue = Volley.newRequestQueue(this);
        String Url = editworker;

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
                                Toast.makeText(EditWorkerActivity.this, jsonObject.getString("message"), Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(EditWorkerActivity.this,
                                        MainActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
                            } else {
                                Toast.makeText(EditWorkerActivity.this, jsonObject.getString("message"), Toast.LENGTH_SHORT).show();
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
                        Toast.makeText(EditWorkerActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();

                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                int selectedGender = genderRadioG.getCheckedRadioButtonId();
                String gender;
                if (selectedGender == R.id.Male) {
                    gender = "Male";
                } else {
                    gender = "Female";
                }


                Map<String, String> params = new HashMap<String, String>();
                params.put("name", name.getText().toString().trim());
                params.put("gender", gender);
                params.put("job", job.getText().toString().trim());
                params.put("salary", salary.getText().toString().trim());
                params.put("start_date", start_date.getText().toString().trim());
                params.put("end_date", end_date.getText().toString().trim());
                params.put("owner_id", owner_id);
                params.put("worker_id", worker_id);
                return params;
            }
        };
        queue.add(sr);

    }


    boolean isValid() {
        if (name.getText().toString().trim().isEmpty()) {
            name.setError("Please provide name");
            name.requestFocus();
            return false;
        } else {
            if (salary.getText().toString().trim().isEmpty()) {
                salary.setError("Please provide salary");
                salary.requestFocus();
                return false;
            } else {

                if (job.getText().toString().trim().isEmpty()) {
                    job.setError("Please provide job");
                    job.requestFocus();
                    return false;
                } else {
                    return true;
                }
            }

        }
    }


}