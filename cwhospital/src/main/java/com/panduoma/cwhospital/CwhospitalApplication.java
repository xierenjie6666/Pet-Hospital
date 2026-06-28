package com.panduoma.cwhospital;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.panduoma.cwhospital.mapper")
public class CwhospitalApplication {
    public static void main(String[] args) {
        SpringApplication.run(CwhospitalApplication.class, args);
    }
}