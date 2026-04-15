# 基于 Java 的农产品溯源系统设计

> 对应论文题目：**基于 Java 的农产品溯源系统设计**

## 1. 项目简介
本项目实现了一个可演示的农产品溯源平台，包含用户、企业、监管与消费者常用功能：

- 用户模块：登录、注册、角色（管理员 / 企业 / 用户）
- 产品管理 + 批次管理
- 生产记录（种植信息 + 农资使用）
- 加工记录（极简）
- 质检管理（合格 / 不合格）
- 仓储管理（入库 / 出库）
- 二维码生成 + 扫码溯源
- ECharts 统计大屏（3个图）
- 溯源链式哈希校验（防篡改）

## 2. 技术栈

### 后端
- Java 17+
- Spring Boot 3
- MyBatis
- H2（默认）/ MySQL（可选）

### 前端
- Vue 3
- Vue Router
- Axios
- ECharts
- qrcode
- Vite

## 3. 目录结构

```text
agri-traceability-java
├── frontend/
│   ├── src/
│   │   ├── api/
│   │   ├── router/
│   │   ├── stores/
│   │   └── views/
├── src/main/java/com/shaoyang/traceability
│   ├── controller/
│   ├── service/
│   ├── mapper/
│   └── domain/
└── src/main/resources
    ├── application.yml
    ├── schema.sql
    └── data.sql
```

## 4. 启动方式

### 4.1 启动后端（8080）

```bash
cd /root/.openclaw/workspace/agri-traceability-java
mvn spring-boot:run
```

后端 API：`http://localhost:8080/api`

H2 控制台：`http://localhost:8080/h2-console`

### 4.2 启动前端（5173）

```bash
cd /root/.openclaw/workspace/agri-traceability-java/frontend
npm install
npm run dev
```

前端地址：`http://localhost:5173`

## 5. 页面说明

- `/auth`：登录 / 注册（角色切换）
- `/dashboard`：ECharts 统计图
- `/ops`：业务管理（产品、批次、生产/加工/质检/仓储、二维码生成）
- `/batch-center`：批次台账与追溯详情
- `/trace`：扫码溯源 / 手动查询

## 6. 演示账号

- 管理员：`admin / admin123`
- 企业：`enterprise / ent123`
- 普通用户：`user / user123`

## 7. 核心 API（节选）

- 认证
  - `POST /api/auth/register`
  - `POST /api/auth/login`
  - `GET /api/auth/me`
- 产品/批次
  - `GET /api/products`
  - `POST /api/products`
  - `POST /api/batches`
  - `PUT /api/batches/{id}/status`
- 业务记录
  - `POST /api/batches/{id}/production-records`
  - `POST /api/batches/{id}/processing-records`
  - `POST /api/batches/{id}/quality-records`
  - `POST /api/batches/{id}/warehouse-records`
- 溯源与统计
  - `GET /api/trace/{qrCode}`
  - `GET /api/stats/product-batches`
  - `GET /api/stats/quality-ratio`
  - `GET /api/stats/monthly-batches?months=6`

完整接口说明见：`docs/api.md`
