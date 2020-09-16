package com.aaron.cas.service;

import com.aaron.cas.model.UserDto;

public interface IUserService {

    UserDto findByUserName(String userName);
}
