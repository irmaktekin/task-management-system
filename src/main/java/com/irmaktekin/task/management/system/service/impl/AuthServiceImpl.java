package com.irmaktekin.task.management.system.service.impl;

import com.irmaktekin.task.management.system.common.exception.RoleNotFoundException;
import com.irmaktekin.task.management.system.dto.request.UserRegisterRequest;
import com.irmaktekin.task.management.system.dto.response.UserDto;
import com.irmaktekin.task.management.system.entity.Role;
import com.irmaktekin.task.management.system.entity.User;
import com.irmaktekin.task.management.system.enums.RoleType;
import com.irmaktekin.task.management.system.repository.RoleRepository;
import com.irmaktekin.task.management.system.repository.UserRepository;
import com.irmaktekin.task.management.system.service.AuthService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.stream.Collectors;


@Service
public class AuthServiceImpl implements AuthService {

    private final PasswordEncoder passwordEncoder;
    private final  UserRepository userRepository;
    private final RoleRepository roleRepository;

    public AuthServiceImpl(PasswordEncoder passwordEncoder, UserRepository userRepository, RoleRepository roleRepository) {
        this.passwordEncoder = passwordEncoder;
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
    }

    @Override
    public UserDto createUser(UserRegisterRequest userRegisterRequest) throws RoleNotFoundException {

        String hashedPassword = passwordEncoder.encode(userRegisterRequest.password());

        Set<Role> roles = getRolesFromRoleTypes(userRegisterRequest.roles());

        User createdUser = new User();
        createdUser.setFullName(userRegisterRequest.fullName());
        createdUser.setUsername(userRegisterRequest.username());
        createdUser.setPassword(hashedPassword);
        createdUser.setActive(userRegisterRequest.active());
        createdUser.setRoles(roles);


        createdUser = userRepository.save(createdUser);

        return new UserDto(createdUser.getId(),createdUser.getFullName(),createdUser.getUsername(),createdUser.isActive());
    }

    public Set<Role> getRolesFromRoleTypes(Set<RoleType> roleTypes) throws RoleNotFoundException {

        Set<Role> roles = roleTypes.stream()
                .map(roleType -> roleRepository.findByRoleType(roleType)
                        .orElseGet(() -> {
                            return roleRepository.findByRoleType(roleType)
                                    .orElseThrow(() -> new RoleNotFoundException("Role not found: " + roleType));
                        }))
                .collect(Collectors.toSet());

        return roles;
    }

}
