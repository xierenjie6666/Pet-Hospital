package com.example.pethospitalapp;

import android.app.Dialog;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.chip.Chip;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.google.android.material.textfield.TextInputEditText;
import okhttp3.*;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.IOException;

public class AdminAnnouncementActivity extends AppCompatActivity {

    private TextInputEditText titleInput;
    private TextInputEditText contentInput;
    private SwitchMaterial enabledSwitch;
    private MaterialButton saveButton;
    private MaterialButton previewButton;
    private MaterialButton newButton;
    private LinearLayout listContainer;
    private ScrollView editSection;
    private ScrollView listSection;

    private OkHttpClient client = new OkHttpClient();
    private int currentAnnouncementId = -1;
    private boolean isEditing = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_announcement_management);

        initializeViews();
        setupListeners();
        showListMode();
        loadAllAnnouncements();
    }

    private void initializeViews() {
        ImageView backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(v -> {
            if (isEditing) {
                showListMode();
                loadAllAnnouncements();
            } else {
                finish();
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left);
            }
        });

        titleInput = findViewById(R.id.announcementTitleInput);
        contentInput = findViewById(R.id.announcementContentInput);
        enabledSwitch = findViewById(R.id.announcementEnabledSwitch);
        saveButton = findViewById(R.id.saveButton);
        previewButton = findViewById(R.id.previewButton);
        listContainer = findViewById(R.id.historyContainer);

        editSection = findViewById(R.id.editSection);
        listSection = findViewById(R.id.listSection);

        newButton = new MaterialButton(this);
        newButton.setText("新建公告");
        newButton.setTextSize(14);
        newButton.setCornerRadius(12);
        newButton.setBackgroundColor(0xFF4A90D9);
        newButton.setTextColor(0xFFFFFFFF);

        Chip chipSpring = findViewById(R.id.chipSpring);
        Chip chipSummer = findViewById(R.id.chipSummer);
        Chip chipWinter = findViewById(R.id.chipWinter);
        Chip chipHoliday = findViewById(R.id.chipHoliday);
        Chip chipPromo = findViewById(R.id.chipPromo);

        chipSpring.setOnClickListener(v -> applyTemplate("spring"));
        chipSummer.setOnClickListener(v -> applyTemplate("summer"));
        chipWinter.setOnClickListener(v -> applyTemplate("winter"));
        chipHoliday.setOnClickListener(v -> applyTemplate("holiday"));
        chipPromo.setOnClickListener(v -> applyTemplate("promo"));
    }

    private void setupListeners() {
        saveButton.setOnClickListener(v -> saveAnnouncement());
        previewButton.setOnClickListener(v -> previewAnnouncement());
    }

    private void showListMode() {
        isEditing = false;
        editSection.setVisibility(View.GONE);
        listSection.setVisibility(View.VISIBLE);
    }

    private void showEditMode() {
        isEditing = true;
        editSection.setVisibility(View.VISIBLE);
        listSection.setVisibility(View.GONE);
    }

    private void loadAllAnnouncements() {
        String url = ApiConfig.ANNOUNCEMENT + "/all";
        Request request = new Request.Builder().url(url).get().build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(() -> {
                    Toast.makeText(AdminAnnouncementActivity.this,
                            "网络连接失败，请检查后端是否启动", Toast.LENGTH_LONG).show();
                    renderList(new JSONArray());
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                try {
                    String responseData = response.body().string();
                    JSONObject root = new JSONObject(responseData);
                    boolean success = root.optBoolean("success", false);

                    if (success) {
                        Object userObj = root.opt("user");
                        JSONArray array = null;
                        if (userObj instanceof JSONArray) {
                            array = (JSONArray) userObj;
                        } else if (userObj instanceof JSONObject) {
                            JSONObject obj = (JSONObject) userObj;
                            if (obj.has("list")) {
                                array = obj.getJSONArray("list");
                            }
                        }

                        if (array == null) {
                            JSONArray emptyArr = new JSONArray();
                            runOnUiThread(() -> renderList(emptyArr));
                            return;
                        }

                        JSONArray finalArray = array;
                        runOnUiThread(() -> renderList(finalArray));
                    } else {
                        String msg = root.optString("message", "");
                        runOnUiThread(() -> {
                            Toast.makeText(AdminAnnouncementActivity.this,
                                    "后端返回错误：" + msg, Toast.LENGTH_LONG).show();
                            renderList(new JSONArray());
                        });
                    }
                } catch (Exception e) {
                    runOnUiThread(() -> {
                        Toast.makeText(AdminAnnouncementActivity.this,
                                "解析数据失败", Toast.LENGTH_SHORT).show();
                        renderList(new JSONArray());
                    });
                }
            }
        });
    }

    private void renderList(JSONArray array) {
        listContainer.removeAllViews();

        LinearLayout topBar = new LinearLayout(this);
        topBar.setOrientation(LinearLayout.HORIZONTAL);
        topBar.setGravity(Gravity.CENTER_VERTICAL);
        topBar.setPadding(0, dp(8), 0, dp(16));

        TextView titleText = new TextView(this);
        titleText.setText("公告管理");
        titleText.setTextSize(18);
        titleText.setTextColor(0xFF333333);
        titleText.setTypeface(null, android.graphics.Typeface.BOLD);
        titleText.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f));

        MaterialButton createBtn = new MaterialButton(this);
        createBtn.setText("+ 新建公告");
        createBtn.setTextSize(13);
        createBtn.setCornerRadius(20);
        createBtn.setBackgroundColor(0xFF4A90D9);
        createBtn.setTextColor(0xFFFFFFFF);
        createBtn.setPadding(dp(16), dp(4), dp(16), dp(4));
        createBtn.setOnClickListener(v -> {
            currentAnnouncementId = -1;
            titleInput.setText("");
            contentInput.setText("");
            enabledSwitch.setChecked(false);
            showEditMode();
        });

        topBar.addView(titleText);
        topBar.addView(createBtn);
        listContainer.addView(topBar);

        if (array.length() == 0) {
            TextView emptyText = new TextView(this);
            emptyText.setText("暂无公告，点击上方按钮新建");
            emptyText.setTextSize(14);
            emptyText.setTextColor(0xFF999999);
            emptyText.setGravity(Gravity.CENTER);
            emptyText.setPadding(0, dp(48), 0, dp(48));
            listContainer.addView(emptyText);
            return;
        }

        for (int i = 0; i < array.length(); i++) {
            try {
                JSONObject obj = array.getJSONObject(i);
                int id = obj.optInt("id", -1);
                String title = obj.optString("title", "");
                String content = obj.optString("content", "");
                String updateTime = obj.optString("updateTime", "");
                boolean enabled = obj.optBoolean("enabled", false);
                addAnnouncementCard(id, title, content, updateTime, enabled);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void addAnnouncementCard(int id, String title, String content, String time, boolean enabled) {
        CardView card = new CardView(this);
        LinearLayout.LayoutParams cardParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        cardParams.bottomMargin = dp(12);
        card.setLayoutParams(cardParams);
        card.setCardElevation(enabled ? 6 : 2);
        card.setRadius(16);
        card.setCardBackgroundColor(enabled ? 0xFFE8F5E9 : 0xFFFFFFFF);
        card.setUseCompatPadding(true);

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(dp(16), dp(16), dp(16), dp(16));

        LinearLayout headerRow = new LinearLayout(this);
        headerRow.setOrientation(LinearLayout.HORIZONTAL);
        headerRow.setGravity(Gravity.CENTER_VERTICAL);

        TextView statusDot = new TextView(this);
        statusDot.setText(enabled ? "● " : "○ ");
        statusDot.setTextSize(16);
        statusDot.setTextColor(enabled ? 0xFF4CAF50 : 0xFFBDBDBD);

        TextView tvTitle = new TextView(this);
        tvTitle.setText(title);
        tvTitle.setTextSize(16);
        tvTitle.setTextColor(0xFF333333);
        tvTitle.setTypeface(null, android.graphics.Typeface.BOLD);
        tvTitle.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f));

        TextView tvStatus = new TextView(this);
        tvStatus.setText(enabled ? "展示中" : "未展示");
        tvStatus.setTextSize(12);
        tvStatus.setTextColor(enabled ? 0xFF4CAF50 : 0xFF999999);
        tvStatus.setPadding(dp(8), dp(4), dp(8), dp(4));
        tvStatus.setBackgroundColor(enabled ? 0xFFE8F5E9 : 0xFFF5F5F5);

        headerRow.addView(statusDot);
        headerRow.addView(tvTitle);
        headerRow.addView(tvStatus);

        TextView tvContent = new TextView(this);
        tvContent.setText(content);
        tvContent.setTextSize(13);
        tvContent.setTextColor(0xFF666666);
        tvContent.setMaxLines(2);
        tvContent.setEllipsize(android.text.TextUtils.TruncateAt.END);
        tvContent.setPadding(0, dp(8), 0, 0);

        TextView tvTime = new TextView(this);
        tvTime.setText(time);
        tvTime.setTextSize(11);
        tvTime.setTextColor(0xFF999999);
        tvTime.setPadding(0, dp(4), 0, 0);

        LinearLayout actionRow = new LinearLayout(this);
        actionRow.setOrientation(LinearLayout.HORIZONTAL);
        actionRow.setGravity(Gravity.END);
        actionRow.setPadding(0, dp(8), 0, 0);

        if (enabled) {
            MaterialButton disableBtn = new MaterialButton(this);
            disableBtn.setText("取消展示");
            disableBtn.setTextSize(12);
            disableBtn.setCornerRadius(16);
            disableBtn.setBackgroundColor(0xFFFF9800);
            disableBtn.setTextColor(0xFFFFFFFF);
            disableBtn.setPadding(dp(12), 0, dp(12), 0);
            disableBtn.setMinimumHeight(dp(32));
            disableBtn.setOnClickListener(v -> disableAnnouncement(id));
            actionRow.addView(disableBtn);
        } else {
            MaterialButton enableBtn = new MaterialButton(this);
            enableBtn.setText("设为展示");
            enableBtn.setTextSize(12);
            enableBtn.setCornerRadius(16);
            enableBtn.setBackgroundColor(0xFF4CAF50);
            enableBtn.setTextColor(0xFFFFFFFF);
            enableBtn.setPadding(dp(12), 0, dp(12), 0);
            enableBtn.setMinimumHeight(dp(32));
            enableBtn.setOnClickListener(v -> enableAnnouncement(id));
            actionRow.addView(enableBtn);
        }

        MaterialButton editBtn = new MaterialButton(this);
        editBtn.setText("编辑");
        editBtn.setTextSize(12);
        editBtn.setCornerRadius(16);
        editBtn.setBackgroundColor(0xFF4A90D9);
        editBtn.setTextColor(0xFFFFFFFF);
        editBtn.setPadding(dp(12), 0, dp(12), 0);
        editBtn.setMinimumHeight(dp(32));
        LinearLayout.LayoutParams editBtnParams = (LinearLayout.LayoutParams) editBtn.getLayoutParams();
        if (editBtnParams == null) editBtnParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        editBtnParams.leftMargin = dp(8);
        editBtn.setLayoutParams(editBtnParams);
        editBtn.setOnClickListener(v -> {
            currentAnnouncementId = id;
            titleInput.setText(title);
            contentInput.setText(content);
            enabledSwitch.setChecked(enabled);
            showEditMode();
        });
        actionRow.addView(editBtn);

        MaterialButton deleteBtn = new MaterialButton(this);
        deleteBtn.setText("删除");
        deleteBtn.setTextSize(12);
        deleteBtn.setCornerRadius(16);
        deleteBtn.setBackgroundColor(0xFFFF4444);
        deleteBtn.setTextColor(0xFFFFFFFF);
        deleteBtn.setPadding(dp(12), 0, dp(12), 0);
        deleteBtn.setMinimumHeight(dp(32));
        LinearLayout.LayoutParams deleteBtnParams = (LinearLayout.LayoutParams) deleteBtn.getLayoutParams();
        if (deleteBtnParams == null) deleteBtnParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        deleteBtnParams.leftMargin = dp(8);
        deleteBtn.setLayoutParams(deleteBtnParams);
        deleteBtn.setOnClickListener(v -> confirmDelete(id, title));
        actionRow.addView(deleteBtn);

        layout.addView(headerRow);
        layout.addView(tvContent);
        layout.addView(tvTime);
        layout.addView(actionRow);

        card.addView(layout);
        listContainer.addView(card);
    }

    private void enableAnnouncement(int id) {
        String url = ApiConfig.ANNOUNCEMENT + "/enable/" + id;
        RequestBody body = RequestBody.create("", MediaType.parse("application/json"));
        Request request = new Request.Builder().url(url).post(body).build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(() -> Toast.makeText(AdminAnnouncementActivity.this,
                        "操作失败", Toast.LENGTH_SHORT).show());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                runOnUiThread(() -> {
                    Toast.makeText(AdminAnnouncementActivity.this,
                            "已设为展示公告，用户登录时将看到此公告弹窗", Toast.LENGTH_LONG).show();
                    loadAllAnnouncements();
                });
            }
        });
    }

    private void disableAnnouncement(int id) {
        String url = ApiConfig.ANNOUNCEMENT + "/disable/" + id;
        RequestBody body = RequestBody.create("", MediaType.parse("application/json"));
        Request request = new Request.Builder().url(url).post(body).build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(() -> Toast.makeText(AdminAnnouncementActivity.this,
                        "操作失败", Toast.LENGTH_SHORT).show());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                runOnUiThread(() -> {
                    Toast.makeText(AdminAnnouncementActivity.this,
                            "已取消展示", Toast.LENGTH_SHORT).show();
                    loadAllAnnouncements();
                });
            }
        });
    }

    private void confirmDelete(int id, String title) {
        new android.app.AlertDialog.Builder(this)
                .setTitle("确认删除")
                .setMessage("确定要删除公告\"" + title + "\"吗？")
                .setPositiveButton("删除", (dialog, which) -> deleteAnnouncement(id))
                .setNegativeButton("取消", null)
                .show();
    }

    private void deleteAnnouncement(int id) {
        String url = ApiConfig.ANNOUNCEMENT + "/delete/" + id;
        Request request = new Request.Builder().url(url).delete().build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(() -> Toast.makeText(AdminAnnouncementActivity.this,
                        "删除失败", Toast.LENGTH_SHORT).show());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                runOnUiThread(() -> {
                    Toast.makeText(AdminAnnouncementActivity.this,
                            "删除成功", Toast.LENGTH_SHORT).show();
                    loadAllAnnouncements();
                });
            }
        });
    }

    private void saveAnnouncement() {
        String title = titleInput.getText().toString().trim();
        String content = contentInput.getText().toString().trim();
        boolean enabled = enabledSwitch.isChecked();

        if (title.isEmpty()) {
            titleInput.setError("请输入公告标题");
            return;
        }
        if (content.isEmpty()) {
            contentInput.setError("请输入公告内容");
            return;
        }

        try {
            JSONObject json = new JSONObject();
            json.put("title", title);
            json.put("content", content);
            json.put("enabled", enabled);
            if (currentAnnouncementId > 0) {
                json.put("id", currentAnnouncementId);
            }

            String url = ApiConfig.ANNOUNCEMENT + "/save";
            RequestBody body = RequestBody.create(json.toString(), MediaType.parse("application/json"));
            Request request = new Request.Builder().url(url).post(body).build();

            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    runOnUiThread(() -> Toast.makeText(AdminAnnouncementActivity.this,
                            "网络异常，保存失败", Toast.LENGTH_SHORT).show());
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    String responseData = response.body() != null ? response.body().string() : "";
                    runOnUiThread(() -> {
                        try {
                            JSONObject root = new JSONObject(responseData);
                            if (root.optBoolean("success", false)) {
                                Toast.makeText(AdminAnnouncementActivity.this,
                                        "公告保存成功", Toast.LENGTH_SHORT).show();
                                showListMode();
                                loadAllAnnouncements();
                            } else {
                                Toast.makeText(AdminAnnouncementActivity.this,
                                        root.optString("message", "保存失败"), Toast.LENGTH_SHORT).show();
                            }
                        } catch (Exception e) {
                            Toast.makeText(AdminAnnouncementActivity.this,
                                    "解析响应失败：" + e.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    });
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "数据异常", Toast.LENGTH_SHORT).show();
        }
    }

    private void previewAnnouncement() {
        String title = titleInput.getText().toString().trim();
        String content = contentInput.getText().toString().trim();

        if (title.isEmpty() && content.isEmpty()) {
            Toast.makeText(this, "请先输入公告内容", Toast.LENGTH_SHORT).show();
            return;
        }

        showAnnouncementDialog(title, content, "预览模式");
    }

    private void showAnnouncementDialog(String title, String content, String time) {
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_announcement);
        dialog.setCancelable(true);
        dialog.setCanceledOnTouchOutside(true);

        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.getWindow().setLayout(
                    (int) (getResources().getDisplayMetrics().widthPixels * 0.85),
                    ViewGroup.LayoutParams.WRAP_CONTENT);
        }

        TextView titleTv = dialog.findViewById(R.id.dialogAnnouncementTitle);
        TextView contentTv = dialog.findViewById(R.id.dialogAnnouncementContent);
        TextView timeTv = dialog.findViewById(R.id.dialogAnnouncementTime);
        com.google.android.material.checkbox.MaterialCheckBox dontShowAgain = dialog.findViewById(R.id.dontShowAgainCheckBox);
        com.google.android.material.button.MaterialButton confirmBtn = dialog.findViewById(R.id.dialogConfirmButton);

        titleTv.setText(title.isEmpty() ? "系统公告" : title);
        contentTv.setText(content);
        timeTv.setText(time);
        dontShowAgain.setVisibility(View.GONE);

        confirmBtn.setOnClickListener(v -> dialog.dismiss());

        dialog.show();
    }

    private void applyTemplate(String type) {
        switch (type) {
            case "spring":
                titleInput.setText("春季防疫提醒");
                contentInput.setText("春季是宠物传染病高发期，请注意以下事项：\n\n1. 及时接种春季疫苗\n2. 注意防寒保暖，避免温差过大\n3. 减少与流浪动物接触\n4. 保持环境卫生，定期消毒\n5. 如有异常症状，请及时就诊");
                break;
            case "summer":
                titleInput.setText("夏季驱虫提醒");
                contentInput.setText("夏季寄生虫活跃，请注意以下事项：\n\n1. 定期进行体内外驱虫\n2. 避免在草丛中长时间活动\n3. 注意防暑降温，避免中暑\n4. 保持饮水充足\n5. 注意食物保鲜，防止肠胃疾病");
                break;
            case "winter":
                titleInput.setText("冬季保暖提醒");
                contentInput.setText("冬季气温骤降，请注意以下事项：\n\n1. 做好宠物保暖措施\n2. 避免长时间户外活动\n3. 注意关节保护，老年宠物尤其注意\n4. 适当增加营养摄入\n5. 如有感冒症状，请及时就诊");
                break;
            case "holiday":
                titleInput.setText("假期就诊安排");
                contentInput.setText("假期就诊时间调整通知：\n\n1. 春节期间门诊时间调整为9:00-17:00\n2. 急诊24小时开放\n3. 建议提前预约，避免排队等候\n4. 假期期间疫苗注射正常进行\n5. 如有紧急情况，请拨打急诊电话");
                break;
            case "promo":
                titleInput.setText("优惠活动通知");
                contentInput.setText("本月优惠活动：\n\n1. 体检套餐8折优惠\n2. 疫苗接种满3针送1针\n3. 绝育手术减免200元\n4. 新用户首诊免挂号费\n5. 老客户推荐新客户双方各享9折\n\n活动时间：即日起至月底");
                break;
        }
        Toast.makeText(this, "已应用模板，可自行修改内容", Toast.LENGTH_SHORT).show();
    }

    private int dp(int dp) {
        return (int) (getResources().getDisplayMetrics().density * dp);
    }
}
