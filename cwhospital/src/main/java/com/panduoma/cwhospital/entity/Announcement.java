package com.panduoma.cwhospital.entity;

import lombok.Data;

@Data
public class Announcement {
    private Integer id;
    private String title;
    private String content;
    private Boolean enabled;
    private String updateTime;
}
