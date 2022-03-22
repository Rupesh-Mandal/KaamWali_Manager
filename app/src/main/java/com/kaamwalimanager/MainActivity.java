package com.kaamwalimanager;

import static com.kaamwalimanager.utils.Constant.add_advance;
import static com.kaamwalimanager.utils.Constant.add_leave;
import static com.kaamwalimanager.utils.Constant.getworker_list;
import static com.kaamwalimanager.utils.Constant.pay_salary;
import static com.kaamwalimanager.utils.Constant.showData;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.itextpdf.io.image.ImageData;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Image;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.property.HorizontalAlignment;
import com.itextpdf.layout.property.TextAlignment;
import com.kaamwalimanager.activity.AddWorkerActivity;
import com.kaamwalimanager.activity.DetaitlWorkerActivity;
import com.kaamwalimanager.activity.EditWorkerActivity;
import com.kaamwalimanager.adapter.WorkerAdapter;
import com.kaamwalimanager.auth.LoginActivity;
import com.kaamwalimanager.helper.PdfHelper;
import com.kaamwalimanager.splash.SplashActivity;
import com.kaamwalimanager.utils.WorkerOnclick;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {
    private final int storegePermision = 13;

    RecyclerView workerRecycler;
    ImageView add_worker;
    String owner_id;
    SharedPreferences sharedpreferences;
    private AdView mAdView;
    JSONObject reciptObject,workerObject;

    ImageView logout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        sharedpreferences = getSharedPreferences("MyPREFERENCES", MODE_PRIVATE);
        String object = sharedpreferences.getString("data", "");
        try {
            JSONObject jsonObject = new JSONObject(object);
            owner_id = jsonObject.getString("id");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        loadAd();

        workerRecycler = findViewById(R.id.workerRecycler);
        add_worker = findViewById(R.id.add_worker);
        logout = findViewById(R.id.logout);

        add_worker.setOnClickListener(view -> {
            startActivity(new Intent(this, AddWorkerActivity.class));
        });
        loadWorkerList();

        logout.setOnClickListener(view -> {
            try {
                new PdfHelper(this).creatTraxinvoice();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }

//            sharedpreferences.edit().clear().commit();
//            Intent intent = new Intent(MainActivity.this,
//                    SplashActivity.class);
//            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//            startActivity(intent);
//            finish();
        });
    }

    private void loadAd() {
        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });

        mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
    }

    private void loadWorkerList() {
        RequestQueue queue = Volley.newRequestQueue(this);
        String Url = getworker_list;

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
                                JSONArray jsonArray = jsonObject.getJSONArray("data");
                                setWorker(jsonArray);
                            } else {
                                Toast.makeText(MainActivity.this, jsonObject.getString("message"), Toast.LENGTH_SHORT).show();
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
                        Toast.makeText(MainActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();

                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("owner_id", owner_id);
                return params;
            }
        };
        queue.add(sr);
    }
    private void showSalaryDailog(String previous_due,String worker_id,JSONObject workerJSONObject) {
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(MainActivity.this, R.style.bottom_shee_dailog_theam);
        View v = LayoutInflater.from(MainActivity.this).
                inflate(R.layout.pay_salary_bottomsheet, (ConstraintLayout) findViewById(R.id.bottom_sheet_layout));
        bottomSheetDialog.setContentView(v);

        EditText pay_amt;
        TextView date;
        Button paySalary;

        pay_amt = v.findViewById(R.id.pay_amt);
        date = v.findViewById(R.id.date);
        paySalary = v.findViewById(R.id.paySalary);
        date.setText(getTodaysDate(0));

        pay_amt.setText(previous_due);

        date.setOnClickListener(view -> {
            Calendar cal = Calendar.getInstance();
            int year = cal.get(Calendar.YEAR);
            int month = cal.get(Calendar.MONTH);
            int day = cal.get(Calendar.DAY_OF_MONTH);

            int style = AlertDialog.THEME_HOLO_LIGHT;

            DatePickerDialog datePickerDialog = new DatePickerDialog(MainActivity.this, style, new DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker datePicker, int years, int months, int days) {
                    date.setText(years + "-" + months + "-" + days);
                }
            }, year, month, day);
            datePickerDialog.show();
        });


        String finalWorker_id = worker_id;
        paySalary.setOnClickListener(view1 -> {
            if (pay_amt.getText().toString().trim().isEmpty()) {
                pay_amt.setError("Please provide amount");
                pay_amt.requestFocus();
            } else {
                addSalary(finalWorker_id, date.getText().toString().trim(), pay_amt.getText().toString().trim(),workerJSONObject);
                bottomSheetDialog.dismiss();
            }
        });


        bottomSheetDialog.show();
    }


    private void setWorker(JSONArray jsonArray) {
        WorkerAdapter workerAdapter = new WorkerAdapter(this, jsonArray, new WorkerOnclick() {
            @Override
            public void onClick(JSONObject jsonObject) {
                Intent intent = new Intent(MainActivity.this, DetaitlWorkerActivity.class);
                intent.putExtra("JSONObject", jsonObject.toString());
                startActivity(intent);
            }

            @Override
            public void onEdit(JSONObject jsonObject) {
                Intent intent = new Intent(MainActivity.this, EditWorkerActivity.class);
                intent.putExtra("JSONObject", jsonObject.toString());
                startActivity(intent);
            }

            @Override
            public void onPaySalary(JSONObject jsonObject,int amoumtToPay) {
                String worker_id = "";
                try {
                    worker_id = jsonObject.getString("worker_id");
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                showSalaryDailog(String.valueOf(amoumtToPay),worker_id,jsonObject);

            }

            @Override
            public void onLeave(JSONObject jsonObject) {
                String worker_id = "";
                try {
                    worker_id = jsonObject.getString("worker_id");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(MainActivity.this, R.style.bottom_shee_dailog_theam);
                View v = LayoutInflater.from(MainActivity.this).
                        inflate(R.layout.add_leave_bottomsheet, (ConstraintLayout) findViewById(R.id.bottom_sheet_layout));
                bottomSheetDialog.setContentView(v);

                TextView start_date = v.findViewById(R.id.start_date);
                TextView end_date = v.findViewById(R.id.end_date);
                TextView total_days = v.findViewById(R.id.total_days);
                Button addLeaveBtn=v.findViewById(R.id.addLeave);
                String finalWorker_id = worker_id;
                addLeaveBtn.setOnClickListener(view -> {
                    addLeave(finalWorker_id,total_days.getText().toString().trim(),start_date.getText().toString().trim(), end_date.getText().toString().trim());
                    bottomSheetDialog.dismiss();

                });

                start_date.setText(getTodaysDate(0));
                end_date.setText(getTodaysDate(0));

                try {
                    total_days.setText(getTotalDate(start_date.getText().toString().trim(), end_date.getText().toString().trim()));
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                start_date.setOnClickListener(view -> {
                    Calendar cal = Calendar.getInstance();
                    int year = cal.get(Calendar.YEAR);
                    int month = cal.get(Calendar.MONTH);
                    int day = cal.get(Calendar.DAY_OF_MONTH);

                    int style = AlertDialog.THEME_HOLO_LIGHT;

                    DatePickerDialog datePickerDialog = new DatePickerDialog(MainActivity.this, style, new DatePickerDialog.OnDateSetListener() {
                        @Override
                        public void onDateSet(DatePicker datePicker, int years, int months, int days) {
                            start_date.setText(years + "-" + months + "-" + days);

                            try {
                                total_days.setText(getTotalDate(start_date.getText().toString().trim(), end_date.getText().toString().trim()));
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
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

                    DatePickerDialog datePickerDialog = new DatePickerDialog(MainActivity.this, style, new DatePickerDialog.OnDateSetListener() {
                        @Override
                        public void onDateSet(DatePicker datePicker, int years, int months, int days) {
                            end_date.setText(years + "-" + months + "-" + days);

                            try {
                                total_days.setText(getTotalDate(start_date.getText().toString().trim(), end_date.getText().toString().trim()));
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                        }
                    }, year, month, day);
                    datePickerDialog.show();
                });
                bottomSheetDialog.show();
            }

            @Override
            public void onAdvance(JSONObject jsonObject) {
                String worker_id = "";
                try {
                    worker_id = jsonObject.getString("worker_id");
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(MainActivity.this, R.style.bottom_shee_dailog_theam);
                View v = LayoutInflater.from(MainActivity.this).
                        inflate(R.layout.pay_advance_bottomsheet, (ConstraintLayout) findViewById(R.id.bottom_sheet_layout));
                bottomSheetDialog.setContentView(v);

                EditText advance_amt;
                TextView date;
                Button payAdvance;

                advance_amt = v.findViewById(R.id.advance_amt);
                date = v.findViewById(R.id.date);
                payAdvance = v.findViewById(R.id.payAdvance);
                date.setText(getTodaysDate(0));

                date.setOnClickListener(view -> {
                    Calendar cal = Calendar.getInstance();
                    int year = cal.get(Calendar.YEAR);
                    int month = cal.get(Calendar.MONTH);
                    int day = cal.get(Calendar.DAY_OF_MONTH);

                    int style = AlertDialog.THEME_HOLO_LIGHT;

                    DatePickerDialog datePickerDialog = new DatePickerDialog(MainActivity.this, style, new DatePickerDialog.OnDateSetListener() {
                        @Override
                        public void onDateSet(DatePicker datePicker, int years, int months, int days) {
                            date.setText(years + "-" + months + "-" + days);
                        }
                    }, year, month, day);
                    datePickerDialog.show();
                });


                String finalWorker_id = worker_id;
                payAdvance.setOnClickListener(view1 -> {
                    if (advance_amt.getText().toString().trim().isEmpty()) {
                        advance_amt.setError("Please provide amount");
                        advance_amt.requestFocus();
                    } else {
                        addAdvance(finalWorker_id, date.getText().toString().trim(), advance_amt.getText().toString().trim());
                        bottomSheetDialog.dismiss();
                    }
                });


                bottomSheetDialog.show();
            }
        });
        workerRecycler.setLayoutManager(new GridLayoutManager(this, 1));
        workerRecycler.setAdapter(workerAdapter);
    }

    private void addSalary(String worker_id, String date, String pay_amt, JSONObject workerJSONObject) {

        RequestQueue queue = Volley.newRequestQueue(this);
        String Url = pay_salary;

        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Please Waite");
        progressDialog.show();

        StringRequest sr = new StringRequest(Request.Method.POST, Url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.e("abcd", response);
                        progressDialog.dismiss();
                        loadWorkerList();
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            if (jsonObject.getString("result").equals("1")) {
                                Toast.makeText(MainActivity.this, jsonObject.getString("message"), Toast.LENGTH_SHORT).show();
                                reciptObject=jsonObject.getJSONObject("data");
                                workerObject=workerJSONObject;
                                if (checkPermissionForGallery()){
                                    try {
                                        generateReceipt(jsonObject.getJSONObject("data"),workerJSONObject);
                                    } catch (FileNotFoundException e) {
                                        e.printStackTrace();
                                    }
                                }
                            } else {
                                Toast.makeText(MainActivity.this, jsonObject.getString("message"), Toast.LENGTH_SHORT).show();
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
                        Toast.makeText(MainActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();

                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("owner_id", owner_id);
                params.put("worker_id", worker_id);
                params.put("date", date);
                params.put("pay_amt", pay_amt);
                params.put("leave_flg", "1");
                return params;
            }
        };
        queue.add(sr);
    }

    private void addLeave(String worker_id,String total_days, String start_date, String end_date) {

        RequestQueue queue = Volley.newRequestQueue(this);
        String Url = add_leave;

        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Please Waite");
        progressDialog.show();

        StringRequest sr = new StringRequest(Request.Method.POST, Url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.e("abcd", response);
                        progressDialog.dismiss();
                        loadWorkerList();

                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            if (jsonObject.getString("result").equals("1")) {
                                Toast.makeText(MainActivity.this, jsonObject.getString("message"), Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(MainActivity.this, jsonObject.getString("message"), Toast.LENGTH_SHORT).show();
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
                        Toast.makeText(MainActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();

                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("owner_id", owner_id);
                params.put("worker_id", worker_id);
                params.put("total_days", total_days);
                params.put("start_date", start_date);
                params.put("end_date", end_date);
                return params;
            }
        };
        queue.add(sr);

    }

    private void addAdvance(String worker_id, String date, String advance_amt) {

        RequestQueue queue = Volley.newRequestQueue(this);
        String Url = add_advance;

        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Please Waite");
        progressDialog.show();

        StringRequest sr = new StringRequest(Request.Method.POST, Url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.e("abcd", response);
                        progressDialog.dismiss();
                        loadWorkerList();

                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            if (jsonObject.getString("result").equals("1")) {
                                Toast.makeText(MainActivity.this, jsonObject.getString("message"), Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(MainActivity.this, jsonObject.getString("message"), Toast.LENGTH_SHORT).show();
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
                        Toast.makeText(MainActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();

                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("owner_id", owner_id);
                params.put("worker_id", worker_id);
                params.put("date", date);
                params.put("advance_amt", advance_amt);
                return params;
            }
        };
        queue.add(sr);
    }


    private String getTodaysDate(int i) {
        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH) + i;
        month = month + 1;
        int day = cal.get(Calendar.DAY_OF_MONTH);
        return year + "-" + month + "-" + day;
    }

    private String getTotalDate(String start_date, String end_date) throws ParseException {
        Date date1 = new SimpleDateFormat("yyyy-MM-dd").parse(start_date);
        Date date2 = new SimpleDateFormat("yyyy-MM-dd").parse(end_date);
        long totalDay = date2.getTime() - date1.getTime();
        return String.valueOf(TimeUnit.DAYS.convert(totalDay, TimeUnit.MILLISECONDS)+1);
    }


    private void generateReceipt(JSONObject data,JSONObject workerJSONObject) throws JSONException, FileNotFoundException {
        String name=workerJSONObject.getString("name");
        String worker_id=workerJSONObject.getString("worker_id");

        String myPath= Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString();
        File file=new File(myPath,name+"_"+worker_id+"_receipt.pdf");

        OutputStream outputStream=new FileOutputStream(file);

        PdfWriter writer=new PdfWriter(String.valueOf(file));
        PdfDocument pdfDocument=new PdfDocument(writer);
        Document document=new Document(pdfDocument);

        pdfDocument.setDefaultPageSize(PageSize.A6);
        document.setMargins(0,0,0,0);

        Drawable d=getDrawable(R.drawable.logo);
        Bitmap bitmap=((BitmapDrawable)d).getBitmap();
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG,100,stream);
        byte[] bitmapData= stream.toByteArray();
        ImageData imageData = ImageDataFactory.create(bitmapData);
        Image image=new Image(imageData).setHorizontalAlignment(HorizontalAlignment.CENTER);

        Paragraph paragraph=new Paragraph("Salary Receipt of "+name).setBold().setFontSize(20).setTextAlignment(TextAlignment.CENTER);

        float[] with={100f,100f};
        Table table=new Table(with);
        table.setHorizontalAlignment(HorizontalAlignment.CENTER);

        table.addCell(new Cell().add(new Paragraph("pay_amount")));
        table.addCell(new Cell().add(new Paragraph(data.getString("pay_amount"))));

        table.addCell(new Cell().add(new Paragraph("leave_charges")));
        table.addCell(new Cell().add(new Paragraph(data.getString("leave_charges"))));

        table.addCell(new Cell().add(new Paragraph("advance")));
        table.addCell(new Cell().add(new Paragraph(data.getString("advance"))));

        table.addCell(new Cell().add(new Paragraph("due_amount")));
        table.addCell(new Cell().add(new Paragraph(data.getString("due_amount"))));

        table.addCell(new Cell().add(new Paragraph("total_pay")));
        table.addCell(new Cell().add(new Paragraph(data.getString("total_pay"))));

        document.add(image);
        document.add(paragraph);
        document.add(table);

        document.close();
        Toast.makeText(this, "Please check Download folder for pdf", Toast.LENGTH_LONG).show();

    }

    private boolean checkPermissionForGallery() {
        int result = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (result == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, storegePermision);
            return false;
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case storegePermision:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    try {
                        generateReceipt(reciptObject,workerObject);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                } else {
                    checkPermissionForGallery();
                }
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

}