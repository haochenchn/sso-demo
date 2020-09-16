package com.aaron.cas.service.impl;

import com.aaron.cas.model.UserDto;
import com.aaron.cas.service.IUserService;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class IUserServiceImpl implements IUserService {

    @Autowired
    private SqlSessionTemplate sqlSession;

    @Override
    public UserDto findByUserName(String userName) {
        return sqlSession.selectOne("user.findByUserName", userName);
    }
}
