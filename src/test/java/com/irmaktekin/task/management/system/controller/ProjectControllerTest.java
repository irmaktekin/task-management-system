package com.irmaktekin.task.management.system.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.irmaktekin.task.management.system.common.mapper.TaskMapper;
import com.irmaktekin.task.management.system.dto.request.ProjectRequest;
import com.irmaktekin.task.management.system.dto.request.TaskCreateRequest;
import com.irmaktekin.task.management.system.dto.response.CommentDto;
import com.irmaktekin.task.management.system.dto.response.ProjectDto;
import com.irmaktekin.task.management.system.dto.response.TaskDto;
import com.irmaktekin.task.management.system.dto.response.UserDto;
import com.irmaktekin.task.management.system.entity.Comment;
import com.irmaktekin.task.management.system.entity.Project;
import com.irmaktekin.task.management.system.entity.Task;
import com.irmaktekin.task.management.system.entity.User;
import com.irmaktekin.task.management.system.enums.ProjectStatus;
import com.irmaktekin.task.management.system.enums.TaskPriority;
import com.irmaktekin.task.management.system.enums.TaskState;
import com.irmaktekin.task.management.system.repository.ProjectRepository;
import com.irmaktekin.task.management.system.service.ProjectService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class ProjectControllerTest {

    private MockMvc mockMvc;

    @Mock
    private ProjectService projectService;

    @Mock
    private ProjectRepository projectRepository;

    @Mock
    private TaskMapper taskMapper;

    @InjectMocks
    private ProjectController projectController;

    private User user;
    private UUID userId;

    private Project project;
    private UUID projectId;
    private UUID commentId;
    private UUID taskId;

    private ProjectDto projectDto;
    private Comment comment;

    private List<UUID> memberIds;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
        projectId = UUID.randomUUID();
        taskId = UUID.randomUUID();

        UserDto userDto = new UserDto(userId, "Irmak Tekin", "irmaktekin", true);

        Task task = Task.builder().id(taskId)
                .priority(TaskPriority.LOW).state(TaskState.IN_DEVELOPMENT)
                .acceptanceCriteria("AC-1").assignee(user)
                .deleted(false)
                .build();

        comment = Comment.builder()
                .id(UUID.randomUUID()).content("Content")
                .task(task).user(user).build();

        projectDto = new ProjectDto(projectId, ProjectStatus.IN_PROGRESS, "IT DEPARMENT", "IT Department Project", "Test Project", List.of(userDto));

        mockMvc = MockMvcBuilders.standaloneSetup(projectController).build();
    }

    @Test
    void createProjectShouldReturnCreatedProject() throws Exception {
        ProjectRequest projectRequest = new ProjectRequest(ProjectStatus.IN_PROGRESS, "IT Department", TaskState.IN_DEVELOPMENT.toString(), "Description Test", "Title Test", List.of(UUID.randomUUID()));

        when(projectService.createProject(projectRequest)).thenReturn(projectDto);

        mockMvc.perform(post("/api/v1/projects")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(projectRequest)))
                .andExpect(status().isCreated());

        verify(projectService, times(1)).createProject(projectRequest);
    }

    @Test
    @WithMockUser(roles = "PROJECT_MANAGER")
    void shouldAddTaskToProject_whenUserHasProjectManagerRole() {
        UUID projectId = UUID.randomUUID();
        TaskCreateRequest taskCreateRequest = new TaskCreateRequest(
                "Task description",
                TaskState.BACKLOG,
                user,
                "Acceptance Criteria",
                "Reason",
                new ArrayList<>(),
                "Task Title",
                projectId
        );
        UserDto userDto = new UserDto(userId, "Irmak Tekin", "irmaktekin", true);
        CommentDto commentDto = new CommentDto(commentId, "Content TEST", userId);


        TaskDto taskDto = new TaskDto(UUID.randomUUID(), "Description 1", TaskPriority.HIGH, TaskState.IN_DEVELOPMENT, userDto, "AC1", List.of(commentDto), false, "Title", null, projectId);
        when(projectService.addTaskToProject(eq(projectId), eq(taskCreateRequest))).thenReturn(taskDto);

        ResponseEntity<TaskDto> responseEntity = projectController.addTaskToProject(projectId, taskCreateRequest);

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(taskDto, responseEntity.getBody());
    }
}
