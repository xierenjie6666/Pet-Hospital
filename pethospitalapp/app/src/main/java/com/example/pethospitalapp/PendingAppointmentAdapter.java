package com.example.pethospitalapp;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class PendingAppointmentAdapter extends RecyclerView.Adapter<PendingAppointmentAdapter.ViewHolder> {

    private List<PendingAppointmentsActivity.AppointmentItem> appointmentList;
    private boolean showConfirmButton;
    private OnActionListener listener;

    public interface OnActionListener {
        void onConfirm(int id);
        void onReject(int id);
    }

    public PendingAppointmentAdapter(List<PendingAppointmentsActivity.AppointmentItem> appointmentList,
                                     boolean showConfirmButton,
                                     OnActionListener listener) {
        this.appointmentList = appointmentList;
        this.showConfirmButton = showConfirmButton;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_pending_appointment, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        PendingAppointmentsActivity.AppointmentItem item = appointmentList.get(position);

        // 安全地设置文本，防止空指针
        if (holder.petNameText != null) {
            holder.petNameText.setText("宠物：" + (item.petName != null ? item.petName : "未知"));
        }

        if (holder.petTypeText != null) {
            holder.petTypeText.setText("类型：" + (item.petType != null && !item.petType.isEmpty() ? item.petType : "未填写"));
        }

        if (holder.serviceText != null) {
            holder.serviceText.setText("服务：" + (item.serviceType != null && !item.serviceType.isEmpty() ? item.serviceType : "未填写"));
        }

        if (holder.doctorText != null) {
            holder.doctorText.setText("医生：" + (item.doctor != null && !item.doctor.isEmpty() ? item.doctor : "未分配"));
        }

        if (holder.timeText != null) {
            String time = (item.appointmentDate != null ? item.appointmentDate : "") + " " + (item.appointmentTime != null ? item.appointmentTime : "");
            holder.timeText.setText("时间：" + (time.trim().isEmpty() ? "未填写" : time));
        }

        if (holder.symptomsText != null) {
            holder.symptomsText.setText("症状：" + (item.symptoms != null && !item.symptoms.isEmpty() ? item.symptoms : "无"));
        }

        // 设置状态文本和颜色
        if (holder.statusText != null) {
            if ("PENDING".equals(item.status)) {
                holder.statusText.setText("待处理");
                holder.statusText.setTextColor(0xFFFFA000);
            } else if ("CANCELLED".equals(item.status)) {
                holder.statusText.setText("待撤销");
                holder.statusText.setTextColor(0xFFF44336);
            } else {
                holder.statusText.setText(item.status != null ? item.status : "未知");
            }
        }

        // 设置按钮可见性
        if (holder.confirmButton != null) {
            if ("PENDING".equals(item.status)) {
                holder.confirmButton.setVisibility(showConfirmButton ? View.VISIBLE : View.GONE);
            } else if ("CANCELLED".equals(item.status)) {
                holder.confirmButton.setVisibility(View.VISIBLE);
            } else {
                holder.confirmButton.setVisibility(View.GONE);
            }

            holder.confirmButton.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onConfirm(item.id);
                }
            });
        }

        if (holder.rejectButton != null) {
            if ("PENDING".equals(item.status) && showConfirmButton) {
                holder.rejectButton.setVisibility(View.VISIBLE);
                holder.rejectButton.setOnClickListener(v -> {
                    if (listener != null) {
                        listener.onReject(item.id);
                    }
                });
            } else {
                holder.rejectButton.setVisibility(View.GONE);
            }
        }
    }

    @Override
    public int getItemCount() {
        return appointmentList != null ? appointmentList.size() : 0;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView petNameText, petTypeText, serviceText, doctorText, timeText, symptomsText, statusText;
        Button confirmButton, rejectButton;

        ViewHolder(View itemView) {
            super(itemView);
            // 确保所有ID都与您的item_pending_appointment.xml中的ID匹配
            petNameText = itemView.findViewById(R.id.petNameText);
            petTypeText = itemView.findViewById(R.id.petTypeText);
            serviceText = itemView.findViewById(R.id.serviceText);
            doctorText = itemView.findViewById(R.id.doctorText);
            timeText = itemView.findViewById(R.id.timeText);
            symptomsText = itemView.findViewById(R.id.symptomsText);
            statusText = itemView.findViewById(R.id.statusText);
            confirmButton = itemView.findViewById(R.id.confirmButton);
            rejectButton = itemView.findViewById(R.id.rejectButton);
        }
    }
}