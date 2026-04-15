package com.shaoyang.traceability.mapper;

import com.shaoyang.traceability.domain.entity.Product;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

@Mapper
public interface ProductMapper {

    @Insert("""
            INSERT INTO product (name, category, description, owner_enterprise)
            VALUES (#{name}, #{category}, #{description}, #{ownerEnterprise})
            """)
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(Product product);

    @Update("""
            UPDATE product
            SET name = #{name}, category = #{category}, description = #{description}, owner_enterprise = #{ownerEnterprise}
            WHERE id = #{id}
            """)
    int update(Product product);

    @Delete("DELETE FROM product WHERE id = #{id}")
    int deleteById(Long id);

    @Select("SELECT * FROM product WHERE id = #{id}")
    Product findById(Long id);

    @Select("SELECT * FROM product ORDER BY id DESC")
    List<Product> findAll();
}
