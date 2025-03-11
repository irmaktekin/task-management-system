package com.irmaktekin.task.management.system.service;

import com.irmaktekin.task.management.system.common.exception.UserNotFoundException;
import com.irmaktekin.task.management.system.entity.User;

import java.util.List;
import java.util.UUID;

public interface UserService {
    User createUser(User user);
    User updateUser(UUID id, User user) throws UserNotFoundException;
    List<User> getUsers();
    User findUserById(UUID id) throws UserNotFoundException;
    void deleteUser(UUID id);
}
