package com.shaoyang.traceability.domain.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class CreateWarehouseRecordRequest {

    @NotBlank(message = "recordType 不能为空")
    @Pattern(regexp = "IN|OUT", message = "recordType 仅支持 IN/OUT")
    private String recordType;

    @NotNull(message = "quantity 不能为空")
    @DecimalMin(value = "0.01", message = "quantity 必须大于 0")
    private BigDecimal quantity;

    @NotBlank(message = "location 不能为空")
    private String location;

    @NotNull(message = "recordTime 不能为空")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime recordTime;

    @NotBlank(message = "operatorName 不能为空")
    private String operatorName;

    public String getRecordType() {
        return recordType;
    }

    public void setRecordType(String recordType) {
        this.recordType = recordType;
    }

    public BigDecimal getQuantity() {
        return quantity;
    }

    public void setQuantity(BigDecimal quantity) {
        this.quantity = quantity;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public LocalDateTime getRecordTime() {
        return recordTime;
    }

    public void setRecordTime(LocalDateTime recordTime) {
        this.recordTime = recordTime;
    }

    public String getOperatorName() {
        return operatorName;
    }

    public void setOperatorName(String operatorName) {
        this.operatorName = operatorName;
    }
}
