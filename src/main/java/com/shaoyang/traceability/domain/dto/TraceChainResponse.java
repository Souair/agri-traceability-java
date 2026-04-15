package com.shaoyang.traceability.domain.dto;

import com.shaoyang.traceability.domain.entity.ProcessingRecord;
import com.shaoyang.traceability.domain.entity.ProductBatch;
import com.shaoyang.traceability.domain.entity.ProductionRecord;
import com.shaoyang.traceability.domain.entity.QualityRecord;
import com.shaoyang.traceability.domain.entity.TraceEvent;
import com.shaoyang.traceability.domain.entity.WarehouseRecord;

import java.util.Collections;
import java.util.List;

public class TraceChainResponse {

    private ProductBatch batch;
    private List<TraceEvent> events;
    private boolean integrityValid;
    private List<ProductionRecord> productionRecords;
    private List<ProcessingRecord> processingRecords;
    private List<QualityRecord> qualityRecords;
    private List<WarehouseRecord> warehouseRecords;

    public TraceChainResponse(ProductBatch batch, List<TraceEvent> events, boolean integrityValid) {
        this.batch = batch;
        this.events = events;
        this.integrityValid = integrityValid;
        this.productionRecords = Collections.emptyList();
        this.processingRecords = Collections.emptyList();
        this.qualityRecords = Collections.emptyList();
        this.warehouseRecords = Collections.emptyList();
    }

    public ProductBatch getBatch() {
        return batch;
    }

    public void setBatch(ProductBatch batch) {
        this.batch = batch;
    }

    public List<TraceEvent> getEvents() {
        return events;
    }

    public void setEvents(List<TraceEvent> events) {
        this.events = events;
    }

    public boolean isIntegrityValid() {
        return integrityValid;
    }

    public void setIntegrityValid(boolean integrityValid) {
        this.integrityValid = integrityValid;
    }

    public List<ProductionRecord> getProductionRecords() {
        return productionRecords;
    }

    public void setProductionRecords(List<ProductionRecord> productionRecords) {
        this.productionRecords = productionRecords;
    }

    public List<ProcessingRecord> getProcessingRecords() {
        return processingRecords;
    }

    public void setProcessingRecords(List<ProcessingRecord> processingRecords) {
        this.processingRecords = processingRecords;
    }

    public List<QualityRecord> getQualityRecords() {
        return qualityRecords;
    }

    public void setQualityRecords(List<QualityRecord> qualityRecords) {
        this.qualityRecords = qualityRecords;
    }

    public List<WarehouseRecord> getWarehouseRecords() {
        return warehouseRecords;
    }

    public void setWarehouseRecords(List<WarehouseRecord> warehouseRecords) {
        this.warehouseRecords = warehouseRecords;
    }
}
