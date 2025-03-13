package com.irmaktekin.task.management.system.service.impl;

import com.irmaktekin.task.management.system.common.exception.UserNotFoundException;
import com.irmaktekin.task.management.system.dto.request.UserCreateRequest;
import com.irmaktekin.task.management.system.dto.response.UserDto;
import com.irmaktekin.task.management.system.entity.User;
import com.irmaktekin.task.management.system.repository.UserRepository;
import com.irmaktekin.task.management.system.service.UserService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public User createUser(@Valid UserCreateRequest userCreateRequest) {
        User createdUser = User.builder().fullName(userCreateRequest.fullName())
                .email(userCreateRequest.email())
                .password(userCreateRequest.password())
                .isActive(userCreateRequest.isActive()).build();
        return userRepository.save(createdUser);
    }

    @Override
    public User updateUser(UUID id, User user) throws UserNotFoundException {
        User existingUser = userRepository.findById(id)
                .orElseThrow(()->new UserNotFoundException("User not found with id: "+id));
        existingUser.setFullName(user.getFullName());
        existingUser.setEmail(user.getEmail());
        existingUser.setPassword(user.getPassword());
        existingUser.setActive(user.isActive());
        return userRepository.save(existingUser);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<UserDto> getUsers(int page, int size) {
        return userRepository.getUsers(PageRequest.of(page,size));
    }

    @Override
    public User findUserById(UUID id) throws UserNotFoundException {
        return userRepository.findById(id)
                .orElseThrow(()->new UserNotFoundException("User not found with id: "+ id));
    }

    @Override
    public void deleteUser(UUID id) {
        User existingUser = userRepository.findById(id)
                .orElseThrow(()->new UserNotFoundException("User not found with id: " + id));
        existingUser.setActive(false);
        userRepository.save(existingUser);
    }
}
