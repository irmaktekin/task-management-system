package com.irmaktekin.task.management.system.service.impl;

import com.irmaktekin.task.management.system.common.exception.ProjectNotFoundException;
import com.irmaktekin.task.management.system.common.exception.UserNotFoundException;
import com.irmaktekin.task.management.system.dto.request.ProjectCreateRequest;
import com.irmaktekin.task.management.system.dto.response.ProjectDto;
import com.irmaktekin.task.management.system.entity.Project;
import com.irmaktekin.task.management.system.entity.User;
import com.irmaktekin.task.management.system.repository.ProjectRepository;
import com.irmaktekin.task.management.system.repository.UserRepository;
import com.irmaktekin.task.management.system.service.ProjectService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
public class ProjectServiceImpl implements ProjectService {
    private final ProjectRepository projectRepository;

    public ProjectServiceImpl(ProjectRepository projectRepository) {
        this.projectRepository = projectRepository;
    }

    @Override
    public Project createProject(ProjectCreateRequest projectCreateRequest) {
        Project createdProject = Project.builder().responsibleDepartmentName(projectCreateRequest.responsibleDepartmentName())
                .description(projectCreateRequest.description())
                .status(projectCreateRequest.projectStatus()).build();
        return projectRepository.save(createdProject);
    }

    @Override
    public Project updateProject(Project project) throws ProjectNotFoundException {
        Project existingProject = projectRepository.findById(project.getId())
                .orElseThrow(()->new UserNotFoundException("Project not found with id: "+project.getId()));
        existingProject.setDescription(project.getDescription());
        existingProject.setTitle(project.getTitle());
        existingProject.setStatus(project.getStatus());
        existingProject.setResponsibleDepartmentName(project.getResponsibleDepartmentName());
        return projectRepository.save(existingProject);
    }

    @Override
    public Page<ProjectDto> getProjects(Pageable pageable) {
        return Optional.ofNullable(projectRepository.getProjects(pageable))
                .orElse(Page.empty())
                .map(project -> new ProjectDto(
                        project.getId(),
                        project.getStatus(),
                        project.getResponsibleDepartmentName(),
                        project.getDescription(),
                        project.getTitle(),
                        project.getMembers().stream().map(User::getId).toList()));
    }

    @Override
    public Project findProjectById(UUID id) throws ProjectNotFoundException {
        return projectRepository.findById(id)
                .orElseThrow(()->new ProjectNotFoundException("Project not found with id: "+ id));
    }
}
