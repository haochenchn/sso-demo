package com.aaron.cas.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.Map;

@Repository("userDao")
public class UserDao {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    /**
     * 通过用户名查询用户角色信息
     * @param userName
     * @return
     */
    public Map<String,Object> findByUserName(String userName){
        return jdbcTemplate.queryForMap("SELECT * FROM user_info WHERE username =?",userName);
    }
}
