package com.agritrace.traceability.domain.dto;

import jakarta.validation.constraints.NotBlank;

public class UpdateBatchStatusRequest {

    @NotBlank(message = "status 不能为空")
    private String status;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
