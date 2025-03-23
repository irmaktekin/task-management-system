package com.irmaktekin.task.management.system.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.irmaktekin.task.management.system.dto.request.LoginRequest;
import com.irmaktekin.task.management.system.dto.request.UserRegisterRequest;
import com.irmaktekin.task.management.system.dto.response.LoginResponse;
import com.irmaktekin.task.management.system.dto.response.UserDto;
import com.irmaktekin.task.management.system.entity.Task;
import com.irmaktekin.task.management.system.entity.User;
import com.irmaktekin.task.management.system.enums.RoleType;
import com.irmaktekin.task.management.system.enums.TaskPriority;
import com.irmaktekin.task.management.system.enums.TaskState;
import com.irmaktekin.task.management.system.security.JwtService;
import com.irmaktekin.task.management.system.service.AuthService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Set;
import java.util.UUID;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
public class AuthControllerTest {
    private MockMvc mockMvc;

    @Mock
    private AuthService authService;

    @Mock
    private JwtService jwtService;
    @Mock
    private UserDetailsService userDetailsService;

    @Mock
    AuthenticationManager authenticationManager;

    @InjectMocks
    private AuthController authController;

    private User user;
    private UUID userId;
    private Task task;
    private UUID taskId;
    private UserDto userDto;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
        taskId = UUID.randomUUID();

        user = User.builder().id(userId).fullName("Irmak Tekin")
                .password("1234").active(true).build();

        task = Task.builder().priority(TaskPriority.HIGH).state(TaskState.IN_DEVELOPMENT)
                .assignee(user).build();

        userDto = new UserDto(userId, "Irmak Tekin", "irmak@test.com", true);

        mockMvc = MockMvcBuilders.standaloneSetup(authController).build();
    }

    @Test
    void authenticateAndGetToken_ShouldReturnToken_WhenCredentialsAreValid() throws Exception {
        String username = "irmaktekin";
        String password = "12345";
        LoginRequest loginRequest = new LoginRequest(username, password);

        Authentication authentication = mock(Authentication.class);
        when(authentication.isAuthenticated()).thenReturn(true);

        UserDetails userDetails = mock(UserDetails.class);
        when(userDetailsService.loadUserByUsername(username)).thenReturn(userDetails);

        String token = "jwttokenexample";
        LoginResponse loginResponse = new LoginResponse(username, token);


        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(authentication);
        when(jwtService.generateToken(any())).thenReturn(token);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/auth")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(loginRequest)))
                .andExpect(status().isOk());

    }

    @Test
    void createUser_ShouldReturnCreatedUser_WhenRequestIsValid() throws Exception {
        String username = "irmaktekin";
        String password = "12345678";
        Set<RoleType> roles = Set.of(RoleType.TEAM_LEADER);

        UserRegisterRequest userRegisterRequest = new UserRegisterRequest("Irmak Tekin", username, password, true, roles, false);

        UserDto createdUser = new UserDto(UUID.randomUUID(), "Irmak Tekin", username, true);

        when(authService.createUser(any(UserRegisterRequest.class))).thenReturn(createdUser);

        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(userRegisterRequest)))
                .andExpect(status().isCreated());

        verify(authService, times(1)).createUser(any(UserRegisterRequest.class));
    }

    @Test
    void createUser_ShouldReturnBadRequest_WhenRequestIsInvalid() throws Exception {
        String username = "irmaktekin";
        String password = "";
        Set<RoleType> roles = Set.of(RoleType.TEAM_LEADER);

        UserRegisterRequest userRegisterRequest = new UserRegisterRequest("Irmak Tekin", username, password, true, roles, true);

        mockMvc.perform(post("/api/v1/auth")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(userRegisterRequest)))
                .andExpect(status().isBadRequest());
    }
}
