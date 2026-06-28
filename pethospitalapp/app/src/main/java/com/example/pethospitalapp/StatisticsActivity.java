package com.example.pethospitalapp;

import android.graphics.Color;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
import okhttp3.*;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class StatisticsActivity extends AppCompatActivity {

    private PieChart pieChart;        // 今日饼图
    private PieChart monthPieChart;   // 本月饼图
    private PieChart yearPieChart;    // 本年度饼图
    private ProgressBar progressBar;
    private Button refreshButton;

    private OkHttpClient client = new OkHttpClient();
    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.CHINA);
    private SimpleDateFormat monthFormat = new SimpleDateFormat("yyyy-MM", Locale.CHINA);
    private SimpleDateFormat yearFormat = new SimpleDateFormat("yyyy", Locale.CHINA);

    // 服务类型对应的颜色映射
    private Map<String, Integer> serviceTypeColors = new HashMap<>();
    private String[] serviceOrder = {"常规检查", "疫苗接种", "绝育手术", "牙齿清洁", "急诊", "其他"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistics);

        initializeViews();
        setupToolbar();
        setupListeners();
        initColors();

        // 加载所有统计数据
        loadAllStatistics();
    }

    private void initializeViews() {
        pieChart = findViewById(R.id.pieChart);
        monthPieChart = findViewById(R.id.monthPieChart);
        yearPieChart = findViewById(R.id.yearPieChart);
        progressBar = findViewById(R.id.progressBar);
        refreshButton = findViewById(R.id.refreshButton);
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("统计分析");
        }
    }

    private void setupListeners() {
        refreshButton.setOnClickListener(v -> loadAllStatistics());
    }

    private void initColors() {
        serviceTypeColors.put("常规检查", Color.parseColor("#FF6B6B"));
        serviceTypeColors.put("疫苗接种", Color.parseColor("#4ECDC4"));
        serviceTypeColors.put("绝育手术", Color.parseColor("#45B7D1"));
        serviceTypeColors.put("牙齿清洁", Color.parseColor("#96CEB4"));
        serviceTypeColors.put("急诊", Color.parseColor("#FFEAA7"));
        serviceTypeColors.put("其他", Color.parseColor("#A9A9A9"));
    }

    private void loadAllStatistics() {
        showLoading(true);

        String url = ApiConfig.APPOINTMENTS + "/all";

        Request request = new Request.Builder()
                .url(url)
                .get()
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(() -> {
                    showLoading(false);
                    Toast.makeText(StatisticsActivity.this,
                            "网络错误：" + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String json = response.body().string();
                runOnUiThread(() -> {
                    showLoading(false);
                    if (response.isSuccessful()) {
                        parseAndDisplayAllStatistics(json);
                    } else {
                        Toast.makeText(StatisticsActivity.this,
                                "加载失败：" + response.code(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    private void parseAndDisplayAllStatistics(String json) {
        try {
            JSONObject root = new JSONObject(json);
            JSONArray appointments = null;

            // 解析JSON数组
            if (root.has("user")) {
                Object userObj = root.get("user");
                if (userObj instanceof JSONArray) {
                    appointments = (JSONArray) userObj;
                }
            } else if (root.has("data")) {
                Object dataObj = root.get("data");
                if (dataObj instanceof JSONArray) {
                    appointments = (JSONArray) dataObj;
                }
            }

            if (appointments == null) {
                Toast.makeText(this, "无法解析数据", Toast.LENGTH_SHORT).show();
                return;
            }

            // 获取当前日期信息
            Calendar calendar = Calendar.getInstance();
            String today = dateFormat.format(calendar.getTime());
            String currentMonth = monthFormat.format(calendar.getTime());
            String currentYear = yearFormat.format(calendar.getTime());

            // 统计三个时间段的数据
            Map<String, Integer> todayCount = new HashMap<>();
            Map<String, Integer> monthCount = new HashMap<>();
            Map<String, Integer> yearCount = new HashMap<>();

            for (int i = 0; i < appointments.length(); i++) {
                JSONObject obj = appointments.getJSONObject(i);
                String appointmentDate = obj.optString("appointmentDate", "");
                String serviceType = obj.optString("serviceType", "其他");

                if (serviceType == null || serviceType.isEmpty()) {
                    serviceType = "其他";
                }
                serviceType = mapServiceType(serviceType);

                // 今日统计
                if (appointmentDate.equals(today)) {
                    todayCount.put(serviceType, todayCount.getOrDefault(serviceType, 0) + 1);
                }

                // 本月统计
                if (appointmentDate.startsWith(currentMonth)) {
                    monthCount.put(serviceType, monthCount.getOrDefault(serviceType, 0) + 1);
                }

                // 本年度统计
                if (appointmentDate.startsWith(currentYear)) {
                    yearCount.put(serviceType, yearCount.getOrDefault(serviceType, 0) + 1);
                }
            }

            // 显示三个饼图
            setupPieChart(pieChart, todayCount, "今日暂无数据");
            setupPieChart(monthPieChart, monthCount, "本月暂无数据");
            setupPieChart(yearPieChart, yearCount, "本年度暂无数据");

        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "解析数据失败：" + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void setupPieChart(PieChart chart, Map<String, Integer> dataMap, String noDataText) {
        if (chart == null) return;

        // 计算总数
        int total = 0;
        for (int count : dataMap.values()) {
            total += count;
        }

        if (total == 0) {
            chart.clear();
            chart.setNoDataText(noDataText);
            chart.invalidate();
            return;
        }

        // 创建饼图数据
        List<PieEntry> entries = new ArrayList<>();
        List<Integer> colors = new ArrayList<>();

        for (String serviceType : serviceOrder) {
            if (dataMap.containsKey(serviceType)) {
                int count = dataMap.get(serviceType);
                float percentage = (float) count / total * 100;
                entries.add(new PieEntry(count, serviceType + " (" + String.format("%.1f", percentage) + "%)"));

                if (serviceTypeColors.containsKey(serviceType)) {
                    colors.add(serviceTypeColors.get(serviceType));
                } else {
                    colors.add(ColorTemplate.COLORFUL_COLORS[colors.size() % ColorTemplate.COLORFUL_COLORS.length]);
                }
            }
        }

        // 如果没有有效数据，显示提示
        if (entries.isEmpty()) {
            chart.clear();
            chart.setNoDataText(noDataText);
            chart.invalidate();
            return;
        }

        PieDataSet dataSet = new PieDataSet(entries, "服务类型占比");
        dataSet.setColors(colors);
        dataSet.setValueTextSize(12f);
        dataSet.setValueTextColor(Color.WHITE);
        dataSet.setValueFormatter(new PercentFormatter(chart));
        dataSet.setSliceSpace(3f);
        dataSet.setSelectionShift(5f);

        PieData pieData = new PieData(dataSet);

        // 配置饼图
        chart.setData(pieData);
        chart.setUsePercentValues(true);
        chart.getDescription().setEnabled(false);
        chart.setExtraOffsets(5, 10, 5, 5);
        chart.setDragDecelerationFrictionCoef(0.95f);
        chart.setDrawHoleEnabled(true);
        chart.setHoleRadius(40f);
        chart.setTransparentCircleRadius(45f);
        chart.setDrawCenterText(true);
        chart.setCenterText("服务类型\n占比");
        chart.setCenterTextSize(14f);
        chart.setCenterTextColor(Color.GRAY);
        chart.animateY(1000);

        // 设置图例
        Legend legend = chart.getLegend();
        legend.setEnabled(true);
        legend.setVerticalAlignment(Legend.LegendVerticalAlignment.BOTTOM);
        legend.setHorizontalAlignment(Legend.LegendHorizontalAlignment.CENTER);
        legend.setOrientation(Legend.LegendOrientation.HORIZONTAL);
        legend.setDrawInside(false);
        legend.setTextSize(10f);

        chart.invalidate();
    }

    private String mapServiceType(String serviceType) {
        if (serviceType == null || serviceType.isEmpty()) {
            return "其他";
        }
        if (serviceType.contains("常规") || serviceType.equals("常规检查")) {
            return "常规检查";
        } else if (serviceType.contains("疫苗") || serviceType.equals("疫苗接种")) {
            return "疫苗接种";
        } else if (serviceType.contains("绝育") || serviceType.equals("绝育手术")) {
            return "绝育手术";
        } else if (serviceType.contains("牙齿") || serviceType.equals("牙齿清洁")) {
            return "牙齿清洁";
        } else if (serviceType.contains("急诊") || serviceType.equals("急诊")) {
            return "急诊";
        } else {
            return "其他";
        }
    }

    private void showLoading(boolean show) {
        progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}