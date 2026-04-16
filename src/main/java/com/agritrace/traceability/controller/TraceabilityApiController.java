package com.agritrace.traceability.controller;

import com.agritrace.traceability.common.ApiResponse;
import com.agritrace.traceability.domain.dto.CreateBatchRequest;
import com.agritrace.traceability.domain.dto.CreateTraceEventRequest;
import com.agritrace.traceability.domain.dto.TraceChainResponse;
import com.agritrace.traceability.domain.entity.ProductBatch;
import com.agritrace.traceability.domain.entity.TraceEvent;
import com.agritrace.traceability.service.AuthService;
import com.agritrace.traceability.service.TraceabilityService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api")
public class TraceabilityApiController {

    private final TraceabilityService traceabilityService;
    private final AuthService authService;

    public TraceabilityApiController(TraceabilityService traceabilityService,
                                     AuthService authService) {
        this.traceabilityService = traceabilityService;
        this.authService = authService;
    }

    @PostMapping("/batches")
    public ApiResponse<ProductBatch> createBatch(@Valid @RequestBody CreateBatchRequest request,
                                                  @RequestHeader(value = "Authorization", required = false) String authorization) {
        authService.requireRole(authorization, "ADMIN", "ENTERPRISE");
        return ApiResponse.ok(traceabilityService.createBatch(request));
    }

    @GetMapping("/batches")
    public ApiResponse<List<ProductBatch>> listBatches() {
        return ApiResponse.ok(traceabilityService.listBatches());
    }

    @PostMapping("/batches/{batchId}/events")
    public ApiResponse<TraceEvent> addEvent(@PathVariable Long batchId,
                                            @Valid @RequestBody CreateTraceEventRequest request,
                                            @RequestHeader(value = "Authorization", required = false) String authorization) {
        authService.requireRole(authorization, "ADMIN", "ENTERPRISE");
        return ApiResponse.ok(traceabilityService.addTraceEvent(batchId, request));
    }

    @GetMapping("/trace/{qrCode}")
    public ApiResponse<TraceChainResponse> query(@PathVariable String qrCode) {
        return ApiResponse.ok(traceabilityService.queryByQrCode(qrCode));
    }
}
