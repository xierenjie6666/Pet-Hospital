package com.panduoma.cwhospital.controller;

import com.panduoma.cwhospital.common.Result;
import com.panduoma.cwhospital.dto.LoginRequest;
import com.panduoma.cwhospital.entity.Admin;
import com.panduoma.cwhospital.mapper.AdminMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@CrossOrigin("*")
@RestController
@RequestMapping("/api/admin")
public class AdminController {

    @Autowired
    private AdminMapper adminMapper;

    // 管理员登录
    @PostMapping("/login")
    public Result login(@RequestBody LoginRequest request) {
        Admin admin = adminMapper.findByEmail(request.getEmail());

        if (admin == null) {
            return Result.error("管理员不存在");
        }
        if (!admin.getPassword().equals(request.getPassword())) {
            return Result.error("密码错误");
        }

        return Result.success("登录成功", "ADMIN", admin);
    }
}