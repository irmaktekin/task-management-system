package com.irmaktekin.task.management.system.service;

import com.irmaktekin.task.management.system.common.exception.ProjectNotFoundException;
import com.irmaktekin.task.management.system.common.exception.UserAlreadyAssignedToProjectException;
import com.irmaktekin.task.management.system.common.exception.UserNotFoundException;
import com.irmaktekin.task.management.system.common.mapper.ProjectMapper;
import com.irmaktekin.task.management.system.common.mapper.TaskMapper;
import com.irmaktekin.task.management.system.dto.request.ProjectRequest;
import com.irmaktekin.task.management.system.dto.request.TaskCreateRequest;
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
import com.irmaktekin.task.management.system.repository.TaskRepository;
import com.irmaktekin.task.management.system.repository.UserRepository;
import com.irmaktekin.task.management.system.service.impl.ProjectServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ProjectServiceImplTest {
    private MockMvc mockMvc;

    @InjectMocks
    private ProjectServiceImpl projectService;

    @Mock
    private ProjectRepository projectRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private TaskRepository taskRepository;

    @Mock
    private ProjectMapper projectMapper;

    private User user;
    private UUID userId;

    private Project project;
    private UUID projectId;
    private UUID taskId;

    private ProjectDto projectDto;
    private UserDto userDto;
    private TaskDto taskDto;

    private Task task;
    private Comment comment;

    private final TaskMapper taskMapper = mock(TaskMapper.class);

    @BeforeEach
    public void setUp() {
        userId = UUID.randomUUID();
        taskId = UUID.randomUUID();
        user = User.builder().id(userId).fullName("Irmak Tekin")
                .username("irmakteki").password("testpassword123.")
                .active(true).build();

        projectId = UUID.randomUUID();
        project = Project.builder().id(projectId).status(ProjectStatus.IN_PROGRESS)
                .description("Test Description").departmentName("Deparment1")
                .title("Task Management Project").build();

        userDto = new UserDto(userId, "Irmak Tekin", "irmaktekin", true);

        projectDto = new ProjectDto(projectId, ProjectStatus.IN_PROGRESS, "IT", "Project Description", "Project Title", List.of(userDto));
        taskDto = new TaskDto(taskId, "Description 1", TaskPriority.HIGH, TaskState.IN_DEVELOPMENT, userDto, "AC1", null, false, "Title", null, projectId);

        task = Task.builder().id(taskId)
                .priority(TaskPriority.LOW).state(TaskState.IN_DEVELOPMENT)
                .acceptanceCriteria("AC-1").assignee(user)
                .deleted(false)
                .build();

        comment = Comment.builder()
                .id(UUID.randomUUID()).content("Content")
                .task(task).user(user).build();
    }

    @Test
    public void createProject_ShouldCreateProject_WhenRequestIsValid() {

        ProjectRequest request = new ProjectRequest(ProjectStatus.IN_PROGRESS, "IT", "Test", "Description", "Title", List.of(userId));
        when(projectRepository.existMemberInProject(request.memberIds(), ProjectStatus.IN_PROGRESS)).thenReturn(false);
        when(userRepository.findAllById(request.memberIds())).thenReturn(List.of(user));
        when(projectRepository.save(any(Project.class))).thenReturn(project);
        when(projectMapper.convertToDto(any(Project.class))).thenReturn(projectDto);

        ProjectDto result = projectService.createProject(request);

        assertEquals("IT", result.departmentName());
        verify(projectRepository, times(1)).save(any(Project.class));

    }

    @Test
    public void createProject_ShouldReturnException_WhenUserAlreadyAssigned() {
        ProjectRequest request = new ProjectRequest(ProjectStatus.IN_PROGRESS, "Department 1", "Test", "Description", "Title", List.of(userId));

        when(projectRepository.existMemberInProject(request.memberIds(), ProjectStatus.IN_PROGRESS)).thenReturn(true);
        assertThrows(UserAlreadyAssignedToProjectException.class, () -> projectService.createProject(request));
        verify(projectRepository, never()).save(any(Project.class));
    }

    @Test
    public void addTaskToProject_ShouldReturnTaskDto_WhenProjectExists() {
        TaskCreateRequest request = new TaskCreateRequest("Description Test", TaskState.IN_DEVELOPMENT, user, "AC1", null, List.of(comment), "Title", projectId);
        when(projectRepository.findById(projectId)).thenReturn(Optional.of(project));
        when(taskRepository.save(any(Task.class))).thenReturn(task);
        when(taskMapper.convertToDto(task)).thenReturn(taskDto);

        TaskDto result = projectService.addTaskToProject(projectId, request);

        assertEquals("Title", result.title());
        verify(taskRepository, times(1)).save(any(Task.class));
    }

    @Test
    public void updateProject_ShouldUpdateProject_WhenValidRequest() {

        when(projectRepository.findById(projectId)).thenReturn(Optional.of(project));
        when(projectRepository.save(project)).thenReturn(project);
        when(projectMapper.convertToDto(project)).thenReturn(projectDto);

        ProjectRequest request = new ProjectRequest(ProjectStatus.IN_PROGRESS, "IT", "Test", "Description", "Title", List.of(userId));

        ProjectDto result = projectService.updateProject(projectId, request);

        assertNotNull(result);
        assertEquals("Project Title", result.title());
        assertEquals("Project Description", result.description());
        verify(projectRepository, times(1)).save(project);
    }

    @Test
    public void updateProject_ShouldThrowProjectNotFoundException_WhenProjectNotFound() {
        ProjectRequest request = new ProjectRequest(ProjectStatus.IN_PROGRESS, "IT", "Test", "Description", "Title", List.of(userId));

        when(projectRepository.findById(projectId)).thenReturn(Optional.empty());

        ProjectNotFoundException exception = assertThrows(ProjectNotFoundException.class,
                () -> projectService.updateProject(projectId, request));
        assertEquals("Project not found with id: " + projectId, exception.getMessage());
    }

    @Test
    public void updateProject_ShouldNotUpdateProject_WhenTitleIsSame() {
        new ProjectRequest(ProjectStatus.IN_PROGRESS, "IT", "Test", "Description", "Title", List.of(userId));
        ProjectRequest request;

        project.setTitle("Existing Title");
        project.setDescription("Existing Description");
        request = new ProjectRequest(ProjectStatus.IN_PROGRESS, "IT", "Test", "Description", "Title", List.of(userId));

        when(projectRepository.findById(projectId)).thenReturn(Optional.of(project));
        when(projectRepository.save(project)).thenReturn(project);
        when(projectMapper.convertToDto(project)).thenReturn(projectDto);

        ProjectDto result = projectService.updateProject(projectId, request);

        assertNotNull(result);
        assertEquals("Project Title", result.title());
        assertEquals("Project Description", result.description());
        verify(projectRepository, times(1)).save(project);
    }

    @Test
    public void updateProject_ShouldNotUpdateProject_WhenDescriptionIsSame() {

        project.setTitle("Existing Title");
        project.setDescription("Existing Description");
        ProjectRequest request = new ProjectRequest(ProjectStatus.IN_PROGRESS, "IT", "Test", "Description", "Title", List.of(userId));

        when(projectRepository.findById(projectId)).thenReturn(Optional.of(project));
        when(projectRepository.save(project)).thenReturn(project);
        when(projectMapper.convertToDto(project)).thenReturn(projectDto);

        ProjectDto result = projectService.updateProject(projectId, request);

        assertNotNull(result);
        assertEquals("Project Title", result.title());
        assertEquals("Project Description", result.description());
        verify(projectRepository, times(1)).save(project);
    }

    @Test
    void softDeleteShouldSetDeletedFalse_WhenProjectExist() {
        when(projectRepository.findById(projectId)).thenReturn(Optional.of(project));

        when(projectRepository.save(any(Project.class))).thenReturn(project);

        Boolean result = projectService.softDeleteProject(projectId);

        verify(projectRepository, times(1)).save(project);
        assertTrue(result);
        assertTrue(project.isDeleted());
    }

    @Test
    void softDeleteShouldThrowProjetNotFound_WhenProjectDoesNotExist() {
        when(projectRepository.findById(projectId)).thenReturn(Optional.empty());

        Exception exception = assertThrows(ProjectNotFoundException.class, () -> {
            projectService.softDeleteProject(projectId);
        });

        assertEquals("Project not found", exception.getMessage());

    }

    @Test
    void createProjectShouldThrowException_WhenNoUserFound() {
        ProjectRequest request = new ProjectRequest(ProjectStatus.IN_PROGRESS, "IT", "Test", "Description", "Title", List.of(userId));

        when(userRepository.findAllById(anyList())).thenReturn(List.of());

        Exception exception = assertThrows(UserNotFoundException.class, () -> {
            projectService.createProject(request);
        });

        assertEquals("One or more users not found!", exception.getMessage());
    }



}
