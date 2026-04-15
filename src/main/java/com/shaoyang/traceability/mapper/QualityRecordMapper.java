package com.shaoyang.traceability.mapper;

import com.shaoyang.traceability.domain.entity.QualityRecord;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface QualityRecordMapper {

    @Insert("""
            INSERT INTO quality_record (batch_id, result, notes, inspector, inspect_time)
            VALUES (#{batchId}, #{result}, #{notes}, #{inspector}, #{inspectTime})
            """)
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(QualityRecord record);

    @Select("SELECT * FROM quality_record WHERE batch_id = #{batchId} ORDER BY inspect_time DESC, id DESC")
    List<QualityRecord> findByBatchId(Long batchId);

    @Select("SELECT * FROM quality_record ORDER BY inspect_time DESC, id DESC")
    List<QualityRecord> findAll();
}
