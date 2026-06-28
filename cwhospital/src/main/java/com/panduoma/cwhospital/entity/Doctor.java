package com.panduoma.cwhospital.entity;

import java.util.List;

public class Doctor {
    private Integer id;
    private String name;
    private String email;
    private String phone;
    private String gender;
    private String animalType;  // 对应数据库 animal_type
    private String appointmentTime;  // 对应数据库 appointment_time
    private List<String> availableSlots;  // 非数据库字段，用于前端展示可预约时间段

    // Constructors
    public Doctor() {}

    public Doctor(Integer id, String name, String email, String phone,
                  String gender, String animalType, String appointmentTime) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.gender = gender;
        this.animalType = animalType;
        this.appointmentTime = appointmentTime;
    }

    // Getters and Setters
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getAnimalType() {
        return animalType;
    }

    public void setAnimalType(String animalType) {
        this.animalType = animalType;
    }

    public String getAppointmentTime() {
        return appointmentTime;
    }

    public void setAppointmentTime(String appointmentTime) {
        this.appointmentTime = appointmentTime;
    }

    public List<String> getAvailableSlots() {
        return availableSlots;
    }

    public void setAvailableSlots(List<String> availableSlots) {
        this.availableSlots = availableSlots;
    }
}