package com.example.pethospitalapp;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.tabs.TabLayout;
import okhttp3.*;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class AppointmentManagementActivity extends AppCompatActivity {

    private TabLayout tabLayout;
    private RecyclerView recyclerView;
    private TextView emptyView;

    private List<AppointmentItem> pendingList = new ArrayList<>();
    private List<AppointmentItem> cancelRequestList = new ArrayList<>();

    private OkHttpClient client = new OkHttpClient();
    private int currentTab = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_appointment_management);

        // 设置返回按钮
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("预约管理");
        }

        initializeViews();
        setupTabs();
        loadAllAppointments();  // 加载所有预约，然后在本地分类
    }

    private void initializeViews() {
        tabLayout = findViewById(R.id.tabLayout);
        recyclerView = findViewById(R.id.recyclerView);
        emptyView = findViewById(R.id.emptyView);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    private void setupTabs() {
        tabLayout.addTab(tabLayout.newTab().setText("待处理预约"));
        tabLayout.addTab(tabLayout.newTab().setText("撤销申请"));

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                currentTab = tab.getPosition();
                refreshDisplay();
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {}

            @Override
            public void onTabReselected(TabLayout.Tab tab) {}
        });
    }

    private void loadAllAppointments() {
        // 使用全部预约接口
        String url = ApiConfig.APPOINTMENTS + "/all";

        Request request = new Request.Builder().url(url).get().build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(() -> {
                    Toast.makeText(AppointmentManagementActivity.this, "网络错误", Toast.LENGTH_SHORT).show();
                    emptyView.setVisibility(View.VISIBLE);
                    emptyView.setText("网络错误，请检查连接");
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String json = response.body().string();
                runOnUiThread(() -> {
                    try {
                        JSONObject root = new JSONObject(json);

                        // 根据后端返回格式，数据在 "user" 字段中，是一个数组
                        JSONArray array = null;
                        if (root.has("user") && root.get("user") instanceof JSONArray) {
                            array = root.getJSONArray("user");
                        } else if (root.has("data") && root.get("data") instanceof JSONArray) {
                            array = root.getJSONArray("data");
                        }

                        pendingList.clear();
                        cancelRequestList.clear();

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

                                // 待处理预约：status = PENDING 且 isCancelled = 0
                                if ("PENDING".equals(item.status) && item.isCancelled == 0) {
                                    pendingList.add(item);
                                }
                                // 撤销申请：isCancelled = 1
                                else if (item.isCancelled == 1) {
                                    cancelRequestList.add(item);
                                }
                            }
                        }

                        refreshDisplay();

                    } catch (Exception e) {
                        e.printStackTrace();
                        Toast.makeText(AppointmentManagementActivity.this, "解析错误：" + e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
            }
        });
    }

    private void refreshDisplay() {
        if (currentTab == 0) {
            // 显示待处理预约
            if (pendingList.isEmpty()) {
                recyclerView.setVisibility(View.GONE);
                emptyView.setVisibility(View.VISIBLE);
                emptyView.setText("暂无待处理预约");
            } else {
                recyclerView.setVisibility(View.VISIBLE);
                emptyView.setVisibility(View.GONE);
                AppointmentAdminAdapter adapter = new AppointmentAdminAdapter(pendingList,
                        new AppointmentAdminAdapter.OnActionListener() {
                            @Override
                            public void onConfirm(int id) { updateStatus(id, "CONFIRMED", 0); }
                            @Override
                            public void onReject(int id) { updateStatus(id, "REJECTED", 0); }
                        });
                recyclerView.setAdapter(adapter);
            }
        } else {
            // 显示撤销申请
            if (cancelRequestList.isEmpty()) {
                recyclerView.setVisibility(View.GONE);
                emptyView.setVisibility(View.VISIBLE);
                emptyView.setText("暂无撤销申请");
            } else {
                recyclerView.setVisibility(View.VISIBLE);
                emptyView.setVisibility(View.GONE);
                CancelRequestAdapter adapter = new CancelRequestAdapter(cancelRequestList,
                        new CancelRequestAdapter.OnActionListener() {
                            @Override
                            public void onApprove(int id) { updateStatus(id, "CANCELLED", 2); }
                            @Override
                            public void onDeny(int id) { updateStatus(id, "PENDING", 0); }
                        });
                recyclerView.setAdapter(adapter);
            }
        }
    }

    private void updateStatus(int id, String status, int isCancelled) {
        String url = ApiConfig.APPOINTMENTS + "/update";

        JSONObject json = new JSONObject();
        try {
            json.put("id", id);
            json.put("status", status);
            json.put("isCancelled", isCancelled);
        } catch (Exception e) {
            e.printStackTrace();
        }

        RequestBody body = RequestBody.create(
                MediaType.parse("application/json; charset=utf-8"),
                json.toString()
        );

        Request request = new Request.Builder().url(url).post(body).build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(() -> Toast.makeText(AppointmentManagementActivity.this, "操作失败", Toast.LENGTH_SHORT).show());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                runOnUiThread(() -> {
                    Toast.makeText(AppointmentManagementActivity.this, "操作成功", Toast.LENGTH_SHORT).show();
                    loadAllAppointments();  // 刷新列表
                });
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
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