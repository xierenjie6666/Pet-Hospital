package com.example.pethospitalapp;

import android.app.TimePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.textfield.TextInputEditText;
import okhttp3.*;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class DoctorManagementActivity extends AppCompatActivity {

    private Spinner doctorSpinner;
    private TextInputEditText doctorIntroInput;
    private MaterialButton startTimeBtn, endTimeBtn, saveDoctorBtn;
    private ChipGroup durationChipGroup, generatedSlotsGroup;
    private TextView generatedSlotsLabel, noDoctorText;
    private LinearLayout doctorListContainer;
    private ImageView backButton;

    private String workStartTime = "";
    private String workEndTime = "";
    private int slotDuration = 30;
    private List<Doctor> allDoctors = new ArrayList<>();
    private int selectedDoctorIndex = -1;

    private OkHttpClient client = new OkHttpClient();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doctor_management);

        initViews();
        setupListeners();
        loadDoctors();
    }

    private void initViews() {
        doctorSpinner = findViewById(R.id.doctorSpinner);
        doctorIntroInput = findViewById(R.id.doctorIntroInput);
        startTimeBtn = findViewById(R.id.startTimeBtn);
        endTimeBtn = findViewById(R.id.endTimeBtn);
        saveDoctorBtn = findViewById(R.id.saveDoctorBtn);
        durationChipGroup = findViewById(R.id.durationChipGroup);
        generatedSlotsGroup = findViewById(R.id.generatedSlotsGroup);
        generatedSlotsLabel = findViewById(R.id.generatedSlotsLabel);
        noDoctorText = findViewById(R.id.noDoctorText);
        doctorListContainer = findViewById(R.id.doctorListContainer);
        backButton = findViewById(R.id.backButton);

        durationChipGroup.check(durationChipGroup.getChildAt(1).getId());
    }

    private void setupListeners() {
        backButton.setOnClickListener(v -> {
            finish();
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left);
        });

        startTimeBtn.setOnClickListener(v -> showTimePicker(true));
        endTimeBtn.setOnClickListener(v -> showTimePicker(false));

        durationChipGroup.setOnCheckedStateChangeListener((group, checkedIds) -> {
            if (checkedIds.isEmpty()) return;
            Chip selected = findViewById(checkedIds.get(0));
            String text = selected.getText().toString();
            if (text.contains("15")) slotDuration = 15;
            else if (text.contains("30")) slotDuration = 30;
            else if (text.contains("45")) slotDuration = 45;
            else if (text.contains("60")) slotDuration = 60;
            updateGeneratedSlots();
        });

        saveDoctorBtn.setOnClickListener(v -> saveDoctorInfo());

        doctorSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedDoctorIndex = position - 1;
                if (selectedDoctorIndex >= 0 && selectedDoctorIndex < allDoctors.size()) {
                    loadDoctorDetail(allDoctors.get(selectedDoctorIndex));
                } else {
                    clearForm();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
    }

    private void loadDoctors() {
        String url = ApiConfig.DOCTOR + "/all";
        Request request = new Request.Builder().url(url).build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(() -> Toast.makeText(DoctorManagementActivity.this, "网络错误", Toast.LENGTH_SHORT).show());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                try {
                    String json = response.body().string();
                    JSONObject root = new JSONObject(json);
                    boolean success = root.optBoolean("success", false);
                    if (!success) return;

                    JSONArray array = root.getJSONArray("user");
                    List<Doctor> doctors = new ArrayList<>();
                    for (int i = 0; i < array.length(); i++) {
                        JSONObject obj = array.getJSONObject(i);
                        Doctor doctor = new Doctor();
                        doctor.setId(String.valueOf(obj.optInt("id", 0)));
                        doctor.setName(obj.optString("name", ""));
                        doctor.setEmail(obj.optString("email", ""));
                        doctor.setPhone(obj.optString("phone", ""));
                        doctor.setGender(obj.optString("gender", ""));
                        doctor.setAnimalType(obj.optString("animalType", ""));
                        doctor.setIntroduction(obj.optString("introduction", ""));
                        doctor.setWorkStartTime(obj.optString("workStartTime", ""));
                        doctor.setWorkEndTime(obj.optString("workEndTime", ""));
                        doctor.setSlotDuration(obj.optInt("slotDuration", 30));
                        doctors.add(doctor);
                    }

                    runOnUiThread(() -> {
                        allDoctors = doctors;
                        setupSpinner();
                        renderDoctorList();
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void setupSpinner() {
        List<String> names = new ArrayList<>();
        names.add("-- 请选择医生 --");
        for (Doctor d : allDoctors) {
            names.add(d.getName() + " (" + d.getAnimalType() + ")");
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, names);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        doctorSpinner.setAdapter(adapter);
    }

    private void loadDoctorDetail(Doctor doctor) {
        doctorIntroInput.setText(doctor.getIntroduction() != null ? doctor.getIntroduction() : "");
        workStartTime = doctor.getWorkStartTime() != null ? doctor.getWorkStartTime() : "";
        workEndTime = doctor.getWorkEndTime() != null ? doctor.getWorkEndTime() : "";
        slotDuration = doctor.getSlotDuration();

        startTimeBtn.setText(workStartTime.isEmpty() ? "开始时间" : workStartTime);
        endTimeBtn.setText(workEndTime.isEmpty() ? "结束时间" : workEndTime);

        int chipIndex = 1;
        if (slotDuration == 15) chipIndex = 0;
        else if (slotDuration == 30) chipIndex = 1;
        else if (slotDuration == 45) chipIndex = 2;
        else if (slotDuration == 60) chipIndex = 3;
        durationChipGroup.check(durationChipGroup.getChildAt(chipIndex).getId());

        updateGeneratedSlots();
    }

    private void clearForm() {
        doctorIntroInput.setText("");
        workStartTime = "";
        workEndTime = "";
        startTimeBtn.setText("开始时间");
        endTimeBtn.setText("结束时间");
        generatedSlotsGroup.removeAllViews();
        generatedSlotsLabel.setVisibility(View.GONE);
        generatedSlotsGroup.setVisibility(View.GONE);
    }

    private void showTimePicker(boolean isStart) {
        Calendar c = Calendar.getInstance();
        TimePickerDialog dialog = new TimePickerDialog(this,
                (view, hourOfDay, minute) -> {
                    String time = String.format("%02d:%02d", hourOfDay, minute);
                    if (isStart) {
                        workStartTime = time;
                        startTimeBtn.setText(time);
                    } else {
                        workEndTime = time;
                        endTimeBtn.setText(time);
                    }
                    updateGeneratedSlots();
                },
                c.get(Calendar.HOUR_OF_DAY), c.get(Calendar.MINUTE), true);
        dialog.show();
    }

    private void updateGeneratedSlots() {
        if (workStartTime.isEmpty() || workEndTime.isEmpty()) return;

        generatedSlotsLabel.setVisibility(View.VISIBLE);
        generatedSlotsGroup.setVisibility(View.VISIBLE);
        generatedSlotsGroup.removeAllViews();

        List<String> slots = generateTimeSlots(workStartTime, workEndTime, slotDuration);
        for (String slot : slots) {
            Chip chip = new Chip(this);
            chip.setText(slot);
            chip.setChipBackgroundColorResource(R.color.background);
            chip.setTextColor(getResources().getColor(R.color.primary));
            chip.setChipStrokeColorResource(R.color.primary_light);
            chip.setChipStrokeWidth(1f);
            chip.setClickable(false);
            chip.setTextSize(11);
            generatedSlotsGroup.addView(chip);
        }
    }

    private List<String> generateTimeSlots(String start, String end, int duration) {
        List<String> slots = new ArrayList<>();
        try {
            String[] startParts = start.split(":");
            String[] endParts = end.split(":");
            int startMin = Integer.parseInt(startParts[0]) * 60 + Integer.parseInt(startParts[1]);
            int endMin = Integer.parseInt(endParts[0]) * 60 + Integer.parseInt(endParts[1]);

            while (startMin + duration <= endMin) {
                int h = startMin / 60;
                int m = startMin % 60;
                slots.add(String.format("%02d:%02d", h, m));
                startMin += duration;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return slots;
    }

    private void saveDoctorInfo() {
        if (selectedDoctorIndex < 0 || selectedDoctorIndex >= allDoctors.size()) {
            Toast.makeText(this, "请先选择医生", Toast.LENGTH_SHORT).show();
            return;
        }
        if (workStartTime.isEmpty() || workEndTime.isEmpty()) {
            Toast.makeText(this, "请设置工作时间", Toast.LENGTH_SHORT).show();
            return;
        }

        Doctor doctor = allDoctors.get(selectedDoctorIndex);
        String intro = doctorIntroInput.getText().toString().trim();

        try {
            JSONObject json = new JSONObject();
            json.put("id", Integer.parseInt(doctor.getId()));
            json.put("introduction", intro);
            json.put("workStartTime", workStartTime);
            json.put("workEndTime", workEndTime);
            json.put("slotDuration", slotDuration);

            String url = ApiConfig.DOCTOR + "/update";
            RequestBody body = RequestBody.create(json.toString(), MediaType.parse("application/json"));
            Request request = new Request.Builder().url(url).post(body).build();

            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    runOnUiThread(() -> Toast.makeText(DoctorManagementActivity.this, "保存失败", Toast.LENGTH_SHORT).show());
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    runOnUiThread(() -> {
                        Toast.makeText(DoctorManagementActivity.this, "保存成功", Toast.LENGTH_SHORT).show();
                        loadDoctors();
                    });
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void renderDoctorList() {
        doctorListContainer.removeAllViews();
        if (allDoctors.isEmpty()) {
            noDoctorText.setVisibility(View.VISIBLE);
            return;
        }
        noDoctorText.setVisibility(View.GONE);

        for (Doctor doctor : allDoctors) {
            CardView card = new CardView(this);
            card.setLayoutParams(new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT));
            card.setCardElevation(2);
            card.setRadius(12);
            card.setCardBackgroundColor(0xFFF4F5FA);
            card.setUseCompatPadding(true);

            LinearLayout layout = new LinearLayout(this);
            layout.setOrientation(LinearLayout.VERTICAL);
            layout.setPadding(dp(14), dp(14), dp(14), dp(14));

            TextView tvName = new TextView(this);
            tvName.setText(doctor.getName() + " · " + doctor.getAnimalType());
            tvName.setTextSize(15);
            tvName.setTextColor(getResources().getColor(R.color.text_primary));
            tvName.setTypeface(null, android.graphics.Typeface.BOLD);

            TextView tvTime = new TextView(this);
            String timeStr = "";
            if (doctor.getWorkStartTime() != null && !doctor.getWorkStartTime().isEmpty()) {
                timeStr = "工作时间：" + doctor.getWorkStartTime() + " - " + doctor.getWorkEndTime();
            } else {
                timeStr = "工作时间未设置";
            }
            tvTime.setText(timeStr);
            tvTime.setTextSize(12);
            tvTime.setTextColor(getResources().getColor(R.color.text_secondary));
            tvTime.setPadding(0, dp(4), 0, 0);

            TextView tvIntro = new TextView(this);
            String intro = doctor.getIntroduction();
            tvIntro.setText((intro == null || intro.isEmpty()) ? "暂无介绍" : intro);
            tvIntro.setTextSize(12);
            tvIntro.setTextColor(getResources().getColor(R.color.text_secondary));
            tvIntro.setMaxLines(2);
            tvIntro.setPadding(0, dp(4), 0, 0);

            layout.addView(tvName);
            layout.addView(tvTime);
            layout.addView(tvIntro);
            card.addView(layout);
            doctorListContainer.addView(card);
        }
    }

    private int dp(int dp) {
        return (int) (getResources().getDisplayMetrics().density * dp);
    }
}
