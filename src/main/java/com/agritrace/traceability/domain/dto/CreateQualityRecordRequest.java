package com.agritrace.traceability.domain.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

import java.time.LocalDateTime;

public class CreateQualityRecordRequest {

    @NotBlank(message = "result 不能为空")
    @Pattern(regexp = "PASS|FAIL", message = "result 仅支持 PASS/FAIL")
    private String result;

    @NotBlank(message = "notes 不能为空")
    private String notes;

    @NotBlank(message = "inspector 不能为空")
    private String inspector;

    @NotNull(message = "inspectTime 不能为空")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime inspectTime;

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
}
