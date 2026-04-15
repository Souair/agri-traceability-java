package com.shaoyang.traceability.domain.dto;

import com.shaoyang.traceability.domain.entity.ProductBatch;
import com.shaoyang.traceability.domain.entity.TraceEvent;

import java.util.List;

public class TraceChainResponse {

    private ProductBatch batch;
    private List<TraceEvent> events;
    private boolean integrityValid;

    public TraceChainResponse(ProductBatch batch, List<TraceEvent> events, boolean integrityValid) {
        this.batch = batch;
        this.events = events;
        this.integrityValid = integrityValid;
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
}
