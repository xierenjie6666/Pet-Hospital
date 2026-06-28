package com.panduoma.cwhospital.entity;
import lombok.Data;

@Data
public class Admin {
    private Integer id;
    private String name;
    private String email;
    private String phone;
    private String password;
}