package com.kaamwalimanager.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.kaamwalimanager.R;
import com.kaamwalimanager.utils.WorkerOnclick;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class WorkerAdapter extends RecyclerView.Adapter<WorkerAdapter.myViewHolder> {

    Context context;
    JSONArray jsonArray;
    WorkerOnclick workerOnclick;

    public WorkerAdapter(Context context, JSONArray jsonArray, WorkerOnclick workerOnclick) {
        this.context = context;
        this.jsonArray = jsonArray;
        this.workerOnclick = workerOnclick;
    }

    @NonNull
    @Override
    public myViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new myViewHolder(LayoutInflater.from(context).inflate(R.layout.worker_item,null,false));
    }

    @Override
    public void onBindViewHolder(@NonNull myViewHolder holder, int position) {
        JSONObject jsonObject=new JSONObject();
        int advance,absent_days,salary;
        int amoumtToPay = 0;
        try {
            jsonObject=jsonArray.getJSONObject(position);
            holder.name.setText(jsonObject.getString("name"));
            if (jsonObject.getString("gender").equals("Female")){
                holder.genderImg.setImageResource(R.drawable.female);
            }
            salary= Integer.parseInt(jsonObject.getString("salary"));
            absent_days=jsonObject.getInt("absent_days");
            advance=jsonObject.getInt("advance");

            int perDaySalary=salary/30;
            int presentDay=30-absent_days;

            int beforAdvance=presentDay*perDaySalary;

            amoumtToPay=beforAdvance-advance;

            holder.salary.setText("Rs."+amoumtToPay);
            holder.absent_days.setText("Total Absent days: "+absent_days);

        } catch (JSONException e) {
            e.printStackTrace();
        }


        JSONObject finalJsonObject = jsonObject;
        holder.itemView.setOnClickListener(view -> {
            workerOnclick.onClick(finalJsonObject);
        });
        holder.edit.setOnClickListener(view -> {
            workerOnclick.onEdit(finalJsonObject);
        });
        int finalAmoumtToPay = amoumtToPay;
        holder.paySalary.setOnClickListener(view -> {
            workerOnclick.onPaySalary(finalJsonObject, finalAmoumtToPay);
        });
        holder.leave.setOnClickListener(view -> {
            workerOnclick.onLeave(finalJsonObject);
        });
        holder.advance.setOnClickListener(view -> {
            workerOnclick.onAdvance(finalJsonObject);
        });

    }

    @Override
    public int getItemCount() {
        return jsonArray.length();
    }

    class myViewHolder extends RecyclerView.ViewHolder {
        TextView name,salary,absent_days;
        Button paySalary,leave,advance;

        ImageView edit,genderImg;
        public myViewHolder(@NonNull View itemView) {
            super(itemView);
            name=itemView.findViewById(R.id.name);
            salary=itemView.findViewById(R.id.salary);
            edit=itemView.findViewById(R.id.edit);
            paySalary=itemView.findViewById(R.id.paySalary);
            leave=itemView.findViewById(R.id.leave);
            advance=itemView.findViewById(R.id.advance);
            genderImg=itemView.findViewById(R.id.genderImg);
            absent_days=itemView.findViewById(R.id.absent_days);
        }
    }
}
