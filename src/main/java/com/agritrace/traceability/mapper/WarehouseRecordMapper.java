package com.agritrace.traceability.mapper;

import com.agritrace.traceability.domain.entity.WarehouseRecord;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface WarehouseRecordMapper {

    @Insert("""
            INSERT INTO warehouse_record (batch_id, record_type, quantity, location, record_time, operator_name)
            VALUES (#{batchId}, #{recordType}, #{quantity}, #{location}, #{recordTime}, #{operatorName})
            """)
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(WarehouseRecord record);

    @Select("SELECT * FROM warehouse_record WHERE batch_id = #{batchId} ORDER BY record_time DESC, id DESC")
    List<WarehouseRecord> findByBatchId(Long batchId);
}
