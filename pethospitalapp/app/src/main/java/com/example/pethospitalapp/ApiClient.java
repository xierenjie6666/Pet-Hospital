package com.example.pethospitalapp;

import android.content.Context;
import android.content.SharedPreferences;
import android.widget.Toast;
import okhttp3.*;
import org.json.JSONObject;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class ApiClient {
    // 请替换为您的实际IP地址和端口
    private static final String BASE_URL = ApiConfig.BASE_URL + "/api";
    private static OkHttpClient client;
    private static SharedPreferences preferences;

    // 初始化
    public static void init(Context context) {
        client = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .build();

        preferences = context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE);
    }

    // 获取认证token
    private static String getAuthToken() {
        return preferences.getString("auth_token", "");
    }

    // 通用POST请求方法
    public static void postRequest(String endpoint, JSONObject jsonData, Callback callback) {
        String url = BASE_URL + endpoint;

        RequestBody body = RequestBody.create(
                jsonData.toString(),
                MediaType.parse("application/json; charset=utf-8"));

        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .addHeader("Content-Type", "application/json")
                .addHeader("Authorization", "Bearer " + getAuthToken())
                .build();

        client.newCall(request).enqueue(callback);
    }

    // 通用GET请求方法
    public static void getRequest(String endpoint, Callback callback) {
        String url = BASE_URL + endpoint;

        Request request = new Request.Builder()
                .url(url)
                .get()
                .addHeader("Authorization", "Bearer " + getAuthToken())
                .build();

        client.newCall(request).enqueue(callback);
    }

    // 通用PUT请求方法
    public static void putRequest(String endpoint, JSONObject jsonData, Callback callback) {
        String url = BASE_URL + endpoint;

        RequestBody body = RequestBody.create(
                jsonData.toString(),
                MediaType.parse("application/json; charset=utf-8"));

        Request request = new Request.Builder()
                .url(url)
                .put(body)
                .addHeader("Content-Type", "application/json")
                .addHeader("Authorization", "Bearer " + getAuthToken())
                .build();

        client.newCall(request).enqueue(callback);
    }

    // 通用DELETE请求方法
    public static void deleteRequest(String endpoint, Callback callback) {
        String url = BASE_URL + endpoint;

        Request request = new Request.Builder()
                .url(url)
                .delete()
                .addHeader("Authorization", "Bearer " + getAuthToken())
                .build();

        client.newCall(request).enqueue(callback);
    }
}