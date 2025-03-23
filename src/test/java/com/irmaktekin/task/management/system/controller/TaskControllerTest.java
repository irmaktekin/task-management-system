package com.irmaktekin.task.management.system.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.irmaktekin.task.management.system.dto.request.TaskCreateRequest;
import com.irmaktekin.task.management.system.dto.request.TaskDetailsUpdateRequest;
import com.irmaktekin.task.management.system.dto.request.TaskStatusUpdateRequest;
import com.irmaktekin.task.management.system.dto.request.UpdateTaskPriorityRequest;
import com.irmaktekin.task.management.system.dto.response.CommentDto;
import com.irmaktekin.task.management.system.dto.response.TaskDto;
import com.irmaktekin.task.management.system.dto.response.UserDto;
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
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
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
    private UUID taskId,taskId2,projectId;
    private UUID commentId;
    private TaskDto activeTaskDto;
    private TaskDto deletedTaskDto;
    private CommentDto commentDto;
    private UserDto activeUserDto;
    private Pageable pageable;
    private Page<TaskDto> taskPage;


    @BeforeEach
    void setUp(){
        userId = UUID.randomUUID();
        taskId = UUID.randomUUID();
        taskId2 = UUID.randomUUID();
        projectId = UUID.randomUUID();
        commentId= UUID.randomUUID();

        activeUserDto = new UserDto(userId,"Irmak Tekin","irmaktekin",true);
        commentDto = new CommentDto(commentId,"Content TEST",userId);

        activeTaskDto = new TaskDto(taskId,"Task Description",TaskPriority.HIGH,TaskState.IN_DEVELOPMENT,activeUserDto,"Irmak",List.of(commentDto),false,"Title Not Deleted","",projectId);
        deletedTaskDto = new TaskDto(taskId2,"Task Description",TaskPriority.HIGH,TaskState.IN_DEVELOPMENT,activeUserDto,"Irmak",List.of(commentDto),true,"Title Deleted","",projectId);
        mockMvc= MockMvcBuilders.standaloneSetup(taskController).build();

        pageable = PageRequest.of(0, 20);
        List<TaskDto> taskList = List.of(activeTaskDto);
        taskPage = new PageImpl<>(taskList, pageable, taskList.size());
    }


    @Test
    void createTask_ShouldReturnCreatedTask() throws Exception{
        TaskCreateRequest taskCreateRequest = new TaskCreateRequest("Desc1",TaskState.IN_DEVELOPMENT,user,"AC-1","Reason",null,"Title",projectId);

        when(taskService.createTask(any(TaskCreateRequest.class))).thenReturn(activeTaskDto);

        mockMvc.perform(post("/api/v1/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(taskCreateRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(activeTaskDto.id().toString()));

        verify(taskService,times(1)).createTask(any(TaskCreateRequest.class));
    }


    @Test
    void assignPriority_ShouldReturnUpdatedTask() throws Exception {
        UpdateTaskPriorityRequest priorityRequest = new UpdateTaskPriorityRequest(TaskPriority.HIGH);

        when(taskService.assignPriority(eq(taskId),eq(TaskPriority.HIGH))).thenReturn(activeTaskDto);

        mockMvc.perform(MockMvcRequestBuilders.put("/api/v1/tasks/{taskId}/priority", taskId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(priorityRequest)))
                .andExpect(status().isOk());

        verify(taskService, times(1)).assignPriority(eq(taskId), eq(TaskPriority.HIGH));
    }

    @Test
    @WithMockUser(roles = "PROJECT_MANAGER")
    void assignUserToTask_ShouldReturnOk_WhenTaskIsAssigned() throws Exception {
        when(taskService.assignTaskToUser(taskId,userId)).thenReturn(activeTaskDto);

        mockMvc.perform(put("/api/v1/tasks/{taskId}/assignee/{userId}",taskId,userId))
                .andExpect(status().isOk());
        verify(taskService,times(1)).assignTaskToUser(taskId,userId);
    }

    @Test
    @WithMockUser(roles = "PROJECT_MANAGER")
    void getTaskProgress_ShouldReturnOk_WhenUserHasProjectManagerRole() throws Exception{
        when(taskService.getTaskProgress(taskId)).thenReturn(activeTaskDto);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/tasks/{taskId}/status",taskId))
                .andExpect(status().isOk());
    }

    @Test
    void addAttachmentToTask_ShouldReturnOk() throws Exception{
        UUID taskId = UUID.randomUUID();
        MockMultipartFile file = new MockMultipartFile("file","test.txt","text/plain","Test content".getBytes());
        when(taskService.addAttachmentToTask(eq(taskId),any(MultipartFile.class))).thenReturn(activeTaskDto);

        mockMvc.perform(MockMvcRequestBuilders.multipart("/api/v1/tasks/{taskI}/attachments",taskId)
                .file(file)
                .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(activeTaskDto.id().toString()));

        verify(taskService,times(1)).addAttachmentToTask(eq(taskId),any(MultipartFile.class));
    }

    @Test
    void updatedTaskState_ShouldReturnUpdatedTask() throws Exception{
        UUID taskId = UUID.randomUUID();
        TaskStatusUpdateRequest taskStatusUpdateRequest = new TaskStatusUpdateRequest(TaskState.IN_DEVELOPMENT,"Reason");
        UserDto userDto = new UserDto(userId,"Irmak Tekin","irmaktekin",true);

            TaskDto updatedTask = new TaskDto(taskId,"Description 1",TaskPriority.HIGH,TaskState.IN_DEVELOPMENT,userDto,"AC1",List.of(commentDto),false,"Title","Reason",projectId);

            when(taskService.updateTaskState(eq(taskId),eq(taskStatusUpdateRequest))).thenReturn(updatedTask);

            mockMvc.perform(MockMvcRequestBuilders.put("/api/v1/tasks/{taskId}/status",taskId)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("{\"state\":\"IN_DEVELOPMENT\", \"reason\":\"Reason\"}"))
                    .andExpect(MockMvcResultMatchers.status().isOk())
                    .andExpect(MockMvcResultMatchers.jsonPath("$.state").value("IN_DEVELOPMENT"))
                    .andExpect(MockMvcResultMatchers.jsonPath("$.reason").value("Reason"))
                    .andDo(MockMvcResultHandlers.print());

    }

    @Test
    public void softDeleteTask_ShouldReturnOk() throws Exception {
        UUID taskId = UUID.randomUUID();

        when(taskService.softDeleteTask(taskId)).thenReturn(null);

        mockMvc.perform(put("/api/v1/tasks/soft-delete/{taskId}", taskId))
                .andExpect(status().isOk());

        verify(taskService, times(1)).softDeleteTask(taskId);
    }

    @Test
    public void updateTaskDetails_ShouldReturnOk() throws Exception {
        UUID taskId = UUID.randomUUID();
        TaskDetailsUpdateRequest request = new TaskDetailsUpdateRequest("Updated Description", "New Title");

        UserDto userDto = new UserDto(userId,"Irmak Tekin","irmaktekin",true);
        TaskDto taskDto = new TaskDto(taskId,"Updated Description",TaskPriority.HIGH,TaskState.IN_DEVELOPMENT,userDto,"AC1",List.of(commentDto),false,"New Title","Reason",projectId);

        when(taskService.updateTaskDetails(eq(taskId), eq(request))).thenReturn(taskDto);

        mockMvc.perform(MockMvcRequestBuilders.patch("/api/v1/tasks/{taskId}/details", taskId)
                        .contentType("application/json")
                        .content("{\"title\":\"New Title\", \"description\":\"Updated Description\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("New Title"))
                .andExpect(jsonPath("$.description").value("Updated Description"));

        verify(taskService, times(1)).updateTaskDetails(eq(taskId), eq(request));
    }

    @Test
    void getTasks_ShouldReturnPaginatedTasks() {
        when(taskService.getTasks(pageable)).thenReturn(taskPage);

        Page<TaskDto> result = taskController.getTasks(pageable);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals(1, result.getContent().size());
        assertEquals("Title Not Deleted", result.getContent().get(0).title());

        verify(taskService, times(1)).getTasks(pageable);
    }
}
