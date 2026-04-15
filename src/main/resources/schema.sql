DROP TABLE IF EXISTS trace_event;
DROP TABLE IF EXISTS product_batch;

CREATE TABLE product_batch (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    batch_code VARCHAR(64) NOT NULL UNIQUE,
    product_name VARCHAR(128) NOT NULL,
    origin VARCHAR(128) NOT NULL,
    producer VARCHAR(128) NOT NULL,
    planting_date DATE,
    harvest_date DATE,
    status VARCHAR(32) NOT NULL,
    qr_code VARCHAR(128) NOT NULL UNIQUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE trace_event (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    batch_id BIGINT NOT NULL,
    stage VARCHAR(32) NOT NULL,
    event_time TIMESTAMP NOT NULL,
    operator_name VARCHAR(64) NOT NULL,
    location VARCHAR(128) NOT NULL,
    details TEXT NOT NULL,
    iot_payload TEXT,
    block_hash CHAR(64) NOT NULL,
    prev_hash CHAR(64) NOT NULL,
    on_chain_tx_id VARCHAR(128),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_trace_batch FOREIGN KEY (batch_id) REFERENCES product_batch(id)
);

CREATE INDEX idx_trace_event_batch_time ON trace_event(batch_id, event_time);
