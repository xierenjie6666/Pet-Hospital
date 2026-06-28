package com.panduoma.cwhospital.entity;

import lombok.Data;

@Data
public class Appointment {
    private Integer id;
    private Integer user_id;
    private String petName;
    private String petType;
    private String serviceType;
    private String appointmentDate;
    private String appointmentTime;
    private String symptoms;
    private String status;
    private String doctor;
    private Integer isCancelled;  // 0-正常 1-待撤销 2-已撤销
}