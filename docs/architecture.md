# 系统架构说明

## 1. 总体架构

系统由前端业务门户、后端业务服务、数据存储与可信存证扩展组成：

- **前端门户（Vue3）**：用户登录、业务录入、统计图表、扫码溯源
- **后端服务（Spring Boot + MyBatis）**：认证授权、业务流程、统计接口、链式校验
- **数据库（H2 / MySQL）**：存储用户、产品、批次、业务记录、溯源链
- **可信扩展层**：关键哈希存证，预留联盟链接口

```text
Web Portal
   │ HTTP/JSON
   ▼
Spring Boot API
   │
   ├── MyBatis -> H2/MySQL
   └── BlockchainEvidenceService (可替换为真实链)
```

## 2. 角色与权限模型

- **管理员（ADMIN）**：全量管理（用户、产品、批次、记录）
- **企业（ENTERPRISE）**：维护产品、批次及业务记录
- **普通用户（USER）**：查询统计与溯源信息

认证方式：`/api/auth/login` 获取 `Bearer token`，后端按角色校验写入权限。

## 3. 业务域模型

### 3.1 主体数据
- `sys_user`：用户与角色
- `product`：产品台账
- `product_batch`：批次台账（二维码核心索引）

### 3.2 过程数据
- `production_record`：生产记录（种植信息 + 农资）
- `processing_record`：加工记录
- `quality_record`：质检记录（PASS/FAIL）
- `warehouse_record`：仓储记录（IN/OUT）
- `trace_event`：链式溯源事件（哈希防篡改）

## 4. 防篡改与溯源链

`trace_event` 记录以下关键字段：
- `prev_hash`
- `block_hash`
- `on_chain_tx_id`

通过 `SHA-256` 形成链式结构，查询时执行完整性校验并返回 `integrityValid`。

## 5. 统计能力（ECharts）

后端提供轻量统计接口，前端直连渲染三张图：
1. 各产品批次数量（柱状图）
2. 质检合格/不合格占比（饼图）
3. 近 6 个月批次趋势（折线图）

## 6. 可扩展方向

- 接入真实区块链（Fabric）替换模拟存证
- 升级为 Spring Security + JWT
- 增加分页、数据权限与审计日志
- 接入 IoT 实时采集与告警链路
