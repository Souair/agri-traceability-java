package com.shaoyang.traceability.domain.entity;

import java.time.LocalDateTime;

public class ProductionRecord {

    private Long id;
    private Long batchId;
    private LocalDateTime eventTime;
    private String plantingInfo;
    private String agriInput;
    private String operatorName;
    private LocalDateTime createdAt;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getBatchId() {
        return batchId;
    }

    public void setBatchId(Long batchId) {
        this.batchId = batchId;
    }

    public LocalDateTime getEventTime() {
        return eventTime;
    }

    public void setEventTime(LocalDateTime eventTime) {
        this.eventTime = eventTime;
    }

    public String getPlantingInfo() {
        return plantingInfo;
    }

    public void setPlantingInfo(String plantingInfo) {
        this.plantingInfo = plantingInfo;
    }

    public String getAgriInput() {
        return agriInput;
    }

    public void setAgriInput(String agriInput) {
        this.agriInput = agriInput;
    }

    public String getOperatorName() {
        return operatorName;
    }

    public void setOperatorName(String operatorName) {
        this.operatorName = operatorName;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
