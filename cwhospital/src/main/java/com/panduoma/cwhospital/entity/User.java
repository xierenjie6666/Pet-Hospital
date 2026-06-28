package com.panduoma.cwhospital.entity;

import lombok.Data;

@Data
public class User {
    private Integer id;
    private String name;
    private String email;
    private String phone;
    private String password;
    private String role;  // 添加这个字段：USER 或 ADMIN
}