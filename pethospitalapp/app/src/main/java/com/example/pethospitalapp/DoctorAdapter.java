package com.example.pethospitalapp;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import java.util.List;

public class DoctorAdapter extends RecyclerView.Adapter<DoctorAdapter.DoctorViewHolder> {

    private List<Doctor> doctors;
    private OnDoctorSelectedListener listener;

    public interface OnDoctorSelectedListener {
        void onDoctorSelected(Doctor doctor);
    }

    public DoctorAdapter(List<Doctor> doctors, OnDoctorSelectedListener listener) {
        this.doctors = doctors;
        this.listener = listener;
    }

    @NonNull
    @Override
    public DoctorViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_doctor, parent, false);
        return new DoctorViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DoctorViewHolder holder, int position) {
        Doctor doctor = doctors.get(position);
        holder.bind(doctor, listener);
    }

    @Override
    public int getItemCount() {
        return doctors.size();
    }

    public void updateList(List<Doctor> newDoctors) {
        this.doctors = newDoctors;
        notifyDataSetChanged();
    }

    static class DoctorViewHolder extends RecyclerView.ViewHolder {
        private ImageView doctorAvatarImg;
        private TextView doctorNameText;
        private TextView doctorGenderText;
        private TextView doctorAnimalTypeText;
        private TextView doctorPhoneText;
        private TextView doctorWorkTimeText;
        private ImageView expandToggleIcon;
        private LinearLayout expandableSection;
        private TextView doctorIntroductionText;
        private ChipGroup availableSlotsChipGroup;
        private MaterialButton selectDoctorBtn;

        public DoctorViewHolder(@NonNull View itemView) {
            super(itemView);
            doctorAvatarImg = itemView.findViewById(R.id.doctorAvatarImg);
            doctorNameText = itemView.findViewById(R.id.doctorNameText);
            doctorGenderText = itemView.findViewById(R.id.doctorGenderText);
            doctorAnimalTypeText = itemView.findViewById(R.id.doctorAnimalTypeText);
            doctorPhoneText = itemView.findViewById(R.id.doctorPhoneText);
            doctorWorkTimeText = itemView.findViewById(R.id.doctorWorkTimeText);
            expandToggleIcon = itemView.findViewById(R.id.expandToggleIcon);
            expandableSection = itemView.findViewById(R.id.expandableSection);
            doctorIntroductionText = itemView.findViewById(R.id.doctorIntroductionText);
            availableSlotsChipGroup = itemView.findViewById(R.id.availableSlotsChipGroup);
            selectDoctorBtn = itemView.findViewById(R.id.selectDoctorBtn);
        }

        public void bind(Doctor doctor, OnDoctorSelectedListener listener) {
            doctorNameText.setText(doctor.getName());
            doctorGenderText.setText(doctor.getGender());
            doctorPhoneText.setText(doctor.getPhone());
            doctorAnimalTypeText.setText(doctor.getAnimalType());

            String workTime = "";
            if (doctor.getWorkStartTime() != null && !doctor.getWorkStartTime().isEmpty()
                    && doctor.getWorkEndTime() != null && !doctor.getWorkEndTime().isEmpty()) {
                workTime = doctor.getWorkStartTime() + " - " + doctor.getWorkEndTime();
            } else if (doctor.getAppointmentTime() != null && !doctor.getAppointmentTime().isEmpty()) {
                workTime = doctor.getAppointmentTime();
            }
            doctorWorkTimeText.setText(workTime.isEmpty() ? "工作时间待定" : workTime);

            String intro = doctor.getIntroduction();
            doctorIntroductionText.setText((intro == null || intro.isEmpty()) ? "暂无介绍" : intro);

            availableSlotsChipGroup.removeAllViews();
            if (doctor.getAvailableSlots() != null && !doctor.getAvailableSlots().isEmpty()) {
                for (String slot : doctor.getAvailableSlots()) {
                    Chip chip = new Chip(itemView.getContext());
                    chip.setText(slot);
                    chip.setChipBackgroundColorResource(R.color.background);
                    chip.setTextColor(itemView.getContext().getResources().getColor(R.color.primary));
                    chip.setChipStrokeColorResource(R.color.primary_light);
                    chip.setChipStrokeWidth(1f);
                    chip.setClickable(false);
                    chip.setTextSize(11);
                    availableSlotsChipGroup.addView(chip);
                }
            }

            expandToggleIcon.setOnClickListener(v -> {
                if (expandableSection.getVisibility() == View.GONE) {
                    expandableSection.setVisibility(View.VISIBLE);
                    expandToggleIcon.setRotation(180f);
                } else {
                    expandableSection.setVisibility(View.GONE);
                    expandToggleIcon.setRotation(0f);
                }
            });

            selectDoctorBtn.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onDoctorSelected(doctor);
                }
            });
        }
    }
}
