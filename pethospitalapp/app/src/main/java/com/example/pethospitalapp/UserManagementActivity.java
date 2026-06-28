package com.example.pethospitalapp;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import okhttp3.*;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class UserManagementActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private TextView emptyView;
    private EditText searchInput;
    private Button addUserButton;

    private UserAdapter adapter;
    private List<UserItem> userList = new ArrayList<>();
    private List<UserItem> filteredList = new ArrayList<>();

    private OkHttpClient client = new OkHttpClient();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_management);

        // 设置自定义 Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // 设置返回箭头和标题
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("用户管理");
        }

        recyclerView = findViewById(R.id.recyclerView);
        emptyView = findViewById(R.id.emptyView);
        searchInput = findViewById(R.id.searchInput);
        addUserButton = findViewById(R.id.addUserButton);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // 从后端加载用户数据
        loadUsersFromBackend();

        addUserButton.setOnClickListener(v -> {
            Toast.makeText(this, "添加用户功能开发中", Toast.LENGTH_SHORT).show();
        });

        // 搜索功能
        searchInput.addTextChangedListener(new android.text.TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override
            public void afterTextChanged(android.text.Editable s) {
                filterUsers(s.toString());
            }
        });
    }

    private void loadUsersFromBackend() {
        String url = ApiConfig.API_USER + "/all";

        Request request = new Request.Builder().url(url).get().build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(() -> {
                    emptyView.setVisibility(View.VISIBLE);
                    emptyView.setText("网络错误：" + e.getMessage());
                    recyclerView.setVisibility(View.GONE);
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String json = response.body().string();
                runOnUiThread(() -> {
                    try {
                        JSONObject root = new JSONObject(json);

                        JSONArray array = null;
                        if (root.has("user") && root.get("user") instanceof JSONArray) {
                            array = root.getJSONArray("user");
                        } else if (root.has("data") && root.get("data") instanceof JSONArray) {
                            array = root.getJSONArray("data");
                        }

                        userList.clear();

                        if (array != null && array.length() > 0) {
                            for (int i = 0; i < array.length(); i++) {
                                JSONObject obj = array.getJSONObject(i);
                                UserItem item = new UserItem();
                                item.id = obj.optInt("id", 0);
                                item.name = obj.optString("name", "");
                                item.email = obj.optString("email", "");
                                item.phone = obj.optString("phone", "");
                                item.role = obj.optString("role", "USER");
                                userList.add(item);
                            }

                            filteredList.clear();
                            filteredList.addAll(userList);

                            if (filteredList.isEmpty()) {
                                emptyView.setVisibility(View.VISIBLE);
                                recyclerView.setVisibility(View.GONE);
                            } else {
                                emptyView.setVisibility(View.GONE);
                                recyclerView.setVisibility(View.VISIBLE);
                                adapter = new UserAdapter(filteredList, new UserAdapter.OnUserActionListener() {
                                    @Override
                                    public void onEdit(UserItem user) {
                                        Toast.makeText(UserManagementActivity.this, "编辑: " + user.name, Toast.LENGTH_SHORT).show();
                                    }
                                    @Override
                                    public void onDelete(int id, String name) {
                                        showDeleteConfirmDialog(id, name);
                                    }
                                });
                                recyclerView.setAdapter(adapter);
                            }
                        } else {
                            emptyView.setVisibility(View.VISIBLE);
                            recyclerView.setVisibility(View.GONE);
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                        emptyView.setVisibility(View.VISIBLE);
                        emptyView.setText("解析错误：" + e.getMessage());
                    }
                });
            }
        });
    }

    private void filterUsers(String query) {
        filteredList.clear();
        if (query.isEmpty()) {
            filteredList.addAll(userList);
        } else {
            for (UserItem user : userList) {
                if (user.name.toLowerCase().contains(query.toLowerCase()) ||
                        user.email.toLowerCase().contains(query.toLowerCase())) {
                    filteredList.add(user);
                }
            }
        }

        if (adapter != null) {
            adapter.updateList(filteredList);
        }

        if (filteredList.isEmpty()) {
            emptyView.setVisibility(View.VISIBLE);
            emptyView.setText("未找到相关用户");
            recyclerView.setVisibility(View.GONE);
        } else {
            emptyView.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
        }
    }

    private void showDeleteConfirmDialog(int id, String name) {
        new android.app.AlertDialog.Builder(this)
                .setTitle("确认删除")
                .setMessage("确定要删除用户 \"" + name + "\" 吗？")
                .setPositiveButton("确定", (dialog, which) -> deleteUser(id))
                .setNegativeButton("取消", null)
                .show();
    }

    private void deleteUser(int id) {
        String url = ApiConfig.API_USER + "/delete/" + id;

        Request request = new Request.Builder().url(url).delete().build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(() -> Toast.makeText(UserManagementActivity.this, "删除失败", Toast.LENGTH_SHORT).show());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                runOnUiThread(() -> {
                    Toast.makeText(UserManagementActivity.this, "删除成功", Toast.LENGTH_SHORT).show();
                    loadUsersFromBackend(); // 刷新列表
                });
            }
        });
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