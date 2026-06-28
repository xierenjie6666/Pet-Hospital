# 📋 API测试指南

本文档提供详细的API测试步骤，帮助你生成README中需要的运行截图。

## 🚀 准备工作

### 1. 启动后端服务

```bash
cd cwhospital
mvn spring-boot:run
```

确保后端服务在 `http://localhost:8080` 正常运行。

### 2. 使用Postman进行测试

- 下载安装 [Postman](https://www.postman.com/)
- 创建新的Collection: "宠物医院API测试"

---

## 📸 截图1: 用户注册API测试

### 测试步骤

**请求配置:**
```
Method: POST
URL: http://localhost:8080/api/user/register
```

**Headers:**
```
Content-Type: application/json
```

**Body (raw JSON):**
```json
{
  "name": "测试用户",
  "email": "test@example.com",
  "phone": "13800138888",
  "password": "test123"
}
```

**预期响应:**
```json
{
  "code": 200,
  "msg": "注册成功"
}
```

### 截图要点
- ✅ 截图包含完整的请求参数
- ✅ 展示返回的成功响应
- ✅ 包含请求URL和Method信息
- ✅ 保存为 `screenshots/api-register.png`

---

## 📸 截图2: 用户登录API测试

### 测试步骤

**请求配置:**
```
Method: POST
URL: http://localhost:8080/api/user/login
```

**Headers:**
```
Content-Type: application/json
```

**Body (raw JSON):**
```json
{
  "email": "zhangsan@example.com",
  "password": "123456"
}
```

**预期响应:**
```json
{
  "code": 200,
  "msg": "登录成功",
  "data": {
    "id": 1,
    "name": "张三",
    "email": "zhangsan@example.com",
    "phone": "13800138001",
    "role": "USER"
  },
  "role": "USER"
}
```

### 截图要点
- ✅ 展示登录成功返回的用户信息
- ✅ 包含用户ID、姓名、邮箱等字段
- ✅ 保存为 `screenshots/api-login.png`

---

## 📸 截图3: 创建预约API测试

### 测试步骤

**请求配置:**
```
Method: POST
URL: http://localhost:8080/appointments/create
```

**Headers:**
```
Content-Type: application/json
```

**Body (raw JSON):**
```json
{
  "user_id": 1,
  "petName": "测试宠物",
  "petType": "狗",
  "serviceType": "疫苗接种",
  "appointmentDate": "2024-02-01",
  "appointmentTime": "09:00-10:00",
  "symptoms": "定期疫苗接种",
  "doctor": "张伟"
}
```

**预期响应:**
```json
{
  "code": "200",
  "msg": "预约成功"
}
```

### 截图要点
- ✅ 展示预约创建成功的响应
- ✅ 包含完整的宠物信息和预约时间
- ✅ 保存为 `screenshots/api-appointment.png`

---

## 📸 其他API测试（可选）

### 管理员登录

**请求配置:**
```
Method: POST
URL: http://localhost:8080/api/admin/login
```

**Body:**
```json
{
  "email": "admin@hospital.com",
  "password": "admin123"
}
```

### 获取所有预约

**请求配置:**
```
Method: GET
URL: http://localhost:8080/appointments/all
```

### 获取待处理预约

**请求配置:**
```
Method: GET
URL: http://localhost:8080/appointments/pending
```

### 查询用户预约

**请求配置:**
```
Method: GET
URL: http://localhost:8080/appointments/my?userId=1
```

### 取消预约

**请求配置:**
```
Method: POST
URL: http://localhost:8080/appointments/cancel
```

**Body:**
```json
{
  "id": 1
}
```

---

## 📊 Postman Collection 导入

可以将以下JSON导入Postman，快速创建测试集合：

### Collection JSON

创建文件 `database/api-tests.postman_collection.json`:

```json
{
  "info": {
    "name": "宠物医院API测试",
    "schema": "https://schema.getpostman.com/json/collection/v2.1.0/collection.json"
  },
  "item": [
    {
      "name": "用户注册",
      "request": {
        "method": "POST",
        "header": [
          {
            "key": "Content-Type",
            "value": "application/json"
          }
        ],
        "body": {
          "mode": "raw",
          "raw": "{\n  \"name\": \"测试用户\",\n  \"email\": \"test@example.com\",\n  \"phone\": \"13800138888\",\n  \"password\": \"test123\"\n}"
        },
        "url": {
          "raw": "http://localhost:8080/api/user/register",
          "protocol": "http",
          "host": ["localhost"],
          "port": "8080",
          "path": ["api", "user", "register"]
        }
      }
    },
    {
      "name": "用户登录",
      "request": {
        "method": "POST",
        "header": [
          {
            "key": "Content-Type",
            "value": "application/json"
          }
        ],
        "body": {
          "mode": "raw",
          "raw": "{\n  \"email\": \"zhangsan@example.com\",\n  \"password\": \"123456\"\n}"
        },
        "url": {
          "raw": "http://localhost:8080/api/user/login",
          "protocol": "http",
          "host": ["localhost"],
          "port": "8080",
          "path": ["api", "user", "login"]
        }
      }
    },
    {
      "name": "创建预约",
      "request": {
        "method": "POST",
        "header": [
          {
            "key": "Content-Type",
            "value": "application/json"
          }
        ],
        "body": {
          "mode": "raw",
          "raw": "{\n  \"user_id\": 1,\n  \"petName\": \"测试宠物\",\n  \"petType\": \"狗\",\n  \"serviceType\": \"疫苗接种\",\n  \"appointmentDate\": \"2024-02-01\",\n  \"appointmentTime\": \"09:00-10:00\",\n  \"symptoms\": \"定期疫苗接种\",\n  \"doctor\": \"张伟\"\n}"
        },
        "url": {
          "raw": "http://localhost:8080/appointments/create",
          "protocol": "http",
          "host": ["localhost"],
          "port": "8080",
          "path": ["appointments", "create"]
        }
      }
    },
    {
      "name": "获取所有预约",
      "request": {
        "method": "GET",
        "url": {
          "raw": "http://localhost:8080/appointments/all",
          "protocol": "http",
          "host": ["localhost"],
          "port": "8080",
          "path": ["appointments", "all"]
        }
      }
    },
    {
      "name": "管理员登录",
      "request": {
        "method": "POST",
        "header": [
          {
            "key": "Content-Type",
            "value": "application/json"
          }
        ],
        "body": {
          "mode": "raw",
          "raw": "{\n  \"email\": \"admin@hospital.com\",\n  \"password\": \"admin123\"\n}"
        },
        "url": {
          "raw": "http://localhost:8080/api/admin/login",
          "protocol": "http",
          "host": ["localhost"],
          "port": "8080",
          "path": ["api", "admin", "login"]
        }
      }
    }
  ]
}
```

---

## 🔍 测试验证要点

### 响应状态码
- ✅ 成功: 200 OK
- ❌ 失败: 500 Internal Server Error

### 响应格式
所有API统一返回格式：
```json
{
  "code": 200,
  "msg": "操作成功",
  "data": { ... },
  "role": "USER"
}
```

### 错误处理
测试错误场景：
- 邮箱已存在
- 用户不存在
- 密码错误
- 参数缺失

---

## 💡 截图技巧

### Postman截图
1. 使用截图工具截取整个Postman窗口
2. 确保请求和响应都清晰可见
3. 可以使用Postman的"Save Response"功能保存响应示例

### 浏览器截图
1. 使用F12开发者工具查看网络请求
2. 在Network面板中找到对应请求
3. 截取请求详情和响应内容

### 标注建议
- 使用截图工具添加箭头指向关键信息
- 添加文字说明API功能
- 高亮显示返回的code和msg字段

---

## 📝 测试数据准备

### 数据库初始化
确保已执行 `database/init.sql`，包含以下测试数据：

**测试用户:**
- zhangsan@example.com / 123456
- lisi@example.com / 123456

**测试管理员:**
- admin@hospital.com / admin123

---

## 🎯 测试顺序建议

1. **先测试用户注册** → 创建新用户
2. **测试用户登录** → 验证登录功能
3. **测试创建预约** → 创建预约记录
4. **测试查询预约** → 查看预约列表
5. **测试管理员登录** → 管理员功能验证

---

## ⚠️ 注意事项

1. 确保后端服务正常运行
2. 数据库连接配置正确
3. 使用正确的端口号 (8080)
4. Content-Type必须设置为application/json
5. 测试完成后保存截图到指定位置

---

**如有API测试问题，请检查:**
- 后端日志输出
- 数据库连接状态
- 请求参数格式
- 网络连接是否正常