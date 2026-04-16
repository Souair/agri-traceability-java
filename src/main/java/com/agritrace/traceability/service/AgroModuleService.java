package com.agritrace.traceability.service;

import com.agritrace.traceability.domain.dto.CreateProcessingRecordRequest;
import com.agritrace.traceability.domain.dto.CreateProductRequest;
import com.agritrace.traceability.domain.dto.CreateProductionRecordRequest;
import com.agritrace.traceability.domain.dto.CreateQualityRecordRequest;
import com.agritrace.traceability.domain.dto.CreateWarehouseRecordRequest;
import com.agritrace.traceability.domain.entity.ProcessingRecord;
import com.agritrace.traceability.domain.entity.Product;
import com.agritrace.traceability.domain.entity.ProductBatch;
import com.agritrace.traceability.domain.entity.ProductionRecord;
import com.agritrace.traceability.domain.entity.QualityRecord;
import com.agritrace.traceability.domain.entity.WarehouseRecord;
import com.agritrace.traceability.mapper.ProcessingRecordMapper;
import com.agritrace.traceability.mapper.ProductBatchMapper;
import com.agritrace.traceability.mapper.ProductMapper;
import com.agritrace.traceability.mapper.ProductionRecordMapper;
import com.agritrace.traceability.mapper.QualityRecordMapper;
import com.agritrace.traceability.mapper.WarehouseRecordMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

@Service
public class AgroModuleService {

    private final ProductMapper productMapper;
    private final ProductBatchMapper productBatchMapper;
    private final ProductionRecordMapper productionRecordMapper;
    private final ProcessingRecordMapper processingRecordMapper;
    private final QualityRecordMapper qualityRecordMapper;
    private final WarehouseRecordMapper warehouseRecordMapper;

    public AgroModuleService(ProductMapper productMapper,
                             ProductBatchMapper productBatchMapper,
                             ProductionRecordMapper productionRecordMapper,
                             ProcessingRecordMapper processingRecordMapper,
                             QualityRecordMapper qualityRecordMapper,
                             WarehouseRecordMapper warehouseRecordMapper) {
        this.productMapper = productMapper;
        this.productBatchMapper = productBatchMapper;
        this.productionRecordMapper = productionRecordMapper;
        this.processingRecordMapper = processingRecordMapper;
        this.qualityRecordMapper = qualityRecordMapper;
        this.warehouseRecordMapper = warehouseRecordMapper;
    }

    public List<Product> listProducts() {
        return productMapper.findAll();
    }

    @Transactional
    public Product createProduct(CreateProductRequest request, String defaultEnterprise) {
        Product product = new Product();
        product.setName(request.getName().trim());
        product.setCategory(request.getCategory().trim());
        product.setDescription(request.getDescription());
        product.setOwnerEnterprise(request.getOwnerEnterprise() != null && !request.getOwnerEnterprise().isBlank()
                ? request.getOwnerEnterprise().trim()
                : defaultEnterprise);
        productMapper.insert(product);
        return product;
    }

    @Transactional
    public Product updateProduct(Long id, CreateProductRequest request, String defaultEnterprise) {
        Product existing = productMapper.findById(id);
        if (existing == null) {
            throw new IllegalArgumentException("产品不存在: " + id);
        }

        existing.setName(request.getName().trim());
        existing.setCategory(request.getCategory().trim());
        existing.setDescription(request.getDescription());
        existing.setOwnerEnterprise(request.getOwnerEnterprise() != null && !request.getOwnerEnterprise().isBlank()
                ? request.getOwnerEnterprise().trim()
                : defaultEnterprise);
        productMapper.update(existing);
        return existing;
    }

    @Transactional
    public void deleteProduct(Long id) {
        productMapper.deleteById(id);
    }

    @Transactional
    public ProductBatch updateBatchStatus(Long batchId, String status) {
        ProductBatch batch = requireBatch(batchId);
        productBatchMapper.updateStatus(batchId, status.trim().toUpperCase(Locale.ROOT));
        return productBatchMapper.findById(batchId);
    }

    public List<ProductionRecord> listProductionRecords(Long batchId) {
        requireBatch(batchId);
        return productionRecordMapper.findByBatchId(batchId);
    }

    @Transactional
    public ProductionRecord addProductionRecord(Long batchId, CreateProductionRecordRequest request) {
        requireBatch(batchId);
        ProductionRecord record = new ProductionRecord();
        record.setBatchId(batchId);
        record.setEventTime(request.getEventTime());
        record.setPlantingInfo(request.getPlantingInfo());
        record.setAgriInput(request.getAgriInput());
        record.setOperatorName(request.getOperatorName());
        productionRecordMapper.insert(record);
        return record;
    }

    public List<ProcessingRecord> listProcessingRecords(Long batchId) {
        requireBatch(batchId);
        return processingRecordMapper.findByBatchId(batchId);
    }

    @Transactional
    public ProcessingRecord addProcessingRecord(Long batchId, CreateProcessingRecordRequest request) {
        requireBatch(batchId);
        ProcessingRecord record = new ProcessingRecord();
        record.setBatchId(batchId);
        record.setEventTime(request.getEventTime());
        record.setProcessInfo(request.getProcessInfo());
        record.setOperatorName(request.getOperatorName());
        processingRecordMapper.insert(record);
        return record;
    }

    public List<QualityRecord> listQualityRecords(Long batchId) {
        requireBatch(batchId);
        return qualityRecordMapper.findByBatchId(batchId);
    }

    @Transactional
    public QualityRecord addQualityRecord(Long batchId, CreateQualityRecordRequest request) {
        requireBatch(batchId);
        QualityRecord record = new QualityRecord();
        record.setBatchId(batchId);
        record.setResult(request.getResult());
        record.setNotes(request.getNotes());
        record.setInspector(request.getInspector());
        record.setInspectTime(request.getInspectTime());
        qualityRecordMapper.insert(record);
        return record;
    }

    public List<WarehouseRecord> listWarehouseRecords(Long batchId) {
        requireBatch(batchId);
        return warehouseRecordMapper.findByBatchId(batchId);
    }

    @Transactional
    public WarehouseRecord addWarehouseRecord(Long batchId, CreateWarehouseRecordRequest request) {
        requireBatch(batchId);
        WarehouseRecord record = new WarehouseRecord();
        record.setBatchId(batchId);
        record.setRecordType(request.getRecordType());
        record.setQuantity(request.getQuantity());
        record.setLocation(request.getLocation());
        record.setRecordTime(request.getRecordTime());
        record.setOperatorName(request.getOperatorName());
        warehouseRecordMapper.insert(record);
        return record;
    }

    public Map<String, Long> productBatchCountStats() {
        Map<String, Long> map = new LinkedHashMap<>();
        List<ProductBatch> batches = productBatchMapper.findAll();
        batches.forEach(batch -> map.merge(batch.getProductName(), 1L, Long::sum));
        return map;
    }

    public Map<String, Long> qualityRatioStats() {
        Map<String, Long> map = new LinkedHashMap<>();
        map.put("PASS", 0L);
        map.put("FAIL", 0L);

        qualityRecordMapper.findAll()
                .forEach(item -> map.computeIfPresent(item.getResult(), (k, v) -> v + 1));

        return map;
    }

    public List<Map<String, Object>> monthlyBatchTrend(int months) {
        int safeMonths = Math.max(1, Math.min(months, 24));
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM");
        YearMonth current = YearMonth.now();

        Map<String, Long> monthlyCounter = new LinkedHashMap<>();
        for (int i = safeMonths - 1; i >= 0; i--) {
            String month = current.minusMonths(i).format(formatter);
            monthlyCounter.put(month, 0L);
        }

        productBatchMapper.findAll().forEach(batch -> {
            if (batch.getCreatedAt() == null) {
                return;
            }
            String key = YearMonth.from(batch.getCreatedAt()).format(formatter);
            if (monthlyCounter.containsKey(key)) {
                monthlyCounter.put(key, monthlyCounter.get(key) + 1);
            }
        });

        List<Map<String, Object>> rows = new ArrayList<>();
        monthlyCounter.forEach((month, count) -> {
            Map<String, Object> row = new LinkedHashMap<>();
            row.put("month", month);
            row.put("count", count);
            rows.add(row);
        });

        return rows;
    }

    private ProductBatch requireBatch(Long batchId) {
        ProductBatch batch = productBatchMapper.findById(batchId);
        if (batch == null) {
            throw new IllegalArgumentException("批次不存在: " + batchId);
        }
        return batch;
    }
}
