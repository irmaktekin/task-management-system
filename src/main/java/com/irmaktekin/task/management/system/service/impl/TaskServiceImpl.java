package com.irmaktekin.task.management.system.service.impl;

import com.irmaktekin.task.management.system.common.exception.*;
import com.irmaktekin.task.management.system.common.mapper.TaskMapper;
import com.irmaktekin.task.management.system.dto.request.TaskCreateRequest;
import com.irmaktekin.task.management.system.dto.request.TaskDetailsUpdateRequest;
import com.irmaktekin.task.management.system.dto.request.TaskStatusUpdateRequest;
import com.irmaktekin.task.management.system.dto.request.TaskUpdateRequest;
import com.irmaktekin.task.management.system.dto.response.TaskDto;
import com.irmaktekin.task.management.system.entity.Comment;
import com.irmaktekin.task.management.system.entity.Task;
import com.irmaktekin.task.management.system.entity.User;
import com.irmaktekin.task.management.system.enums.TaskPriority;
import com.irmaktekin.task.management.system.enums.TaskState;
import com.irmaktekin.task.management.system.repository.CommentRepository;
import com.irmaktekin.task.management.system.repository.TaskRepository;
import com.irmaktekin.task.management.system.repository.UserRepository;
import com.irmaktekin.task.management.system.service.TaskService;
import jakarta.transaction.TransactionScoped;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TaskServiceImpl implements TaskService {

    private final TaskRepository taskRepository;
    private final TaskMapper taskMapper;
    private final UserRepository userRepository;
    private final CommentRepository commentRepository;

    private static final Map<TaskState, List<TaskState>> VALID_TRANSITIONS = Map.of(
            TaskState.BACKLOG, List.of(TaskState.IN_ANALYSIS),
            TaskState.IN_ANALYSIS, List.of(TaskState.BACKLOG, TaskState.IN_DEVELOPMENT,TaskState.BLOCKED),
            TaskState.IN_DEVELOPMENT, List.of(TaskState.IN_ANALYSIS, TaskState.COMPLETED,TaskState.BLOCKED),
            TaskState.BLOCKED,List.of(TaskState.IN_DEVELOPMENT,TaskState.IN_ANALYSIS)
    );

    @Override
    public TaskDto createTask(TaskCreateRequest taskCreateRequest) {
        assignDefaultPriorityForMemberRoles(taskCreateRequest);

        Task task = taskMapper.taskCreateRequestToTask(taskCreateRequest);

        Task savedTask = taskRepository.save(task);
        return taskMapper.convertToDto(savedTask);
    }

    private TaskCreateRequest assignDefaultPriorityForMemberRoles(TaskCreateRequest taskCreateRequest){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        boolean isAllowed = authentication.getAuthorities().stream()
                .anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals("ROLE_PROJECT_MANAGER")||
                          grantedAuthority.getAuthority().equals("ROLE_TEAM_LEADER"));

        if(!isAllowed && taskCreateRequest.priority() == null){
            return new TaskCreateRequest(
                    taskCreateRequest.description(),
                    TaskPriority.LOW,
                    taskCreateRequest.state(),
                    taskCreateRequest.assignee(),
                    taskCreateRequest.acceptanceCriteria(),
                    taskCreateRequest.reason(),
                    taskCreateRequest.attachments(),
                    taskCreateRequest.comments());
        }
        return taskCreateRequest;
    }
    /*


    @Override
    public TaskDto updateTask(UUID taskId,TaskUpdateRequest taskUpdateRequest) throws TaskNotFoundException {
        Task existingTask = taskRepository.findById(taskId)
                .orElseThrow(()->new TaskNotFoundException("Task not found with id: "+taskId));

        User assignee  = userRepository.findById(taskUpdateRequest.assigneeId())
                        .orElseThrow(()->new UserNotFoundException("User not found"));

        existingTask.setTaskPriority(taskUpdateRequest.taskPriority());
        existingTask.setAcceptanceCriteria(taskUpdateRequest.acceptanceCriteria());
        existingTask.setAssignee(assignee);

        validateTaskStatePath(taskUpdateRequest,taskUpdateRequest.taskState());
        validateTaskStateReason(taskUpdateRequest,taskUpdateRequest.taskState());

        existingTask.setTaskState(taskUpdateRequest.taskState());

        Task updatedTask = taskRepository.save(existingTask);
        return new TaskDto(updatedTask.getId(),updatedTask.getUserStoryDescription(),updatedTask.getTaskPriority(),updatedTask.getTaskState(),updatedTask.getAssignee().getId(),updatedTask.getAssigneeName(),updatedTask.getAcceptanceCriteria());
    }*/

    @Transactional(readOnly = true)
    @Override
    public Page<TaskDto> getTasks(Pageable pageable) {
        return taskRepository.getTasks(pageable)
                .map(taskMapper::convertToDto);
    }


    @Transactional
    @Override
    public TaskDto assignTaskToUser(UUID taskId, UUID userId) {
        Task task =  taskRepository.findById(taskId).orElseThrow(()->new TaskNotFoundException("Task not found"));
        User user = userRepository.findById(userId).orElseThrow(()->new UserNotFoundException("User not found"));

        if(isTaskAssignedToUser(task,userId)){
            return taskMapper.convertToDto(task);
        }
        task.setAssignee(user);
        TaskDto taskDto = taskMapper.convertToDto(task);
        return taskDto;
    }

    private boolean isTaskAssignedToUser(Task task,UUID userId){
        return task.getAssignee() != null && task.getAssignee().getId()==userId;
    }
/*
    @Override
    public Task changePriority(UUID taskId, TaskPriority updatedPriority) {
        Task task = taskRepository.findById(taskId).orElseThrow(()->new TaskNotFoundException("Task not found"));
        task.setTaskPriority(updatedPriority);
        return taskRepository.save(task);
    }
*/

    @Override
    public TaskDto getTaskProgress(UUID taskId) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(()->new TaskNotFoundException("Task not found"));
        return taskMapper.convertToDto(task);
    }
/*
    @Override
    public void softDeleteTask(UUID taskId) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(()->new TaskNotFoundException("Task not found"));
        task.setDeleted(true);
       taskRepository.save(task);
    }

    public void validateTaskStatePath(TaskUpdateRequest taskUpdateRequest,TaskState targetState){
        if(taskUpdateRequest.taskState()==TaskState.COMPLETED){
            throw new TaskAlreadyCompletedException("Task already completed.");
        }
        if(targetState==TaskState.CANCELLED){
            if(taskUpdateRequest.taskState()== TaskState.COMPLETED){
                throw new TaskAlreadyCompletedException("Task already completed, you cannot change it to cancel.");
            }
        }
        if(targetState == TaskState.BLOCKED){
            if(taskUpdateRequest.taskState() != TaskState.IN_ANALYSIS && taskUpdateRequest.taskState() != TaskState.IN_DEVELOPMENT){
                throw new TaskBlockedTransitionException("Task cannot  be moved to 'Block' state.");
            }
        }
    }
    public void validateTaskStateReason(TaskUpdateRequest taskUpdateRequest, TaskState taskState){
        if(taskUpdateRequest.taskState()==TaskState.BLOCKED || taskUpdateRequest.taskState()==TaskState.CANCELLED){
            if(taskUpdateRequest.reason() == null || taskUpdateRequest.reason().isEmpty()){
                throw new TaskReasonRequiredException("Reason must have some value");
            }
        }
    }

    public Comment addCommentToTask(UUID taskId, String content){
        Task task = taskRepository.findById(taskId)
                .orElseThrow(()->new TaskNotFoundException("Task not found"));

        Comment comment = Comment.builder()
                .content(content)
                .task(task)
                .build();

        return commentRepository.save(comment);
    }

    @Override
    public TaskDto updateTaskDetails(UUID taskId, TaskDetailsUpdateRequest request) {
        Task existingTask = taskRepository.findById(taskId)
                .orElseThrow(()->new TaskNotFoundException("Task not found"));
        existingTask.setUserStoryDescription(request.description());
        existingTask.setTitle(request.title());
        Task createdTask =  taskRepository.save(existingTask);
        return new TaskDto(createdTask.getId(),createdTask.getUserStoryDescription(),createdTask.getTaskPriority(),createdTask.getTaskState(),createdTask.getAssignee().getId(),createdTask.getAssigneeName(),createdTask.getAcceptanceCriteria());

    }
*/
    @Override
    public TaskDto updateTaskState(UUID taskId, TaskStatusUpdateRequest request) throws InvalidTaskStateException, TaskNotFoundException {

        Task task = taskRepository.findById(taskId)
                .orElseThrow(()->new TaskNotFoundException("Task not found"));

        List<TaskState> validNextStates = VALID_TRANSITIONS.getOrDefault(task.getState(),List.of());

        if(!validNextStates.contains(request.state())){
            throw new InvalidTaskStateException("Cannot transition from "+task.getState() + " to " + request.state());
        }

        checkReasonRequiredForTask(request.state(),request.reason());

        task.setState(request.state());
        task.setReason(request.reason());

        Task updatedTask = taskRepository.save(task);

        return taskMapper.convertToDto(updatedTask);

    }

    public void checkReasonRequiredForTask(TaskState targetState,String reason){
        if((targetState==TaskState.CANCELLED || targetState==TaskState.BLOCKED) && reason == null || reason.trim().isEmpty() ){
            throw new TaskReasonRequiredException("Reason must be provided for" + targetState);
        }
    }
}
