package com.shaoyang.traceability.domain.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotBlank;

import java.time.LocalDate;

public class CreateBatchRequest {

    private String batchCode;

    @NotBlank(message = "productName 不能为空")
    private String productName;

    @NotBlank(message = "origin 不能为空")
    private String origin;

    @NotBlank(message = "producer 不能为空")
    private String producer;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate plantingDate;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate harvestDate;

    public String getBatchCode() {
        return batchCode;
    }

    public void setBatchCode(String batchCode) {
        this.batchCode = batchCode;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getOrigin() {
        return origin;
    }

    public void setOrigin(String origin) {
        this.origin = origin;
    }

    public String getProducer() {
        return producer;
    }

    public void setProducer(String producer) {
        this.producer = producer;
    }

    public LocalDate getPlantingDate() {
        return plantingDate;
    }

    public void setPlantingDate(LocalDate plantingDate) {
        this.plantingDate = plantingDate;
    }

    public LocalDate getHarvestDate() {
        return harvestDate;
    }

    public void setHarvestDate(LocalDate harvestDate) {
        this.harvestDate = harvestDate;
    }
}
