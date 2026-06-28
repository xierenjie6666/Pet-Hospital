package com.panduoma.cwhospital.mapper;

import com.panduoma.cwhospital.entity.User;
import org.apache.ibatis.annotations.*;
import java.util.List;

@Mapper
public interface UserMapper {

    // 注册
    @Insert("INSERT INTO user(name, email, phone, password, role) VALUES(#{name}, #{email}, #{phone}, #{password}, #{role})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void register(User user);

    // 登录
    @Select("SELECT * FROM user WHERE email = #{email}")
    User findByEmail(String email);

    // 检查邮箱是否存在
    @Select("SELECT COUNT(*) FROM user WHERE email = #{email}")
    int checkEmailExists(String email);

    // 检查邮箱是否在管理员表中存在
    @Select("SELECT COUNT(*) FROM admin WHERE email = #{email}")
    int checkEmailExistsInAdmin(String email);

    // ========== 管理员接口 ==========

    // 获取所有用户
    @Select("SELECT * FROM user ORDER BY id DESC")
    List<User> findAll();

    // 根据ID查询用户
    @Select("SELECT * FROM user WHERE id = #{id}")
    User findById(@Param("id") Integer id);

    // 添加用户
    @Insert("INSERT INTO user(name, email, phone, password, role) VALUES(#{name}, #{email}, #{phone}, #{password}, #{role})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(User user);

    // 删除用户
    @Delete("DELETE FROM user WHERE id = #{id}")
    int deleteById(@Param("id") Integer id);

    // 更新用户
    @Update("UPDATE user SET name = #{name}, email = #{email}, phone = #{phone}, role = #{role} WHERE id = #{id}")
    int updateById(User user);
}