package com.example.pethospitalapp;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.widget.Toast;

/**
 * 测试登录助手 - 独立类，不修改原LoginActivity
 * 提供直接登录功能，绕过网络验证
 */
public class TestLoginHelper {

    // 测试账号数据
    public static class TestAccount {
        public String email;
        public String password;
        public String name;
        public boolean isAdmin;

        public TestAccount(String email, String password, String name, boolean isAdmin) {
            this.email = email;
            this.password = password;
            this.name = name;
            this.isAdmin = isAdmin;
        }
    }

    // 预定义的测试账号
    public static final TestAccount ADMIN_ACCOUNT =
            new TestAccount("admin@pet.com", "admin123", "管理员", true);

    public static final TestAccount USER_ACCOUNT =
            new TestAccount("user@pet.com", "user123", "普通用户", false);

    /**
     * 完全绕过LoginActivity的登录方法
     * 直接从测试按钮调用，不经过LoginActivity的attemptLogin
     */
    public static void bypassLogin(Context context, boolean isAdmin) {
        // 模拟加载过程
        ProgressDialog progressDialog = new ProgressDialog(context);
        progressDialog.setMessage("测试登录中...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        // 模拟网络延迟
        new android.os.Handler().postDelayed(() -> {
            progressDialog.dismiss();

            TestAccount account = isAdmin ? ADMIN_ACCOUNT : USER_ACCOUNT;

            // 保存用户信息
            SharedPreferences prefs = context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = prefs.edit();
            editor.putString("user_email", account.email);
            editor.putString("user_name", account.name);
            editor.putBoolean("is_admin", account.isAdmin);
            editor.putBoolean("is_logged_in", true);
            editor.putBoolean("is_test_account", true);
            editor.apply();

            // 直接跳转，不经过LoginActivity
            Class<?> targetActivity = account.isAdmin ?
                    AdminDashboardActivity.class : UserDashboardActivity.class;

            Intent intent = new Intent(context, targetActivity);
            intent.putExtra("USER_NAME", account.name);
            intent.putExtra("USER_EMAIL", account.email);
            intent.putExtra("IS_ADMIN", account.isAdmin);
            intent.putExtra("BYPASS_LOGIN", true); // 标记为绕过登录

            if (context instanceof android.app.Activity) {
                android.app.Activity activity = (android.app.Activity) context;
                activity.startActivity(intent);
                activity.finish();
            } else {
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
            }

            Toast.makeText(context,
                    "✅ 测试登录成功！\n" + account.name + " - " + account.email,
                    Toast.LENGTH_SHORT).show();

        }, 1000);
    }

    /**
     * 快速管理员登录（旧版方法）
     */
    public static void loginAsAdmin(Context context) {
        bypassLogin(context, true);
    }

    /**
     * 快速普通用户登录（旧版方法）
     */
    public static void loginAsUser(Context context) {
        bypassLogin(context, false);
    }

    /**
     * 获取所有测试账号信息
     */
    public static String getTestAccountsInfo() {
        return "📋 测试账号信息:\n\n" +
                "1. " + ADMIN_ACCOUNT.name + "\n" +
                "   邮箱: " + ADMIN_ACCOUNT.email + "\n" +
                "   密码: " + ADMIN_ACCOUNT.password + "\n\n" +
                "2. " + USER_ACCOUNT.name + "\n" +
                "   邮箱: " + USER_ACCOUNT.email + "\n" +
                "   密码: " + USER_ACCOUNT.password;
    }
}