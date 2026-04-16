package com.agritrace.traceability.controller;

import com.agritrace.traceability.common.ApiResponse;
import com.agritrace.traceability.domain.dto.CreateProcessingRecordRequest;
import com.agritrace.traceability.domain.dto.CreateProductRequest;
import com.agritrace.traceability.domain.dto.CreateProductionRecordRequest;
import com.agritrace.traceability.domain.dto.CreateQualityRecordRequest;
import com.agritrace.traceability.domain.dto.CreateWarehouseRecordRequest;
import com.agritrace.traceability.domain.dto.UpdateBatchStatusRequest;
import com.agritrace.traceability.domain.entity.ProcessingRecord;
import com.agritrace.traceability.domain.entity.Product;
import com.agritrace.traceability.domain.entity.ProductBatch;
import com.agritrace.traceability.domain.entity.ProductionRecord;
import com.agritrace.traceability.domain.entity.QualityRecord;
import com.agritrace.traceability.domain.entity.WarehouseRecord;
import com.agritrace.traceability.service.AgroModuleService;
import com.agritrace.traceability.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class AgroModuleController {

    private final AgroModuleService agroModuleService;
    private final AuthService authService;

    public AgroModuleController(AgroModuleService agroModuleService, AuthService authService) {
        this.agroModuleService = agroModuleService;
        this.authService = authService;
    }

    @GetMapping("/products")
    public ApiResponse<List<Product>> listProducts() {
        return ApiResponse.ok(agroModuleService.listProducts());
    }

    @PostMapping("/products")
    public ApiResponse<Product> createProduct(@Valid @RequestBody CreateProductRequest request,
                                              @RequestHeader(value = "Authorization", required = false) String authorization) {
        AuthService.AuthUser authUser = authService.requireRole(authorization, "ADMIN", "ENTERPRISE");
        return ApiResponse.ok(agroModuleService.createProduct(request, authUser.username()));
    }

    @PutMapping("/products/{id}")
    public ApiResponse<Product> updateProduct(@PathVariable Long id,
                                              @Valid @RequestBody CreateProductRequest request,
                                              @RequestHeader(value = "Authorization", required = false) String authorization) {
        AuthService.AuthUser authUser = authService.requireRole(authorization, "ADMIN", "ENTERPRISE");
        return ApiResponse.ok(agroModuleService.updateProduct(id, request, authUser.username()));
    }

    @DeleteMapping("/products/{id}")
    public ApiResponse<Void> deleteProduct(@PathVariable Long id,
                                           @RequestHeader(value = "Authorization", required = false) String authorization) {
        authService.requireRole(authorization, "ADMIN");
        agroModuleService.deleteProduct(id);
        return ApiResponse.ok(null);
    }

    @PutMapping("/batches/{batchId}/status")
    public ApiResponse<ProductBatch> updateBatchStatus(@PathVariable Long batchId,
                                                       @Valid @RequestBody UpdateBatchStatusRequest request,
                                                       @RequestHeader(value = "Authorization", required = false) String authorization) {
        authService.requireRole(authorization, "ADMIN", "ENTERPRISE");
        return ApiResponse.ok(agroModuleService.updateBatchStatus(batchId, request.getStatus()));
    }

    @GetMapping("/batches/{batchId}/production-records")
    public ApiResponse<List<ProductionRecord>> listProductionRecords(@PathVariable Long batchId) {
        return ApiResponse.ok(agroModuleService.listProductionRecords(batchId));
    }

    @PostMapping("/batches/{batchId}/production-records")
    public ApiResponse<ProductionRecord> createProductionRecord(@PathVariable Long batchId,
                                                                @Valid @RequestBody CreateProductionRecordRequest request,
                                                                @RequestHeader(value = "Authorization", required = false) String authorization) {
        authService.requireRole(authorization, "ADMIN", "ENTERPRISE");
        return ApiResponse.ok(agroModuleService.addProductionRecord(batchId, request));
    }

    @GetMapping("/batches/{batchId}/processing-records")
    public ApiResponse<List<ProcessingRecord>> listProcessingRecords(@PathVariable Long batchId) {
        return ApiResponse.ok(agroModuleService.listProcessingRecords(batchId));
    }

    @PostMapping("/batches/{batchId}/processing-records")
    public ApiResponse<ProcessingRecord> createProcessingRecord(@PathVariable Long batchId,
                                                                @Valid @RequestBody CreateProcessingRecordRequest request,
                                                                @RequestHeader(value = "Authorization", required = false) String authorization) {
        authService.requireRole(authorization, "ADMIN", "ENTERPRISE");
        return ApiResponse.ok(agroModuleService.addProcessingRecord(batchId, request));
    }

    @GetMapping("/batches/{batchId}/quality-records")
    public ApiResponse<List<QualityRecord>> listQualityRecords(@PathVariable Long batchId) {
        return ApiResponse.ok(agroModuleService.listQualityRecords(batchId));
    }

    @PostMapping("/batches/{batchId}/quality-records")
    public ApiResponse<QualityRecord> createQualityRecord(@PathVariable Long batchId,
                                                          @Valid @RequestBody CreateQualityRecordRequest request,
                                                          @RequestHeader(value = "Authorization", required = false) String authorization) {
        authService.requireRole(authorization, "ADMIN", "ENTERPRISE");
        return ApiResponse.ok(agroModuleService.addQualityRecord(batchId, request));
    }

    @GetMapping("/batches/{batchId}/warehouse-records")
    public ApiResponse<List<WarehouseRecord>> listWarehouseRecords(@PathVariable Long batchId) {
        return ApiResponse.ok(agroModuleService.listWarehouseRecords(batchId));
    }

    @PostMapping("/batches/{batchId}/warehouse-records")
    public ApiResponse<WarehouseRecord> createWarehouseRecord(@PathVariable Long batchId,
                                                              @Valid @RequestBody CreateWarehouseRecordRequest request,
                                                              @RequestHeader(value = "Authorization", required = false) String authorization) {
        authService.requireRole(authorization, "ADMIN", "ENTERPRISE");
        return ApiResponse.ok(agroModuleService.addWarehouseRecord(batchId, request));
    }

    @GetMapping("/stats/product-batches")
    public ApiResponse<Map<String, Long>> productBatchStats() {
        return ApiResponse.ok(agroModuleService.productBatchCountStats());
    }

    @GetMapping("/stats/quality-ratio")
    public ApiResponse<Map<String, Long>> qualityRatioStats() {
        return ApiResponse.ok(agroModuleService.qualityRatioStats());
    }

    @GetMapping("/stats/monthly-batches")
    public ApiResponse<List<Map<String, Object>>> monthlyBatchStats(@RequestParam(defaultValue = "6") int months) {
        return ApiResponse.ok(agroModuleService.monthlyBatchTrend(months));
    }
}
