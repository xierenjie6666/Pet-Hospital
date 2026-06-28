package com.example.pethospitalapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.checkbox.MaterialCheckBox;
import okhttp3.*;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class UserDashboardActivity extends AppCompatActivity {
    private TextView welcomeText, userNameText, userEmailText, userPhoneText, selectedDoctorText;
    private MaterialButton newAppointmentButton, logoutButton, selectDoctorButton;
    private LinearLayout appointmentsContainer;
    private TextView noAppointmentsText;

    private String selectedDoctorId = "";
    private String selectedDoctorName = "";
    private String selectedDoctorPhone = "";
    private String selectedDoctorGender = "";
    private String selectedDoctorAnimalType = "";
    private String selectedDoctorAvailableTime = "";
    private List<String> selectedDoctorAvailableSlots = new ArrayList<>();

    private OkHttpClient client = new OkHttpClient();

    private static final int REQUEST_SELECT_DOCTOR = 1001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_dashboard);
        initializeViews();
        loadUserInfo();
        loadMyAppointments();
        setupListeners();

        new Handler(Looper.getMainLooper()).postDelayed(this::showAnnouncementIfEnabled, 800);
    }

    private void initializeViews() {
        welcomeText = findViewById(R.id.welcomeText);
        userNameText = findViewById(R.id.userNameText);
        userEmailText = findViewById(R.id.userEmailText);
        userPhoneText = findViewById(R.id.userPhoneText);
        newAppointmentButton = findViewById(R.id.newAppointmentButton);
        logoutButton = findViewById(R.id.logoutButton);
        selectDoctorButton = findViewById(R.id.selectDoctorButton);
        selectedDoctorText = findViewById(R.id.selectedDoctorText);
        appointmentsContainer = findViewById(R.id.appointmentsContainer);
        noAppointmentsText = findViewById(R.id.noAppointmentsText);
        TextView viewAllText = findViewById(R.id.viewAllText);
        if (viewAllText != null) {
            viewAllText.setOnClickListener(v -> {
                Intent intent = new Intent(UserDashboardActivity.this, MyAppointmentsActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            });
        }
    }

    private void loadUserInfo() {
        SharedPreferences sp = getSharedPreferences("user", MODE_PRIVATE);
        String name = sp.getString("name", "未设置");
        String phone = sp.getString("phone", "未设置");
        String email = sp.getString("email", "未设置");
        welcomeText.setText("欢迎回来，" + name);
        userNameText.setText(name);
        userPhoneText.setText(phone);
        userEmailText.setText(email);
    }

    private void loadMyAppointments() {
        SharedPreferences sp = getSharedPreferences("user", MODE_PRIVATE);
        String userId = sp.getString("id", "");
        if (userId.isEmpty()) {
            Toast.makeText(this, "用户未登录", Toast.LENGTH_SHORT).show();
            return;
        }
        String url = ApiConfig.APPOINTMENTS + "/my?userId=" + userId;
        Request request = new Request.Builder().url(url).get().build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(() -> Toast.makeText(UserDashboardActivity.this, "网络错误", Toast.LENGTH_SHORT).show());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String json = response.body().string();
                runOnUiThread(() -> {
                    try {
                        JSONObject root = new JSONObject(json);

                        // 根据您的后端返回格式解析
                        JSONArray array = null;
                        if (root.has("data")) {
                            array = root.getJSONArray("data");
                        } else if (root.has("user")) {
                            Object userObj = root.get("user");
                            if (userObj instanceof JSONObject) {
                                JSONObject userObj2 = (JSONObject) userObj;
                                if (userObj2.has("list")) {
                                    array = userObj2.getJSONArray("list");
                                }
                            } else if (userObj instanceof JSONArray) {
                                array = (JSONArray) userObj;
                            }
                        } else if (root.has("appointments")) {
                            array = root.getJSONArray("appointments");
                        }

                        appointmentsContainer.removeAllViews();

                        if (array == null || array.length() == 0) {
                            noAppointmentsText.setVisibility(View.VISIBLE);
                            return;
                        }

                        noAppointmentsText.setVisibility(View.GONE);

                        // 只显示第一条预约记录
                        JSONObject firstAppointment = array.getJSONObject(0);

                        int appointmentId = firstAppointment.optInt("id", 0);
                        String petName = getJsonString(firstAppointment, new String[]{"petName", "pet_name", "pet"});
                        String petType = getJsonString(firstAppointment, new String[]{"petType", "pet_type", "type"});
                        String serviceType = getJsonString(firstAppointment, new String[]{"serviceType", "service_type", "service"});
                        String date = getJsonString(firstAppointment, new String[]{"appointmentDate", "date", "appointment_date"});
                        String time = getJsonString(firstAppointment, new String[]{"appointmentTime", "time", "appointment_time"});
                        String status = getJsonString(firstAppointment, new String[]{"status"});
                        String doctor = getJsonString(firstAppointment, new String[]{"doctor", "doctorName", "doctor_name"});
                        int isCancelled = firstAppointment.optInt("isCancelled", 0);

                        // 修改：传递完整的8个参数
                        addItem(appointmentId, petName, petType, serviceType, date, time, status, doctor, isCancelled);

                        // 如果有多条预约记录，显示省略号提示
                        if (array.length() > 1) {
                            addEllipsisView();
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                        Toast.makeText(UserDashboardActivity.this, "加载预约失败：" + e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
            }
        });
    }

    // 添加省略号视图
    // 添加省略号视图（可点击跳转）
    private void addEllipsisView() {
        TextView ellipsisText = new TextView(this);
        ellipsisText.setText("...... 查看更多 ......");
        ellipsisText.setTextSize(14);
        ellipsisText.setTextColor(0xFF2196F3);
        ellipsisText.setGravity(android.view.Gravity.CENTER);
        ellipsisText.setPadding(0, dp(8), 0, dp(8));

        // 设置点击事件，跳转到我的预约页面
        ellipsisText.setOnClickListener(v -> {
            Intent intent = new Intent(UserDashboardActivity.this, MyAppointmentsActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        });

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        appointmentsContainer.addView(ellipsisText, params);
    }

    // 添加"查看更多"按钮
    private void addViewMoreButton() {
        CardView card = new CardView(this);
        card.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT));
        card.setCardElevation(2);
        card.setRadius(8);
        card.setCardBackgroundColor(0xFFFFFFFF);

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(dp(12), dp(12), dp(12), dp(12));

        TextView tvMore = new TextView(this);
        tvMore.setText("查看更多预约 →");
        tvMore.setTextSize(14);
        tvMore.setTextColor(0xFF2196F3);
        tvMore.setGravity(android.view.Gravity.CENTER);
        tvMore.setPadding(0, dp(8), 0, dp(8));

        layout.addView(tvMore);
        card.addView(layout);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        params.bottomMargin = dp(8);
        card.setLayoutParams(params);
        appointmentsContainer.addView(card);

        // 添加点击事件，跳转到我的预约界面
        card.setOnClickListener(v -> {
            Intent intent = new Intent(UserDashboardActivity.this, MyAppointmentsActivity.class);
            startActivity(intent);
        });
    }

    private String getJsonString(JSONObject obj, String[] possibleKeys) {
        for (String key : possibleKeys) {
            if (obj.has(key) && !obj.isNull(key)) {
                return obj.optString(key, "");
            }
        }
        return "";
    }

    private void addItem(int appointmentId, String petName, String petType, String service,
                         String date, String time, String status, String doctor, int isCancelled) {
        CardView card = new CardView(this);
        card.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        card.setCardElevation(2);
        card.setRadius(8);
        card.setCardBackgroundColor(0xFFFFFFFF);

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(dp(12), dp(12), dp(12), dp(12));

        TextView tvPet = new TextView(this);
        tvPet.setText("宠物：" + petName + (petType.isEmpty() ? "" : "（" + petType + "）"));
        tvPet.setTextSize(16);
        tvPet.setTextColor(0xFF333333);
        tvPet.setTypeface(null, android.graphics.Typeface.BOLD);

        TextView tvService = new TextView(this);
        tvService.setText("服务：" + (service.isEmpty() ? "未填写" : service));
        tvService.setTextSize(14);
        tvService.setTextColor(0xFF757575);
        tvService.setPadding(0, dp(4), 0, 0);

        TextView tvDoctor = new TextView(this);
        String doctorText = (doctor == null || doctor.isEmpty()) ? "无" : doctor;
        tvDoctor.setText("医生：" + doctorText);
        tvDoctor.setTextSize(14);
        tvDoctor.setTextColor(0xFF757575);
        tvDoctor.setPadding(0, dp(2), 0, 0);

        TextView tvTime = new TextView(this);
        String timeText = (date.isEmpty() ? "" : date + " ") + (time.isEmpty() ? "" : time);
        tvTime.setText("时间：" + (timeText.trim().isEmpty() ? "未填写" : timeText.trim()));
        tvTime.setTextSize(14);
        tvTime.setTextColor(0xFF757575);
        tvTime.setPadding(0, dp(2), 0, 0);

        TextView tvStatus = new TextView(this);
        String showStatus;
        int statusColor;

        // 根据状态显示不同颜色
        if (isCancelled == 1 || "CANCELLED".equals(status)) {
            showStatus = "待撤销";
            statusColor = 0xFFFF0000;  // 红色
        } else if (status.equals("PENDING")) {
            showStatus = "待处理";
            statusColor = 0xFFFFA000;  // 橙色
        } else if (status.equals("CONFIRMED")) {
            showStatus = "已确认";
            statusColor = 0xFF4CAF50;  // 绿色
        } else {
            showStatus = status;
            statusColor = 0xFF757575;
        }

        tvStatus.setText("状态：" + showStatus);
        tvStatus.setTextSize(14);
        tvStatus.setTextColor(statusColor);
        tvStatus.setPadding(0, dp(4), 0, 0);

        layout.addView(tvPet);
        layout.addView(tvService);
        layout.addView(tvDoctor);
        layout.addView(tvTime);
        layout.addView(tvStatus);

        // 添加取消按钮（只有待处理的预约才显示取消按钮）
        if (status.equals("PENDING") && isCancelled == 0) {
            Button cancelButton = new Button(this);
            cancelButton.setText("取消预约");
            cancelButton.setTextSize(14);
            cancelButton.setBackgroundTintList(android.content.res.ColorStateList.valueOf(0xFFFF0000));

            LinearLayout.LayoutParams buttonParams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    dp(40));
            buttonParams.topMargin = dp(8);
            cancelButton.setLayoutParams(buttonParams);

            // 设置取消按钮点击事件
            cancelButton.setOnClickListener(v -> cancelAppointment(appointmentId));

            layout.addView(cancelButton);
        } else if (isCancelled == 1) {
            TextView tvCancelHint = new TextView(this);
            tvCancelHint.setText("撤销申请已提交，等待处理");
            tvCancelHint.setTextSize(12);
            tvCancelHint.setTextColor(0xFFFF0000);
            tvCancelHint.setPadding(0, dp(8), 0, dp(4));
            layout.addView(tvCancelHint);
        }

        card.addView(layout);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        params.bottomMargin = dp(8);
        card.setLayoutParams(params);
        appointmentsContainer.addView(card);
    }

    // 取消预约方法
    private void cancelAppointment(int appointmentId) {
        String url = ApiConfig.APPOINTMENTS + "/cancel";

        JSONObject json = new JSONObject();
        try {
            json.put("id", appointmentId);
        } catch (Exception e) {
            e.printStackTrace();
        }

        RequestBody body = RequestBody.create(
                MediaType.parse("application/json; charset=utf-8"),
                json.toString()
        );

        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(() -> {
                    Toast.makeText(UserDashboardActivity.this, "网络错误：" + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                runOnUiThread(() -> {
                    if (response.isSuccessful()) {
                        Toast.makeText(UserDashboardActivity.this, "取消预约申请已提交", Toast.LENGTH_SHORT).show();
                        // 刷新预约列表
                        loadMyAppointments();
                    } else {
                        Toast.makeText(UserDashboardActivity.this, "取消失败，请重试", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    private void setupListeners() {
        selectDoctorButton.setOnClickListener(v -> {
            Intent intent = new Intent(UserDashboardActivity.this, SelectDoctorActivity.class);
            startActivityForResult(intent, REQUEST_SELECT_DOCTOR);
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        });

        newAppointmentButton.setOnClickListener(v -> {
            Intent intent = new Intent(UserDashboardActivity.this, AppointmentActivity.class);
            intent.putExtra("doctorName", selectedDoctorName != null ? selectedDoctorName : "");
            intent.putExtra("doctorId", selectedDoctorId != null ? selectedDoctorId : "");
            startActivity(intent);
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        });

        logoutButton.setOnClickListener(v -> {
            SharedPreferences sp = getSharedPreferences("user", MODE_PRIVATE);
            sp.edit().clear().apply();
            startActivity(new Intent(UserDashboardActivity.this, LoginActivity.class));
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out_scale);
            finish();
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_SELECT_DOCTOR && resultCode == RESULT_OK && data != null) {
            // 获取选中的医生信息
            selectedDoctorId = data.getStringExtra("selected_doctor_id");
            selectedDoctorName = data.getStringExtra("selected_doctor_name");

            // 确保不为null
            if (selectedDoctorId == null) selectedDoctorId = "";
            if (selectedDoctorName == null) selectedDoctorName = "";

            // 显示已选医生信息（只显示姓名）
            if (selectedDoctorText != null && !selectedDoctorName.isEmpty()) {
                selectedDoctorText.setText("已选医生：" + selectedDoctorName);
                selectedDoctorText.setVisibility(View.VISIBLE);
            }

            Toast.makeText(this, "已选择医生：" + selectedDoctorName, Toast.LENGTH_SHORT).show();
        }
    }

    private int dp(int dp) {
        return (int) (getResources().getDisplayMetrics().density * dp);
    }

    private void showAnnouncementIfEnabled() {
        String today = new SimpleDateFormat("yyyy-MM-dd", Locale.CHINA).format(new Date());
        String userId = getSharedPreferences("user", MODE_PRIVATE).getString("id", "");
        SharedPreferences dismissSp = getSharedPreferences("announcement_dismiss", MODE_PRIVATE);
        String dismissedDay = dismissSp.getString("dismissed_day_" + userId, "");
        if (today.equals(dismissedDay)) return;

        String url = ApiConfig.ANNOUNCEMENT + "/enabled";
        Request request = new Request.Builder().url(url).get().build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(() -> showAnnouncementFromLocal());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                try {
                    String responseData = response.body().string();
                    JSONObject root = new JSONObject(responseData);
                    boolean success = root.optBoolean("success", false);

                    if (success) {
                        Object userObj = root.opt("user");
                        if (userObj != null && userObj instanceof JSONObject) {
                            JSONObject annObj = (JSONObject) userObj;
                            boolean enabled = annObj.optBoolean("enabled", false);
                            if (!enabled) return;

                            String title = annObj.optString("title", "");
                            String content = annObj.optString("content", "");
                            String updateTime = annObj.optString("updateTime", "");

                            if (title.isEmpty() && content.isEmpty()) return;

                            runOnUiThread(() -> showAnnouncementDialog(title, content, updateTime));
                        }
                    } else {
                        runOnUiThread(() -> showAnnouncementFromLocal());
                    }
                } catch (Exception e) {
                    runOnUiThread(() -> showAnnouncementFromLocal());
                }
            }
        });
    }

    private void showAnnouncementFromLocal() {
        SharedPreferences announcementSp = getSharedPreferences("announcement", MODE_PRIVATE);
        boolean enabled = announcementSp.getBoolean("enabled", false);
        String title = announcementSp.getString("title", "");
        String content = announcementSp.getString("content", "");

        if (!enabled) return;
        if (title.isEmpty() && content.isEmpty()) return;

        long updateTime = announcementSp.getLong("updateTime", 0);
        String timeStr = "";
        if (updateTime > 0) {
            timeStr = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.CHINA).format(new Date(updateTime));
        }

        showAnnouncementDialog(title, content, timeStr);
    }

    private void showAnnouncementDialog(String title, String content, String updateTime) {
        android.app.Dialog dialog = new android.app.Dialog(this);
        dialog.setContentView(R.layout.dialog_announcement);
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);

        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new android.graphics.drawable.ColorDrawable(android.graphics.Color.TRANSPARENT));
            dialog.getWindow().setLayout(
                    (int) (getResources().getDisplayMetrics().widthPixels * 0.85),
                    android.view.ViewGroup.LayoutParams.WRAP_CONTENT);
        }

        TextView titleTv = dialog.findViewById(R.id.dialogAnnouncementTitle);
        TextView contentTv = dialog.findViewById(R.id.dialogAnnouncementContent);
        TextView timeTv = dialog.findViewById(R.id.dialogAnnouncementTime);
        MaterialCheckBox dontShowAgain = dialog.findViewById(R.id.dontShowAgainCheckBox);
        MaterialButton confirmBtn = dialog.findViewById(R.id.dialogConfirmButton);

        titleTv.setText(title.isEmpty() ? "系统公告" : title);
        contentTv.setText(content);
        if (updateTime != null && !updateTime.isEmpty()) {
            timeTv.setText(updateTime);
        }

        confirmBtn.setOnClickListener(v -> {
            if (dontShowAgain.isChecked()) {
                String userId = getSharedPreferences("user", MODE_PRIVATE).getString("id", "");
                String today = new SimpleDateFormat("yyyy-MM-dd", Locale.CHINA).format(new Date());
                getSharedPreferences("announcement_dismiss", MODE_PRIVATE)
                        .edit().putString("dismissed_day_" + userId, today).apply();
            }
            dialog.dismiss();
        });

        dialog.show();
    }

}