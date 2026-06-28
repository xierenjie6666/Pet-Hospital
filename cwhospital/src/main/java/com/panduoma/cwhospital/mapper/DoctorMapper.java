package com.panduoma.cwhospital.mapper;

import com.panduoma.cwhospital.entity.Doctor;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import java.util.List;

@Mapper
public interface DoctorMapper {

    // 查询所有医生
    @Select("SELECT id, name, email, phone, gender, animal_type as animalType, appointment_time as appointmentTime FROM doctor")
    List<Doctor> findAll();

    // 根据ID查询医生
    @Select("SELECT id, name, email, phone, gender, animal_type as animalType, appointment_time as appointmentTime FROM doctor WHERE id = #{id}")
    Doctor findById(@Param("id") Integer id);

    // 根据动物类型查询医生
    @Select("SELECT id, name, email, phone, gender, animal_type as animalType, appointment_time as appointmentTime FROM doctor WHERE animal_type LIKE CONCAT('%', #{animalType}, '%')")
    List<Doctor> findByAnimalType(@Param("animalType") String animalType);
}