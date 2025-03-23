package com.irmaktekin.task.management.system.service.impl;

import com.irmaktekin.task.management.system.common.ErrorMessage;
import com.irmaktekin.task.management.system.common.exception.UserNotFoundException;
import com.irmaktekin.task.management.system.dto.response.UserDto;
import com.irmaktekin.task.management.system.entity.Role;
import com.irmaktekin.task.management.system.entity.User;
import com.irmaktekin.task.management.system.enums.RoleType;
import com.irmaktekin.task.management.system.repository.RoleRepository;
import com.irmaktekin.task.management.system.repository.UserRepository;
import com.irmaktekin.task.management.system.service.UserService;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final ApplicationEventPublisher publisher;

    public UserServiceImpl(UserRepository userRepository, RoleRepository roleRepository, ApplicationEventPublisher publisher) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.publisher = publisher;
    }

    @Override
    public Boolean softDeleteUser(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(()->new UserNotFoundException(ErrorMessage.USER_NOT_FOUND));
        user.setDeleted(true);
        userRepository.save(user);
        return true;
    }

    @Override
    public User getUserByName(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(()->new UserNotFoundException("User not found"));
        return user;
    }


    @Override
    public User updateUser(User user) throws UserNotFoundException {
        User existingUser = userRepository.findById(user.getId())
                .orElseThrow(()->new UserNotFoundException("User not found with id: "+user.getId()));
        existingUser.setFullName(user.getFullName());
        existingUser.setPassword(user.getPassword());
        existingUser.setActive(user.isActive());
        return userRepository.save(existingUser);
    }

    @Override
    public Page<UserDto> getUsers(int page, int size) {
        return userRepository.getUsers(PageRequest.of(page,size));
    }

    @Override
    public User findUserById(UUID id) throws UserNotFoundException {
        return userRepository.findById(id)
                .orElseThrow(()->new UserNotFoundException("User not found with id: "+ id));
    }

    @Override
    public User assignRoleToUser(UUID userId, RoleType roleType) {
        User user = userRepository.findById(userId).orElseThrow(()->new UserNotFoundException("User not found"));

        Role existingRole = roleRepository.findByRoleType(roleType)
                .orElseThrow(()->new IllegalArgumentException("Role not found"));

        user.getRoles().add(existingRole);
        return userRepository.save(user);
    }

    public User getAuthenticatedUser() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (principal instanceof UserDetails userDetails) {
            return userRepository.findByUsername(userDetails.getUsername())
                    .orElseThrow(() -> new UsernameNotFoundException("User not found: " + userDetails.getUsername()));
        }

        throw new UsernameNotFoundException("Authenticated user not found");
    }
}
