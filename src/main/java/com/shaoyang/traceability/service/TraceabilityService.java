package com.shaoyang.traceability.service;

import com.shaoyang.traceability.domain.dto.CreateBatchRequest;
import com.shaoyang.traceability.domain.dto.CreateTraceEventRequest;
import com.shaoyang.traceability.domain.dto.TraceChainResponse;
import com.shaoyang.traceability.domain.entity.ProductBatch;
import com.shaoyang.traceability.domain.entity.TraceEvent;
import com.shaoyang.traceability.mapper.ProductBatchMapper;
import com.shaoyang.traceability.mapper.TraceEventMapper;
import com.shaoyang.traceability.util.HashUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

@Service
public class TraceabilityService {

    private final ProductBatchMapper productBatchMapper;
    private final TraceEventMapper traceEventMapper;
    private final BlockchainEvidenceService blockchainEvidenceService;

    public TraceabilityService(ProductBatchMapper productBatchMapper,
                               TraceEventMapper traceEventMapper,
                               BlockchainEvidenceService blockchainEvidenceService) {
        this.productBatchMapper = productBatchMapper;
        this.traceEventMapper = traceEventMapper;
        this.blockchainEvidenceService = blockchainEvidenceService;
    }

    @Transactional
    public ProductBatch createBatch(CreateBatchRequest request) {
        ProductBatch batch = new ProductBatch();
        batch.setBatchCode(StringUtils.hasText(request.getBatchCode())
                ? request.getBatchCode()
                : generateBatchCode());
        batch.setProductName(request.getProductName());
        batch.setOrigin(request.getOrigin());
        batch.setProducer(request.getProducer());
        batch.setPlantingDate(request.getPlantingDate());
        batch.setHarvestDate(request.getHarvestDate());
        batch.setStatus("IN_PRODUCTION");
        batch.setQrCode(UUID.randomUUID().toString().replace("-", ""));
        productBatchMapper.insert(batch);
        return batch;
    }

    public List<ProductBatch> listBatches() {
        return productBatchMapper.findAll();
    }

    @Transactional
    public TraceEvent addTraceEvent(Long batchId, CreateTraceEventRequest request) {
        ProductBatch batch = productBatchMapper.findById(batchId);
        if (batch == null) {
            throw new IllegalArgumentException("批次不存在: " + batchId);
        }

        TraceEvent latest = traceEventMapper.findLatestByBatchId(batchId);
        String prevHash = latest == null ? "GENESIS" : latest.getBlockHash();

        TraceEvent event = new TraceEvent();
        event.setBatchId(batchId);
        event.setStage(request.getStage().name());
        event.setEventTime(request.getEventTime());
        event.setOperatorName(request.getOperatorName());
        event.setLocation(request.getLocation());
        event.setDetails(request.getDetails());
        event.setIotPayload(request.getIotPayload());
        event.setPrevHash(prevHash);

        String payload = buildHashPayload(event);
        String blockHash = HashUtils.sha256(payload);
        event.setBlockHash(blockHash);
        event.setOnChainTxId(blockchainEvidenceService.anchorHash(blockHash));

        traceEventMapper.insert(event);
        return event;
    }

    public TraceChainResponse queryByQrCode(String qrCode) {
        ProductBatch batch = productBatchMapper.findByQrCode(qrCode);
        if (batch == null) {
            throw new IllegalArgumentException("未找到该二维码对应的农产品批次");
        }

        List<TraceEvent> events = traceEventMapper.findByBatchId(batch.getId());
        boolean integrityValid = verifyChain(events);
        return new TraceChainResponse(batch, events, integrityValid);
    }

    private boolean verifyChain(List<TraceEvent> events) {
        String expectedPrev = "GENESIS";
        for (TraceEvent event : events) {
            if (!expectedPrev.equals(event.getPrevHash())) {
                return false;
            }
            String currentHash = HashUtils.sha256(buildHashPayload(event));
            if (!currentHash.equals(event.getBlockHash())) {
                return false;
            }
            expectedPrev = event.getBlockHash();
        }
        return true;
    }

    private String buildHashPayload(TraceEvent event) {
        return String.join("|",
                safe(event.getBatchId()),
                safe(event.getStage()),
                safe(event.getEventTime()),
                safe(event.getOperatorName()),
                safe(event.getLocation()),
                safe(event.getDetails()),
                safe(event.getIotPayload()),
                safe(event.getPrevHash()));
    }

    private String safe(Object value) {
        return value == null ? "" : String.valueOf(value);
    }

    private String generateBatchCode() {
        String ts = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        String random = UUID.randomUUID().toString().substring(0, 6).toUpperCase();
        return "BATCH-" + ts + "-" + random;
    }
}
