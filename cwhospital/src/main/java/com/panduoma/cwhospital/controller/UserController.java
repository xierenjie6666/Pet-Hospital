package com.panduoma.cwhospital.controller;

import com.panduoma.cwhospital.common.Result;
import com.panduoma.cwhospital.dto.LoginRequest;
import com.panduoma.cwhospital.entity.User;
import com.panduoma.cwhospital.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@CrossOrigin("*")
@RestController
@RequestMapping("/api/user")
public class UserController {

    @Autowired
    private UserMapper userMapper;


    @PostMapping("/register")
    public Map<String, Object> register(@RequestBody User user) {
        Map<String, Object> map = new HashMap<>();

        String email = user.getEmail();
        System.out.println("前端传过来的邮箱：" + email);

        if (email == null || email.isEmpty()) {
            map.put("code", 500);
            map.put("msg", "邮箱不能为空");
            return map;
        }


        int countInUser = userMapper.checkEmailExists(email);
        int countInAdmin = userMapper.checkEmailExistsInAdmin(email);

        int total = countInUser + countInAdmin;
        System.out.println("user表数量：" + countInUser + "  admin表数量：" + countInAdmin);

        if (total > 0) {
            map.put("code", 500);
            map.put("msg", "该邮箱已被注册！");
            return map;
        }

        try {
            user.setRole("USER");
            userMapper.register(user);
            map.put("code", 200);
            map.put("msg", "注册成功");
        } catch (Exception e) {
            map.put("code", 500);
            map.put("msg", "注册失败：" + e.getMessage());
        }

        return map;
    }

    // 用户登录
    @PostMapping("/login")
    public Result login(@RequestBody LoginRequest request) {
        User user = userMapper.findByEmail(request.getEmail());

        if (user == null) {
            return Result.error("用户不存在");
        }
        if (!user.getPassword().equals(request.getPassword())) {
            return Result.error("密码错误");
        }

        return Result.success("登录成功", "USER", user);
    }


    // 获取所有用户
    @GetMapping("/all")
    public Result getAllUsers() {
        try {
            List<User> userList = userMapper.findAll();
            return Result.success("200", "查询成功", userList);
        } catch (Exception e) {
            return Result.error("获取用户列表失败：" + e.getMessage());
        }
    }

    // 添加用户
    @PostMapping("/add")
    public Result addUser(@RequestBody User user) {
        try {
            // 检查邮箱是否已存在
            int countInUser = userMapper.checkEmailExists(user.getEmail());
            int countInAdmin = userMapper.checkEmailExistsInAdmin(user.getEmail());

            if (countInUser + countInAdmin > 0) {
                return Result.error("邮箱已被注册");
            }

            user.setRole("USER");
            userMapper.insert(user);
            return Result.success("200", "添加成功", null);
        } catch (Exception e) {
            return Result.error("添加用户失败：" + e.getMessage());
        }
    }

    // 删除用户
    @DeleteMapping("/delete/{id}")
    public Result deleteUser(@PathVariable Integer id) {
        try {
            userMapper.deleteById(id);
            return Result.success("200", "删除成功", null);
        } catch (Exception e) {
            return Result.error("删除用户失败：" + e.getMessage());
        }
    }

    // 更新用户
    @PutMapping("/update")
    public Result updateUser(@RequestBody User user) {
        try {
            userMapper.updateById(user);
            return Result.success("200", "更新成功", null);
        } catch (Exception e) {
            return Result.error("更新用户失败：" + e.getMessage());
        }
    }

    // 根据ID查询用户
    @GetMapping("/{id}")
    public Result getUserById(@PathVariable Integer id) {
        try {
            User user = userMapper.findById(id);
            if (user != null) {
                return Result.success("200", "查询成功", user);
            } else {
                return Result.error("用户不存在");
            }
        } catch (Exception e) {
            return Result.error("查询失败：" + e.getMessage());
        }
    }
}