# 🚀 部署与运行指南

本文档提供详细的部署和运行步骤，帮助你启动各个平台并生成运行截图。

---

## 📋 系统要求

### 后端 (Spring Boot)
- JDK 21+
- Maven 3.6+
- MySQL 8.0+

### Web前端 (Vue 3)
- Node.js 16+
- npm 或 yarn

### Android应用
- Android Studio
- Android SDK (API 21+)
- Java 或 Kotlin开发环境

---

## 🗄️ 数据库初始化

### 1. 创建数据库

```bash
# 登录MySQL
mysql -u root -p

# 执行初始化脚本
source d:/pet_hospital/database/init.sql
```

或使用MySQL Workbench等工具导入 `database/init.sql`

### 2. 验证数据库

```sql
USE cwhospital;
SHOW TABLES;

# 查看测试数据
SELECT * FROM user;
SELECT * FROM admin;
SELECT * FROM doctor;
```

**默认账号:**
- 管理员: `admin@hospital.com` / `admin123`
- 用户: `zhangsan@example.com` / `123456`

---

## ⚙️ 后端部署

### 1. 配置数据库连接

编辑 `cwhospital/src/main/resources/application.yml`:

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/cwhospital?useSSL=false&serverTimezone=Asia/Shanghai
    username: root
    password: your_password  # 修改为你的MySQL密码
```

### 2. 启动后端服务

**方式一: 使用Maven**
```bash
cd cwhospital
mvn clean install
mvn spring-boot:run
```

**方式二: 使用IDE**
- IDEA: 右键 `CwhospitalApplication.java` → Run
- Eclipse: Run As → Spring Boot App

### 3. 验证后端运行

访问: `http://localhost:8080`

或使用Postman测试:
```bash
POST http://localhost:8080/api/user/login
Body: {"email":"zhangsan@example.com","password":"123456"}
```

---

## 🌐 Web管理端部署

### 1. 安装依赖

```bash
cd cwhospital-web
npm install
```

或使用yarn:
```bash
yarn install
```

### 2. 配置后端地址

检查 `src/api/index.js` 中的API地址:

```javascript
const BASE_URL = 'http://localhost:8080'
```

如果后端在其他地址，修改此处。

### 3. 启动开发服务器

```bash
npm run dev
```

或:
```bash
yarn dev
```

### 4. 访问Web管理端

浏览器访问: `http://localhost:5173`

**登录账号:**
- 邮箱: `admin@hospital.com`
- 密码: `admin123`

---

## 📸 Web端截图指南

### 截图1: 登录页面

**步骤:**
1. 打开浏览器访问 `http://localhost:5173`
2. 截取完整的登录界面
3. 保存为 `screenshots/web-login.png`

**要点:**
- ✅ 展示登录表单
- ✅ 包含Element Plus UI组件
- ✅ 界面整洁美观

### 截图2: 管理员仪表盘

**步骤:**
1. 使用管理员账号登录
2. 进入首页/仪表盘
3. 截取仪表盘界面
4. 保存为 `screenshots/web-dashboard.png`

**要点:**
- ✅ 展示数据统计卡片
- ✅ 包含侧边栏导航
- ✅ 顶部用户信息

### 截图3-7: 功能页面截图

按照相同方法截图其他页面:
- `web-users.png` - 用户管理
- `web-doctors.png` - 医生管理
- `web-appointments.png` - 预约管理
- `web-announcements.png` - 公告管理

---

## 📱 Android应用部署

### 1. 配置Android Studio

**打开项目:**
```bash
# 使用Android Studio打开
Open an Existing Project → 选择 pethospitalapp 目录
```

### 2. 配置后端地址

修改 `ApiClient.java`:

```java
public class ApiClient {
    // 如果在本地测试，使用电脑IP地址（不是localhost）
    private static final String BASE_URL = "http://192.168.1.100:8080";  // 修改为你的电脑IP
    
    // 如果使用真机测试，需要确保电脑和手机在同一局域网
    // 或使用公网IP
}
```

**获取电脑IP:**
```bash
# Windows
ipconfig

# 查看IPv4地址，例如: 192.168.1.100
```

### 3. 启动Android模拟器

**创建模拟器:**
1. Tools → Device Manager
2. Create Device
3. 选择设备型号（推荐Pixel 4）
4. 选择系统镜像（Android 11或更高）
5. Finish

**启动模拟器:**
1. Device Manager → 点击启动按钮
2. 等待模拟器启动完成

### 4. 运行Android应用

**运行步骤:**
1. 选择运行设备: 模拟器或真机
2. 点击 Run 按钮 (绿色三角形)
3. 等待应用安装并启动

**首次运行:**
- Gradle会自动下载依赖
- 编译时间可能较长
- 确保网络畅通

---

## 📸 Android端截图指南

### 用户端截图

**截图8-13: 用户功能界面**

#### 准备工作:
1. 启动Android应用
2. 使用用户账号登录: `zhangsan@example.com` / `123456`

#### 截图步骤:
1. **登录界面**
   - 截取登录Activity
   - 保存为 `screenshots/android-user-login.png`

2. **注册界面**
   - 点击注册按钮
   - 截取注册Activity
   - 保存为 `screenshots/android-user-register.png`

3. **用户主页**
   - 登录成功后的主界面
   - 展示功能入口卡片
   - 保存为 `screenshots/android-user-home.png`

4. **预约挂号**
   - 点击预约功能
   - 填写宠物信息和预约时间
   - 截取预约表单界面
   - 保存为 `screenshots/android-appointment.png`

5. **我的预约**
   - 点击"我的预约"
   - 展示预约列表
   - 保存为 `screenshots/android-my-appointments.png`

### 管理员端截图

**截图14-19: 管理员功能界面**

#### 准备工作:
1. 退出用户登录
2. 使用管理员账号登录: `admin@hospital.com` / `admin123`

#### 截图步骤:
1. **管理员登录**
   - 管理员登录界面
   - 保存为 `screenshots/android-admin-login.png`

2. **管理员主页**
   - 管理员功能主界面
   - 保存为 `screenshots/android-admin-home.png`

3. **用户管理**
   - 点击用户管理
   - 展示用户列表
   - 保存为 `screenshots/android-user-management.png`

4. **医生管理**
   - 点击医生管理
   - 展示医生列表和管理功能
   - 保存为 `screenshots/android-doctor-management.png`

5. **待审核预约**
   - 点击待审核预约
   - 展示预约审核列表
   - 保存为 `screenshots/android-pending-appointments.png`

6. **统计报表**
   - 点击统计功能
   - 展示数据统计图表
   - 保存为 `screenshots/android-statistics.png`

---

## 🛠️ Android截图技巧

### 使用Android Studio截图

1. Logcat窗口 → 选择设备
2. 点击Screenshot按钮
3. 选择保存位置

### 使用模拟器截图

1. 模拟器侧边栏 → 点击相机图标
2. 截图自动保存

### 使用真机截图

- **Android 11+:** 电源键 + 音量减键
- 截图后传输到电脑

---

## ⚠️ 常见问题

### 后端无法启动

**问题1: 数据库连接失败**
```
检查MySQL是否启动
确认数据库配置正确
检查防火墙设置
```

**问题2: 端口占用**
```bash
# Windows查看端口占用
netstat -ano | findstr 8080

# 结束占用进程
taskkill /PID 进程号 /F
```

### Web端无法访问后端

**问题1: 跨域问题**
```
后端已配置@CrossOrigin("*")
允许跨域访问
```

**问题2: 网络地址错误**
```javascript
// 检查 api/index.js 中的 BASE_URL
const BASE_URL = 'http://localhost:8080'
```

### Android无法连接后端

**问题1: 网络地址错误**
```java
// 不要使用localhost或127.0.0.1
// 使用电脑的实际IP地址
private static final String BASE_URL = "http://192.168.1.100:8080";
```

**问题2: 防火墙阻止**
```
Windows防火墙 → 允许应用通过防火墙
添加Java应用允许访问
```

**问题3: 模拟器网络问题**
```
重启模拟器
检查模拟器网络设置
使用真机测试
```

---

## 🎯 完整测试流程

### 流程图

```
1. 启动MySQL数据库
   ↓
2. 执行数据库初始化脚本
   ↓
3. 启动Spring Boot后端
   ↓
4. 测试API接口 (Postman)
   ↓
5. 启动Web管理端
   ↓
6. 测试Web端功能并截图
   ↓
7. 启动Android应用
   ↓
8. 测试Android用户端并截图
   ↓
9. 测试Android管理员端并截图
   ↓
10. 整理截图到screenshots文件夹
```

---

## 📝 截图检查清单

### 后端API截图
- ✅ api-register.png
- ✅ api-login.png
- ✅ api-appointment.png

### Web管理端截图
- ✅ web-login.png
- ✅ web-dashboard.png
- ✅ web-users.png
- ✅ web-doctors.png
- ✅ web-appointments.png

### Android用户端截图
- ✅ android-user-login.png
- ✅ android-user-register.png
- ✅ android-user-home.png
- ✅ android-appointment.png
- ✅ android-my-appointments.png

### Android管理员端截图
- ✅ android-admin-login.png
- ✅ android-admin-home.png
- ✅ android-user-management.png
- ✅ android-doctor-management.png
- ✅ android-pending-appointments.png
- ✅ android-statistics.png

---

## 💡 提示

1. **按顺序启动服务:**
   - 先启动数据库
   - 然后启动后端
   - 最后启动前端和Android

2. **截图时机:**
   - 功能正常运行时截图
   - 展示典型使用场景
   - 避免显示错误信息

3. **截图质量:**
   - 清晰度高
   - 界面完整
   - 内容真实

4. **敏感信息处理:**
   - 真实手机号可打码
   - 真实邮箱可打码
   - 保留测试账号信息

---

**如有部署问题，请参考README.md或联系开发者**