package com.panduoma.cwhospital.mapper;
import com.panduoma.cwhospital.entity.Admin;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface AdminMapper {
    @Select("SELECT * FROM admin WHERE email = #{email}")
    Admin findByEmail(String email);
}
