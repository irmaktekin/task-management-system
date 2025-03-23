package com.irmaktekin.task.management.system.service.impl;

import com.irmaktekin.task.management.system.common.ErrorMessage;
import com.irmaktekin.task.management.system.common.exception.*;
import com.irmaktekin.task.management.system.common.mapper.TaskMapper;
import com.irmaktekin.task.management.system.dto.request.TaskCreateRequest;
import com.irmaktekin.task.management.system.dto.request.TaskDetailsUpdateRequest;
import com.irmaktekin.task.management.system.dto.request.TaskStatusUpdateRequest;
import com.irmaktekin.task.management.system.dto.response.TaskDto;
import com.irmaktekin.task.management.system.entity.*;
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

       Task task = taskMapper.taskCreateRequestToTask(taskCreateRequest);

        taskRepository.save(task);

        if(taskCreateRequest.comments()!=null){
            taskCreateRequest.comments().forEach(comment -> {
                        comment.setTask(task);

                    }
            );
            commentRepository.saveAll(taskCreateRequest.comments());
        }

        return taskMapper.convertToDto(task);
    }

    private Attachment createAttachment (MultipartFile file, UUID taskId) throws IOException {
        Task task = taskRepository.findByIdAndDeletedFalse(taskId)
                .orElseThrow(()->new TaskNotFoundException(ErrorMessage.TASK_NOT_FOUND));

        String fileUrl = fileStorageService.uploadFile(file);

        Attachment attachment= Attachment.builder().fileName(file.getOriginalFilename())
                .fileType(file.getContentType())
                .task(task)
                .build();

        Attachment savedAttachment = attachmentRepository.save(attachment);
        return savedAttachment;

    }

    @Override
    public Page<TaskDto> getTasks(Pageable pageable) {
        return taskRepository.findByDeletedFalse(pageable)
                .map(taskMapper::convertToDto);
    }

    @Transactional
    @Override
    public TaskDto assignTaskToUser(UUID taskId, UUID userId) throws TaskNotFoundException {
        Task task =  taskRepository.findByIdAndDeletedFalse(taskId).orElseThrow(()->new TaskNotFoundException(ErrorMessage.TASK_NOT_FOUND));
        User user = userRepository.findByIdAndDeletedFalse(userId).orElseThrow(()->new UserNotFoundException(ErrorMessage.USER_NOT_FOUND));

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
        Task task = taskRepository.findByIdAndDeletedFalse(taskId).orElseThrow(()->new TaskNotFoundException(ErrorMessage.TASK_NOT_FOUND));
        task.setPriority(updatedPriority);
        return taskMapper.convertToDto(task);
    }

    @Override
    public TaskDto addAttachmentToTask(UUID taskId, MultipartFile file) throws IOException,TaskNotFoundException {
        Task task = taskRepository.findByIdAndDeletedFalse(taskId)
                .orElseThrow(()->new TaskNotFoundException(ErrorMessage.TASK_NOT_FOUND));

        String fileUrl = fileStorageService.uploadFile(file);

        Attachment attachment = Attachment.builder().fileName(fileUrl)
                .fileType(file.getContentType()).task(task).build();

        attachmentRepository.save(attachment);

        return taskMapper.convertToDto(task);
    }


    @Override
    public TaskDto getTaskProgress(UUID taskId) {
        Task task = taskRepository.findByIdAndDeletedFalse(taskId)
                .orElseThrow(()->new TaskNotFoundException(ErrorMessage.TASK_NOT_FOUND));
        return taskMapper.convertToDto(task);
    }
    public Comment addCommentToTask(UUID taskId, String content){
        Task task = taskRepository.findByIdAndDeletedFalse(taskId)
                .orElseThrow(()->new TaskNotFoundException(ErrorMessage.TASK_NOT_FOUND));

        Comment comment = Comment.builder()
                .content(content)
                .task(task)
                .build();

        return commentRepository.save(comment);
    }

    @Override
    public TaskDto updateTaskState(UUID taskId, TaskStatusUpdateRequest request) throws InvalidTaskStateException, TaskNotFoundException {

        Task task = taskRepository.findByIdAndDeletedFalse(taskId)
                .orElseThrow(()->new TaskNotFoundException(ErrorMessage.TASK_NOT_FOUND));

        if(task.getState()== request.state()){
            throw new TaskStateAlreadySameException(ErrorMessage.TASK_STATE_ALREADY_SAME);
        }

        List<TaskState> validNextStates = VALID_TRANSITIONS.getOrDefault(task.getState(),List.of());

        if(!validNextStates.contains(request.state())){
            throw new InvalidTaskStateException(ErrorMessage.INVALID_TASK_STATE);
        }

        checkReasonRequiredForTask(request.state(),request.reason());

        task.setState(request.state());
        task.setReason(request.reason());

        Task updatedTask = taskRepository.save(task);

        return taskMapper.convertToDto(updatedTask);

    }

    public void checkReasonRequiredForTask(TaskState targetState,String reason){
        if((targetState==TaskState.CANCELLED || targetState==TaskState.BLOCKED) && (reason == null || reason.trim().isEmpty()) ){
            throw new TaskReasonRequiredException(ErrorMessage.TASK_REASON_REQUIRED);
        }
    }
    @Override
    public TaskDto updateTaskDetails(UUID taskId, TaskDetailsUpdateRequest request) {
        Task existingTask = taskRepository.findById(taskId)
                .orElseThrow(()->new TaskNotFoundException(ErrorMessage.TASK_NOT_FOUND));
        existingTask.setDescription(request.description());
        existingTask.setTitle(request.title());
        Task createdTask =  taskRepository.save(existingTask);
        return taskMapper.convertToDto(createdTask);

    }
    @Override
    public Boolean softDeleteTask(UUID taskId) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(()->new TaskNotFoundException(ErrorMessage.TASK_NOT_FOUND));
        task.setDeleted(true);
        taskRepository.save(task);
        return true;
    }
}
