package com.panduoma.cwhospital.mapper;

import com.panduoma.cwhospital.entity.Appointment;
import org.apache.ibatis.annotations.*;
import java.util.List;

@Mapper
public interface AppointmentMapper {

    // 插入预约
    @Insert("INSERT INTO appointment(user_id, pet_name, pet_type, service_type, appointment_date, appointment_time, symptoms, status, doctor, is_cancelled) " +
            "VALUES(#{user_id}, #{petName}, #{petType}, #{serviceType}, #{appointmentDate}, #{appointmentTime}, #{symptoms}, #{status}, #{doctor}, #{isCancelled})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(Appointment appointment);

    // 查询用户的预约列表
    @Select("SELECT id, user_id, pet_name as petName, pet_type as petType, service_type as serviceType, " +
            "appointment_date as appointmentDate, appointment_time as appointmentTime, symptoms, status, doctor, is_cancelled as isCancelled " +
            "FROM appointment WHERE user_id = #{userId} ORDER BY id DESC")
    List<Appointment> selectByUserId(@Param("userId") Integer userId);

    // 根据ID查询预约
    @Select("SELECT id, user_id, pet_name as petName, pet_type as petType, service_type as serviceType, " +
            "appointment_date as appointmentDate, appointment_time as appointmentTime, symptoms, status, doctor, is_cancelled as isCancelled " +
            "FROM appointment WHERE id = #{id}")
    Appointment selectById(@Param("id") Integer id);

    // 取消预约
    @Update("UPDATE appointment SET status = 'CANCELLED', is_cancelled = 1 WHERE id = #{id}")
    int cancelAppointment(@Param("id") Integer id);

    // 撤销预约
    @Update("UPDATE appointment SET status = 'CANCELLED', is_cancelled = 2 WHERE id = #{id}")
    int revokeAppointment(@Param("id") Integer id);

    // ========== 管理员接口 ==========

    // 获取所有预约
    @Select("SELECT id, user_id, pet_name as petName, pet_type as petType, service_type as serviceType, " +
            "appointment_date as appointmentDate, appointment_time as appointmentTime, symptoms, status, doctor, is_cancelled as isCancelled " +
            "FROM appointment ORDER BY id DESC")
    List<Appointment> findAll();

    // 获取待处理预约
    @Select("SELECT id, user_id, pet_name as petName, pet_type as petType, service_type as serviceType, " +
            "appointment_date as appointmentDate, appointment_time as appointmentTime, symptoms, status, doctor, is_cancelled as isCancelled " +
            "FROM appointment WHERE status = 'PENDING' AND is_cancelled = 0 ORDER BY id DESC")
    List<Appointment> findPendingAppointments();

    // 获取撤销申请
    @Select("SELECT id, user_id, pet_name as petName, pet_type as petType, service_type as serviceType, " +
            "appointment_date as appointmentDate, appointment_time as appointmentTime, symptoms, status, doctor, is_cancelled as isCancelled " +
            "FROM appointment WHERE is_cancelled = 1 ORDER BY id DESC")
    List<Appointment> findCancelRequests();

    // 更新预约状态
    @Update("UPDATE appointment SET status = #{status}, is_cancelled = #{isCancelled} WHERE id = #{id}")
    int updateAppointmentStatus(@Param("id") Integer id,
                                @Param("status") String status,
                                @Param("isCancelled") Integer isCancelled);

    // 获取今日统计
    @Select("SELECT COUNT(*) FROM appointment WHERE DATE(appointment_date) = CURDATE()")
    int getTodayTotal();

    @Select("SELECT COUNT(*) FROM appointment WHERE status = 'PENDING' AND is_cancelled = 0")
    int getPendingCount();

    @Select("SELECT COUNT(*) FROM appointment WHERE status = 'CONFIRMED'")
    int getCompletedCount();

    // 获取最近预约
    @Select("SELECT id, user_id, pet_name as petName, pet_type as petType, service_type as serviceType, " +
            "appointment_date as appointmentDate, appointment_time as appointmentTime, symptoms, status, doctor, is_cancelled as isCancelled " +
            "FROM appointment ORDER BY id DESC LIMIT #{limit}")
    List<Appointment> findRecentAppointments(@Param("limit") int limit);
}