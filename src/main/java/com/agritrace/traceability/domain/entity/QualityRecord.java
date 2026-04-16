package com.agritrace.traceability.domain.entity;

import java.time.LocalDateTime;

public class QualityRecord {

    private Long id;
    private Long batchId;
    private String result;
    private String notes;
    private String inspector;
    private LocalDateTime inspectTime;
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

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public String getInspector() {
        return inspector;
    }

    public void setInspector(String inspector) {
        this.inspector = inspector;
    }

    public LocalDateTime getInspectTime() {
        return inspectTime;
    }

    public void setInspectTime(LocalDateTime inspectTime) {
        this.inspectTime = inspectTime;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
