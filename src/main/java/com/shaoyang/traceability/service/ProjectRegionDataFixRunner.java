package com.shaoyang.traceability.service;

import com.shaoyang.traceability.mapper.ProjectRegionDataFixMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
public class ProjectRegionDataFixRunner implements ApplicationRunner {

    private static final Logger log = LoggerFactory.getLogger(ProjectRegionDataFixRunner.class);

    private final ProjectRegionDataFixMapper dataFixMapper;
    private final boolean enabled;

    public ProjectRegionDataFixRunner(
            ProjectRegionDataFixMapper dataFixMapper,
            @Value("${project.data-fix.region.enabled:true}") boolean enabled
    ) {
        this.dataFixMapper = dataFixMapper;
        this.enabled = enabled;
    }

    @Override
    public void run(ApplicationArguments args) {
        if (!enabled) {
            log.info("项目地区纠偏已关闭，跳过执行");
            return;
        }

        int affected = 0;
        affected += dataFixMapper.normalizeUsers();
        affected += dataFixMapper.normalizeProducts();
        affected += dataFixMapper.normalizeBatches();
        affected += dataFixMapper.normalizeTraceEvents();
        affected += dataFixMapper.normalizeWarehouseRecords();

        if (affected > 0) {
            log.info("项目地区纠偏完成：共更新 {} 条历史数据为邵阳市大祥区设定", affected);
        } else {
            log.info("项目地区纠偏检查完成：未发现需要修正的历史数据");
        }
    }
}
