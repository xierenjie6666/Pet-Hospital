package com.example.pethospitalapp;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class AppointmentAdminAdapter extends RecyclerView.Adapter<AppointmentAdminAdapter.ViewHolder> {

    private List<AppointmentManagementActivity.AppointmentItem> list;
    private OnActionListener listener;

    public interface OnActionListener {
        void onConfirm(int id);
        void onReject(int id);
    }

    public AppointmentAdminAdapter(List<AppointmentManagementActivity.AppointmentItem> list, OnActionListener listener) {
        this.list = list;
        this.listener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_appointment_admin, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        AppointmentManagementActivity.AppointmentItem item = list.get(position);

        holder.petInfoText.setText("宠物：" + item.petName + (item.petType.isEmpty() ? "" : "（" + item.petType + "）"));
        holder.serviceText.setText("服务：" + item.serviceType);
        holder.doctorText.setText("医生：" + (item.doctor.isEmpty() ? "无" : item.doctor));
        holder.timeText.setText("时间：" + item.appointmentDate + " " + item.appointmentTime);
        holder.symptomsText.setText("症状：" + (item.symptoms.isEmpty() ? "无" : item.symptoms));

        holder.confirmButton.setOnClickListener(v -> listener.onConfirm(item.id));
        holder.rejectButton.setOnClickListener(v -> listener.onReject(item.id));
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView petInfoText, serviceText, doctorText, timeText, symptomsText;
        Button confirmButton, rejectButton;

        ViewHolder(View itemView) {
            super(itemView);
            petInfoText = itemView.findViewById(R.id.petInfoText);
            serviceText = itemView.findViewById(R.id.serviceText);
            doctorText = itemView.findViewById(R.id.doctorText);
            timeText = itemView.findViewById(R.id.timeText);
            symptomsText = itemView.findViewById(R.id.symptomsText);
            confirmButton = itemView.findViewById(R.id.confirmButton);
            rejectButton = itemView.findViewById(R.id.rejectButton);
        }
    }
}