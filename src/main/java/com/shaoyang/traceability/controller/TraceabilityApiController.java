package com.shaoyang.traceability.controller;

import com.shaoyang.traceability.common.ApiResponse;
import com.shaoyang.traceability.domain.dto.CreateBatchRequest;
import com.shaoyang.traceability.domain.dto.CreateTraceEventRequest;
import com.shaoyang.traceability.domain.dto.TraceChainResponse;
import com.shaoyang.traceability.domain.entity.ProductBatch;
import com.shaoyang.traceability.domain.entity.TraceEvent;
import com.shaoyang.traceability.service.TraceabilityService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api")
public class TraceabilityApiController {

    private final TraceabilityService traceabilityService;

    public TraceabilityApiController(TraceabilityService traceabilityService) {
        this.traceabilityService = traceabilityService;
    }

    @PostMapping("/batches")
    public ApiResponse<ProductBatch> createBatch(@Valid @RequestBody CreateBatchRequest request) {
        return ApiResponse.ok(traceabilityService.createBatch(request));
    }

    @GetMapping("/batches")
    public ApiResponse<List<ProductBatch>> listBatches() {
        return ApiResponse.ok(traceabilityService.listBatches());
    }

    @PostMapping("/batches/{batchId}/events")
    public ApiResponse<TraceEvent> addEvent(@PathVariable Long batchId,
                                            @Valid @RequestBody CreateTraceEventRequest request) {
        return ApiResponse.ok(traceabilityService.addTraceEvent(batchId, request));
    }

    @GetMapping("/trace/{qrCode}")
    public ApiResponse<TraceChainResponse> query(@PathVariable String qrCode) {
        return ApiResponse.ok(traceabilityService.queryByQrCode(qrCode));
    }
}
