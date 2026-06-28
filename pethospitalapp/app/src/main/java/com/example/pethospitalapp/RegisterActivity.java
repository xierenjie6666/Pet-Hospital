package com.example.pethospitalapp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.textfield.TextInputLayout;
import okhttp3.*;
import org.json.JSONObject;
import java.io.IOException;
import java.util.regex.Pattern;

public class RegisterActivity extends AppCompatActivity {

    private EditText nameEditText, emailEditText, phoneEditText, passwordEditText, confirmPasswordEditText;
    private TextInputLayout emailLayout, phoneLayout, pwdLayout, confirmPwdLayout;
    private Button registerButton, backButton;
    private OkHttpClient client = new OkHttpClient();

    private static final Pattern EMAIL_PATTERN =
            Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");

    private static final Pattern PHONE_PATTERN =
            Pattern.compile("^1[3-9]\\d{9}$");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        initializeViews();
        setupListeners();
        setupFocusValidation();
    }

    private void initializeViews() {
        nameEditText = findViewById(R.id.nameEditText);
        emailEditText = findViewById(R.id.emailEditText);
        phoneEditText = findViewById(R.id.phoneEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        confirmPasswordEditText = findViewById(R.id.confirmPasswordEditText);

        emailLayout = findViewById(R.id.emailLayout);
        phoneLayout = findViewById(R.id.phoneLayout);
        pwdLayout = findViewById(R.id.pwdLayout);
        confirmPwdLayout = findViewById(R.id.confirmPwdLayout);

        registerButton = findViewById(R.id.registerButton);
        backButton = findViewById(R.id.backButton);
    }

    private void setupListeners() {
        registerButton.setOnClickListener(v -> attemptRegister());
        backButton.setOnClickListener(v -> {
            startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left);
            finish();
        });
    }


    private void setupFocusValidation() {
        // 邮箱
        emailEditText.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) {
                String email = emailEditText.getText().toString().trim();
                if (email.isEmpty()) {
                    emailLayout.setError(null);
                } else if (!EMAIL_PATTERN.matcher(email).matches()) {
                    emailLayout.setError("格式：example@mail.com");
                } else {
                    emailLayout.setError(null);
                }
            }
        });

        // 手机
        phoneEditText.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) {
                String phone = phoneEditText.getText().toString().trim();
                if (phone.isEmpty()) {
                    phoneLayout.setError(null);
                } else if (!PHONE_PATTERN.matcher(phone).matches()) {
                    phoneLayout.setError("请输入11位有效手机号");
                } else {
                    phoneLayout.setError(null);
                }
            }
        });

        // 密码
        passwordEditText.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) {
                String pwd = passwordEditText.getText().toString().trim();
                if (pwd.isEmpty()) {
                    pwdLayout.setError(null);
                } else if (pwd.length() < 6) {
                    pwdLayout.setError("密码至少6位");
                } else {
                    pwdLayout.setError(null);
                }
            }
        });

        // 确认密码
        confirmPasswordEditText.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) {
                String pwd = passwordEditText.getText().toString().trim();
                String confirm = confirmPasswordEditText.getText().toString().trim();
                if (confirm.isEmpty()) {
                    confirmPwdLayout.setError(null);
                } else if (!pwd.equals(confirm)) {
                    confirmPwdLayout.setError("两次密码不一致");
                } else {
                    confirmPwdLayout.setError(null);
                }
            }
        });
    }

    private void attemptRegister() {
        String name = nameEditText.getText().toString().trim();
        String email = emailEditText.getText().toString().trim();
        String phone = phoneEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();
        String confirmPassword = confirmPasswordEditText.getText().toString().trim();

        emailLayout.setError(null);
        phoneLayout.setError(null);
        pwdLayout.setError(null);
        confirmPwdLayout.setError(null);

        if (name.isEmpty() || email.isEmpty() || phone.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "请填写所有字段", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!EMAIL_PATTERN.matcher(email).matches()) {
            emailLayout.setError("格式：example@mail.com");
            return;
        }

        if (!PHONE_PATTERN.matcher(phone).matches()) {
            phoneLayout.setError("请输入11位有效手机号");
            return;
        }

        if (password.length() < 6) {
            pwdLayout.setError("密码至少6位");
            return;
        }

        if (!password.equals(confirmPassword)) {
            confirmPwdLayout.setError("两次密码不一致");
            return;
        }

        registerUser(name, email, phone, password);
    }

    private void registerUser(String name, String email, String phone, String password) {
        String url = ApiConfig.API_USER + "/register";

        try {
            JSONObject json = new JSONObject();
            json.put("name", name);
            json.put("email", email);
            json.put("phone", phone);
            json.put("password", password);

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
                    runOnUiThread(() -> Toast.makeText(RegisterActivity.this, "网络错误", Toast.LENGTH_SHORT).show());
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    String body = response.body().string();
                    try {
                        JSONObject result = new JSONObject(body);
                        int code = result.getInt("code");
                        String msg = result.getString("msg");

                        runOnUiThread(() -> {
                            if (code == 200) {
                                Toast.makeText(RegisterActivity.this, "注册成功！", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
                                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left);
                                finish();
                            } else {
                                Toast.makeText(RegisterActivity.this, msg, Toast.LENGTH_SHORT).show();
                            }
                        });
                    } catch (Exception e) {
                        runOnUiThread(() -> Toast.makeText(RegisterActivity.this, "注册失败", Toast.LENGTH_SHORT).show());
                    }
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}