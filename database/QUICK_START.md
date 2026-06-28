# 🎯 快速启动指南

本文档帮助你快速启动宠物医院预约平台的所有组件，以便生成运行截图。

---

## ⚡ 一键启动流程

### 📋 准备清单

在启动前，请确保已安装：
- ✅ JDK 21+
- ✅ Maven 3.6+
- ✅ MySQL 8.0+ (并已启动)
- ✅ Node.js 16+
- ✅ Android Studio (可选)

---

## 🚀 三步快速启动

### 步骤1: 初始化数据库 (5分钟)

```bash
# 1. 登录MySQL
mysql -u root -p

# 2. 执行初始化脚本
source d:/pet_hospital/database/init.sql

# 3. 验证数据
USE cwhospital;
SELECT * FROM admin;
SELECT * FROM user;

# 4. 退出MySQL
exit;
```

**默认账号:**
- 管理员: `admin@hospital.com` / `admin123`
- 用户: `zhangsan@example.com` / `123456`

---

### 步骤2: 启动后端服务 (2分钟)

```bash
# 1. 进入后端目录
cd d:/pet_hospital/cwhospital

# 2. 编译并启动
mvn spring-boot:run

# 3. 等待启动完成
# 看到 "Started CwhospitalApplication" 表示启动成功

# 4. 测试API
# 浏览器访问: http://localhost:8080
```

**后端运行在:**
- 地址: `http://localhost:8080`
- 端口: `8080`

---

### 步骤3: 启动Web管理端 (1分钟)

```bash
# 1. 进入Web前端目录
cd d:/pet_hospital/cwhospital-web

# 2. 安装依赖（首次需要）
npm install

# 3. 启动开发服务器
npm run dev

# 4. 浏览器访问
# http://localhost:5173
```

**Web管理端运行在:**
- 地址: `http://localhost:5173`
- 端口: `5173`

---

## 📱 Android应用启动 (可选)

### 使用Android Studio

```bash
# 1. 打开Android Studio
# 2. Open Existing Project → 选择 d:/pet_hospital/pethospitalapp
# 3. 等待Gradle同步完成
# 4. 选择运行设备（模拟器或真机）
# 5. 点击 Run 按钮
```

**重要配置:**

修改 `ApiClient.java` 中的后端地址:
```java
// 使用电脑的IP地址（不是localhost）
private static final String BASE_URL = "http://你的电脑IP:8080";
```

查看电脑IP:
```bash
# Windows
ipconfig

# 找到 IPv4 地址，例如: 192.168.1.100
```

---

## 📸 快速截图流程

### 后端API截图 (Postman)

#### 1. 导入Postman Collection
```
Postman → Import → 选择文件
d:/pet_hospital/database/api-tests.postman_collection.json
```

#### 2. 测试关键接口

**用户注册:**
```
POST http://localhost:8080/api/user/register
Body: {"name":"测试","email":"test@example.com","phone":"13800138888","password":"test123"}
→ 截图保存为 screenshots/api-register.png
```

**用户登录:**
```
POST http://localhost:8080/api/user/login
Body: {"email":"zhangsan@example.com","password":"123456"}
→ 截图保存为 screenshots/api-login.png
```

**创建预约:**
```
POST http://localhost:8080/appointments/create
Body: {"user_id":1,"petName":"小白","petType":"狗","serviceType":"疫苗接种","appointmentDate":"2024-02-01","appointmentTime":"09:00-10:00","symptoms":"定期疫苗接种","doctor":"张伟"}
→ 截图保存为 screenshots/api-appointment.png
```

---

### Web管理端截图

#### 1. 登录并截图
```
访问 http://localhost:5173
账号: admin@hospital.com / admin123
登录后截图 → screenshots/web-login.png
```

#### 2. 仪表盘截图
```
登录成功后的首页
截图 → screenshots/web-dashboard.png
```

#### 3. 其他功能页面
```
用户管理 → screenshots/web-users.png
医生管理 → screenshots/web-doctors.png
预约管理 → screenshots/web-appointments.png
```

---

### Android应用截图

#### 用户端截图
```
1. 登录: zhangsan@example.com / 123456
2. 截取各功能界面
3. 保存到 screenshots 文件夹
```

#### 管理员端截图
```
1. 登录: admin@hospital.com / admin123
2. 截取管理功能界面
3. 保存到 screenshots 文件夹
```

---

## ⚠️ 启动问题排查

### 后端启动失败

**数据库连接失败:**
```bash
# 检查MySQL是否启动
# Windows: 服务管理器 → MySQL80 → 启动

# 检查配置
# cwhospital/src/main/resources/application.yml
# 确认 username 和 password 正确
```

**端口8080被占用:**
```bash
# Windows查看端口
netstat -ano | findstr 8080

# 结束占用进程
taskkill /PID 进程号 /F
```

---

### Web端启动失败

**依赖安装失败:**
```bash
# 清除npm缓存
npm cache clean --force

# 重新安装
npm install
```

**无法连接后端:**
```javascript
# 检查 src/api/index.js
# 确认 BASE_URL 正确
const BASE_URL = 'http://localhost:8080'
```

---

### Android连接失败

**无法连接后端:**
```java
# 不要使用 localhost 或 127.0.0.1
# 使用电脑实际IP地址
192.168.x.x

# 检查防火墙
# Windows防火墙 → 允许Java通过防火墙
```

---

## 🎯 完整启动时间估算

| 步骤 | 时间 | 说明 |
|------|------|------|
| 数据库初始化 | 5分钟 | 导入SQL脚本 |
| 后端启动 | 2分钟 | Spring Boot启动 |
| Web端启动 | 1分钟 | Vue开发服务器 |
| Android启动 | 3分钟 | 首次需要Gradle同步 |
| API测试截图 | 10分钟 | Postman测试 |
| Web端截图 | 5分钟 | 功能界面截图 |
| Android截图 | 10分钟 | 用户端和管理端 |
| **总计** | **36分钟** | 完整启动和截图 |


---

## ✅ 成功验证

### 后端成功标志
```
✅ 看到 "Started CwhospitalApplication"
✅ 访问 http://localhost:8080 无报错
✅ Postman测试返回正常响应
```

### Web端成功标志
```
✅ 访问 http://localhost:5173 显示登录页面
✅ 管理员登录成功
✅ 功能页面正常显示
```

### Android成功标志
```
✅ 应用成功安装并启动
✅ 登录功能正常
✅ 可以连接后端API
✅ 数据正常显示
```

---

## 🚨 紧急问题处理

### 所有服务无法启动

**重启所有服务:**
```bash
# 1. 重启MySQL
Windows服务管理器 → MySQL80 → 重新启动

# 2. 结束所有占用进程
taskkill /F /IM java.exe
taskkill /F /IM node.exe

# 3. 按顺序重新启动
# 数据库 → 后端 → Web → Android
```

---

### 数据库连接问题

**重置数据库:**
```bash
# 1. 删除并重建数据库
mysql -u root -p

DROP DATABASE IF EXISTS cwhospital;
CREATE DATABASE cwhospital CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

# 2. 重新导入脚本
source d:/pet_hospital/database/init.sql

exit;
```

---

## 📞 获取帮助

如遇到无法解决的问题:

1. 查看 `database/DEPLOYMENT_GUIDE.md` 详细部署文档
2. 查看 `database/API_TEST_GUIDE.md` API测试指南
3. 查看 `README.md` 项目文档
4. 检查后端日志输出
5. 检查数据库连接状态

---

