package com.example.pethospitalapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import okhttp3.*;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;

public class LoginActivity extends AppCompatActivity {

    //声明文本框和按钮
    private EditText emailEditText, passwordEditText;
    private Button loginButton, registerButton, switchButton;
    private TextView titleText;
    private boolean isAdminLogin = false;
    private OkHttpClient client = new OkHttpClient();

    //调用
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        //链接控件
        initializeViews();
        //监听
        setupListeners();
        //检测登录后的界面
        setupTestButtons();
    }


    private void setupTestButtons() {
        // 直接测试按钮 - 完全绕过网络
        Button directTestAdminBtn = findViewById(R.id.directTestAdminButton);
        if (directTestAdminBtn != null) {
            directTestAdminBtn.setOnClickListener(v -> {
                // 直接调用TestLoginHelper，不经过attemptLogin
                TestLoginHelper.bypassLogin(LoginActivity.this, true);
            });
        }

        Button directTestUserBtn = findViewById(R.id.directTestUserButton);
        if (directTestUserBtn != null) {
            directTestUserBtn.setOnClickListener(v -> {
                TestLoginHelper.bypassLogin(LoginActivity.this, false);
            });}}



    private void initializeViews() {
        emailEditText = findViewById(R.id.emailEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        loginButton = findViewById(R.id.loginButton);
        registerButton = findViewById(R.id.registerButton);
        switchButton = findViewById(R.id.switchLoginButton);
        titleText = findViewById(R.id.titleText);
    }

    private void setupListeners() {
        loginButton.setOnClickListener(v -> attemptLogin());

        registerButton.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        });

        switchButton.setOnClickListener(v -> {
            isAdminLogin = !isAdminLogin;
            if (isAdminLogin) {
                titleText.setText("管理员登录");
                switchButton.setText("切换到用户登录");
            } else {
                titleText.setText("用户登录");
                switchButton.setText("切换到管理员登录");
            }
        });
    }

    private void attemptLogin() {
        String email = emailEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "请输入邮箱和密码", Toast.LENGTH_SHORT).show();
            return;
        }

        // 调用登录API
        loginUser(email, password, isAdminLogin);
    }

    private void loginUser(String email, String password, boolean isAdmin) {
        // 关键：根据是否管理员，用不同的接口地址
        String url;
        if (isAdmin) {
            url = ApiConfig.API_ADMIN + "/login";
        } else {
            url = ApiConfig.API_USER + "/login";
        }

        // 创建JSON对象
        JSONObject json = new JSONObject();
        try {
            json.put("email", email);
            json.put("password", password);
            // json.put("isAdmin", isAdmin);
        } catch (JSONException e) {
            e.printStackTrace();
            runOnUiThread(() ->
                    Toast.makeText(LoginActivity.this, "数据格式错误", Toast.LENGTH_SHORT).show());
            return;
        }

        RequestBody body = RequestBody.create(
                json.toString(),
                MediaType.parse("application/json"));

        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(() ->
                        Toast.makeText(LoginActivity.this, "网络错误: " + e.getMessage(), Toast.LENGTH_SHORT).show());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String responseBody = response.body() != null ? response.body().string() : "";

                runOnUiThread(() -> {
                    try {
                        if (response.isSuccessful()) {
                            JSONObject jsonResponse = new JSONObject(responseBody);

                            if (jsonResponse.has("success") && jsonResponse.getBoolean("success")) {
                                String userType = jsonResponse.getString("userType");

                                Intent intent;
                                if ("ADMIN".equals(userType)) {
                                    intent = new Intent(LoginActivity.this, AdminDashboardActivity.class);
                                } else {
                                    intent = new Intent(LoginActivity.this, UserDashboardActivity.class);
                                }

                                if (jsonResponse.has("user")) {
                                    JSONObject userObj = jsonResponse.getJSONObject("user");
                                    String userData = userObj.toString();
                                    intent.putExtra("USER_DATA", userData);

                                    // 安全获取用户ID
                                    String userId = "";
                                    if (userObj.has("id")) userId = userObj.getString("id");
                                    else if (userObj.has("userId")) userId = userObj.getString("userId");

                                    // 安全获取用户名
                                    String username = "";
                                    if (userObj.has("username")) username = userObj.getString("username");
                                    else if (userObj.has("name")) username = userObj.getString("name");

                                    //
                                    String email = "";
                                    if (userObj.has("email")) email = userObj.getString("email");

                                    String phone = "";
                                    if (userObj.has("phone")) phone = userObj.getString("phone");
                                    else if (userObj.has("telephone")) phone = userObj.getString("telephone");
                                    //

                                    // 保存全部信息到本地
                                    SharedPreferences sp = getSharedPreferences("user", MODE_PRIVATE);
                                    sp.edit().putString("id", userId).apply();
                                    sp.edit().putString("name", username).apply();
                                    sp.edit().putString("email", email).apply();  // 保存邮箱
                                    sp.edit().putString("phone", phone).apply();  // 保存手机
                                }

                                startActivity(intent);
                                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                                finish();
                            } else {
                                String message = jsonResponse.optString("message", "登录失败");
                                Toast.makeText(LoginActivity.this, message, Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            try {
                                JSONObject errorJson = new JSONObject(responseBody);
                                String errorMsg = errorJson.optString("message", "登录失败");
                                Toast.makeText(LoginActivity.this, errorMsg, Toast.LENGTH_SHORT).show();
                            } catch (JSONException e) {
                                Toast.makeText(LoginActivity.this,
                                        "登录失败，状态码: " + response.code(),
                                        Toast.LENGTH_SHORT).show();
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(LoginActivity.this,
                                "数据解析错误: " + e.getMessage(),
                                Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }
}