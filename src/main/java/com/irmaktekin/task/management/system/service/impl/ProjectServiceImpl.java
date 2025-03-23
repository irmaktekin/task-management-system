package com.irmaktekin.task.management.system.service.impl;

import com.irmaktekin.task.management.system.common.exception.ProjectNotFoundException;
import com.irmaktekin.task.management.system.common.exception.UserAlreadyAssignedToProjectException;
import com.irmaktekin.task.management.system.common.exception.UserNotFoundException;
import com.irmaktekin.task.management.system.common.mapper.ProjectMapper;
import com.irmaktekin.task.management.system.common.mapper.TaskMapper;
import com.irmaktekin.task.management.system.dto.request.ProjectRequest;
import com.irmaktekin.task.management.system.dto.request.TaskCreateRequest;
import com.irmaktekin.task.management.system.dto.response.ProjectDto;
import com.irmaktekin.task.management.system.dto.response.TaskDto;
import com.irmaktekin.task.management.system.entity.Project;
import com.irmaktekin.task.management.system.entity.Task;
import com.irmaktekin.task.management.system.entity.User;
import com.irmaktekin.task.management.system.enums.ProjectStatus;
import com.irmaktekin.task.management.system.repository.ProjectRepository;
import com.irmaktekin.task.management.system.repository.TaskRepository;
import com.irmaktekin.task.management.system.repository.UserRepository;
import com.irmaktekin.task.management.system.service.ProjectService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProjectServiceImpl implements ProjectService {
    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;
    private final ProjectMapper projectMapper;
    private final TaskRepository taskRepository;
    private final TaskMapper taskMapper;

    @Override
    public ProjectDto createProject(ProjectRequest projectRequest) {
        checkUserAlreadyAssigned(projectRequest);

        Project createdProject = Project.builder().departmentName(projectRequest.departmentName())
                .description(projectRequest.description())
                .title(projectRequest.title())
                .status(projectRequest.projectStatus()).build();

        List<User> users = userRepository.findAllById(projectRequest.memberIds());
        if (users.size() != projectRequest.memberIds().size()) {
            throw new UserNotFoundException("One or more users not found!");
        }
        createdProject.setMembers(users);
        Project project =  projectRepository.save(createdProject);

        return projectMapper.convertToDto(project);
    }

    @Override
    public TaskDto addTaskToProject(UUID projectId, TaskCreateRequest taskCreateRequest) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(()->new ProjectNotFoundException("Project not found"));
        Task task = new Task();
        task.setDescription(taskCreateRequest.description());
        task.setAssignee(taskCreateRequest.assignee());
        task.setComments(taskCreateRequest.comments());
        task.setAcceptanceCriteria(taskCreateRequest.acceptanceCriteria());
        task.setTitle(taskCreateRequest.title());
        task.setState(taskCreateRequest.state());
        task.setProject(project);
        Task savedTask = taskRepository.save(task);
        return taskMapper.convertToDto(savedTask);
    }

    private void checkUserAlreadyAssigned(ProjectRequest projectRequest){
        boolean userAlreadyAssigned = projectRepository.existMemberInProject(projectRequest.memberIds(), ProjectStatus.IN_PROGRESS);
        if(userAlreadyAssigned){
            throw new UserAlreadyAssignedToProjectException("User already assigned to an active project");
        }
    }

   @Override
    public ProjectDto updateProject(UUID projectId,ProjectRequest projectRequest) throws ProjectNotFoundException {
        Project existingProject = projectRepository.findById(projectId)
                .orElseThrow(()->new ProjectNotFoundException("Project not found with id: "+projectId));

        existingProject.setTitle(projectRequest.title());
        existingProject.setDescription(projectRequest.description());
        Project updatedProject = projectRepository.save(existingProject);

       return projectMapper.convertToDto(updatedProject);

    }

    @Override
    public Boolean softDeleteProject(UUID projectId) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(()->new ProjectNotFoundException("Project not found"));
        project.setDeleted(true);
        projectRepository.save(project);

        return true;
    }
}
