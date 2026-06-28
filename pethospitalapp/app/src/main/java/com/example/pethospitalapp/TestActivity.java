package com.example.pethospitalapp;

import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class TestActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TextView tv = new TextView(this);
        tv.setText("测试成功！\n用户管理界面可以打开了");
        tv.setTextSize(20);
        tv.setGravity(android.view.Gravity.CENTER);
        setContentView(tv);
    }
}