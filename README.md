# 基于 Java 的农产品溯源系统设计（Web 项目）

> 对应论文题目：**基于 Java 的农产品溯源系统设计**  
> 技术栈：Spring Boot 3 + MyBatis + Thymeleaf + H2/MySQL + SHA-256 链式存证（可扩展到区块链）

## 1. 项目简介
本项目实现了一个可落地演示的农产品全链条溯源 Web 系统，覆盖：

- 农产品批次建档（种植主体、产地、时间）
- 溯源事件记录（种植/加工/物流/销售/检测）
- 消费者扫码查询（通过二维码唯一标识）
- 关键数据链式哈希存证（防篡改验证）
- 多角色扩展接口（生产者/监管者/消费者）

> 说明：当前版本使用“链式哈希 + 模拟上链交易号”的轻量方案，便于毕业设计演示与低成本部署；后续可直接替换为 Hyperledger Fabric 等联盟链。

## 2. 功能模块

### 2.1 批次管理
- 创建农产品批次
- 自动生成二维码标识 `qrCode`
- 批次列表查询

### 2.2 全生命周期事件管理
- 为批次追加溯源事件（按阶段）
- 记录时间、地点、操作人、详情、IoT 载荷
- 自动计算 `blockHash` 与 `prevHash`

### 2.3 溯源查询
- 消费者通过 `qrCode` 查询全流程
- 展示完整时间线
- 给出链路完整性校验结果

### 2.4 监管与可信存证（演示版）
- 每条记录生成链式哈希
- 生成模拟上链交易号 `SIM-xxxxx`
- 支持数据完整性检验

## 3. 目录结构

```text
agri-traceability-java
├── docs/
│   ├── api.md
│   └── architecture.md
├── src/main/java/com/shaoyang/traceability
│   ├── common
│   ├── controller
│   ├── domain
│   ├── mapper
│   ├── service
│   └── util
└── src/main/resources
    ├── static/css
    ├── templates
    ├── application.yml
    ├── data.sql
    └── schema.sql
```

## 4. 运行方式

### 4.1 环境要求
- JDK 17+
- Maven 3.9+

### 4.2 本地运行（默认 H2）
```bash
mvn spring-boot:run
```

启动后访问：
- 首页（消费者查询）：`http://localhost:8080/`
- 管理端（演示录入）：`http://localhost:8080/admin`
- H2 控制台：`http://localhost:8080/h2-console`

### 4.3 使用 MySQL（可选）
修改 `application.yml` 的 `spring.datasource` 配置为 MySQL，执行 `schema.sql` 初始化即可。

## 5. 核心接口（节选）

- `POST /api/batches`：创建批次
- `GET /api/batches`：批次列表
- `POST /api/batches/{batchId}/events`：新增溯源事件
- `GET /api/trace/{qrCode}`：按二维码查询溯源链

完整文档见 `docs/api.md`。

## 6. 与文献综述的对应关系

1. **可信数据层**：采用链式哈希（可扩展区块链）回应“数据易篡改”痛点。  
2. **全链条场景覆盖**：覆盖种植、加工、物流、销售、检测的生命周期。  
3. **IoT 融合能力**：事件支持写入传感器数据 JSON（温湿度/定位等）。  
4. **轻量可推广**：单体架构、部署成本低，适配本科毕业设计与基层应用。  

## 7. 后续可扩展建议（论文加分项）

- 引入 Spring Security + JWT（多角色权限）
- Redis 缓存热点溯源查询
- 对接 Hyperledger Fabric 完成真正链上存证
- 接入消息队列（Kafka/RabbitMQ）处理 IoT 海量数据
- 对接二维码生成与扫码组件

---
如果你要作为毕业设计提交，建议在此基础上补：
- UML 用例图/时序图
- 数据库 E-R 图
- 压测结果（QPS、响应时间）
- 与传统溯源方案对比实验
