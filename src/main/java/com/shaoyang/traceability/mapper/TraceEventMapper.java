package com.shaoyang.traceability.mapper;

import com.shaoyang.traceability.domain.entity.TraceEvent;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface TraceEventMapper {

    @Insert("""
            INSERT INTO trace_event (batch_id, stage, event_time, operator_name, location, details, iot_payload, block_hash, prev_hash, on_chain_tx_id)
            VALUES (#{batchId}, #{stage}, #{eventTime}, #{operatorName}, #{location}, #{details}, #{iotPayload}, #{blockHash}, #{prevHash}, #{onChainTxId})
            """)
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(TraceEvent event);

    @Select("SELECT * FROM trace_event WHERE batch_id = #{batchId} ORDER BY event_time ASC, id ASC")
    List<TraceEvent> findByBatchId(Long batchId);

    @Select("SELECT * FROM trace_event WHERE batch_id = #{batchId} ORDER BY event_time DESC, id DESC LIMIT 1")
    TraceEvent findLatestByBatchId(Long batchId);
}
