package com.irmaktekin.task.management.system.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.irmaktekin.task.management.system.common.exception.UserNotFoundException;
import com.irmaktekin.task.management.system.dto.request.UserCreateRequest;
import com.irmaktekin.task.management.system.dto.response.UserDto;
import com.irmaktekin.task.management.system.entity.Task;
import com.irmaktekin.task.management.system.entity.User;
import com.irmaktekin.task.management.system.enums.TaskPriority;
import com.irmaktekin.task.management.system.enums.TaskState;
import com.irmaktekin.task.management.system.repository.UserRepository;
import com.irmaktekin.task.management.system.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class UserControllerTest {

    private MockMvc mockMvc;

    @Mock
    private UserService userService;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserController userController;

    private User user;
    private UUID userId;
    private Task task;
    private UUID taskId;
    private UserDto userDto;

    @BeforeEach
    void setUp(){
        userId = UUID.randomUUID();
        taskId = UUID.randomUUID();

        user = User.builder().id(userId).fullName("Irmak Tekin").email("irmak@test.com")
                .password("1234").isActive(true).build();
        task = Task.builder().taskPriority(TaskPriority.HIGH).taskState(TaskState.IN_DEVELOPMENT)
                .assignee(user).build();

        userDto = new UserDto(userId,"Irmak Tekin","irmak@test.com","1234",true);
        mockMvc= MockMvcBuilders.standaloneSetup(userController).build();
    }

    @Test
    void getUsers_ShouldReturnPageOfUsers_whenUsersExist(){
        var page = 0;
        var size = 20;
        Page <UserDto> pageUser = new PageImpl<>(List.of(userDto));
        when(userService.getUsers(page,size)).thenReturn(pageUser);

        Page<UserDto> result = userController.getUsers(page,size);

        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).fullName()).isEqualTo("Irmak Tekin");
        verify(userService,times(1)).getUsers(0,20);
    }

    @Test
    void getUserById_ShouldReturnUser_whenUserExist() throws Exception{
        when(userService.findUserById(userId)).thenReturn(user);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/users/{id}",userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(userId.toString()))
                .andExpect(jsonPath("$.fullName").value("Irmak Tekin"))
                .andExpect(jsonPath("$.email").value("irmak@test.com"))
                .andExpect(jsonPath("$.password").value("1234"))
                .andExpect(jsonPath("$.isActive").value(true));

        verify(userService,times(1)).findUserById(userId);
    }

    @Test
    void createUser_ShouldReturnCreatedUser() throws Exception{
        UserCreateRequest userCreateRequest = new UserCreateRequest("Irmak Tekin","irmak@test.com","12345",true);

        when(userService.createUser(userCreateRequest)).thenReturn(user);

        mockMvc.perform(post("/api/v1/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(userCreateRequest)))
                .andExpect(status().isCreated());

        verify(userService,times(1)).createUser(userCreateRequest);
    }

    @Test
    void updateUser_ShouldReturnUpdatedUser() throws Exception{
        when(userService.updateUser(any(User.class))).thenReturn(user);

       mockMvc.perform(put("/api/v1/users/{id}",userId)
               .contentType(MediaType.APPLICATION_JSON)
               .content(new ObjectMapper().writeValueAsString(user)))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.fullName").value("Irmak Tekin"))
               .andExpect(jsonPath("$.email").value("irmak@test.com"))
               .andExpect(jsonPath("$.password").value("1234"))
               .andExpect(jsonPath("$.isActive").value(true));

        verify(userService,times(1)).updateUser(user);
    }

    @Test
    void updateUser_ShouldThrowException_WhenUserDoesNotExist() throws Exception {
        when(userService.updateUser(any(User.class))).thenThrow(new UserNotFoundException("User not found with id: "+ userId));

        assertThrows(UserNotFoundException.class,()->userService.updateUser(user));
    }
}
