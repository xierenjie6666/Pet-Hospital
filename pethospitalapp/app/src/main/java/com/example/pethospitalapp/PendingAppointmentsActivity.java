package com.example.pethospitalapp;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.tabs.TabLayout;
import okhttp3.*;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import okhttp3.MediaType;
import okhttp3.RequestBody;

public class PendingAppointmentsActivity extends AppCompatActivity {

    private TabLayout tabLayout;
    private LinearLayout pendingContainer;
    private LinearLayout cancelledContainer;
    private TextView noPendingText;
    private TextView noCancelledText;

    private RecyclerView pendingRecyclerView;
    private RecyclerView cancelledRecyclerView;
    private PendingAppointmentAdapter pendingAdapter;
    private PendingAppointmentAdapter cancelledAdapter;

    private List<AppointmentItem> pendingList = new ArrayList<>();
    private List<AppointmentItem> cancelledList = new ArrayList<>();

    private OkHttpClient client = new OkHttpClient();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pending_appointments);

        // 设置Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("管理预约");
        }

        initializeViews();
        setupListeners();

        // 加载数据
        loadPendingAppointments();
        loadCancelledAppointments();
    }

    private void initializeViews() {
        tabLayout = findViewById(R.id.tabLayout);
        pendingContainer = findViewById(R.id.pendingContainer);
        cancelledContainer = findViewById(R.id.cancelledContainer);
        noPendingText = findViewById(R.id.noPendingText);
        noCancelledText = findViewById(R.id.noCancelledText);
        pendingRecyclerView = findViewById(R.id.pendingRecyclerView);
        cancelledRecyclerView = findViewById(R.id.cancelledRecyclerView);

        // 设置RecyclerView
        pendingRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        cancelledRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        pendingAdapter = new PendingAppointmentAdapter(pendingList, true, new PendingAppointmentAdapter.OnActionListener() {
            @Override
            public void onConfirm(int id) {
                updateAppointmentStatus(id, "CONFIRMED");
            }

            @Override
            public void onReject(int id) {
                updateAppointmentStatus(id, "REJECTED");
            }
        });

        cancelledAdapter = new PendingAppointmentAdapter(cancelledList, true, new PendingAppointmentAdapter.OnActionListener() {
            @Override
            public void onConfirm(int id) {
                updateAppointmentStatus(id, "CONFIRMED");
            }

            @Override
            public void onReject(int id) {
                updateAppointmentStatus(id, "REJECTED");
            }
        });

        pendingRecyclerView.setAdapter(pendingAdapter);
        cancelledRecyclerView.setAdapter(cancelledAdapter);
    }

    private void setupListeners() {
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                int position = tab.getPosition();
                if (position == 0) {
                    pendingContainer.setVisibility(View.VISIBLE);
                    cancelledContainer.setVisibility(View.GONE);
                } else {
                    pendingContainer.setVisibility(View.GONE);
                    cancelledContainer.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {}

            @Override
            public void onTabReselected(TabLayout.Tab tab) {}
        });
    }

    private void loadPendingAppointments() {
        String url = ApiConfig.APPOINTMENTS + "/pending";

        noPendingText.setVisibility(View.VISIBLE);
        noPendingText.setText("加载中...");
        pendingRecyclerView.setVisibility(View.GONE);

        Request request = new Request.Builder().url(url).get().build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(() -> {
                    noPendingText.setVisibility(View.VISIBLE);
                    pendingRecyclerView.setVisibility(View.GONE);
                    noPendingText.setText("网络错误：" + e.getMessage());
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String json = response.body().string();
                runOnUiThread(() -> {
                    try {
                        List<AppointmentItem> items = parseAppointments(json);
                        pendingList.clear();

                        for (AppointmentItem item : items) {
                            if ("PENDING".equals(item.status)) {
                                pendingList.add(item);
                            }
                        }

                        if (!pendingList.isEmpty()) {
                            pendingAdapter.notifyDataSetChanged();
                            pendingRecyclerView.setVisibility(View.VISIBLE);
                            noPendingText.setVisibility(View.GONE);
                        } else {
                            pendingRecyclerView.setVisibility(View.GONE);
                            noPendingText.setVisibility(View.VISIBLE);
                            noPendingText.setText("暂无待处理预约");
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        pendingRecyclerView.setVisibility(View.GONE);
                        noPendingText.setVisibility(View.VISIBLE);
                        noPendingText.setText("解析错误：" + e.getMessage());
                    }
                });
            }
        });
    }

    private void loadCancelledAppointments() {
        // 使用获取所有预约的API
        String url = ApiConfig.APPOINTMENTS + "/all";

        noCancelledText.setVisibility(View.VISIBLE);
        noCancelledText.setText("加载中...");
        cancelledRecyclerView.setVisibility(View.GONE);

        Request request = new Request.Builder().url(url).get().build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(() -> {
                    android.util.Log.e("PendingAppointments", "加载待撤销失败: " + e.getMessage());
                    cancelledRecyclerView.setVisibility(View.GONE);
                    noCancelledText.setVisibility(View.VISIBLE);
                    noCancelledText.setText("网络错误：" + e.getMessage());
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String json = response.body().string();
                android.util.Log.d("PendingAppointments", "待撤销API响应: " + json);

                runOnUiThread(() -> {
                    try {
                        JSONObject root = new JSONObject(json);
                        JSONArray array = null;

                        // 根据实际返回的JSON格式，数据在 "user" 字段中
                        if (root.has("user")) {
                            Object userObj = root.get("user");
                            if (userObj instanceof JSONArray) {
                                array = (JSONArray) userObj;
                                android.util.Log.d("PendingAppointments", "从user字段获取到数组，长度: " + array.length());
                            }
                        } else if (root.has("data")) {
                            Object dataObj = root.get("data");
                            if (dataObj instanceof JSONArray) {
                                array = (JSONArray) dataObj;
                            }
                        } else if (root.has("list")) {
                            array = root.getJSONArray("list");
                        }

                        cancelledList.clear();

                        if (array != null && array.length() > 0) {
                            for (int i = 0; i < array.length(); i++) {
                                JSONObject obj = array.getJSONObject(i);
                                String status = obj.optString("status", "");

                                android.util.Log.d("PendingAppointments", "预约ID: " + obj.optInt("id") +
                                        ", 状态: " + status +
                                        ", 宠物: " + obj.optString("petName"));

                                // 检查是否为CANCELLED状态
                                if ("CANCELLED".equals(status)) {
                                    AppointmentItem item = new AppointmentItem();
                                    item.id = obj.optInt("id", 0);
                                    item.petName = obj.optString("petName", "");
                                    item.petType = obj.optString("petType", "");
                                    item.serviceType = obj.optString("serviceType", "");
                                    item.doctor = obj.optString("doctor", "");
                                    item.appointmentDate = obj.optString("appointmentDate", "");
                                    item.appointmentTime = obj.optString("appointmentTime", "");
                                    item.symptoms = obj.optString("symptoms", "");
                                    item.status = obj.optString("status", "");
                                    item.isCancelled = obj.optInt("isCancelled", 0);
                                    cancelledList.add(item);
                                    android.util.Log.d("PendingAppointments", "添加CANCELLED预约: " + item.id + ", 宠物: " + item.petName);
                                }
                            }
                        }

                        android.util.Log.d("PendingAppointments", "最终CANCELLED预约数量: " + cancelledList.size());

                        if (!cancelledList.isEmpty()) {
                            cancelledAdapter.notifyDataSetChanged();
                            cancelledRecyclerView.setVisibility(View.VISIBLE);
                            noCancelledText.setVisibility(View.GONE);
                        } else {
                            cancelledRecyclerView.setVisibility(View.GONE);
                            noCancelledText.setVisibility(View.VISIBLE);
                            noCancelledText.setText("暂无待撤销预约");
                        }

                    } catch (Exception e) {
                        android.util.Log.e("PendingAppointments", "解析错误: " + e.getMessage(), e);
                        cancelledRecyclerView.setVisibility(View.GONE);
                        noCancelledText.setVisibility(View.VISIBLE);
                        noCancelledText.setText("解析错误：" + e.getMessage());
                    }
                });
            }
        });
    }

    private AppointmentItem parseAppointmentItem(JSONObject obj) {
        AppointmentItem item = new AppointmentItem();
        item.id = obj.optInt("id", 0);
        item.petName = obj.optString("petName", "");
        item.petType = obj.optString("petType", "");
        item.serviceType = obj.optString("serviceType", "");
        item.doctor = obj.optString("doctor", "");
        item.appointmentDate = obj.optString("appointmentDate", "");
        item.appointmentTime = obj.optString("appointmentTime", "");
        item.symptoms = obj.optString("symptoms", "");
        item.status = obj.optString("status", "");
        item.isCancelled = obj.optInt("isCancelled", 0);
        return item;
    }

    private void loadCancelledAppointmentsAlternative() {
        // 备用方案：尝试使用现有的pending端点但不过滤
        String url = ApiConfig.APPOINTMENTS + "/pending";

        Request request = new Request.Builder().url(url).get().build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(() -> {
                    cancelledRecyclerView.setVisibility(View.GONE);
                    noCancelledText.setVisibility(View.VISIBLE);
                    noCancelledText.setText("无法加载待撤销预约");
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String json = response.body().string();
                runOnUiThread(() -> {
                    try {
                        // 尝试从响应中获取CANCELLED状态的数据
                        JSONObject root = new JSONObject(json);
                        JSONArray array = null;

                        // 安全地获取JSONArray
                        if (root.has("data")) {
                            Object dataObj = root.get("data");
                            if (dataObj instanceof JSONArray) {
                                array = (JSONArray) dataObj;
                            } else if (dataObj instanceof JSONObject) {
                                JSONObject dataObj2 = (JSONObject) dataObj;
                                if (dataObj2.has("cancelled")) {
                                    Object cancelledObj = dataObj2.get("cancelled");
                                    if (cancelledObj instanceof JSONArray) {
                                        array = (JSONArray) cancelledObj;
                                    }
                                }
                            }
                        }

                        cancelledList.clear();
                        if (array != null && array.length() > 0) {
                            for (int i = 0; i < array.length(); i++) {
                                JSONObject obj = array.getJSONObject(i);
                                AppointmentItem item = new AppointmentItem();
                                item.id = obj.optInt("id", 0);
                                item.petName = obj.optString("petName", "");
                                item.petType = obj.optString("petType", "");
                                item.serviceType = obj.optString("serviceType", "");
                                item.doctor = obj.optString("doctor", "");
                                item.appointmentDate = obj.optString("appointmentDate", "");
                                item.appointmentTime = obj.optString("appointmentTime", "");
                                item.symptoms = obj.optString("symptoms", "");
                                item.status = obj.optString("status", "");
                                item.isCancelled = obj.optInt("isCancelled", 0);
                                cancelledList.add(item);
                            }
                        }

                        if (!cancelledList.isEmpty()) {
                            cancelledAdapter.notifyDataSetChanged();
                            cancelledRecyclerView.setVisibility(View.VISIBLE);
                            noCancelledText.setVisibility(View.GONE);
                        } else {
                            cancelledRecyclerView.setVisibility(View.GONE);
                            noCancelledText.setVisibility(View.VISIBLE);
                            noCancelledText.setText("暂无待撤销预约");
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        cancelledRecyclerView.setVisibility(View.GONE);
                        noCancelledText.setVisibility(View.VISIBLE);
                        noCancelledText.setText("暂无待撤销预约");
                    }
                });
            }
        });
    }

    private List<AppointmentItem> parseAppointments(String json) throws Exception {
        List<AppointmentItem> items = new ArrayList<>();
        JSONObject root = new JSONObject(json);

        JSONArray array = null;

        // 安全地解析各种可能的JSON格式
        if (root.has("data")) {
            Object dataObj = root.get("data");
            if (dataObj instanceof JSONArray) {
                array = (JSONArray) dataObj;
            } else if (dataObj instanceof JSONObject) {
                JSONObject dataObj2 = (JSONObject) dataObj;
                if (dataObj2.has("list") && dataObj2.get("list") instanceof JSONArray) {
                    array = dataObj2.getJSONArray("list");
                }
            }
        } else if (root.has("user")) {
            Object userObj = root.get("user");
            if (userObj instanceof JSONArray) {
                array = (JSONArray) userObj;
            } else if (userObj instanceof JSONObject) {
                JSONObject userObj2 = (JSONObject) userObj;
                if (userObj2.has("list") && userObj2.get("list") instanceof JSONArray) {
                    array = userObj2.getJSONArray("list");
                }
            }
        } else if (root.has("list") && root.get("list") instanceof JSONArray) {
            array = root.getJSONArray("list");
        } else if (root.has("appointments") && root.get("appointments") instanceof JSONArray) {
            array = root.getJSONArray("appointments");
        }

        // 如果找不到数组，尝试检查是否整个响应就是一个数组
        if (array == null) {
            try {
                array = new JSONArray(json);
            } catch (Exception e) {
                // 如果不是数组，保持null
            }
        }

        if (array != null) {
            for (int i = 0; i < array.length(); i++) {
                JSONObject obj = array.getJSONObject(i);
                AppointmentItem item = new AppointmentItem();
                item.id = obj.optInt("id", 0);
                item.petName = obj.optString("petName", "");
                item.petType = obj.optString("petType", "");
                item.serviceType = obj.optString("serviceType", "");
                item.doctor = obj.optString("doctor", "");
                item.appointmentDate = obj.optString("appointmentDate", "");
                item.appointmentTime = obj.optString("appointmentTime", "");
                item.symptoms = obj.optString("symptoms", "");
                item.status = obj.optString("status", "");
                item.isCancelled = obj.optInt("isCancelled", 0);
                items.add(item);
            }
        }

        return items;
    }

    private void updateAppointmentStatus(int id, String status) {
        // 尝试多个可能的API端点
        String url = ApiConfig.APPOINTMENTS + "/update";

        // 根据后端实际接受的参数格式来构建请求
        JSONObject json = new JSONObject();
        try {
            json.put("id", id);
            json.put("status", status);
            // 注意：不修改 is_cancelled 字段
        } catch (Exception e) {
            e.printStackTrace();
        }

        android.util.Log.d("PendingAppointments", "更新预约URL: " + url);
        android.util.Log.d("PendingAppointments", "请求参数: " + json.toString());

        RequestBody body = RequestBody.create(
                MediaType.parse("application/json; charset=utf-8"),
                json.toString()
        );

        Request request = new Request.Builder()
                .url(url)
                .post(body)  // 使用POST方法
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(() -> {
                    android.util.Log.e("PendingAppointments", "操作失败: " + e.getMessage());
                    Toast.makeText(PendingAppointmentsActivity.this,
                            "操作失败：" + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String responseBody = response.body().string();
                runOnUiThread(() -> {
                    android.util.Log.d("PendingAppointments", "响应码: " + response.code());
                    android.util.Log.d("PendingAppointments", "响应内容: " + responseBody);

                    if (response.isSuccessful()) {
                        Toast.makeText(PendingAppointmentsActivity.this, "操作成功", Toast.LENGTH_SHORT).show();
                        // 刷新两个列表
                        loadPendingAppointments();
                        loadCancelledAppointments();
                    } else {
                        Toast.makeText(PendingAppointmentsActivity.this,
                                "操作失败：" + response.code(), Toast.LENGTH_SHORT).show();

                        // 如果POST失败，尝试PUT方法
                        updateAppointmentStatusWithPut(id, status);
                    }
                });
            }
        });
    }

    // 备用方案：使用PUT方法
    private void updateAppointmentStatusWithPut(int id, String status) {
        String url = ApiConfig.APPOINTMENTS + "/" + id;

        JSONObject json = new JSONObject();
        try {
            json.put("status", status);
        } catch (Exception e) {
            e.printStackTrace();
        }

        android.util.Log.d("PendingAppointments", "尝试PUT方法: " + url);

        RequestBody body = RequestBody.create(
                MediaType.parse("application/json; charset=utf-8"),
                json.toString()
        );

        Request request = new Request.Builder()
                .url(url)
                .put(body)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(() -> {
                    Toast.makeText(PendingAppointmentsActivity.this,
                            "操作失败：" + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String responseBody = response.body().string();
                runOnUiThread(() -> {
                    android.util.Log.d("PendingAppointments", "PUT响应码: " + response.code());
                    android.util.Log.d("PendingAppointments", "PUT响应内容: " + responseBody);

                    if (response.isSuccessful()) {
                        Toast.makeText(PendingAppointmentsActivity.this, "操作成功", Toast.LENGTH_SHORT).show();
                        loadPendingAppointments();
                        loadCancelledAppointments();
                    } else {
                        // 如果还是失败，尝试使用GET方式的特殊端点
                        updateAppointmentStatusWithGet(id, status);
                    }
                });
            }
        });
    }

    // 第三种方案：使用GET请求的特殊端点
    private void updateAppointmentStatusWithGet(int id, String status) {
        String url = ApiConfig.APPOINTMENTS + "/updateStatus?id=" + id + "&status=" + status;

        android.util.Log.d("PendingAppointments", "尝试GET方法: " + url);

        Request request = new Request.Builder()
                .url(url)
                .get()
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(() -> {
                    Toast.makeText(PendingAppointmentsActivity.this,
                            "所有更新方式都失败了", Toast.LENGTH_SHORT).show();
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                runOnUiThread(() -> {
                    if (response.isSuccessful()) {
                        Toast.makeText(PendingAppointmentsActivity.this, "操作成功", Toast.LENGTH_SHORT).show();
                        loadPendingAppointments();
                        loadCancelledAppointments();
                    } else {
                        Toast.makeText(PendingAppointmentsActivity.this,
                                "更新失败，请检查后端接口", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    static class AppointmentItem {
        int id;
        String petName;
        String petType;
        String serviceType;
        String doctor;
        String appointmentDate;
        String appointmentTime;
        String symptoms;
        String status;
        int isCancelled;
    }
}