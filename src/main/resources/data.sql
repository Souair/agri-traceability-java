INSERT INTO sys_user (id, username, password_hash, role, enterprise_name)
VALUES (1, 'admin', '240be518fabd2724ddb6f04eeb1da5967448d7e831c08c8fa822809f74c720a9', 'ADMIN', '平台管理中心');

INSERT INTO sys_user (id, username, password_hash, role, enterprise_name)
VALUES (2, 'enterprise', 'c7699e56b753b5fe782b724f2a7074054d88eee7ce3e9ae5d308c5d73f8b1644', 'ENTERPRISE', '雨湖区示范农场');

INSERT INTO sys_user (id, username, password_hash, role, enterprise_name)
VALUES (3, 'user', 'e606e38b0d8c19b24cf0ee3808183162ea7cd63ff7912dbb22b5e803286b4446', 'USER', '消费者账号');

INSERT INTO product (id, name, category, description, owner_enterprise)
VALUES (1, '有机番茄', '蔬菜', '温室滴灌种植，低农残控制', '雨湖区示范农场');

INSERT INTO product (id, name, category, description, owner_enterprise)
VALUES (2, '生态辣椒', '蔬菜', '山地生态种植，品质稳定', '雨湖区示范农场');

INSERT INTO product (id, name, category, description, owner_enterprise)
VALUES (3, '富硒大米', '粮食', '富硒土壤种植，全程监测', '雨湖区示范农场');

INSERT INTO product_batch (id, batch_code, product_name, origin, producer, planting_date, harvest_date, status, qr_code, created_at)
VALUES (1, 'BATCH-20260415-001', '有机番茄', '湖南省湘潭市雨湖区', '雨湖区示范农场', '2026-03-20', '2026-04-15', 'IN_PRODUCTION', 'demo-qr-001', '2026-04-15 10:30:00');

INSERT INTO product_batch (id, batch_code, product_name, origin, producer, planting_date, harvest_date, status, qr_code, created_at)
VALUES (2, 'BATCH-20260320-001', '生态辣椒', '湖南省湘潭市雨湖区', '雨湖区示范农场', '2026-02-15', '2026-03-20', 'COMPLETED', 'demo-qr-002', '2026-03-20 09:20:00');

INSERT INTO product_batch (id, batch_code, product_name, origin, producer, planting_date, harvest_date, status, qr_code, created_at)
VALUES (3, 'BATCH-20260218-001', '富硒大米', '湖南省湘潭市雨湖区', '雨湖区示范农场', '2026-01-10', '2026-02-18', 'COMPLETED', 'demo-qr-003', '2026-02-18 16:20:00');

INSERT INTO product_batch (id, batch_code, product_name, origin, producer, planting_date, harvest_date, status, qr_code, created_at)
VALUES (4, 'BATCH-20260112-001', '有机番茄', '湖南省湘潭市雨湖区', '雨湖区示范农场', '2025-12-10', '2026-01-12', 'COMPLETED', 'demo-qr-004', '2026-01-12 15:10:00');

INSERT INTO product_batch (id, batch_code, product_name, origin, producer, planting_date, harvest_date, status, qr_code, created_at)
VALUES (5, 'BATCH-20251218-001', '生态辣椒', '湖南省湘潭市雨湖区', '雨湖区示范农场', '2025-11-01', '2025-12-18', 'COMPLETED', 'demo-qr-005', '2025-12-18 13:20:00');

INSERT INTO product_batch (id, batch_code, product_name, origin, producer, planting_date, harvest_date, status, qr_code, created_at)
VALUES (6, 'BATCH-20251122-001', '富硒大米', '湖南省湘潭市雨湖区', '雨湖区示范农场', '2025-10-05', '2025-11-22', 'COMPLETED', 'demo-qr-006', '2025-11-22 10:50:00');

INSERT INTO trace_event (batch_id, stage, event_time, operator_name, location, details, iot_payload, block_hash, prev_hash, on_chain_tx_id)
VALUES (1, 'PLANTING', '2026-03-20 08:00:00', '周洁', '雨湖区示范基地 A 区', '完成移栽，启用滴灌系统', '{"soilMoisture":61,"temperature":24}',
        '72c32a522de96e43ca6114c507d3b9e6acc99cd965ea8ca1099bf3d407cfe83f', 'GENESIS', 'SIM-A5DB3998D445F544');

INSERT INTO trace_event (batch_id, stage, event_time, operator_name, location, details, iot_payload, block_hash, prev_hash, on_chain_tx_id)
VALUES (1, 'INSPECTION', '2026-04-10 09:30:00', '质检员李工', '湘潭农残检测站', '农残检测合格，批次允许流通', '{"pesticideResidue":"PASS"}',
        'eac06e6aa469856f7acfe9c9944d9644af55d7ab03f7a9f53a2b9d152ae63080', '72c32a522de96e43ca6114c507d3b9e6acc99cd965ea8ca1099bf3d407cfe83f', 'SIM-2A2F2E1431CDF6F5');

INSERT INTO production_record (batch_id, event_time, planting_info, agri_input, operator_name)
VALUES (1, '2026-03-20 08:00:00', '温室土壤翻耕、定植完成', '生物菌肥 30kg / 亩', '周洁');
INSERT INTO production_record (batch_id, event_time, planting_info, agri_input, operator_name)
VALUES (2, '2026-02-16 07:30:00', '辣椒苗移栽完成', '有机复合肥 20kg / 亩', '王强');
INSERT INTO production_record (batch_id, event_time, planting_info, agri_input, operator_name)
VALUES (3, '2026-01-12 09:10:00', '稻田插秧完成', '缓释肥 25kg / 亩', '李鹏');

INSERT INTO processing_record (batch_id, event_time, process_info, operator_name)
VALUES (1, '2026-04-12 11:00:00', '分拣、清洗、包装完成', '赵敏');
INSERT INTO processing_record (batch_id, event_time, process_info, operator_name)
VALUES (2, '2026-03-18 10:10:00', '辣椒分级包装完成', '陈亮');

INSERT INTO quality_record (batch_id, result, notes, inspector, inspect_time)
VALUES (1, 'PASS', '农残、重金属检测合格', '质检员李工', '2026-04-10 09:30:00');
INSERT INTO quality_record (batch_id, result, notes, inspector, inspect_time)
VALUES (2, 'PASS', '外观与农残指标合格', '质检员王敏', '2026-03-19 16:00:00');
INSERT INTO quality_record (batch_id, result, notes, inspector, inspect_time)
VALUES (3, 'FAIL', '水分超标，需返工烘干', '质检员唐斌', '2026-02-17 15:30:00');
INSERT INTO quality_record (batch_id, result, notes, inspector, inspect_time)
VALUES (4, 'PASS', '抽检合格', '质检员李工', '2026-01-11 11:20:00');

INSERT INTO warehouse_record (batch_id, record_type, quantity, location, record_time, operator_name)
VALUES (1, 'IN', 1200.00, '湘潭雨湖冷库A区', '2026-04-13 10:00:00', '仓管员周明');
INSERT INTO warehouse_record (batch_id, record_type, quantity, location, record_time, operator_name)
VALUES (1, 'OUT', 320.00, '湘潭雨湖冷库A区', '2026-04-14 16:20:00', '仓管员周明');
INSERT INTO warehouse_record (batch_id, record_type, quantity, location, record_time, operator_name)
VALUES (2, 'IN', 980.00, '湘潭雨湖冷库B区', '2026-03-19 09:45:00', '仓管员李翔');
