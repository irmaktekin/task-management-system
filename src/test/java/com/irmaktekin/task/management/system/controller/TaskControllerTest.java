package com.irmaktekin.task.management.system.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.irmaktekin.task.management.system.common.exception.TaskNotFoundException;
import com.irmaktekin.task.management.system.dto.request.TaskCreateRequest;
import com.irmaktekin.task.management.system.dto.response.TaskDto;
import com.irmaktekin.task.management.system.entity.Task;
import com.irmaktekin.task.management.system.entity.User;
import com.irmaktekin.task.management.system.enums.TaskPriority;
import com.irmaktekin.task.management.system.enums.TaskState;
import com.irmaktekin.task.management.system.service.TaskService;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class TaskControllerTest {

    private MockMvc mockMvc;

    @Mock
    private TaskService taskService;

    @InjectMocks
    private TaskController taskController;

    private User user;
    private UUID userId;
    private Task task;
    private UUID taskId;
    private TaskDto taskDto;

    @BeforeEach
    void setUp(){
        userId = UUID.randomUUID();
        taskId = UUID.randomUUID();

        user = User.builder().id(userId).fullName("Irmak Tekin").email("irmak@test.com")
                .password("1234").isActive(true).build();
        task = Task.builder().taskPriority(TaskPriority.HIGH).taskState(TaskState.IN_DEVELOPMENT)
                .assignee(user).build();

        taskDto = new TaskDto(taskId,"Task Description",TaskPriority.HIGH,TaskState.IN_DEVELOPMENT,userId,"Irmak","AC-1");
        mockMvc= MockMvcBuilders.standaloneSetup(taskController).build();
    }

    @Test
    void getTasks_ShouldReturnPageOfTasks_WhenTasksExist(){
        var page = 0;
        var size = 20;
        Page<TaskDto> pageTask = new PageImpl<>(List.of(taskDto));
        when(taskService.getTasks(page,size)).thenReturn(pageTask);

        Page<TaskDto> result = taskController.getTasks(page,size);

        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).description()).isEqualTo("Task Description");
        verify(taskService,times(1)).getTasks(0,20);
    }

    @Test
    void getTaskById_ShouldReturnTask_whenTaskExist() throws Exception{
        when(taskService.findTaskById(taskId)).thenReturn(task);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/tasks/{id}",taskId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.taskPriority").value("HIGH"))
                .andExpect(jsonPath("$.taskState").value("IN_DEVELOPMENT"));

        verify(taskService,times(1)).findTaskById(taskId);
    }

    @Test
    void createTask_ShouldReturnCreatedTask() throws Exception{
        TaskCreateRequest taskCreateRequest = new TaskCreateRequest("Task Description",TaskPriority.HIGH,TaskState.IN_DEVELOPMENT.IN_ANALYSIS,userId,"AC-1");

        when(taskService.createTask(taskCreateRequest)).thenReturn(task);

        mockMvc.perform(post("/api/v1/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(taskCreateRequest)))
                .andExpect(status().isCreated());

        verify(taskService,times(1)).createTask(taskCreateRequest);
    }

    @Test
    void updateTask_ShouldReturnUpdatedTask() throws Exception{
        when(taskService.updateTask(any(Task.class))).thenReturn(task);

        mockMvc.perform(put("/api/v1/tasks/{id}",taskId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(task)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.taskPriority").value("HIGH"))
                .andExpect(jsonPath("$.taskState").value("IN_DEVELOPMENT"));

        verify(taskService,times(1)).updateTask(task);
    }

    @Test
    void updateTask_ShouldThrowException_WhenTaskDoesNotExist() throws Exception {
        when(taskService.updateTask(any(Task.class))).thenThrow(new TaskNotFoundException("Task not found with id: "+ taskId));

        assertThrows(TaskNotFoundException.class,()->taskService.updateTask(task));
    }
}
