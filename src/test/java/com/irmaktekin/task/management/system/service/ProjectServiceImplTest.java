package com.irmaktekin.task.management.system.service;

import com.irmaktekin.task.management.system.common.exception.ProjectNotFoundException;
import com.irmaktekin.task.management.system.dto.request.ProjectCreateRequest;
import com.irmaktekin.task.management.system.entity.Project;
import com.irmaktekin.task.management.system.entity.User;
import com.irmaktekin.task.management.system.enums.ProjectStatus;
import com.irmaktekin.task.management.system.repository.ProjectRepository;
import com.irmaktekin.task.management.system.service.impl.ProjectServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
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

    private User user;
    private UUID userId;
    private Project project;
    private UUID projectId;


    @BeforeEach
    public void setUp(){
        userId = UUID.randomUUID();
        user = User.builder().id(userId).fullName("Irmak Tekin")
                .email("irmaktekin@gmail.com").password("testpassword123.")
                .isActive(true).build();

        projectId = UUID.randomUUID();
        project = Project.builder().id(projectId).status(ProjectStatus.IN_PROGRESS)
                .description("Test Description").responsibleDepartmentName("Deparment1")
                .title("Task Management Project").build();
    }

    @Test
    public void shouldCreateProject_WhenRequestIsValid(){
        var request = new ProjectCreateRequest(ProjectStatus.COMPLETED,"Deparment 1","Description Test","Title", List.of());
        UUID id3 = UUID.randomUUID();
        Project project = Project.builder().id(id3).description(request.description()).responsibleDepartmentName(request.responsibleDepartmentName()).title(request.title()).status(request.projectStatus()).build();

        when(projectRepository.save(any(Project.class))).thenReturn(project);
        projectService.createProject(request);

        verify(projectRepository,times(1)).save(any(Project.class));
    }

    @Test
    public void shouldUpdateProject_WhenProjectExists(){
        when(projectRepository.findById(projectId)).thenReturn(Optional.of(project));
        when(projectRepository.save(project)).thenReturn(project);

        Project updatedProject = projectService.updateProject(project);

        assertNotNull(updatedProject);
        assertEquals(project.getStatus(),updatedProject.getStatus());

        verify(projectRepository,times(1)).findById(projectId);
        verify(projectRepository,times(1)).save(project);
    }

    @Test
    public void shouldReturnAllProjects(){
        var page = 0;
        var size = 20;
        var pageable = PageRequest.of(page,size);

        projectService.getProjects(pageable);

        verify(projectRepository,times(1)).getProjects(PageRequest.of(page,size));
    }

    @Test
    public void shouldReturnProject_WhenProjectExist(){
        when(projectRepository.findById(projectId)).thenReturn(Optional.of(project));

        Project foundProject = projectService.findProjectById(projectId);

        assertNotNull(foundProject);
        assertEquals(project.getId(),foundProject.getId());

        verify(projectRepository,times(1)).findById(projectId);
    }

    @Test
    public void shouldThrowException_WhenProjectNotFound(){
        when(projectRepository.findById(projectId)).thenReturn(Optional.empty());

        assertThrows(ProjectNotFoundException.class,()->projectService.findProjectById(projectId));
    }
}
