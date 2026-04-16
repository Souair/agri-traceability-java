package com.agritrace.traceability.domain.entity;

import java.time.LocalDateTime;

public class TraceEvent {

    private Long id;
    private Long batchId;
    private String stage;
    private LocalDateTime eventTime;
    private String operatorName;
    private String location;
    private String details;
    private String iotPayload;
    private String blockHash;
    private String prevHash;
    private String onChainTxId;
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

    public String getStage() {
        return stage;
    }

    public void setStage(String stage) {
        this.stage = stage;
    }

    public LocalDateTime getEventTime() {
        return eventTime;
    }

    public void setEventTime(LocalDateTime eventTime) {
        this.eventTime = eventTime;
    }

    public String getOperatorName() {
        return operatorName;
    }

    public void setOperatorName(String operatorName) {
        this.operatorName = operatorName;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public String getIotPayload() {
        return iotPayload;
    }

    public void setIotPayload(String iotPayload) {
        this.iotPayload = iotPayload;
    }

    public String getBlockHash() {
        return blockHash;
    }

    public void setBlockHash(String blockHash) {
        this.blockHash = blockHash;
    }

    public String getPrevHash() {
        return prevHash;
    }

    public void setPrevHash(String prevHash) {
        this.prevHash = prevHash;
    }

    public String getOnChainTxId() {
        return onChainTxId;
    }

    public void setOnChainTxId(String onChainTxId) {
        this.onChainTxId = onChainTxId;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
