-- ==========================================
-- 宠物医院预约平台数据库初始化脚本
-- ==========================================

-- 创建数据库
CREATE DATABASE IF NOT EXISTS cwhospital 
CHARACTER SET utf8mb4 
COLLATE utf8mb4_unicode_ci;

USE cwhospital;

-- ==========================================
-- 1. 用户表
-- ==========================================
DROP TABLE IF EXISTS `user`;
CREATE TABLE `user` (
  `id` INT NOT NULL AUTO_INCREMENT COMMENT '用户ID',
  `name` VARCHAR(50) NOT NULL COMMENT '用户姓名',
  `email` VARCHAR(100) NOT NULL UNIQUE COMMENT '邮箱地址',
  `phone` VARCHAR(20) COMMENT '手机号码',
  `password` VARCHAR(100) NOT NULL COMMENT '密码',
  `role` VARCHAR(20) DEFAULT 'USER' COMMENT '角色: USER-普通用户, ADMIN-管理员',
  `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `idx_email` (`email`),
  KEY `idx_role` (`role`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户表';

-- ==========================================
-- 2. 管理员表
-- ==========================================
DROP TABLE IF EXISTS `admin`;
CREATE TABLE `admin` (
  `id` INT NOT NULL AUTO_INCREMENT COMMENT '管理员ID',
  `name` VARCHAR(50) NOT NULL COMMENT '管理员姓名',
  `email` VARCHAR(100) NOT NULL UNIQUE COMMENT '邮箱地址',
  `password` VARCHAR(100) NOT NULL COMMENT '密码',
  `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `idx_email` (`email`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='管理员表';

-- ==========================================
-- 3. 医生表
-- ==========================================
DROP TABLE IF EXISTS `doctor`;
CREATE TABLE `doctor` (
  `id` INT NOT NULL AUTO_INCREMENT COMMENT '医生ID',
  `name` VARCHAR(50) NOT NULL COMMENT '医生姓名',
  `department` VARCHAR(50) NOT NULL COMMENT '所属科室',
  `phone` VARCHAR(20) COMMENT '联系电话',
  `email` VARCHAR(100) COMMENT '邮箱地址',
  `specialization` VARCHAR(100) COMMENT '专业方向',
  `qualification` VARCHAR(100) COMMENT '资质证书',
  `introduction` TEXT COMMENT '医生简介',
  `status` TINYINT DEFAULT 1 COMMENT '状态: 1-在职, 0-离职',
  `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_department` (`department`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='医生表';

-- ==========================================
-- 4. 预约表
-- ==========================================
DROP TABLE IF EXISTS `appointment`;
CREATE TABLE `appointment` (
  `id` INT NOT NULL AUTO_INCREMENT COMMENT '预约ID',
  `user_id` INT NOT NULL COMMENT '用户ID',
  `pet_name` VARCHAR(50) NOT NULL COMMENT '宠物名称',
  `pet_type` VARCHAR(50) NOT NULL COMMENT '宠物类型(猫、狗等)',
  `pet_age` INT COMMENT '宠物年龄(月)',
  `pet_gender` VARCHAR(10) COMMENT '宠物性别',
  `service_type` VARCHAR(50) NOT NULL COMMENT '服务类型(疫苗接种、诊疗、手术等)',
  `appointment_date` DATE NOT NULL COMMENT '预约日期',
  `appointment_time` VARCHAR(20) NOT NULL COMMENT '预约时间段',
  `symptoms` TEXT COMMENT '症状描述',
  `doctor` VARCHAR(50) COMMENT '接诊医生',
  `status` VARCHAR(20) DEFAULT 'PENDING' COMMENT '状态: PENDING-待处理, CONFIRMED-已确认, COMPLETED-已完成, CANCELLED-已取消',
  `is_cancelled` TINYINT DEFAULT 0 COMMENT '取消状态: 0-正常, 1-待撤销, 2-已撤销',
  `cancel_reason` VARCHAR(200) COMMENT '取消原因',
  `notes` TEXT COMMENT '备注信息',
  `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_appointment_date` (`appointment_date`),
  KEY `idx_status` (`status`),
  KEY `idx_doctor` (`doctor`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='预约表';

-- ==========================================
-- 5. 公告表
-- ==========================================
DROP TABLE IF EXISTS `announcement`;
CREATE TABLE `announcement` (
  `id` INT NOT NULL AUTO_INCREMENT COMMENT '公告ID',
  `title` VARCHAR(200) NOT NULL COMMENT '公告标题',
  `content` TEXT NOT NULL COMMENT '公告内容',
  `type` VARCHAR(20) DEFAULT 'NORMAL' COMMENT '公告类型: NORMAL-普通, IMPORTANT-重要, URGENT-紧急',
  `publisher` VARCHAR(50) COMMENT '发布人',
  `status` TINYINT DEFAULT 1 COMMENT '状态: 1-已发布, 0-草稿',
  `publish_time` TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '发布时间',
  `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_status` (`status`),
  KEY `idx_publish_time` (`publish_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='公告表';

-- ==========================================
-- 6. 宠物信息表
-- ==========================================
DROP TABLE IF EXISTS `pet`;
CREATE TABLE `pet` (
  `id` INT NOT NULL AUTO_INCREMENT COMMENT '宠物ID',
  `user_id` INT NOT NULL COMMENT '主人ID',
  `name` VARCHAR(50) NOT NULL COMMENT '宠物名称',
  `type` VARCHAR(50) NOT NULL COMMENT '宠物类型(猫、狗等)',
  `breed` VARCHAR(50) COMMENT '品种',
  `gender` VARCHAR(10) COMMENT '性别',
  `birth_date` DATE COMMENT '出生日期',
  `weight` DECIMAL(5,2) COMMENT '体重(kg)',
  `color` VARCHAR(50) COMMENT '毛色',
  `photo` VARCHAR(200) COMMENT '照片URL',
  `medical_history` TEXT COMMENT '病史记录',
  `allergies` TEXT COMMENT '过敏史',
  `vaccination_status` VARCHAR(50) COMMENT '疫苗接种状态',
  `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_type` (`type`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='宠物信息表';

-- ==========================================
-- 7. 诊疗记录表
-- ==========================================
DROP TABLE IF EXISTS `medical_record`;
CREATE TABLE `medical_record` (
  `id` INT NOT NULL AUTO_INCREMENT COMMENT '诊疗记录ID',
  `appointment_id` INT NOT NULL COMMENT '预约ID',
  `user_id` INT NOT NULL COMMENT '用户ID',
  `pet_id` INT NOT NULL COMMENT '宠物ID',
  `doctor_id` INT NOT NULL COMMENT '医生ID',
  `diagnosis` TEXT COMMENT '诊断结果',
  `treatment` TEXT COMMENT '治疗方案',
  `prescription` TEXT COMMENT '处方',
  `cost` DECIMAL(10,2) COMMENT '费用',
  `notes` TEXT COMMENT '备注',
  `follow_up_date` DATE COMMENT '复查日期',
  `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_appointment_id` (`appointment_id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_pet_id` (`pet_id`),
  KEY `idx_doctor_id` (`doctor_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='诊疗记录表';

-- ==========================================
-- 插入测试数据
-- ==========================================

-- 插入管理员数据
INSERT INTO `admin` (`name`, `email`, `password`) VALUES
('系统管理员', 'admin@hospital.com', 'admin123'),
('李医生', 'doctor.li@hospital.com', 'doctor123');

-- 插入测试用户数据
INSERT INTO `user` (`name`, `email`, `phone`, `password`, `role`) VALUES
('张三', 'zhangsan@example.com', '13800138001', '123456', 'USER'),
('李四', 'lisi@example.com', '13800138002', '123456', 'USER'),
('王五', 'wangwu@example.com', '13800138003', '123456', 'USER');

-- 插入医生数据
INSERT INTO `doctor` (`name`, `department`, `phone`, `email`, `specialization`, `introduction`) VALUES
('李华', '内科', '13900139001', 'lihua@hospital.com', '内科疾病诊断', '从医10年，擅长内科疾病诊断和治疗'),
('王芳', '外科', '13900139002', 'wangfang@hospital.com', '外科手术', '从医8年，擅长各类外科手术'),
('张伟', '疫苗接种', '13900139003', 'zhangwei@hospital.com', '疫苗接种', '从医5年，专业疫苗接种医师'),
('刘敏', '产科', '13900139004', 'liumin@hospital.com', '宠物产科', '从医7年，擅长宠物产科护理');

-- 插入测试预约数据
INSERT INTO `appointment` (`user_id`, `pet_name`, `pet_type`, `service_type`, `appointment_date`, `appointment_time`, `symptoms`, `doctor`, `status`) VALUES
(1, '小白', '狗', '疫苗接种', '2024-01-15', '09:00-10:00', '定期疫苗接种', '张伟', 'CONFIRMED'),
(2, '咪咪', '猫', '常规体检', '2024-01-15', '10:00-11:00', '年度体检', '李华', 'PENDING'),
(1, '大黄', '狗', '诊疗', '2024-01-16', '14:00-15:00', '食欲不振，精神萎靡', '李华', 'PENDING');

-- 插入公告数据
INSERT INTO `announcement` (`title`, `content`, `type`, `publisher`, `status`) VALUES
('系统上线公告', '宠物医院预约平台正式上线，欢迎使用在线预约服务！', 'IMPORTANT', '系统管理员', 1),
('春节放假通知', '春节期间（2024年2月10日-2月17日）医院正常营业，欢迎预约！', 'NORMAL', '系统管理员', 1),
('疫苗接种优惠活动', '本月疫苗接种享受8折优惠，快来预约吧！', 'NORMAL', '系统管理员', 1);

-- 插入测试宠物数据
INSERT INTO `pet` (`user_id`, `name`, `type`, `breed`, `gender`, `birth_date`, `weight`, `vaccination_status`) VALUES
(1, '小白', '狗', '金毛', '公', '2022-03-15', 25.50, '已完成'),
(1, '大黄', '狗', '拉布拉多', '公', '2021-08-20', 30.20, '已完成'),
(2, '咪咪', '猫', '英短', '母', '2023-01-10', 4.50, '部分完成'),
(3, '球球', '猫', '橘猫', '公', '2022-12-05', 6.00, '已完成');

-- ==========================================
-- 创建视图
-- ==========================================

-- 预约详情视图
CREATE OR REPLACE VIEW `appointment_detail` AS
SELECT 
    a.id,
    a.pet_name,
    a.pet_type,
    a.service_type,
    a.appointment_date,
    a.appointment_time,
    a.symptoms,
    a.doctor,
    a.status,
    a.is_cancelled,
    a.cancel_reason,
    a.created_at,
    u.name AS user_name,
    u.email AS user_email,
    u.phone AS user_phone
FROM appointment a
LEFT JOIN user u ON a.user_id = u.id;

-- ==========================================
-- 创建存储过程
-- ==========================================

DELIMITER //

-- 获取用户预约统计
CREATE PROCEDURE `get_user_appointment_stats`(IN p_user_id INT)
BEGIN
    SELECT 
        COUNT(*) AS total_appointments,
        SUM(CASE WHEN status = 'PENDING' THEN 1 ELSE 0 END) AS pending_count,
        SUM(CASE WHEN status = 'CONFIRMED' THEN 1 ELSE 0 END) AS confirmed_count,
        SUM(CASE WHEN status = 'COMPLETED' THEN 1 ELSE 0 END) AS completed_count,
        SUM(CASE WHEN status = 'CANCELLED' THEN 1 ELSE 0 END) AS cancelled_count
    FROM appointment
    WHERE user_id = p_user_id;
END //

-- 获取医生排班统计
CREATE PROCEDURE `get_doctor_appointment_stats`(IN p_doctor_name VARCHAR(50))
BEGIN
    SELECT 
        COUNT(*) AS total_appointments,
        SUM(CASE WHEN appointment_date = CURDATE() THEN 1 ELSE 0 END) AS today_count,
        SUM(CASE WHEN appointment_date > CURDATE() THEN 1 ELSE 0 END) AS future_count
    FROM appointment
    WHERE doctor = p_doctor_name AND status != 'CANCELLED';
END //

DELIMITER ;

-- ==========================================
-- 数据库说明
-- ==========================================

/*
数据库设计说明：

1. 用户表(user)：存储普通用户信息，支持用户和管理员角色区分
2. 管理员表(admin)：单独存储管理员信息，提高安全性
3. 医生表(doctor)：存储医生基本信息和专业方向
4. 预约表(appointment)：核心业务表，记录所有预约信息
5. 公告表(announcement)：系统公告和通知
6. 宠物信息表(pet)：用户的宠物信息
7. 诊疗记录表(medical_record)：诊疗历史记录

表关系：
- user -> appointment: 一对多
- user -> pet: 一对多
- doctor -> appointment: 一对多
- appointment -> medical_record: 一对多

状态说明：
- 预约状态: PENDING(待处理), CONFIRMED(已确认), COMPLETED(已完成), CANCELLED(已取消)
- 取消状态: 0(正常), 1(待撤销), 2(已撤销)
- 公告类型: NORMAL(普通), IMPORTANT(重要), URGENT(紧急)

使用说明：
1. 执行本脚本前请确保MySQL服务已启动
2. 确保有创建数据库的权限
3. 默认管理员账号: admin@hospital.com / admin123
4. 测试用户账号: zhangsan@example.com / 123456
*/