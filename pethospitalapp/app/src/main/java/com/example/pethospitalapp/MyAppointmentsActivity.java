package com.example.pethospitalapp;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import okhttp3.*;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.IOException;

public class MyAppointmentsActivity extends AppCompatActivity {

    private LinearLayout appointmentsContainer;
    private TextView noAppointmentsText;
    private OkHttpClient client = new OkHttpClient();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_appointments);

        initializeViews();
        loadMyAppointments();
    }

    private void initializeViews() {
        appointmentsContainer = findViewById(R.id.appointmentsContainer);
        noAppointmentsText = findViewById(R.id.noAppointmentsText);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("我的预约");
        }
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
                runOnUiThread(() -> {
                    Toast.makeText(MyAppointmentsActivity.this, "网络错误", Toast.LENGTH_SHORT).show();
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String json = response.body().string();
                runOnUiThread(() -> {
                    try {
                        JSONObject root = new JSONObject(json);

                        JSONObject data = root.getJSONObject("user");
                        JSONArray array = data.getJSONArray("list");

                        appointmentsContainer.removeAllViews();

                        if (array == null || array.length() == 0) {
                            noAppointmentsText.setVisibility(View.VISIBLE);
                            return;
                        }

                        noAppointmentsText.setVisibility(View.GONE);

                        for (int i = 0; i < array.length(); i++) {
                            JSONObject o = array.getJSONObject(i);

                            int id = o.optInt("id", 0);
                            String petName = o.optString("petName", "");
                            String petType = o.optString("petType", "");
                            String serviceType = o.optString("serviceType", "");
                            String date = o.optString("appointmentDate", "");
                            String time = o.optString("appointmentTime", "");
                            String status = o.optString("status", "");
                            String doctor = o.optString("doctor", "");
                            int isCancelled = o.optInt("isCancelled", 0);

                            addAppointmentItem(id, petName, petType, serviceType, date, time, status, doctor, isCancelled);
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                        Toast.makeText(MyAppointmentsActivity.this, "加载预约失败：" + e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
            }
        });
    }

    private void addAppointmentItem(int id, String petName, String petType, String service,
                                    String date, String time, String status, String doctor, int isCancelled) {
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
        } else if ("PENDING".equals(status)) {
            showStatus = "待处理";
            statusColor = 0xFFFFA000;  // 橙色
        } else if ("CONFIRMED".equals(status)) {
            showStatus = "已确认";
            statusColor = 0xFF4CAF50;  // 绿色
        } else if ("REJECTED".equals(status)) {
            showStatus = "拒绝处理";
            statusColor = 0xFFFF0000;}
        else {
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
        if ("PENDING".equals(status) && isCancelled == 0) {
            Button cancelButton = new Button(this);
            cancelButton.setText("取消预约");
            cancelButton.setTextSize(14);
            cancelButton.setBackgroundTintList(android.content.res.ColorStateList.valueOf(0xFFFF0000));
            cancelButton.setPadding(0, dp(8), 0, dp(8));

            // 设置取消按钮点击事件
            cancelButton.setOnClickListener(v -> cancelAppointment(id));

            layout.addView(cancelButton);
        } else if (isCancelled == 1) {
            TextView tvCancelHint = new TextView(this);
            tvCancelHint.setText("撤销申请已提交，等待管理员处理");
            tvCancelHint.setTextSize(12);
            tvCancelHint.setTextColor(0xFFFF0000);
            tvCancelHint.setPadding(0, dp(4), 0, 0);
            layout.addView(tvCancelHint);
        }

        card.addView(layout);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        params.bottomMargin = dp(8);
        card.setLayoutParams(params);
        appointmentsContainer.addView(card);
    }

    // 取消预约
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
                    Toast.makeText(MyAppointmentsActivity.this, "网络错误：" + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                runOnUiThread(() -> {
                    if (response.isSuccessful()) {
                        Toast.makeText(MyAppointmentsActivity.this, "取消预约申请已提交", Toast.LENGTH_SHORT).show();
                        // 刷新预约列表
                        loadMyAppointments();
                    } else {
                        Toast.makeText(MyAppointmentsActivity.this, "取消失败，请重试", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    private int dp(int dp) {
        return (int) (getResources().getDisplayMetrics().density * dp);
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
}