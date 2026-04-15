# API 文档

## 通用响应

```json
{
  "code": 0,
  "message": "OK",
  "data": {}
}
```

- `code = 0`：成功
- 非 0：业务错误

---

## 1. 用户模块

### 1.1 注册
`POST /api/auth/register`

```json
{
  "username": "test001",
  "password": "123456",
  "role": "USER",
  "enterpriseName": "可选"
}
```

### 1.2 登录
`POST /api/auth/login`

```json
{
  "username": "admin",
  "password": "admin123"
}
```

### 1.3 当前用户
`GET /api/auth/me`

> 需要请求头：`Authorization: Bearer <token>`

---

## 2. 产品与批次

### 2.1 产品列表
`GET /api/products`

### 2.2 新增产品（管理员/企业）
`POST /api/products`

```json
{
  "name": "有机番茄",
  "category": "蔬菜",
  "description": "温室种植",
  "ownerEnterprise": "雨湖区示范农场"
}
```

### 2.3 创建批次（管理员/企业）
`POST /api/batches`

```json
{
  "productName": "有机番茄",
  "origin": "湖南省湘潭市雨湖区",
  "producer": "雨湖区示范农场",
  "plantingDate": "2026-03-20",
  "harvestDate": "2026-04-15"
}
```

### 2.4 批次列表
`GET /api/batches`

### 2.5 更新批次状态（管理员/企业）
`PUT /api/batches/{batchId}/status`

```json
{
  "status": "IN_TRANSIT"
}
```

---

## 3. 业务记录

### 3.1 生产记录（种植 + 农资）
- `GET /api/batches/{batchId}/production-records`
- `POST /api/batches/{batchId}/production-records`

```json
{
  "eventTime": "2026-04-15T08:30:00",
  "plantingInfo": "完成移栽",
  "agriInput": "生物菌肥 30kg/亩",
  "operatorName": "周洁"
}
```

### 3.2 加工记录（极简）
- `GET /api/batches/{batchId}/processing-records`
- `POST /api/batches/{batchId}/processing-records`

```json
{
  "eventTime": "2026-04-15T11:00:00",
  "processInfo": "清洗、分拣、包装",
  "operatorName": "赵敏"
}
```

### 3.3 质检管理（合格/不合格）
- `GET /api/batches/{batchId}/quality-records`
- `POST /api/batches/{batchId}/quality-records`

```json
{
  "result": "PASS",
  "notes": "农残检测合格",
  "inspector": "李工",
  "inspectTime": "2026-04-15T14:20:00"
}
```

### 3.4 仓储记录（入库/出库）
- `GET /api/batches/{batchId}/warehouse-records`
- `POST /api/batches/{batchId}/warehouse-records`

```json
{
  "recordType": "IN",
  "quantity": 1200,
  "location": "湘潭雨湖冷库A区",
  "recordTime": "2026-04-15T17:00:00",
  "operatorName": "周明"
}
```

---

## 4. 溯源与二维码

### 4.1 新增链式事件（管理员/企业）
`POST /api/batches/{batchId}/events`

### 4.2 按二维码查询
`GET /api/trace/{qrCode}`

响应包含：
- 批次信息
- 链式溯源事件 + `integrityValid`
- 生产/加工/质检/仓储记录

---

## 5. 统计接口（ECharts）

### 5.1 产品批次数量（柱状图）
`GET /api/stats/product-batches`

### 5.2 质检合格/不合格占比（饼图）
`GET /api/stats/quality-ratio`

### 5.3 近 N 个月批次趋势（折线图）
`GET /api/stats/monthly-batches?months=6`
