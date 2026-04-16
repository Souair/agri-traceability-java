package com.agritrace.traceability.domain.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public class CreateProductionRecordRequest {

    @NotNull(message = "eventTime 不能为空")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime eventTime;

    @NotBlank(message = "plantingInfo 不能为空")
    private String plantingInfo;

    @NotBlank(message = "agriInput 不能为空")
    private String agriInput;

    @NotBlank(message = "operatorName 不能为空")
    private String operatorName;

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
}
