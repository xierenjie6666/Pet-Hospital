package com.example.pethospitalapp;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class SelectDoctorActivity extends AppCompatActivity {

    private RecyclerView doctorRecyclerView;
    private EditText searchInput;
    private ProgressBar progressBar;
    private TextView emptyView;

    private DoctorAdapter doctorAdapter;
    private List<Doctor> allDoctors = new ArrayList<>();
    private List<Doctor> filteredDoctors = new ArrayList<>();

    private OkHttpClient client = new OkHttpClient();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            setContentView(R.layout.activity_select_doctor);
            setupToolbar();
            initializeViews();
            loadDoctorsFromBackend();
            setupSearch();
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "初始化失败：" + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private void setupToolbar() {
        try {
            Toolbar toolbar = findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);
            if (getSupportActionBar() != null) {
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                getSupportActionBar().setDisplayShowHomeEnabled(true);
                getSupportActionBar().setTitle("选择医生");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initializeViews() {
        doctorRecyclerView = findViewById(R.id.doctorRecyclerView);
        searchInput = findViewById(R.id.searchDoctorInput);
        progressBar = findViewById(R.id.progressBar);
        emptyView = findViewById(R.id.emptyView);

        if (doctorRecyclerView != null) {
            doctorRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        }
    }

    private void loadDoctorsFromBackend() {
        showLoading(true);

        // 直接使用 /all 接口获取所有医生
        String url = ApiConfig.DOCTOR + "/all";

        Request request = new Request.Builder()
                .url(url)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(() -> {
                    showLoading(false);
                    showEmptyView(true);
                    Toast.makeText(SelectDoctorActivity.this,
                            "网络连接失败：" + e.getMessage(), Toast.LENGTH_LONG).show();
                    if (emptyView != null) {
                        emptyView.setText("网络连接失败\n请检查网络后重试");
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String json = response.body().string();
                runOnUiThread(() -> {
                    try {
                        JSONObject root = new JSONObject(json);

                        boolean success = root.optBoolean("success", false);

                        if (!success) {
                            String message = root.optString("message", "获取医生列表失败");
                            showLoading(false);
                            showEmptyView(true);
                            if (emptyView != null) {
                                emptyView.setText(message);
                            }
                            Toast.makeText(SelectDoctorActivity.this, message, Toast.LENGTH_SHORT).show();
                            return;
                        }

                        // 获取医生数据
                        Object userObj = root.get("user");
                        JSONArray array = null;

                        if (userObj instanceof JSONArray) {
                            array = (JSONArray) userObj;
                        } else if (userObj instanceof JSONObject) {
                            JSONObject userObj2 = (JSONObject) userObj;
                            if (userObj2.has("list")) {
                                array = userObj2.getJSONArray("list");
                            } else if (userObj2.has("doctors")) {
                                array = userObj2.getJSONArray("doctors");
                            }
                        }

                        if (array == null || array.length() == 0) {
                            showLoading(false);
                            showEmptyView(true);
                            if (emptyView != null) {
                                emptyView.setText("暂无医生数据");
                            }
                            return;
                        }

                        allDoctors.clear();
                        for (int i = 0; i < array.length(); i++) {
                            JSONObject obj = array.getJSONObject(i);
                            Doctor doctor = new Doctor();

                            doctor.setId(String.valueOf(obj.optInt("id", 0)));
                            doctor.setName(obj.optString("name", ""));
                            doctor.setEmail(obj.optString("email", ""));
                            doctor.setPhone(obj.optString("phone", ""));
                            doctor.setGender(obj.optString("gender", ""));
                            doctor.setAnimalType(obj.optString("animalType", ""));
                            doctor.setAppointmentTime(obj.optString("appointmentTime", ""));
                            doctor.setIntroduction(obj.optString("introduction", ""));
                            doctor.setAvatar(obj.optString("avatar", ""));
                            doctor.setWorkStartTime(obj.optString("workStartTime", ""));
                            doctor.setWorkEndTime(obj.optString("workEndTime", ""));
                            doctor.setSlotDuration(obj.optInt("slotDuration", 30));

                            // 解析可预约时间段
                            if (obj.has("availableSlots")) {
                                JSONArray slotsArray = obj.getJSONArray("availableSlots");
                                List<String> slots = new ArrayList<>();
                                for (int j = 0; j < slotsArray.length(); j++) {
                                    slots.add(slotsArray.getString(j));
                                }
                                doctor.setAvailableSlots(slots);
                            } else {
                                List<String> defaultSlots = new ArrayList<>();
                                defaultSlots.add("09:00");
                                defaultSlots.add("10:00");
                                defaultSlots.add("11:00");
                                defaultSlots.add("14:00");
                                defaultSlots.add("15:00");
                                defaultSlots.add("16:00");
                                doctor.setAvailableSlots(defaultSlots);
                            }

                            allDoctors.add(doctor);
                        }

                        filteredDoctors.clear();
                        filteredDoctors.addAll(allDoctors);

                        showLoading(false);

                        if (filteredDoctors.isEmpty()) {
                            showEmptyView(true);
                        } else {
                            showEmptyView(false);
                            setupAdapter();
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                        showLoading(false);
                        showEmptyView(true);
                        if (emptyView != null) {
                            emptyView.setText("数据解析错误：" + e.getMessage());
                        }
                        Toast.makeText(SelectDoctorActivity.this,
                                "数据解析错误：" + e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
            }
        });
    }

    private void setupAdapter() {
        doctorAdapter = new DoctorAdapter(filteredDoctors, doctor -> {
            Intent intent = new Intent();
            intent.putExtra("selected_doctor_id", doctor.getId() != null ? doctor.getId() : "");
            intent.putExtra("selected_doctor_name", doctor.getName() != null ? doctor.getName() : "");
            setResult(RESULT_OK, intent);
            finish();
        });
        doctorRecyclerView.setAdapter(doctorAdapter);
    }

    private void setupSearch() {
        if (searchInput == null) return;

        searchInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                filterDoctors(s.toString());
            }
        });
    }

    private void filterDoctors(String query) {
        filteredDoctors.clear();

        if (query.isEmpty()) {
            filteredDoctors.addAll(allDoctors);
        } else {
            for (Doctor doctor : allDoctors) {
                if (doctor.getName().toLowerCase().contains(query.toLowerCase())) {
                    filteredDoctors.add(doctor);
                }
            }
        }

        if (doctorAdapter != null) {
            doctorAdapter.updateList(filteredDoctors);
        }

        showEmptyView(filteredDoctors.isEmpty());
    }

    private void showLoading(boolean show) {
        if (progressBar != null) {
            progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
        }
        if (doctorRecyclerView != null) {
            doctorRecyclerView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    private void showEmptyView(boolean show) {
        if (emptyView != null) {
            emptyView.setVisibility(show ? View.VISIBLE : View.GONE);
        }
        if (doctorRecyclerView != null && !show) {
            doctorRecyclerView.setVisibility(View.VISIBLE);
        }
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