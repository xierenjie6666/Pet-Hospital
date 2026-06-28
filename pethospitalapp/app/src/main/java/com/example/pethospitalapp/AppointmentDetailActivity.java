package com.example.pethospitalapp;

import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class AppointmentDetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_appointment_detail);

        // 简单的详情页面，可以根据需要扩展
        TextView detailText = findViewById(R.id.detailText);
        detailText.setText("预约详情页面\n\n功能待实现");
    }
}