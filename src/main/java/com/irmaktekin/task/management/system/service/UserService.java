package com.irmaktekin.task.management.system.service;

import com.irmaktekin.task.management.system.common.exception.UserNotFoundException;
import com.irmaktekin.task.management.system.dto.request.UserCreateRequest;
import com.irmaktekin.task.management.system.dto.response.UserDto;
import com.irmaktekin.task.management.system.entity.User;
import org.springframework.data.domain.Page;
import java.util.UUID;

public interface UserService {
    User createUser(UserCreateRequest userCreateRequest);
    User updateUser(UUID id, User user) throws UserNotFoundException;
    Page<UserDto> getUsers(int page, int size);
    User findUserById(UUID id) throws UserNotFoundException;
    void deleteUser(UUID id);
}
