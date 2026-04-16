package com.agritrace.traceability.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface ProjectRegionDataFixMapper {

    @Update("""
            UPDATE sys_user
            SET enterprise_name = REPLACE(REPLACE(REPLACE(enterprise_name, '湖南省湘潭市雨湖区', '湖南省邵阳市大祥区'), '湘潭市雨湖区', '邵阳市大祥区'), '雨湖区示范农场', '大祥区示范农场')
            WHERE enterprise_name LIKE '%湘潭%' OR enterprise_name LIKE '%雨湖%'
            """)
    int normalizeUsers();

    @Update("""
            UPDATE product
            SET owner_enterprise = REPLACE(REPLACE(REPLACE(owner_enterprise, '湖南省湘潭市雨湖区', '湖南省邵阳市大祥区'), '湘潭市雨湖区', '邵阳市大祥区'), '雨湖区示范农场', '大祥区示范农场')
            WHERE owner_enterprise LIKE '%湘潭%' OR owner_enterprise LIKE '%雨湖%'
            """)
    int normalizeProducts();

    @Update("""
            UPDATE product_batch
            SET origin = REPLACE(REPLACE(REPLACE(origin, '湖南省湘潭市雨湖区', '湖南省邵阳市大祥区'), '湘潭市雨湖区', '邵阳市大祥区'), '湘潭雨湖区', '邵阳大祥区'),
                producer = REPLACE(REPLACE(REPLACE(producer, '雨湖区示范农场', '大祥区示范农场'), '雨湖生态合作社', '大祥生态合作社'), '雨湖', '大祥')
            WHERE origin LIKE '%湘潭%' OR origin LIKE '%雨湖%' OR producer LIKE '%湘潭%' OR producer LIKE '%雨湖%'
            """)
    int normalizeBatches();

    @Update("""
            UPDATE trace_event
            SET location = REPLACE(REPLACE(REPLACE(REPLACE(location, '湖南省湘潭市雨湖区', '湖南省邵阳市大祥区'), '湘潭市雨湖区', '邵阳市大祥区'), '湘潭', '邵阳'), '雨湖', '大祥')
            WHERE location LIKE '%湘潭%' OR location LIKE '%雨湖%'
            """)
    int normalizeTraceEvents();

    @Update("""
            UPDATE warehouse_record
            SET location = REPLACE(REPLACE(REPLACE(REPLACE(location, '湖南省湘潭市雨湖区', '湖南省邵阳市大祥区'), '湘潭市雨湖区', '邵阳市大祥区'), '湘潭', '邵阳'), '雨湖', '大祥')
            WHERE location LIKE '%湘潭%' OR location LIKE '%雨湖%'
            """)
    int normalizeWarehouseRecords();
}
