package com.example.pethospitalapp;

public class Appointment {
    private int id;
    private Integer user_id;
    private String petName;
    private String petType;
    private String serviceType;
    private String appointmentDate;
    private String appointmentTime;
    private String symptoms;
    private String status;
    private String doctor; //


    public String getDoctor() {
        return doctor;
    }
    public Integer getUser_id() {
        return user_id;
    }

    public int getId() { return id; }
    public String getPetName() { return petName; }
    public String getPetType() { return petType; }
    public String getServiceType() { return serviceType; }
    public String getAppointmentDate() { return appointmentDate; }
    public String getAppointmentTime() { return appointmentTime; }
    public String getSymptoms() { return symptoms; }
    public String getStatus() { return status; }
}