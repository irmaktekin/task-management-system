package com.irmaktekin.task.management.system.service;

import com.irmaktekin.task.management.system.common.exception.UserNotFoundException;
import com.irmaktekin.task.management.system.dto.response.UserDto;
import com.irmaktekin.task.management.system.entity.User;
import com.irmaktekin.task.management.system.enums.RoleType;
import org.springframework.data.domain.Page;
import java.util.UUID;

public interface UserService {
    User updateUser(User user) throws UserNotFoundException;
    Page<UserDto> getUsers(int page, int size);
    User findUserById(UUID id) throws UserNotFoundException;
    User assignRoleToUser(UUID userId, RoleType roleType);
    User softDeleteUser(UUID userId);
    User getUserByName(String username);
}
