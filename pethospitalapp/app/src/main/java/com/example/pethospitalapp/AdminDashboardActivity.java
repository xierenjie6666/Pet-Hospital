package com.example.pethospitalapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import okhttp3.*;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.IOException;

public class AdminDashboardActivity extends AppCompatActivity {
    private Button logoutButton, refreshButton, viewAllButton;
    private TextView todayTotalText, todayPendingText, todayCompletedText, todayCancelledText;
    private LinearLayout appointmentsList;
    private TextView noAppointmentsText;

    private OkHttpClient client = new OkHttpClient();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_dashboard);

        initializeViews();
        setupListeners();

        // 加载数据
        loadTodayStats();
        loadRecentAppointments();
        loadCancelledCount(); // 新增：加载待撤销预约数量

        setupCardClick();
    }

    private void initializeViews() {
        logoutButton = findViewById(R.id.logoutButton);
        refreshButton = findViewById(R.id.refreshButton);
        viewAllButton = findViewById(R.id.viewAllButton);

        todayTotalText = findViewById(R.id.todayTotalText);
        todayPendingText = findViewById(R.id.todayPendingText);
        todayCompletedText = findViewById(R.id.todayCompletedText);
        todayCancelledText = findViewById(R.id.todayCancelledText); // 新增

        appointmentsList = findViewById(R.id.appointmentsList);
        noAppointmentsText = findViewById(R.id.noAppointmentsText);
    }

    private void setupCardClick() {
        // 管理预约卡片
        View appointmentCard = findViewById(R.id.cardAppointment);
        if (appointmentCard != null) {
            appointmentCard.setOnClickListener(v -> {
                Intent intent = new Intent(AdminDashboardActivity.this, PendingAppointmentsActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            });
        }

        // 待撤销卡片点击事件
        View cancelledCard = findViewById(R.id.todayCancelledText);
        if (cancelledCard != null) {
            cancelledCard.setOnClickListener(v -> {
                Intent intent = new Intent(AdminDashboardActivity.this, PendingAppointmentsActivity.class);
                intent.putExtra("defaultTab", 1);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            });
        }

        // 用户管理卡片
        View userCard = findViewById(R.id.cardUser);
        if (userCard != null) {
            userCard.setOnClickListener(v -> {
                Toast.makeText(AdminDashboardActivity.this, "打开用户管理", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(AdminDashboardActivity.this, UserManagementActivity.class));
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            });
        } else {
            Toast.makeText(this, "找不到用户管理卡片", Toast.LENGTH_SHORT).show();
        }

        // 统计分析卡片
        View statisticsCard = findViewById(R.id.cardStatistics);
        if (statisticsCard != null) {
            statisticsCard.setOnClickListener(v -> {
                Intent intent = new Intent(AdminDashboardActivity.this, StatisticsActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            });
        }

        // 系统设置卡片
        View settingsCard = findViewById(R.id.cardSettings);
        if (settingsCard != null) {
            settingsCard.setOnClickListener(v -> {
                Intent intent = new Intent(AdminDashboardActivity.this, AdminAnnouncementActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            });
        }

        View doctorManageCard = findViewById(R.id.cardDoctorManage);
        if (doctorManageCard != null) {
            doctorManageCard.setOnClickListener(v -> {
                Intent intent = new Intent(AdminDashboardActivity.this, DoctorManagementActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            });
        }
    }

    private void setupListeners() {
        logoutButton.setOnClickListener(v -> logout());

        refreshButton.setOnClickListener(v -> {
            loadTodayStats();
            loadRecentAppointments();
            loadCancelledCount(); // 刷新待撤销数量
        });

        viewAllButton.setOnClickListener(v -> {
            Intent intent = new Intent(AdminDashboardActivity.this, AppointmentManagementActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        });
    }

    private void loadTodayStats() {
        String url = ApiConfig.APPOINTMENTS + "/stats/today";

        Request request = new Request.Builder().url(url).get().build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(() -> {
                    todayTotalText.setText("0");
                    todayPendingText.setText("0");
                    todayCompletedText.setText("0");
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String json = response.body().string();
                runOnUiThread(() -> {
                    try {
                        JSONObject root = new JSONObject(json);

                        JSONObject data = null;
                        if (root.has("user")) {
                            Object userObj = root.get("user");
                            if (userObj instanceof JSONObject) {
                                data = (JSONObject) userObj;
                            }
                        } else if (root.has("data")) {
                            data = root.getJSONObject("data");
                        }

                        if (data != null) {
                            int total = data.optInt("total", 0);
                            int pending = data.optInt("pending", 0);
                            int completed = data.optInt("completed", 0);

                            todayTotalText.setText(String.valueOf(total));
                            todayPendingText.setText(String.valueOf(pending));
                            todayCompletedText.setText(String.valueOf(completed));
                        } else {
                            todayTotalText.setText("0");
                            todayPendingText.setText("0");
                            todayCompletedText.setText("0");
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                        todayTotalText.setText("0");
                        todayPendingText.setText("0");
                        todayCompletedText.setText("0");
                    }
                });
            }
        });
    }

    // 新增：加载待撤销预约数量
    // 加载待撤销预约数量
    private void loadCancelledCount() {
        // 尝试多个可能的API端点
        String url1 = ApiConfig.APPOINTMENTS + "/cancelled/count";
        String url2 = ApiConfig.APPOINTMENTS + "/status/CANCELLED";
        String url3 = ApiConfig.APPOINTMENTS + "/all";

        // 先尝试获取所有预约，然后统计CANCELLED状态
        getAllAppointmentsAndCountCancelled();
    }

    private void getAllAppointmentsAndCountCancelled() {
        String url = ApiConfig.APPOINTMENTS + "/all";

        Request request = new Request.Builder().url(url).get().build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(() -> {
                    if (todayCancelledText != null) {
                        todayCancelledText.setText("0");
                    }
                    android.util.Log.e("AdminDashboard", "获取预约列表失败: " + e.getMessage());
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String json = response.body().string();
                android.util.Log.d("AdminDashboard", "所有预约响应: " + json);

                runOnUiThread(() -> {
                    try {
                        int cancelledCount = 0;
                        JSONObject root = new JSONObject(json);

                        // 根据实际返回的JSON格式，数据在 "user" 字段中
                        JSONArray array = null;
                        if (root.has("user")) {
                            Object userObj = root.get("user");
                            if (userObj instanceof JSONArray) {
                                array = (JSONArray) userObj;
                                android.util.Log.d("AdminDashboard", "从user字段获取到数组，长度: " + array.length());
                            }
                        } else if (root.has("data")) {
                            Object dataObj = root.get("data");
                            if (dataObj instanceof JSONArray) {
                                array = (JSONArray) dataObj;
                            }
                        } else if (root.has("list")) {
                            array = root.getJSONArray("list");
                        }

                        if (array != null) {
                            for (int i = 0; i < array.length(); i++) {
                                JSONObject obj = array.getJSONObject(i);
                                String status = obj.optString("status", "");
                                android.util.Log.d("AdminDashboard", "预约ID: " + obj.optInt("id") +
                                        ", 状态: " + status +
                                        ", 宠物: " + obj.optString("petName"));

                                // 检查是否为CANCELLED状态
                                if ("CANCELLED".equals(status)) {
                                    cancelledCount++;
                                    android.util.Log.d("AdminDashboard", "找到CANCELLED预约，当前计数: " + cancelledCount);
                                }
                            }
                        } else {
                            android.util.Log.e("AdminDashboard", "未找到数据数组");
                        }

                        android.util.Log.d("AdminDashboard", "最终找到CANCELLED预约数量: " + cancelledCount);

                        if (todayCancelledText != null) {
                            todayCancelledText.setText(String.valueOf(cancelledCount));

                            // 如果有待撤销预约，改变背景色突出显示
                            if (cancelledCount > 0) {
                                View cardView = (View) todayCancelledText.getParent().getParent();
                                if (cardView instanceof androidx.cardview.widget.CardView) {
                                    ((androidx.cardview.widget.CardView) cardView).setCardBackgroundColor(0xFFFFCC80);
                                }
                            }
                        }

                    } catch (Exception e) {
                        android.util.Log.e("AdminDashboard", "解析错误: " + e.getMessage(), e);
                        if (todayCancelledText != null) {
                            todayCancelledText.setText("0");
                        }
                    }
                });
            }
        });
    }

    private void loadRecentAppointments() {
        String url = ApiConfig.APPOINTMENTS + "/recent?limit=5";

        Request request = new Request.Builder().url(url).get().build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(() -> {
                    noAppointmentsText.setVisibility(View.VISIBLE);
                    noAppointmentsText.setText("网络错误，加载失败");
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String json = response.body().string();
                runOnUiThread(() -> {
                    try {
                        JSONObject root = new JSONObject(json);

                        JSONArray array = null;
                        if (root.has("data")) {
                            Object dataObj = root.get("data");
                            if (dataObj instanceof JSONArray) {
                                array = (JSONArray) dataObj;
                            } else if (dataObj instanceof JSONObject) {
                                JSONObject dataObj2 = (JSONObject) dataObj;
                                if (dataObj2.has("list")) {
                                    array = dataObj2.getJSONArray("list");
                                }
                            }
                        } else if (root.has("user")) {
                            Object userObj = root.get("user");
                            if (userObj instanceof JSONArray) {
                                array = (JSONArray) userObj;
                            } else if (userObj instanceof JSONObject) {
                                JSONObject userObj2 = (JSONObject) userObj;
                                if (userObj2.has("list")) {
                                    array = userObj2.getJSONArray("list");
                                }
                            }
                        }

                        appointmentsList.removeAllViews();

                        if (array == null || array.length() == 0) {
                            noAppointmentsText.setVisibility(View.VISIBLE);
                            return;
                        }

                        noAppointmentsText.setVisibility(View.GONE);

                        for (int i = 0; i < array.length(); i++) {
                            JSONObject obj = array.getJSONObject(i);
                            addRecentAppointmentItem(obj);
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                        noAppointmentsText.setVisibility(View.VISIBLE);
                        noAppointmentsText.setText("暂无预约");
                    }
                });
            }
        });
    }

    private void addRecentAppointmentItem(JSONObject obj) throws Exception {
        String petName = obj.optString("petName", "");
        String petType = obj.optString("petType", "");
        String serviceType = obj.optString("serviceType", "");
        String doctor = obj.optString("doctor", "");
        String date = obj.optString("appointmentDate", "");
        String time = obj.optString("appointmentTime", "");
        String status = obj.optString("status", "");

        View itemView = getLayoutInflater().inflate(R.layout.item_appointment_recent, null);

        TextView tvPet = itemView.findViewById(R.id.petInfoText);
        TextView tvService = itemView.findViewById(R.id.serviceText);
        TextView tvDoctor = itemView.findViewById(R.id.doctorText);
        TextView tvTime = itemView.findViewById(R.id.timeText);
        TextView tvStatus = itemView.findViewById(R.id.statusText);

        String petInfo = "宠物：" + petName + (petType.isEmpty() ? "" : "（" + petType + "）");
        tvPet.setText(petInfo);
        tvService.setText("服务：" + (serviceType.isEmpty() ? "未填写" : serviceType));
        tvDoctor.setText("医生：" + (doctor.isEmpty() ? "未分配" : doctor));
        String timeText = (date.isEmpty() ? "" : date + " ") + (time.isEmpty() ? "" : time);
        tvTime.setText("时间：" + (timeText.trim().isEmpty() ? "未填写" : timeText.trim()));

        if ("PENDING".equals(status)) {
            tvStatus.setText("待处理");
            tvStatus.setTextColor(0xFFFFA000);
        } else if ("CONFIRMED".equals(status)) {
            tvStatus.setText("已确认");
            tvStatus.setTextColor(0xFF4CAF50);
        } else if ("CANCELLED".equals(status)) {
            tvStatus.setText("已撤销");
            tvStatus.setTextColor(0xFFF44336);
        } else if ("REJECTED".equals(status)) {
            tvStatus.setText("拒绝处理");
            tvStatus.setTextColor(0xFFFFA000);
        }else {
            tvStatus.setText(status);
        }

        appointmentsList.addView(itemView);
    }

    private void logout() {
        getSharedPreferences("user_prefs", MODE_PRIVATE)
                .edit()
                .clear()
                .apply();

        Intent intent = new Intent(AdminDashboardActivity.this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out_scale);
        finish();
    }
}