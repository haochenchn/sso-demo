package com.aaron.cas.dao;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface UserMapper {

    /**
     * 创建用户
     * @param username
     * @param password
     * @return
     */
    void insert(@Param("username") String username, @Param("password") String password);
}
