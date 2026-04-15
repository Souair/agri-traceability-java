# API 文档

## 通用响应

```json
{
  "code": 0,
  "message": "OK",
  "data": {}
}
```

- `code = 0` 表示成功
- 非 0 表示业务错误

---

## 1) 创建批次

`POST /api/batches`

### 请求体
```json
{
  "batchCode": "BATCH-20260415-001",
  "productName": "有机番茄",
  "origin": "湖南省湘潭市雨湖区",
  "producer": "雨湖区示范农场",
  "plantingDate": "2026-03-20",
  "harvestDate": "2026-04-15"
}
```

### 响应
返回创建后的批次对象（含 `id`、`qrCode`）。

---

## 2) 查询批次列表

`GET /api/batches`

### 响应
返回批次数组（按 id 倒序）。

---

## 3) 新增溯源事件

`POST /api/batches/{batchId}/events`

### 请求体
```json
{
  "stage": "LOGISTICS",
  "eventTime": "2026-04-15T10:30:00",
  "operatorName": "王师傅",
  "location": "湘潭冷链中转站",
  "details": "冷链运输，车厢温度 4°C",
  "iotPayload": "{\"temperature\":4,\"humidity\":55,\"gps\":\"112.91,27.86\"}"
}
```

### stage 枚举
- `SEED`
- `PLANTING`
- `PROCESSING`
- `INSPECTION`
- `LOGISTICS`
- `SALES`

### 响应
返回新增事件（含 `blockHash`、`prevHash`、`onChainTxId`）。

---

## 4) 按二维码查询溯源链

`GET /api/trace/{qrCode}`

### 响应
```json
{
  "batch": {},
  "events": [],
  "integrityValid": true
}
```
