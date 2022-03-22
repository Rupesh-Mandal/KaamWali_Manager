package com.kaamwalimanager.activity;

import static com.kaamwalimanager.utils.Constant.addworker;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

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
import android.widget.Spinner;
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
import com.kaamwalimanager.auth.LoginActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class AddWorkerActivity extends AppCompatActivity {

    EditText name, salary;
    Spinner job;
    TextView start_date, end_date;
    String owner_id;
    SharedPreferences sharedpreferences;

    String[] jobList = {"All Purpose", "Cooking", "Cleanning", "Washing Clothes", "Washing utensils", "Other Work"};

    AppCompatButton add_worker;

    RadioGroup genderRadioG;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_worker);
        sharedpreferences = getSharedPreferences("MyPREFERENCES", MODE_PRIVATE);
        String object = sharedpreferences.getString("data", "");
        try {
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
        job = (Spinner) findViewById(R.id.job);
        start_date = findViewById(R.id.start_date);
        end_date = findViewById(R.id.end_date);
        add_worker = findViewById(R.id.add_worker);

        add_worker.setOnClickListener(view -> {
            if (isValid()) {
                addWorker();
            }
        });

        start_date.setText(getTodaysDate(0));
        end_date.setText(getTodaysDate(1));

        // which hold the data as the list item.
        ArrayAdapter<String> jobListAdapter = new ArrayAdapter<String>(this, android.R.layout.select_dialog_item, jobList);

        genderRadioG.check(R.id.Male);

        job.setAdapter(jobListAdapter);

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

    private void addWorker() {
        RequestQueue queue = Volley.newRequestQueue(this);
        String Url = addworker;

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
                                Toast.makeText(AddWorkerActivity.this, jsonObject.getString("message"), Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(AddWorkerActivity.this,
                                        MainActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
                            } else {
                                Toast.makeText(AddWorkerActivity.this, jsonObject.getString("message"), Toast.LENGTH_SHORT).show();
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
                        Toast.makeText(AddWorkerActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();

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
                params.put("job", job.getSelectedItem().toString());
                params.put("salary", salary.getText().toString().trim());
                params.put("start_date", start_date.getText().toString().trim());
                params.put("end_date", end_date.getText().toString().trim());
                params.put("owner_id", owner_id);
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


                return true;


            }
        }
    }


    private String getTodaysDate(int i) {
        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH) + i;
        month = month + 1;
        int day = cal.get(Calendar.DAY_OF_MONTH);
        return year + "-" + month + "-" + day;
    }
}