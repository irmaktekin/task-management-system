package com.irmaktekin.task.management.system.service.impl;

import com.irmaktekin.task.management.system.common.exception.UserNotFoundException;
import com.irmaktekin.task.management.system.entity.User;
import com.irmaktekin.task.management.system.service.UserService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class UserServiceImpl implements UserService {

    @Override
    public User createUser(User user) {
        return null;
    }

    @Override
    public User updateUser(UUID id, User user) throws UserNotFoundException {
        return null;
    }

    @Override
    public List<User> getUsers() {
        return List.of();
    }

    @Override
    public User findUserById(UUID id) throws UserNotFoundException {
        return null;
    }

    @Override
    public void deleteUser(UUID id) {

    }
}
