package com.agritrace.traceability.mapper;

import com.agritrace.traceability.domain.entity.ProductBatch;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

@Mapper
public interface ProductBatchMapper {

    @Insert("""
            INSERT INTO product_batch (batch_code, product_name, origin, producer, planting_date, harvest_date, status, qr_code)
            VALUES (#{batchCode}, #{productName}, #{origin}, #{producer}, #{plantingDate}, #{harvestDate}, #{status}, #{qrCode})
            """)
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(ProductBatch batch);

    @Select("SELECT * FROM product_batch WHERE id = #{id}")
    ProductBatch findById(Long id);

    @Select("SELECT * FROM product_batch WHERE qr_code = #{qrCode}")
    ProductBatch findByQrCode(String qrCode);

    @Select("SELECT * FROM product_batch ORDER BY id DESC")
    List<ProductBatch> findAll();

    @Update("UPDATE product_batch SET status = #{status} WHERE id = #{id}")
    int updateStatus(@Param("id") Long id, @Param("status") String status);
}
