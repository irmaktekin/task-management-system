package com.irmaktekin.task.management.system.service;

import com.irmaktekin.task.management.system.common.exception.RoleNotFoundException;
import com.irmaktekin.task.management.system.dto.request.UserRegisterRequest;
import com.irmaktekin.task.management.system.dto.response.UserDto;
import com.irmaktekin.task.management.system.entity.Role;
import com.irmaktekin.task.management.system.entity.User;
import com.irmaktekin.task.management.system.enums.RoleType;
import com.irmaktekin.task.management.system.repository.RoleRepository;
import com.irmaktekin.task.management.system.repository.UserRepository;
import com.irmaktekin.task.management.system.service.impl.AuthServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AuthServiceImplTest {
    @InjectMocks
    private AuthServiceImpl authService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    private UserRegisterRequest userRegisterRequest;

    @BeforeEach
    public void setUp() {
        userRegisterRequest = new UserRegisterRequest("John Doe", "john.doe", "password", true, Set.of(RoleType.MEMBER),false);
    }

    @Test
    public void testCreateUser_Success() throws Exception {
        when(passwordEncoder.encode(anyString())).thenReturn("hjao392rurjajkfjhd2081.");
        Role role = new Role();
        when(roleRepository.findByRoleType(RoleType.MEMBER)).thenReturn(Optional.of(role));

        User savedUser = new User();
        savedUser.setId(UUID.randomUUID());
        savedUser.setFullName(userRegisterRequest.fullName());
        savedUser.setUsername(userRegisterRequest.username());
        savedUser.setPassword("hashed_password");
        savedUser.setActive(userRegisterRequest.active());
        savedUser.setRoles(Set.of(role));
        when(userRepository.save(ArgumentMatchers.any(User.class))).thenReturn(savedUser);

        UserDto userDto = authService.createUser(userRegisterRequest);

        assertNotNull(userDto);
        assertEquals(savedUser.getId(), userDto.id());
        assertEquals(savedUser.getFullName(), userDto.fullName());
        assertEquals(savedUser.getUsername(), userDto.username());
        assertEquals(savedUser.isActive(), userDto.isActive());

        verify(roleRepository, times(1)).findByRoleType(RoleType.MEMBER);
    }

    @Test
    public void createUserShouldThrowException_WhenRoleIsNotFound() {
        when(roleRepository.findByRoleType(RoleType.MEMBER)).thenReturn(Optional.empty());

        RoleNotFoundException thrown = assertThrows(RoleNotFoundException.class, () -> {
            authService.createUser(userRegisterRequest);
        });

        assertEquals("Role not found: MEMBER", thrown.getMessage());
    }
    @Test
    void getRolesFromRoleTypes_ShouldThrowException_WhenRoleNotFound() {
        Set<RoleType> roleTypes = Set.of(RoleType.PROJECT_MANAGER);

        when(roleRepository.findByRoleType(RoleType.PROJECT_MANAGER)).thenReturn(Optional.empty());

        RoleNotFoundException exception = assertThrows(RoleNotFoundException.class, () -> {
            authService.getRolesFromRoleTypes(roleTypes);
        });

        assertEquals("Role not found: PROJECT_MANAGER", exception.getMessage());
    }
}
