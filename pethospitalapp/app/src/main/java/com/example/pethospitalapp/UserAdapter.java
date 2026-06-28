package com.example.pethospitalapp;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.ViewHolder> {

    private List<UserItem> list; // 修改点：引用独立的 UserItem
    private OnUserActionListener listener;

    public interface OnUserActionListener {
        void onEdit(UserItem user); // 修改点
        void onDelete(int id, String name);
    }

    public UserAdapter(List<UserItem> list, OnUserActionListener listener) { // 修改点
        this.list = list;
        this.listener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_user, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        UserItem user = list.get(position); // 修改点

        holder.userNameText.setText(user.name);
        holder.userEmailText.setText("邮箱：" + user.email);
        holder.userPhoneText.setText("电话：" + (user.phone == null || user.phone.isEmpty() ? "未填写" : user.phone));
        holder.userRoleText.setText( "普通用户" );

        holder.editButton.setOnClickListener(v -> listener.onEdit(user));
        holder.deleteButton.setOnClickListener(v -> listener.onDelete(user.id, user.name));
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public void updateList(List<UserItem> newList) { // 修改点
        this.list = newList;
        notifyDataSetChanged();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView userNameText, userEmailText, userPhoneText, userRoleText;
        Button editButton, deleteButton;

        ViewHolder(View itemView) {
            super(itemView);
            userNameText = itemView.findViewById(R.id.userNameText);
            userEmailText = itemView.findViewById(R.id.userEmailText);
            userPhoneText = itemView.findViewById(R.id.userPhoneText);
            userRoleText = itemView.findViewById(R.id.userRoleText);
            editButton = itemView.findViewById(R.id.editButton);
            deleteButton = itemView.findViewById(R.id.deleteButton);
        }
    }
}