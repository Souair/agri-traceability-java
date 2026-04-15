package com.shaoyang.traceability.mapper;

import com.shaoyang.traceability.domain.entity.ProcessingRecord;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface ProcessingRecordMapper {

    @Insert("""
            INSERT INTO processing_record (batch_id, event_time, process_info, operator_name)
            VALUES (#{batchId}, #{eventTime}, #{processInfo}, #{operatorName})
            """)
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(ProcessingRecord record);

    @Select("SELECT * FROM processing_record WHERE batch_id = #{batchId} ORDER BY event_time DESC, id DESC")
    List<ProcessingRecord> findByBatchId(Long batchId);
}
