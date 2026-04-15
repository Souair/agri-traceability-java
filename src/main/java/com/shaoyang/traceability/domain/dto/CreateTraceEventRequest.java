package com.shaoyang.traceability.domain.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.shaoyang.traceability.domain.enums.TraceStage;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public class CreateTraceEventRequest {

    @NotNull(message = "stage 不能为空")
    private TraceStage stage;

    @NotNull(message = "eventTime 不能为空")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime eventTime;

    @NotBlank(message = "operatorName 不能为空")
    private String operatorName;

    @NotBlank(message = "location 不能为空")
    private String location;

    @NotBlank(message = "details 不能为空")
    private String details;

    private String iotPayload;

    public TraceStage getStage() {
        return stage;
    }

    public void setStage(TraceStage stage) {
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
}
