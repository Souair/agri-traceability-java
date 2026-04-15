INSERT INTO product_batch (id, batch_code, product_name, origin, producer, planting_date, harvest_date, status, qr_code)
VALUES (1, 'BATCH-20260415-001', '有机番茄', '湖南省湘潭市雨湖区', '雨湖区示范农场', '2026-03-20', '2026-04-15', 'IN_PRODUCTION', 'demo-qr-001');

INSERT INTO trace_event (batch_id, stage, event_time, operator_name, location, details, iot_payload, block_hash, prev_hash, on_chain_tx_id)
VALUES (1, 'PLANTING', '2026-03-20 08:00:00', '周洁', '雨湖区示范基地 A 区', '完成移栽，启用滴灌系统', '{"soilMoisture":61,"temperature":24}',
        '72c32a522de96e43ca6114c507d3b9e6acc99cd965ea8ca1099bf3d407cfe83f', 'GENESIS', 'SIM-A5DB3998D445F544');

INSERT INTO trace_event (batch_id, stage, event_time, operator_name, location, details, iot_payload, block_hash, prev_hash, on_chain_tx_id)
VALUES (1, 'INSPECTION', '2026-04-10 09:30:00', '质检员李工', '湘潭农残检测站', '农残检测合格，批次允许流通', '{"pesticideResidue":"PASS"}',
        'eac06e6aa469856f7acfe9c9944d9644af55d7ab03f7a9f53a2b9d152ae63080', '72c32a522de96e43ca6114c507d3b9e6acc99cd965ea8ca1099bf3d407cfe83f', 'SIM-2A2F2E1431CDF6F5');
