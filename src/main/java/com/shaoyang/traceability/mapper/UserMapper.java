package com.shaoyang.traceability.mapper;

import com.shaoyang.traceability.domain.entity.SysUser;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface UserMapper {

    @Insert("""
            INSERT INTO sys_user (username, password_hash, role, enterprise_name)
            VALUES (#{username}, #{passwordHash}, #{role}, #{enterpriseName})
            """)
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(SysUser user);

    @Select("SELECT * FROM sys_user WHERE username = #{username}")
    SysUser findByUsername(String username);

    @Select("SELECT * FROM sys_user WHERE id = #{id}")
    SysUser findById(Long id);
}
