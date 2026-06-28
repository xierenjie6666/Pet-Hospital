package com.example.pethospitalapp;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import okhttp3.*;
import org.json.JSONObject;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class AppointmentActivity extends AppCompatActivity {
    private EditText petNameEditText, petTypeEditText, symptomsEditText;
    private Spinner serviceTypeSpinner;
    private Button datePickerButton, timePickerButton, submitButton;
    private TextView selectedDoctorText, timeSlotLabel;
    private ChipGroup timeSlotChipGroup;
    private String selectedDate = "", selectedTime = "";
    private String doctorName = "";
    private String doctorId = "";
    private List<String> doctorSlots = new ArrayList<>();
    private OkHttpClient client = new OkHttpClient();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_appointment);

        Intent intent = getIntent();
        if (intent != null) {
            doctorName = intent.getStringExtra("doctorName");
            if (doctorName == null) doctorName = "";
            doctorId = intent.getStringExtra("doctorId");
            if (doctorId == null) doctorId = "";
        }

        initializeViews();
        setupSpinner();
        setupListeners();

        if (!doctorName.isEmpty()) {
            selectedDoctorText.setText("预约医生：" + doctorName);
            selectedDoctorText.setVisibility(TextView.VISIBLE);
        }

        if (!doctorId.isEmpty()) {
            loadDoctorTimeSlots();
        }
    }

    private void initializeViews() {
        petNameEditText = findViewById(R.id.petNameEditText);
        petTypeEditText = findViewById(R.id.petTypeEditText);
        symptomsEditText = findViewById(R.id.symptomsEditText);
        serviceTypeSpinner = findViewById(R.id.serviceTypeSpinner);
        datePickerButton = findViewById(R.id.datePickerButton);
        timePickerButton = findViewById(R.id.timePickerButton);
        submitButton = findViewById(R.id.submitButton);
        selectedDoctorText = findViewById(R.id.selectedDoctorText);
        timeSlotLabel = findViewById(R.id.timeSlotLabel);
        timeSlotChipGroup = findViewById(R.id.timeSlotChipGroup);
    }

    private void setupSpinner() {
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this,
                R.array.service_types,
                android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        serviceTypeSpinner.setAdapter(adapter);
    }

    private void loadDoctorTimeSlots() {
        String url = ApiConfig.DOCTOR + "/" + doctorId;
        Request request = new Request.Builder().url(url).build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(() -> showTimePickerFallback());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                try {
                    String json = response.body().string();
                    JSONObject root = new JSONObject(json);
                    boolean success = root.optBoolean("success", false);
                    if (!success) {
                        runOnUiThread(() -> showTimePickerFallback());
                        return;
                    }

                    JSONObject doctorObj = root.getJSONObject("user");
                    List<String> slots = new ArrayList<>();
                    if (doctorObj.has("availableSlots")) {
                        org.json.JSONArray slotsArray = doctorObj.getJSONArray("availableSlots");
                        for (int i = 0; i < slotsArray.length(); i++) {
                            slots.add(slotsArray.getString(i));
                        }
                    }

                    runOnUiThread(() -> {
                        if (!slots.isEmpty()) {
                            doctorSlots = slots;
                            showTimeSlotChips();
                        } else {
                            showTimePickerFallback();
                        }
                    });
                } catch (Exception e) {
                    runOnUiThread(() -> showTimePickerFallback());
                }
            }
        });
    }

    private void showTimeSlotChips() {
        timeSlotLabel.setVisibility(View.VISIBLE);
        timeSlotChipGroup.setVisibility(View.VISIBLE);
        timePickerButton.setVisibility(View.GONE);

        timeSlotChipGroup.removeAllViews();
        for (String slot : doctorSlots) {
            Chip chip = new Chip(this);
            chip.setText(slot);
            chip.setChipBackgroundColorResource(R.color.background);
            chip.setTextColor(getResources().getColor(R.color.text_secondary));
            chip.setChipStrokeColorResource(R.color.primary_light);
            chip.setChipStrokeWidth(1f);
            chip.setCheckable(true);
            chip.setCheckedIconVisible(false);
            chip.setTextSize(13);

            chip.setOnCheckedChangeListener((buttonView, isChecked) -> {
                if (isChecked) {
                    selectedTime = slot;
                    for (int i = 0; i < timeSlotChipGroup.getChildCount(); i++) {
                        Chip other = (Chip) timeSlotChipGroup.getChildAt(i);
                        if (other != chip && other.isChecked()) {
                            other.setChecked(false);
                        }
                    }
                    chip.setChipBackgroundColorResource(R.color.primary);
                    chip.setTextColor(getResources().getColor(R.color.white));
                } else {
                    chip.setChipBackgroundColorResource(R.color.background);
                    chip.setTextColor(getResources().getColor(R.color.text_secondary));
                }
            });

            timeSlotChipGroup.addView(chip);
        }
    }

    private void showTimePickerFallback() {
        timeSlotLabel.setVisibility(View.GONE);
        timeSlotChipGroup.setVisibility(View.GONE);
        timePickerButton.setVisibility(View.VISIBLE);
    }

    private void setupListeners() {
        datePickerButton.setOnClickListener(v -> showDatePicker());
        timePickerButton.setOnClickListener(v -> showTimePicker());
        submitButton.setOnClickListener(v -> submitAppointment());
    }

    private void showDatePicker() {
        Calendar calendar = Calendar.getInstance();
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                (view, year, month, dayOfMonth) -> {
                    selectedDate = String.format("%04d-%02d-%02d", year, month + 1, dayOfMonth);
                    datePickerButton.setText("预约日期: " + selectedDate);
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
        );
        datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis());
        datePickerDialog.show();
    }

    private void showTimePicker() {
        Calendar calendar = Calendar.getInstance();
        TimePickerDialog timePickerDialog = new TimePickerDialog(
                this,
                (view, hourOfDay, minute) -> {
                    selectedTime = String.format("%02d:%02d", hourOfDay, minute);
                    timePickerButton.setText("预约时间: " + selectedTime);
                },
                calendar.get(Calendar.HOUR_OF_DAY),
                calendar.get(Calendar.MINUTE),
                true
        );
        timePickerDialog.show();
    }

    private void submitAppointment() {
        String petName = petNameEditText.getText().toString().trim();
        String petType = petTypeEditText.getText().toString().trim();
        String serviceType = serviceTypeSpinner.getSelectedItem().toString();
        String symptoms = symptomsEditText.getText().toString().trim();

        if (petName.isEmpty() || petType.isEmpty() || selectedDate.isEmpty() || selectedTime.isEmpty()) {
            Toast.makeText(this, "请填写所有必填项", Toast.LENGTH_SHORT).show();
            return;
        }

        SharedPreferences sp = getSharedPreferences("user", MODE_PRIVATE);
        String userId = sp.getString("id", "");

        if (userId.isEmpty()) {
            Toast.makeText(this, "请先登录", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            JSONObject json = new JSONObject();
            json.put("user_id", userId);
            json.put("petName", petName);
            json.put("petType", petType);
            json.put("serviceType", serviceType);
            json.put("appointmentDate", selectedDate);
            json.put("appointmentTime", selectedTime);
            json.put("symptoms", symptoms.isEmpty() ? "无" : symptoms);
            json.put("status", "PENDING");
            json.put("doctor", doctorName);
            json.put("doctorId", doctorId);

            createAppointment(json.toString());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void createAppointment(String data) {
        String url = ApiConfig.APPOINTMENTS + "/create";

        RequestBody body = RequestBody.create(data, MediaType.parse("application/json"));
        Request request = new Request.Builder().url(url).post(body).build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(() -> Toast.makeText(AppointmentActivity.this, "网络错误", Toast.LENGTH_SHORT).show());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                runOnUiThread(() -> {
                    Toast.makeText(AppointmentActivity.this, "预约提交成功", Toast.LENGTH_SHORT).show();
                    finish();
                    overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left);
                });
            }
        });
    }
}
