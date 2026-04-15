INSERT INTO product_batch (id, batch_code, product_name, origin, producer, planting_date, harvest_date, status, qr_code)
VALUES (1, 'BATCH-20260415-001', '有机番茄', '湖南省湘潭市雨湖区', '雨湖区示范农场', '2026-03-20', '2026-04-15', 'IN_PRODUCTION', 'demo-qr-001');

INSERT INTO trace_event (batch_id, stage, event_time, operator_name, location, details, iot_payload, block_hash, prev_hash, on_chain_tx_id)
VALUES (1, 'PLANTING', '2026-03-20 08:00:00', '周洁', '雨湖区示范基地 A 区', '完成移栽，启用滴灌系统', '{"soilMoisture":61,"temperature":24}',
        'a5db3998d445f544f5e4f28ca86bce8022f2df7b1ff4ab9f57f3397fdfa4f6ce', 'GENESIS', 'SIM-A5DB3998D445F544');

INSERT INTO trace_event (batch_id, stage, event_time, operator_name, location, details, iot_payload, block_hash, prev_hash, on_chain_tx_id)
VALUES (1, 'INSPECTION', '2026-04-10 09:30:00', '质检员李工', '湘潭农残检测站', '农残检测合格，批次允许流通', '{"pesticideResidue":"PASS"}',
        '2a2f2e1431cdf6f50780795d9643db5cbd5fc25ec27a8d2caee9628604f8372d', 'a5db3998d445f544f5e4f28ca86bce8022f2df7b1ff4ab9f57f3397fdfa4f6ce', 'SIM-2A2F2E1431CDF6F5');
