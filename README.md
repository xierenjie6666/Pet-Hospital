# 🏥 宠物医院预约平台

> Pet Hospital Appointment Platform

## 📋 项目简介

一个基于四层分离架构的完整宠物医院预约管理系统，包含Spring Boot后端、Android客户端和Vue Web管理端。支持用户在线预约挂号、管理员后台管理等完整业务流程。

---

## 🏗️ 系统架构

本项目采用**四层分离架构设计**，实现前后端分离、多端统一API：

```
┌─────────────────────────────────────────────────────────┐
│                    客户端层 (Client Layer)                │
│  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐  │
│  │ Android用户端 │  │Android管理员端│  │ Web管理后台  │  │
│  │   (Kotlin)   │  │   (Kotlin)   │  │  (Vue 3)    │  │
│  └──────────────┘  └──────────────┘  └──────────────┘  │
└─────────────────────────────────────────────────────────┘
                          ↓ HTTP/HTTPS
┌─────────────────────────────────────────────────────────┐
│                    网络层 (Network Layer)                │
│  ┌──────────────────────────────────────────────────┐  │
│  │     RESTful API (统一接口)                        │  │
│  │  - JWT Token认证                                  │  │
│  │  - 统一返回格式 Result<T>                          │  │
│  │  - 统一异常处理                                    │  │
│  └──────────────────────────────────────────────────┘  │
└─────────────────────────────────────────────────────────┘
                          ↓
┌─────────────────────────────────────────────────────────┐
│                    业务层 (Business Layer)               │
│  ┌──────────────────────────────────────────────────┐  │
│  │        Spring Boot Application                   │  │
│  │  - Controller层: 接收请求                        │  │
│  │  - Service层: 业务逻辑处理                        │  │
│  │  - Mapper层: 数据访问                             │  │
│  │  - 实体类: User/Admin/Doctor/Appointment        │  │
│  └──────────────────────────────────────────────────┘  │
└─────────────────────────────────────────────────────────┘
                          ↓
┌─────────────────────────────────────────────────────────┐
│                    数据层 (Data Layer)                   │
│  ┌──────────────┐  ┌──────────────┐                    │
│  │ MySQL 8.0    │  │ Redis Cache  │                    │
│  │ (持久化存储)  │  │  (缓存层)    │                    │
│  └──────────────┘  └──────────────┘                    │
└─────────────────────────────────────────────────────────┘
```

---

## 🛠️ 技术栈

### 后端技术 (Spring Boot)
- **核心框架**: Spring Boot 3.2.0
- **Java版本**: Java 21
- **ORM框架**: MyBatis 3.0.3
- **数据库**: MySQL 8.0
- **构建工具**: Maven
- **其他**: Lombok, DevTools

### 前端技术 (Vue 3)
- **框架**: Vue 3.4.0
- **UI组件库**: Element Plus 2.7.0
- **路由**: Vue Router 4.3.0
- **HTTP客户端**: Axios 1.7.0
- **构建工具**: Vite 5.4.0

### 移动端技术 (Android)
- **开发语言**: Java
- **最低SDK**: Android 5.0 (API 21)
- **网络库**: OkHttp
- **JSON解析**: Gson

---

## 📱 功能模块

### 👤 用户端功能 (Android APP)
- ✅ 用户注册/登录
- ✅ 宠物信息管理
- ✅ 在线预约挂号
- ✅ 查询预约记录
- ✅ 取消预约申请
- ✅ 查看诊疗记录
- ✅ 查看公告通知

### 👨‍💼 管理员端功能 (Android APP)
- ✅ 管理员登录
- ✅ 用户管理（查看、删除用户）
- ✅ 医生管理（添加、编辑、删除）
- ✅ 预约管理（审核预约、处理取消请求）
- ✅ 统计报表（预约统计）
- ✅ 公告管理

### 💻 Web管理端功能 (Vue)
- ✅ 管理员登录
- ✅ 仪表盘数据展示
- ✅ 用户管理
- ✅ 医生管理
- ✅ 预约管理
- ✅ 公告管理

---

## 📂 项目结构

```
pet_hospital/
├── cwhospital/              # Spring Boot后端
│   ├── src/main/java/
│   │   └── com/panduoma/cwhospital/
│   │       ├── controller/    # 控制器层
│   │       ├── service/       # 业务层
│   │       ├── mapper/        # 数据访问层
│   │       ├── entity/        # 实体类
│   │       ├── dto/           # 数据传输对象
│   │       └── common/        # 公共类
│   ├── src/main/resources/
│   │   ├── application.yml    # 配置文件
│   │   └── mapper/            # MyBatis XML
│   └── pom.xml
│
├── cwhospital-web/          # Vue前端管理端
│   ├── src/
│   │   ├── api/              # API接口
│   │   ├── views/            # 页面组件
│   │   ├── router/           # 路由配置
│   │   ├── layout/           # 布局组件
│   │   └── utils/            # 工具类
│   ├── package.json
│   └── vite.config.js
│
└── pethospitalapp/          # Android应用
    ├── app/src/main/java/
    │   └── com/example/pethospitalapp/
    │       ├── LoginActivity          # 登录
    │       ├── RegisterActivity       # 注册
    │       ├── UserDashboardActivity  # 用户中心
    │       ├── AdminDashboardActivity # 管理员中心
    │       ├── AppointmentActivity    # 预约功能
    │       ├── DoctorManagementActivity# 医生管理
    │       └── UserManagementActivity # 用户管理
    ├── app/src/main/res/              # 资源文件
    └── build.gradle.kts
```

---

## 🚀 快速开始

### 前置要求
- JDK 21+
- Maven 3.6+
- MySQL 8.0+
- Node.js 16+
- Android Studio (for Android development)

### 📦 1. 数据库配置

创建数据库并导入初始数据：

```sql
CREATE DATABASE cwhospital CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

修改后端配置文件 `cwhospital/src/main/resources/application.yml`：

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/cwhospital?useSSL=false&serverTimezone=Asia/Shanghai
    username: root
    password: your_password
```

### 🔧 2. 启动后端服务

```bash
cd cwhospital
mvn clean install
mvn spring-boot:run
```

后端服务将在 `http://localhost:8080` 启动

### 🌐 3. 启动Web管理端

```bash
cd cwhospital-web
npm install
npm run dev
```

Web管理端将在 `http://localhost:5173` 启动

### 📱 4. 运行Android应用

1. 使用 Android Studio 打开 `pethospitalapp` 目录
2. 修改 `ApiClient.java` 中的后端地址：
   ```java
   private static final String BASE_URL = "http://你的电脑IP:8080";
   ```
3. 连接Android设备或启动模拟器
4. 点击 Run 按钮运行应用

---

## 📸 运行截图

### 后端API测试

#### 用户注册接口
![用户注册API](screenshots/api-register.png)
*测试用户注册接口返回*

#### 用户登录接口
![用户登录API](screenshots/api-login.png)
*测试用户登录接口返回*

#### 预约创建接口
![创建预约API](screenshots/api-appointment.png)
*测试预约创建接口返回*

---

### Web管理端

#### 管理员登录页面
![Web登录](screenshots/web-login.png)
*管理员登录界面*

#### 管理员仪表盘
![Web仪表盘](screenshots/web-dashboard.png)
*数据统计仪表盘*

#### 用户管理页面
![Web用户管理](screenshots/web-users.png)
*用户列表管理*

#### 医生管理页面
![Web医生管理](screenshots/web-doctors.png)
*医生信息管理*

#### 预约管理页面
![Web预约管理](screenshots/web-appointments.png)
*预约记录管理*

---

### Android用户端

#### 用户登录界面
![Android用户登录](screenshots/android-user-login.png)
*用户登录界面*

#### 用户注册界面
![Android用户注册](screenshots/android-user-register.png)
*用户注册界面*

#### 用户主页面
![Android用户主页](screenshots/android-user-home.png)
*用户功能主页面*

#### 预约挂号界面
![Android预约挂号](screenshots/android-appointment.png)
*在线预约挂号*

#### 我的预约列表
![Android我的预约](screenshots/android-my-appointments.png)
*查看预约记录*

---

### Android管理员端

#### 管理员登录界面
![Android管理员登录](screenshots/android-admin-login.png)
*管理员登录界面*

#### 管理员主页面
![Android管理员主页](screenshots/android-admin-home.png)
*管理员功能主页面*

#### 用户管理功能
![Android用户管理](screenshots/android-user-management.png)
*用户列表管理*

#### 医生管理功能
![Android医生管理](screenshots/android-doctor-management.png)
*医生信息管理*

#### 待审核预约
![Android待审核预约](screenshots/android-pending-appointments.png)
*预约审核列表*

#### 统计报表
![Android统计报表](screenshots/android-statistics.png)
*数据统计展示*

---

## 🔌 API文档

### 用户相关API

#### 用户注册
```
POST /api/user/register
Content-Type: application/json

Request Body:
{
  "name": "张三",
  "email": "zhangsan@example.com",
  "phone": "13800138000",
  "password": "123456"
}

Response:
{
  "code": 200,
  "msg": "注册成功"
}
```

#### 用户登录
```
POST /api/user/login
Content-Type: application/json

Request Body:
{
  "email": "zhangsan@example.com",
  "password": "123456"
}

Response:
{
  "code": 200,
  "msg": "登录成功",
  "data": {
    "id": 1,
    "name": "张三",
    "email": "zhangsan@example.com",
    "role": "USER"
  }
}
```

### 预约相关API

#### 创建预约
```
POST /appointments/create
Content-Type: application/json

Request Body:
{
  "user_id": 1,
  "petName": "小白",
  "petType": "狗",
  "serviceType": "疫苗接种",
  "appointmentDate": "2024-01-15",
  "appointmentTime": "09:00-10:00",
  "symptoms": "定期疫苗接种",
  "doctor": "李医生"
}

Response:
{
  "code": "200",
  "msg": "预约成功"
}
```

#### 查询用户预约
```
GET /appointments/my?userId=1

Response:
{
  "code": "200",
  "msg": "查询成功",
  "data": {
    "list": [
      {
        "id": 1,
        "petName": "小白",
        "appointmentDate": "2024-01-15",
        "status": "PENDING"
      }
    ]
  }
}
```

#### 取消预约
```
POST /appointments/cancel
Content-Type: application/json

Request Body:
{
  "id": 1
}

Response:
{
  "code": "200",
  "msg": "取消预约申请已提交"
}
```

### 管理员API

#### 管理员登录
```
POST /api/admin/login
Content-Type: application/json

Request Body:
{
  "email": "admin@example.com",
  "password": "admin123"
}

Response:
{
  "success": true,
  "user": {
    "id": 1,
    "name": "管理员",
    "role": "ADMIN"
  }
}
```

#### 获取所有用户
```
GET /api/user/all

Response:
{
  "code": 200,
  "msg": "查询成功",
  "data": [
    {
      "id": 1,
      "name": "张三",
      "email": "zhangsan@example.com"
    }
  ]
}
```

---

## 🗄️ 数据库设计

### 用户表 (user)
| 字段 | 类型 | 说明 |
|------|------|------|
| id | INT | 主键 |
| name | VARCHAR(50) | 用户名 |
| email | VARCHAR(100) | 邮箱 |
| phone | VARCHAR(20) | 手机号 |
| password | VARCHAR(100) | 密码 |
| role | VARCHAR(20) | 角色(USER/ADMIN) |

### 管理员表 (admin)
| 字段 | 类型 | 说明 |
|------|------|------|
| id | INT | 主键 |
| name | VARCHAR(50) | 管理员姓名 |
| email | VARCHAR(100) | 邮箱 |
| password | VARCHAR(100) | 密码 |

### 医生表 (doctor)
| 字段 | 类型 | 说明 |
|------|------|------|
| id | INT | 主键 |
| name | VARCHAR(50) | 医生姓名 |
| department | VARCHAR(50) | 科室 |
| phone | VARCHAR(20) | 联系电话 |

### 预约表 (appointment)
| 字段 | 类型 | 说明 |
|------|------|------|
| id | INT | 主键 |
| user_id | INT | 用户ID |
| pet_name | VARCHAR(50) | 宠物名称 |
| pet_type | VARCHAR(50) | 宠物类型 |
| service_type | VARCHAR(50) | 服务类型 |
| appointment_date | DATE | 预约日期 |
| appointment_time | VARCHAR(20) | 预约时间 |
| symptoms | TEXT | 症状描述 |
| status | VARCHAR(20) | 状态(PENDING/CONFIRMED/CANCELLED) |
| doctor | VARCHAR(50) | 接诊医生 |
| is_cancelled | INT | 是否取消(0正常/1待撤销/2已撤销) |

---

## 🎯 核心特性

### ✨ 统一API设计
- Android端和Web端共用同一套后端API
- 通过JWT Token和角色标识区分用户权限
- 统一返回格式，便于多端调用

### 🔐 权限管理
- 基于角色的访问控制 (RBAC)
- 用户和管理员权限分离
- API级别权限验证

### 📱 多端适配
- Android原生应用，性能优越
- Web管理后台，操作便捷
- 响应式设计，适配不同设备

### 🔄 实时通信
- 预约状态实时更新
- 管理员即时审核通知
- 系统公告推送

---

## 🛠️ 开发指南

### 后端开发
1. 在 `controller` 包下创建新的控制器类
2. 使用 `@RestController` 和 `@RequestMapping` 注解
3. 返回统一的 `Result` 对象
4. 在 `mapper` 包下创建对应的Mapper接口

### 前端开发
1. 在 `src/views` 下创建Vue组件
2. 在 `src/api/index.js` 中添加API调用方法
3. 在 `src/router/index.js` 中配置路由

### Android开发
1. 在 `java/com/example/pethospitalapp` 下创建Activity
2. 在 `res/layout` 下创建布局文件
3. 使用 `ApiClient` 调用后端API
4. 在 `AndroidManifest.xml` 中注册Activity

---

## 📝 版本历史

### v1.0.0 (当前版本)
- ✅ 完成用户注册登录功能
- ✅ 完成预约管理功能
- ✅ 完成医生管理功能
- ✅ 完成Android用户端和管理员端
- ✅ 完成Web管理端
- ✅ 实现四层分离架构

---

## 👥 贡献者

- 项目开发者：[你的名字]
- 联系邮箱：[your.email@example.com]

---

## 📄 许可证

本项目仅供学习和研究使用。

---

## 🙏 致谢

感谢以下开源项目：
- Spring Boot
- Vue.js
- Element Plus
- Android Open Source Project

---

## 📞 联系方式

如有问题或建议，请通过以下方式联系：
- Email: [your.email@example.com]
- GitHub Issues: [项目Issues页面]

---

**注：请将实际运行截图保存到项目的 `screenshots` 文件夹中，并替换上述截图路径。**