
package com.panduoma.cwhospital.controller;

import com.panduoma.cwhospital.common.Result;
import com.panduoma.cwhospital.entity.Doctor;
import com.panduoma.cwhospital.mapper.DoctorMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/doctor")
@CrossOrigin(origins = "*")
public class DoctorController {

    @Autowired
    private DoctorMapper doctorMapper;

    // 获取所有医生
    @GetMapping("/all")
    public Result getAllDoctors() {
        try {
            List<Doctor> doctors = doctorMapper.findAll();
            for (Doctor doctor : doctors) {
                setAvailableSlotsForDoctor(doctor);
            }
            return Result.success("获取成功", "DOCTOR_LIST", doctors);
        } catch (Exception e) {
            return Result.error("获取医生列表失败：" + e.getMessage());
        }
    }

    // 根据宠物类型推荐医生 - 修改为显示所有医生
    @GetMapping("/recommend")
    public Result getDoctorsByPetType(@RequestParam String petType) {
        try {
            // 直接返回所有医生，不再过滤
            List<Doctor> doctors = doctorMapper.findAll();

            // 为每个医生设置可预约时间段
            for (Doctor doctor : doctors) {
                setAvailableSlotsForDoctor(doctor);
            }

            if (doctors.isEmpty()) {
                return Result.error("暂无医生数据");
            }

            return Result.success("获取成功", "DOCTOR_LIST", doctors);
        } catch (Exception e) {
            return Result.error("获取医生列表失败：" + e.getMessage());
        }
    }

    // 根据医生ID获取详情
    @GetMapping("/{id}")
    public Result getDoctorById(@PathVariable Integer id) {
        try {
            Doctor doctor = doctorMapper.findById(id);
            if (doctor != null) {
                setAvailableSlotsForDoctor(doctor);
                return Result.success("获取成功", "DOCTOR", doctor);
            } else {
                return Result.error("医生不存在");
            }
        } catch (Exception e) {
            return Result.error("获取医生信息失败：" + e.getMessage());
        }
    }

    // 根据动物类型筛选医生（可选，用于高级搜索）
    @GetMapping("/filter")
    public Result filterDoctorsByAnimalType(@RequestParam(required = false) String animalType) {
        try {
            List<Doctor> doctors;
            if (animalType != null && !animalType.isEmpty()) {
                doctors = doctorMapper.findByAnimalType(animalType);
            } else {
                doctors = doctorMapper.findAll();
            }

            for (Doctor doctor : doctors) {
                setAvailableSlotsForDoctor(doctor);
            }

            return Result.success("获取成功", "DOCTOR_LIST", doctors);
        } catch (Exception e) {
            return Result.error("获取医生列表失败：" + e.getMessage());
        }
    }

    // 设置医生的可预约时间段
    private void setAvailableSlotsForDoctor(Doctor doctor) {
        List<String> slots = new ArrayList<>();

        // 根据医生ID设置不同的时间段
        if (doctor.getId() == 1) {
            slots = Arrays.asList("09:00", "10:00", "11:00", "14:00", "15:00", "16:00");
        } else if (doctor.getId() == 2) {
            slots = Arrays.asList("10:00", "11:00", "14:00", "15:00", "16:00", "17:00");
        } else if (doctor.getId() == 3) {
            slots = Arrays.asList("09:00", "10:00", "11:00", "13:00", "14:00", "15:00");
        } else if (doctor.getId() == 4) {
            slots = Arrays.asList("08:00", "09:00", "10:00", "14:00", "15:00", "16:00");
        } else if (doctor.getId() == 5) {
            slots = Arrays.asList("09:00", "10:00", "11:00", "14:00", "15:00", "16:00");
        } else if (doctor.getId() == 6) {
            slots = Arrays.asList("10:00", "11:00", "13:00", "14:00", "15:00", "16:00");
        } else if (doctor.getId() == 7) {
            slots = Arrays.asList("09:00", "10:00", "11:00", "13:00", "14:00", "15:00", "16:00", "17:00");
        } else if (doctor.getId() == 8) {
            slots = Arrays.asList("09:00", "10:00", "11:00", "14:00", "15:00", "16:00");
        } else if (doctor.getId() == 9) {
            slots = Arrays.asList("10:00", "11:00", "14:00", "15:00", "16:00", "17:00");
        } else if (doctor.getId() == 10) {
            slots = Arrays.asList("09:00", "10:00", "11:00", "13:00", "14:00", "15:00");
        } else {
            slots = Arrays.asList("09:00", "10:00", "11:00", "14:00", "15:00", "16:00");
        }

        doctor.setAvailableSlots(slots);
    }
}