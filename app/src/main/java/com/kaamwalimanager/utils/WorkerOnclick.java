package com.kaamwalimanager.utils;

import org.json.JSONObject;

public interface WorkerOnclick {
    void onClick(JSONObject jsonObject);
    void onEdit(JSONObject jsonObject);
    void onPaySalary(JSONObject jsonObject,int amoumtToPay);
    void onLeave(JSONObject jsonObject);
    void onAdvance(JSONObject jsonObject);
}
