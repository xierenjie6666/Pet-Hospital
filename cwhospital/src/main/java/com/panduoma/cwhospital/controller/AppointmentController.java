package com.panduoma.cwhospital.controller;

import com.panduoma.cwhospital.common.Result;
import com.panduoma.cwhospital.entity.Appointment;
import com.panduoma.cwhospital.mapper.AppointmentMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@CrossOrigin("*")
@RestController
@RequestMapping("/appointments")
public class AppointmentController {

    @Autowired
    private AppointmentMapper appointmentMapper;

    // 用户端：创建预约
    @PostMapping("/create")
    public Result create(@RequestBody Appointment appointment) {
        appointment.setStatus("PENDING");
        appointment.setIsCancelled(0);
        appointmentMapper.insert(appointment);
        return Result.success("200", "预约成功", null);
    }

    // 用户端：获取用户的预约列表
    @GetMapping("/my")
    public Result my(Integer userId) {
        List<Appointment> list = appointmentMapper.selectByUserId(userId);
        Map<String, Object> result = new HashMap<>();
        result.put("list", list);
        return Result.success("200", "查询成功", result);
    }

    // 用户端：取消预约
    @PostMapping("/cancel")
    public Result cancel(@RequestBody Map<String, Integer> request) {
        try {
            Integer id = request.get("id");
            if (id == null) {
                return Result.error("预约ID不能为空");
            }
            appointmentMapper.cancelAppointment(id);
            return Result.success("200", "取消预约申请已提交", null);
        } catch (Exception e) {
            return Result.error("取消预约失败：" + e.getMessage());
        }
    }

    // ========== 管理员端接口 ==========

    // 获取所有预约
    @GetMapping("/all")
    public Result getAllAppointments() {
        try {
            List<Appointment> list = appointmentMapper.findAll();
            System.out.println("获取到预约数量: " + (list != null ? list.size() : 0));
            return Result.success("200", "查询成功", list);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("获取预约列表失败：" + e.getMessage());
        }
    }

    // 获取待处理预约
    // 获取待处理预约
    @GetMapping("/pending")
    public Result getPendingAppointments() {
        try {
            List<Appointment> list = appointmentMapper.findPendingAppointments();
            System.out.println("待处理预约数量: " + (list != null ? list.size() : 0));
            return Result.success("200", "查询成功", list);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("获取待处理预约失败：" + e.getMessage());
        }
    }

    // 获取撤销申请
    @GetMapping("/cancel-requests")
    public Result getCancelRequests() {
        try {
            List<Appointment> list = appointmentMapper.findCancelRequests();
            return Result.success("200", "查询成功", list);
        } catch (Exception e) {
            return Result.error("获取撤销申请失败：" + e.getMessage());
        }
    }

    // 更新预约状态
    @PostMapping("/update")
    public Result updateAppointment(@RequestBody Map<String, Object> request) {
        try {
            Integer id = (Integer) request.get("id");
            String status = (String) request.get("status");
            Integer isCancelled = (Integer) request.get("isCancelled");

            appointmentMapper.updateAppointmentStatus(id, status, isCancelled);
            return Result.success("200", "操作成功", null);
        } catch (Exception e) {
            return Result.error("操作失败：" + e.getMessage());
        }
    }

    // 获取今日统计数据
    @GetMapping("/stats/today")
    public Result getTodayStats() {
        try {
            Map<String, Integer> stats = new HashMap<>();
            stats.put("total", appointmentMapper.getTodayTotal());
            stats.put("pending", appointmentMapper.getPendingCount());
            stats.put("completed", appointmentMapper.getCompletedCount());
            return Result.success("200", "查询成功", stats);
        } catch (Exception e) {
            return Result.error("获取统计失败：" + e.getMessage());
        }
    }

    // 获取最近预约
    @GetMapping("/recent")
    public Result getRecentAppointments(@RequestParam(defaultValue = "5") int limit) {
        try {
            List<Appointment> list = appointmentMapper.findRecentAppointments(limit);
            return Result.success("200", "查询成功", list);
        } catch (Exception e) {
            return Result.error("获取最近预约失败：" + e.getMessage());
        }
    }

}