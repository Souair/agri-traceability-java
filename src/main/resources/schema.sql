DROP TABLE IF EXISTS warehouse_record;
DROP TABLE IF EXISTS quality_record;
DROP TABLE IF EXISTS processing_record;
DROP TABLE IF EXISTS production_record;
DROP TABLE IF EXISTS trace_event;
DROP TABLE IF EXISTS product_batch;
DROP TABLE IF EXISTS product;
DROP TABLE IF EXISTS sys_user;

CREATE TABLE sys_user (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(64) NOT NULL UNIQUE,
    password_hash CHAR(64) NOT NULL,
    role VARCHAR(32) NOT NULL,
    enterprise_name VARCHAR(128),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE product (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(128) NOT NULL,
    category VARCHAR(64) NOT NULL,
    description VARCHAR(255),
    owner_enterprise VARCHAR(128),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

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

CREATE TABLE production_record (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    batch_id BIGINT NOT NULL,
    event_time TIMESTAMP NOT NULL,
    planting_info VARCHAR(255) NOT NULL,
    agri_input VARCHAR(255) NOT NULL,
    operator_name VARCHAR(64) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_prod_batch FOREIGN KEY (batch_id) REFERENCES product_batch(id)
);

CREATE TABLE processing_record (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    batch_id BIGINT NOT NULL,
    event_time TIMESTAMP NOT NULL,
    process_info VARCHAR(255) NOT NULL,
    operator_name VARCHAR(64) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_processing_batch FOREIGN KEY (batch_id) REFERENCES product_batch(id)
);

CREATE TABLE quality_record (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    batch_id BIGINT NOT NULL,
    result VARCHAR(8) NOT NULL,
    notes VARCHAR(255) NOT NULL,
    inspector VARCHAR(64) NOT NULL,
    inspect_time TIMESTAMP NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_quality_batch FOREIGN KEY (batch_id) REFERENCES product_batch(id)
);

CREATE TABLE warehouse_record (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    batch_id BIGINT NOT NULL,
    record_type VARCHAR(8) NOT NULL,
    quantity DECIMAL(12, 2) NOT NULL,
    location VARCHAR(128) NOT NULL,
    record_time TIMESTAMP NOT NULL,
    operator_name VARCHAR(64) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_warehouse_batch FOREIGN KEY (batch_id) REFERENCES product_batch(id)
);

CREATE INDEX idx_trace_event_batch_time ON trace_event(batch_id, event_time);
CREATE INDEX idx_production_batch_time ON production_record(batch_id, event_time);
CREATE INDEX idx_processing_batch_time ON processing_record(batch_id, event_time);
CREATE INDEX idx_quality_batch_time ON quality_record(batch_id, inspect_time);
CREATE INDEX idx_warehouse_batch_time ON warehouse_record(batch_id, record_time);
