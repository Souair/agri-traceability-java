# 基于 Java 的农产品溯源系统设计（Vue 前后端分离版）

> 对应论文题目：**基于 Java 的农产品溯源系统设计**  
> 当前架构：**Spring Boot + MyBatis（后端 API）** + **Vue3 + Vite（前端 SPA）**

## 1. 项目简介
本项目实现了农产品全链条溯源系统，覆盖：

- 批次建档（产品、产地、生产主体）
- 生命周期事件记录（种植/加工/检测/物流/销售）
- 消费者二维码查询
- 链式哈希防篡改校验
- 模拟上链存证（后续可接入联盟链）

## 2. 目录结构

```text
agri-traceability-java
├── frontend/                        # Vue 前端
│   ├── src/
│   │   ├── api/
│   │   ├── router/
│   │   └── views/
│   ├── package.json
│   └── vite.config.js
├── src/main/java/com/shaoyang/traceability
│   ├── controller/                  # 纯 REST API
│   ├── service/
│   ├── mapper/
│   └── config/CorsConfig.java       # 跨域配置
└── src/main/resources
    ├── application.yml
    ├── schema.sql
    └── data.sql
```

## 3. 技术栈

### 后端
- Java 17+
- Spring Boot 3
- MyBatis
- H2（默认）/ MySQL（可选）

### 前端
- Vue 3
- Vue Router
- Axios
- Vite

## 4. 启动方式

## 4.1 启动后端（端口 8080）

```bash
cd /root/.openclaw/workspace/agri-traceability-java
mvn spring-boot:run
```

后端 API 地址：`http://localhost:8080/api`

可选：H2 控制台 `http://localhost:8080/h2-console`

## 4.2 启动前端（端口 5173）

```bash
cd /root/.openclaw/workspace/agri-traceability-java/frontend
npm install
npm run dev
```

前端访问：`http://localhost:5173`

> 开发模式下，Vite 已把 `/api` 代理到 `http://localhost:8080`。

## 5. 页面功能

- `/`：消费者溯源查询（输入二维码）
- `/admin`：批次创建 + 事件录入 + 批次列表

默认演示二维码：`demo-qr-001`

## 6. API（节选）

- `POST /api/batches`：创建批次
- `GET /api/batches`：批次列表
- `POST /api/batches/{batchId}/events`：新增溯源事件
- `GET /api/trace/{qrCode}`：按二维码查询溯源链

完整说明：`docs/api.md`

## 7. 与文献综述对应点

1. **可信性**：使用链式哈希校验，解决“数据易篡改”问题。  
2. **全链条**：覆盖种植、加工、检测、物流、销售。  
3. **可扩展 IoT**：支持写入传感器 JSON 数据。  
4. **易推广**：前后端分离，便于后续扩展移动端/小程序。

## 8. 后续可扩展（论文加分项）

- 接入 Hyperledger Fabric 完成真实链上存证
- 增加 Spring Security + JWT 权限控制
- 引入 Redis 缓存与分页查询
- 将 IoT 数据接入 MQ 流式处理
- 前端引入图表展示溯源统计与异常分析
