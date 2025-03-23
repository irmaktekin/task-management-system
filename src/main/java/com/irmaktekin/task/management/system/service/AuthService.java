package com.irmaktekin.task.management.system.service;

import com.irmaktekin.task.management.system.dto.request.UserRegisterRequest;
import com.irmaktekin.task.management.system.dto.response.UserDto;

public interface AuthService {
    UserDto createUser(UserRegisterRequest userRegisterRequest) throws Exception;
}
