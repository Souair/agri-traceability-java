package com.agritrace.traceability.mapper;

import com.agritrace.traceability.domain.entity.ProductionRecord;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface ProductionRecordMapper {

    @Insert("""
            INSERT INTO production_record (batch_id, event_time, planting_info, agri_input, operator_name)
            VALUES (#{batchId}, #{eventTime}, #{plantingInfo}, #{agriInput}, #{operatorName})
            """)
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(ProductionRecord record);

    @Select("SELECT * FROM production_record WHERE batch_id = #{batchId} ORDER BY event_time DESC, id DESC")
    List<ProductionRecord> findByBatchId(Long batchId);
}
