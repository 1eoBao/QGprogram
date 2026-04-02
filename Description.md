# 宿舍报修管理系统 - 完整使用说明

## 一、项目简介

宿舍报修管理系统是一个**前后端分离**的 Web 应用，用于学生在线提交宿舍报修申请，管理员统一管理和处理报修单。

### 技术栈

| 层次 | 技术 |
|------|------|
| 后端 | Spring Boot + MyBatis Plus + MySQL + JWT |
| 前端 | Vue 3 + Axios + Element Plus |
| 数据库 | MySQL 8.0 |

---

## 二、环境要求

| 软件 | 版本要求 |
|------|----------|
| JDK | 11 或更高 |
| MySQL | 8.0 |
| Node.js | 16 或更高 |
| Maven | 3.6 或更高 |
| IDE | IDEA / VSCode |

---

## 三、快速启动（5分钟）



### 第1步：创建数据库

```sql
CREATE DATABASE dormitory_repair;
USE dormitory_repair;

-- 创建用户表
CREATE TABLE IF NOT EXISTS `user` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '用户ID，主键自增',
    `account` VARCHAR(20) NOT NULL COMMENT '账号（学号/工号）',
    `password` VARCHAR(255) NOT NULL COMMENT '密码（BCrypt加密后存储）',
    `role` VARCHAR(10) NOT NULL COMMENT '角色：student-学生, admin-管理员',
    `name` VARCHAR(50) DEFAULT NULL COMMENT '真实姓名',
    `phone` VARCHAR(20) DEFAULT NULL COMMENT '联系电话',
    `building` VARCHAR(20) DEFAULT NULL COMMENT '宿舍楼栋（学生专用）',
    `room` VARCHAR(10) DEFAULT NULL COMMENT '房间号（学生专用）',
    `status` TINYINT DEFAULT 1 COMMENT '状态：1-正常, 0-禁用',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_account` (`account`),
    KEY `idx_role` (`role`),
    KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户表';



-- 创建报修单表
CREATE TABLE IF NOT EXISTS `repair_order` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '报修单ID，主键自增',
    `order_no` VARCHAR(32) NOT NULL COMMENT '报修单号（UUID）',
    `student_id` BIGINT NOT NULL COMMENT '报修学生ID，关联user表',
    `device_type` VARCHAR(50) NOT NULL COMMENT '设备类型（水龙头、电灯、空调等）',
    `description` TEXT NOT NULL COMMENT '问题描述',
    `image_url` VARCHAR(500) DEFAULT NULL COMMENT '图片URL（支持多张，逗号分隔）',
    `status` VARCHAR(20) NOT NULL DEFAULT 'pending' COMMENT '状态：pending-待处理, processing-处理中, completed-已完成, cancelled-已取消',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_order_no` (`order_no`),
    KEY `idx_student_id` (`student_id`),
    KEY `idx_status` (`status`),
    KEY `idx_create_time` (`create_time`),
    CONSTRAINT `fk_repair_order_student` FOREIGN KEY (`student_id`) REFERENCES `user` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='报修单表';


```

执行项目中的 `schema.sql` 文件创建表结构。

### 第2步：启动后端

**在 IDEA 中：**
1. 打开后端项目
2. 找到 `DormitoryRepairApplication.java`
3. 点击绿色三角形运行

**或在命令行：**
```bash
cd 后端项目目录
mvn spring-boot:run
```

**成功标志：**
```
========================================
宿舍报修管理系统后端启动成功！
API地址: http://localhost:8080
========================================
```

### 第3步：启动前端

**在 VSCode 或命令行中：**
```bash
cd 前端项目目录
npm install   # 首次运行需要
npm run dev
```

**成功标志：**
```
VITE ready in 500 ms
➜ Local: http://localhost:5173/
```

### 第4步：访问系统

打开浏览器访问：**http://localhost:5173**

---

## 四、功能使用指南

### 4.1 注册账号

1. 点击 **注册** 标签
2. 填写信息：
   - 账号：学生 `3125xxxxxx`，管理员 `0025xxxxxx`
   - 角色：选择"学生"或"管理员"
   - 姓名、电话、密码
3. 点击 **注册** 按钮

### 4.2 登录系统


1. 输入账号和密码
2. 点击 **登录** 按钮
3. 自动跳转到对应角色页面

---

### 4.3 学生端功能

登录成功后进入学生端，包含三个模块：

#### 模块1：绑定宿舍（首次登录必须）

| 字段 | 说明 | 示例 |
|------|------|------|
| 楼栋 | 宿舍楼名称 | A栋、B栋、C栋 |
| 房间号 | 具体房间号 | 101、202、305 |

点击 **绑定** 按钮保存。

#### 模块2：创建报修单

| 字段 | 说明 | 示例 |
|------|------|------|
| 设备类型 | 需要维修的设备 | 水龙头、电灯、空调 |
| 问题描述 | 详细描述故障情况 | 水龙头漏水严重 |

点击 **提交报修** 按钮创建报修单。

#### 模块3：我的报修记录

显示所有报修单，包含：
- 单号、设备类型、问题描述
- 状态（待处理/处理中/已完成/已取消）
- 创建时间

**操作**：待处理的报修单可以点击 **取消报修** 按钮取消。

---

### 4.4 管理员端功能

登录管理员账号后，可以：

#### 功能1：查看所有报修单

显示所有学生的报修单，包含：
- 单号、学生ID、设备类型
- 问题描述、状态、创建时间

#### 功能2：按状态筛选

| 状态 | 说明 |
|------|------|
| 全部 | 显示所有报修单 |
| 待处理 | 新提交的报修单 |
| 处理中 | 正在处理的报修单 |
| 已完成 | 维修完成的报修单 |

#### 功能3：修改报修单状态

选择报修单，从下拉框选择新状态：
- 待处理 → 处理中
- 处理中 → 已完成

#### 功能4：删除报修单

点击报修单旁边的 **删除** 按钮即可删除（需要确认）。

---

## 五、状态流转说明

```
学生提交报修单
      ↓
   待处理 (pending)  ← 可以取消
      ↓ 管理员修改
   处理中 (processing)
      ↓ 管理员修改
   已完成 (completed)
```

| 状态 | 英文 | 颜色 | 说明 |
|------|------|------|------|
| 待处理 | pending | 橙色 | 等待管理员处理，学生可取消 |
| 处理中 | processing | 蓝色 | 管理员已接手处理 |
| 已完成 | completed | 绿色 | 维修已完成 |
| 已取消 | cancelled | 红色 | 学生自行取消 |

---

## 六、常见问题

### Q1: 登录失败怎么办？

**检查清单：**
1. 后端是否已启动（访问 http://localhost:8080 看是否返回 Whitelabel Error Page）
2. 数据库是否已连接
3. 账号密码是否正确

### Q2: 注册失败怎么办？

**检查清单：**
1. 账号格式是否正确（学生：3125/3225开头，管理员：0025开头）
2. 账号是否已存在
3. 两次密码是否一致

### Q3: 创建报修单失败？

**检查清单：**
1. 是否已绑定宿舍
2. 设备类型和问题描述是否已填写

### Q4: 前端页面打不开？

**检查清单：**
1. Node.js 是否已安装：`node -v`
2. 依赖是否已安装：`npm install`
3. 前端是否已启动：`npm run dev`

---

## 七、项目结构

### 后端结构
```
src/main/java/com/dormitory/
├── controller/      # 控制层（接收请求）
├── service/         # 业务层（核心逻辑）
├── mapper/          # 数据层（数据库操作）
├── entity/          # 实体类
├── dto/             # 数据传输对象
├── utils/           # 工具类（JWT、加密）
├── config/          # 配置类（跨域）
└── exception/       # 异常处理
```

### 前端结构
```
src/
├── api/             # API 接口
├── utils/           # 工具类（axios封装）
├── views/           # 页面
│   ├── Login.vue    # 登录注册页
│   ├── Student.vue  # 学生端
│   └── Admin.vue    # 管理员端
├── router/          # 路由配置
├── App.vue          # 根组件
└── main.js          # 入口文件
```

---

## 八、API 接口列表

| 方法 | 路径 | 功能 | 权限 |
|------|------|------|------|
| POST | /user/register | 注册 | 公开 |
| POST | /user/login | 登录 | 公开 |
| POST | /user/change-password | 修改密码 | 需登录 |
| POST | /user/bind-dormitory | 绑定宿舍 | 学生 |
| POST | /repair/create | 创建报修单 | 学生 |
| GET | /repair/my-orders | 我的报修单 | 学生 |
| PUT | /repair/cancel/{id} | 取消报修 | 学生 |
| GET | /repair/admin/orders | 所有报修单 | 管理员 |
| PUT | /repair/admin/order/{id}/status | 修改状态 | 管理员 |
| DELETE | /repair/admin/order/{id} | 删除报修单 | 管理员 |

---