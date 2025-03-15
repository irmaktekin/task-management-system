package com.irmaktekin.task.management.system.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.irmaktekin.task.management.system.dto.request.ProjectCreateRequest;
import com.irmaktekin.task.management.system.dto.response.ProjectDto;
import com.irmaktekin.task.management.system.entity.Project;
import com.irmaktekin.task.management.system.entity.User;
import com.irmaktekin.task.management.system.enums.ProjectStatus;
import com.irmaktekin.task.management.system.service.ProjectService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;
import java.util.UUID;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class ProjectControllerTest {

    private MockMvc mockMvc;

    @Mock
    private ProjectService projectService;

    @InjectMocks
    private ProjectController projectController;

    private User user;
    private UUID userId;

    private Project project;
    private UUID projectId;

    private ProjectDto projectDto;

    private List <UUID> memberIds;

    @BeforeEach
    void setUp(){
        userId = UUID.randomUUID();
        projectId = UUID.randomUUID();

        user = User.builder().id(userId).fullName("Irmak Tekin").email("irmak@test.com")
                .password("1234").isActive(true).build();
        project = Project.builder().responsibleDepartmentName("IT Department").status(ProjectStatus.CANCELLED)
                .members(List.of(user)).build();

        memberIds = project.getMembers().stream()
                .map(User::getId)
                .toList();

        projectDto = new ProjectDto(projectId,project.getStatus(),project.getResponsibleDepartmentName(),project.getDescription(),project.getTitle(),memberIds);
        mockMvc= MockMvcBuilders.standaloneSetup(projectController).build();
    }

    @Test
    void getProjectById_ShouldReturnProject_WhenProjectExist() throws Exception{
        when(projectService.findProjectById(projectId)).thenReturn(project);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/projects/{id}",projectId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("CANCELLED"))
                .andExpect(jsonPath("$.responsibleDepartmentName").value("IT Department"));

        verify(projectService,times(1)).findProjectById(projectId);
    }

    @Test
    void updateProject_ShouldReturnUpdatedProject() throws Exception{
        when(projectService.updateProject(any(Project.class))).thenReturn(project);

        mockMvc.perform(put("/api/v1/projects/{id}",projectId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(project)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("CANCELLED"))
                .andExpect(jsonPath("$.responsibleDepartmentName").value("IT Department"));

        verify(projectService,times(1)).updateProject(project);
    }

    @Test
    void createProjectShouldReturnCreatedProject() throws Exception{
        ProjectCreateRequest projectCreateRequest = new ProjectCreateRequest(ProjectStatus.IN_PROGRESS,"IT Department","Description Test","Title Test",memberIds);

        when(projectService.createProject(projectCreateRequest)).thenReturn(project);

        mockMvc.perform(post("/api/v1/projects")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(projectCreateRequest)))
                .andExpect(status().isCreated());

        verify(projectService,times(1)).createProject(projectCreateRequest);
    }
}
