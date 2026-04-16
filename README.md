# 基于 Java 的农产品溯源系统设计

> 对应论文题目：**基于 Java 的农产品溯源系统设计**

## 1. 项目简介
本项目实现了一个可演示的农产品溯源平台，覆盖登录鉴权、批次建档、过程采集、质检留痕、仓储流转、二维码生成与扫码溯源。

当前版本重点做了两类增强：
- **成品化前端体验**：登录页、首页统计卡片、批次中心文案与主题风格精修，支持浅色 / 深色手动切换。
- **MySQL 项目数据收口**：后端默认使用 **MySQL**，并在启动时自动将本项目历史示例数据中的“湘潭 / 雨湖”展示文案纠偏为 **邵阳市大祥区** 设定。

## 2. 技术栈

### 后端
- Java 17+
- Spring Boot 3
- MyBatis
- MySQL 8+

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

先准备 MySQL 数据库，例如：

```sql
CREATE DATABASE agri_traceability DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

再设置环境变量（也可以直接使用 `application.yml` 中的默认值）：

```bash
export DB_HOST=127.0.0.1
export DB_PORT=3306
export DB_NAME=agri_traceability
export DB_USERNAME=root
export DB_PASSWORD=123456
```

如果你希望首次启动时自动执行 `schema.sql` 和 `data.sql` 初始化：

```bash
export APP_SQL_INIT_MODE=always
```

启动后端：

```bash
cd /root/.openclaw/workspace/agri-traceability-java
mvn spring-boot:run
```

后端 API：`http://localhost:8080/api`

> 注意：默认不会每次启动都自动重建表。历史 MySQL 数据会在启动时执行一次**项目地区纠偏**，把旧的“湘潭 / 雨湖”项目示例数据自动更新为“邵阳市大祥区”设定。

### 4.2 启动前端（5173）

```bash
cd /root/.openclaw/workspace/agri-traceability-java/frontend
npm install
npm run dev
```

前端地址：`http://localhost:5173`

如需让手机扫码后直接打开溯源页，建议额外配置：

```bash
export VITE_PUBLIC_TRACE_BASE_URL=https://你的可访问域名
```

## 5. 页面说明

- `/auth`：登录 / 注册（未登录不可进入其他页面）
- `/dashboard`：运营总览、统计图、关键指标卡片
- `/ops`：业务管理（产品、批次、生产/加工/质检/仓储、二维码生成）
- `/batch-center`：批次台账、链路校验与追溯详情
- `/trace`：扫码溯源 / 手动查询

## 6. 演示账号

- 管理员：`admin / admin123`
- 企业：`enterprise / ent123`
- 普通用户：`user / user123`

## 7. 项目地区设定说明

- **用户本人长期记忆位置** 与本项目展示地区设定无关。
- **仅本项目**默认展示 / 示例产地按 **湖南省邵阳市大祥区** 处理。
- 若数据库中已有旧示例数据，后端启动时会自动执行一次纠偏更新。

## 8. 核心 API（节选）

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
