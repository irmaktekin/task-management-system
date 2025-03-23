package com.irmaktekin.task.management.system.service.impl;

import com.irmaktekin.task.management.system.common.exception.*;
import com.irmaktekin.task.management.system.common.mapper.TaskMapper;
import com.irmaktekin.task.management.system.dto.request.TaskCreateRequest;
import com.irmaktekin.task.management.system.dto.request.TaskDetailsUpdateRequest;
import com.irmaktekin.task.management.system.dto.request.TaskStatusUpdateRequest;
import com.irmaktekin.task.management.system.dto.response.TaskDto;
import com.irmaktekin.task.management.system.entity.Attachment;
import com.irmaktekin.task.management.system.entity.Comment;
import com.irmaktekin.task.management.system.entity.Task;
import com.irmaktekin.task.management.system.entity.User;
import com.irmaktekin.task.management.system.enums.TaskPriority;
import com.irmaktekin.task.management.system.enums.TaskState;
import com.irmaktekin.task.management.system.repository.AttachmentRepository;
import com.irmaktekin.task.management.system.repository.CommentRepository;
import com.irmaktekin.task.management.system.repository.TaskRepository;
import com.irmaktekin.task.management.system.repository.UserRepository;
import com.irmaktekin.task.management.system.service.TaskService;
import com.irmaktekin.task.management.system.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TaskServiceImpl implements TaskService {

    private final TaskRepository taskRepository;
    private final TaskMapper taskMapper;
    private final UserRepository userRepository;
    private final AttachmentRepository attachmentRepository;
    private final FileStorageService fileStorageService;
    private final UserService userService;
    private final CommentRepository commentRepository;


    private static final Map<TaskState, List<TaskState>> VALID_TRANSITIONS = Map.of(
            TaskState.BACKLOG, List.of(TaskState.IN_ANALYSIS,TaskState.CANCELLED,TaskState.IN_DEVELOPMENT),
            TaskState.IN_ANALYSIS, List.of(TaskState.BACKLOG, TaskState.IN_DEVELOPMENT,TaskState.BLOCKED,TaskState.CANCELLED),
            TaskState.IN_DEVELOPMENT, List.of(TaskState.IN_ANALYSIS, TaskState.COMPLETED,TaskState.BLOCKED,TaskState.CANCELLED,TaskState.BACKLOG),
            TaskState.BLOCKED,List.of(TaskState.IN_DEVELOPMENT,TaskState.IN_ANALYSIS,TaskState.CANCELLED),
            TaskState.CANCELLED,List.of(TaskState.IN_DEVELOPMENT,TaskState.IN_ANALYSIS,TaskState.BACKLOG, TaskState.BLOCKED)
    );

    @Override
    public TaskDto createTask(TaskCreateRequest taskCreateRequest) throws Exception {

            User assignee = userRepository.findByIdAndDeletedFalse(taskCreateRequest.assignee().getId())
                    .orElseThrow(()->new UserNotFoundException("User not found."));

        Task task = taskMapper.taskCreateRequestToTask(taskCreateRequest);

        taskRepository.save(task);

        if(taskCreateRequest.comments()!=null){
            taskCreateRequest.comments().forEach(comment -> {
                        comment.setTask(task);
                        comment.setUser(assignee);

                    }
            );
            commentRepository.saveAll(taskCreateRequest.comments());
        }

        return taskMapper.convertToDto(task);
    }

    private Attachment createAttachment (MultipartFile file, UUID taskId) throws IOException {
        Task task = taskRepository.findByIdAndDeletedFalse(taskId)
                .orElseThrow(()->new TaskNotFoundException("Task not found"));

        String fileUrl = fileStorageService.uploadFile(file);

        Attachment attachment= Attachment.builder().fileName(file.getOriginalFilename())
                .fileType(file.getContentType())
                .task(task)
                .build();

        Attachment savedAttachment = attachmentRepository.save(attachment);
        return savedAttachment;

    }

    @Transactional(readOnly = true)
    @Override
    public Page<TaskDto> getTasks(Pageable pageable) {
        return taskRepository.findByDeletedFalse(pageable)
                .map(taskMapper::convertToDto);
    }


    @Transactional
    @Override
    public TaskDto assignTaskToUser(UUID taskId, UUID userId) throws TaskNotFoundException {
        Task task =  taskRepository.findByIdAndDeletedFalse(taskId).orElseThrow(()->new TaskNotFoundException("Task not found"));
        User user = userRepository.findByIdAndDeletedFalse(userId).orElseThrow(()->new UserNotFoundException("User not found"));

        if(isTaskAssignedToUser(task,userId)){
            return taskMapper.convertToDto(task);
        }
        task.setAssignee(user);
        TaskDto taskDto = taskMapper.convertToDto(task);
        return taskDto;
    }

    protected boolean isTaskAssignedToUser(Task task,UUID userId){
        return task.getAssignee() != null && task.getAssignee().getId().equals(userId);
    }

    @Override
    public TaskDto assignPriority(UUID taskId, TaskPriority updatedPriority) {
        Task task = taskRepository.findByIdAndDeletedFalse(taskId).orElseThrow(()->new TaskNotFoundException("Task not found"));
        task.setPriority(updatedPriority);
        return taskMapper.convertToDto(task);
    }

    @Override
    public TaskDto addAttachmentToTask(UUID taskId, MultipartFile file) throws IOException,TaskNotFoundException {
        Task task = taskRepository.findByIdAndDeletedFalse(taskId)
                .orElseThrow(()->new TaskNotFoundException("Task not found"));

        String fileUrl = fileStorageService.uploadFile(file);

        Attachment attachment = Attachment.builder().fileName(fileUrl)
                .fileType(file.getContentType()).task(task).build();

        attachmentRepository.save(attachment);

        return taskMapper.convertToDto(task);
    }


    @Override
    public TaskDto getTaskProgress(UUID taskId) {
        Task task = taskRepository.findByIdAndDeletedFalse(taskId)
                .orElseThrow(()->new TaskNotFoundException("Task not found"));
        return taskMapper.convertToDto(task);
    }
    public Comment addCommentToTask(UUID taskId, String content){
        Task task = taskRepository.findByIdAndDeletedFalse(taskId)
                .orElseThrow(()->new TaskNotFoundException("Task not found"));

        Comment comment = Comment.builder()
                .content(content)
                .task(task)
                .build();

        return commentRepository.save(comment);
    }

    @Override
    public TaskDto updateTaskState(UUID taskId, TaskStatusUpdateRequest request) throws InvalidTaskStateException, TaskNotFoundException {

        Task task = taskRepository.findByIdAndDeletedFalse(taskId)
                .orElseThrow(()->new TaskNotFoundException("Task not found"));

        if(task.getState()== request.state()){
            throw new TaskStateAlreadySameException("Task is already in the " + request.state());
        }

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
        if((targetState==TaskState.CANCELLED || targetState==TaskState.BLOCKED) && (reason == null || reason.trim().isEmpty()) ){
            throw new TaskReasonRequiredException("Reason must be provided for" + targetState);
        }
    }
    @Override
    public TaskDto updateTaskDetails(UUID taskId, TaskDetailsUpdateRequest request) {
        Task existingTask = taskRepository.findById(taskId)
                .orElseThrow(()->new TaskNotFoundException("Task not found"));
        existingTask.setDescription(request.description());
        existingTask.setTitle(request.title());
        Task createdTask =  taskRepository.save(existingTask);
        return taskMapper.convertToDto(createdTask);

    }
}
