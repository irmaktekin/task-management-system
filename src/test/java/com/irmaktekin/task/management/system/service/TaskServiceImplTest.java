package com.irmaktekin.task.management.system.service;

import com.irmaktekin.task.management.system.common.exception.*;
import com.irmaktekin.task.management.system.common.mapper.TaskMapper;
import com.irmaktekin.task.management.system.dto.request.TaskCreateRequest;
import com.irmaktekin.task.management.system.dto.request.TaskStatusUpdateRequest;
import com.irmaktekin.task.management.system.dto.response.CommentDto;
import com.irmaktekin.task.management.system.dto.response.TaskDto;
import com.irmaktekin.task.management.system.dto.response.UserDto;
import com.irmaktekin.task.management.system.entity.*;
import com.irmaktekin.task.management.system.enums.TaskPriority;
import com.irmaktekin.task.management.system.enums.TaskState;
import com.irmaktekin.task.management.system.repository.AttachmentRepository;
import com.irmaktekin.task.management.system.repository.CommentRepository;
import com.irmaktekin.task.management.system.repository.TaskRepository;
import com.irmaktekin.task.management.system.repository.UserRepository;
import com.irmaktekin.task.management.system.service.impl.FileStorageService;
import com.irmaktekin.task.management.system.service.impl.TaskServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TaskServiceImplTest {
    @InjectMocks
    private TaskServiceImpl taskService;

    @Mock
    private TaskRepository taskRepository;

    @Mock
    private FileStorageService fileStorageService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private AttachmentRepository attachmentRepository;

    @Mock
    private CommentRepository commentRepository;

    private Task task;
    private Comment comment;
    private UUID taskId,commentId;
    private User user;
    private UUID userId;
    private UserDto userDto;
    private CommentDto commentDto;
    private UUID projectId;
    private TaskDto taskDto;
    private MultipartFile file;
    private final TaskMapper taskMapper = mock(TaskMapper.class);

    @Mock
    private SecurityContext securityContext;

    @Mock
    private Authentication authentication;

    private TaskCreateRequest taskCreateRequest;

    @BeforeEach
    public void setUp(){

        commentId = UUID.randomUUID();
        projectId= UUID.randomUUID();
        userId = UUID.randomUUID();
        user = User.builder().id(userId).fullName("Irmak Tekin")
                .password("testpassword123.")
                .active(true).build();

        taskId = UUID.randomUUID();
        task = Task.builder().id(taskId)
                .priority(TaskPriority.LOW).state(TaskState.IN_DEVELOPMENT)
                .acceptanceCriteria("AC-1").assignee(user)
                .deleted(false)
                .build();

        comment = Comment.builder()
                .id(UUID.randomUUID()).content("Content")
                .task(task).user(user).build();

        userDto = new UserDto(userId,"Irmak Tekin","irmaktekin",true);
        commentDto = new CommentDto(commentId,"Content TEST",userId);
        taskDto = new TaskDto(taskId,"Description 1",TaskPriority.HIGH,TaskState.IN_DEVELOPMENT,userDto,"AC1",List.of(commentDto),false,"Title",null,projectId);

        file = new MockMultipartFile("file", "example.txt", "text/plain", "File content".getBytes());
        taskCreateRequest = new TaskCreateRequest(
                "Test task", TaskState.IN_DEVELOPMENT,
                user, "Acceptance Criteria", "Reason", List.of(comment), "Test task title",projectId
        );
    }

    @Test
    public void getTasks_ShouldReturnPageOfTaskDto_WhenTaskExists(){
        UUID userId = UUID.randomUUID();
        UserDto assignee = new UserDto(userId,"Irmak Tekin","irmaktekin",true);
        List<CommentDto> comments = List.of(new CommentDto(UUID.randomUUID(),"Content",userId));

        Pageable pageable = Pageable.unpaged();
        TaskDto taskDto = new TaskDto(UUID.randomUUID(),"Description",TaskPriority.HIGH,TaskState.IN_DEVELOPMENT,assignee,"AC-1",comments,false,"Title",null,projectId);

        List<Task> tasks  = Arrays.asList(task);
        Page<Task> taskPage = new PageImpl<>(tasks,pageable,tasks.size());

        when(taskRepository.findByDeletedFalse(pageable)).thenReturn(taskPage);
        when(taskMapper.convertToDto(task)).thenReturn(taskDto);

        Page<TaskDto> result = taskService.getTasks(pageable);


        verify(taskRepository,times(1)).findByDeletedFalse(pageable);
    }

    @Test
    void  assignTaskToUser_ShouldAssignTask_WhenTaskAndUserExist(){
        UUID taskId = UUID.randomUUID();

        TaskDto expectedTask = new TaskDto(taskId,"Description 1",TaskPriority.HIGH,TaskState.IN_DEVELOPMENT,userDto,"AC1",List.of(commentDto),false,"Title",null,projectId);
        when(taskRepository.findByIdAndDeletedFalse(taskId)).thenReturn(Optional.of(task));
        when(userRepository.findByIdAndDeletedFalse(userId)).thenReturn(Optional.of(user));

        when(taskMapper.convertToDto(task)).thenReturn(expectedTask);

        TaskDto result = taskService.assignTaskToUser(taskId,userId);

        assertNotNull(result);
        assertEquals(taskId,result.id());
        assertEquals("Description 1",result.description());

        verify(taskRepository,times(1)).findByIdAndDeletedFalse(taskId);
        verify(userRepository,times(1)).findByIdAndDeletedFalse(userId);
        verify(taskMapper,times(1)).convertToDto(task);
    }

    @Test
    void  assignPriority_ShouldAssignUpdatedPriority_WhenTaskAndUserExist() throws Exception{
        TaskPriority updatedPriority = TaskPriority.HIGH;

        when(taskRepository.findByIdAndDeletedFalse(taskId)).thenReturn(Optional.of(task));

        when(taskMapper.convertToDto(task)).thenReturn(taskDto);

        TaskDto result = taskService.assignPriority(taskId,updatedPriority);

        assertNotNull(result);
        assertEquals(updatedPriority,result.priority());
        verify(taskRepository,times(1)).findByIdAndDeletedFalse(taskId);
    }

    @Test
    void addAttachmentToTask_ShouldReturnUpdatedTask_WhenTaskExist() throws Exception{
        MultipartFile file = new MockMultipartFile("file","testFile.txt","text/plain","content".getBytes());
        String fileUrl = "http://testurl.com/testFile.txt";

        when(taskRepository.findByIdAndDeletedFalse(taskId)).thenReturn(Optional.of(task));
        when(fileStorageService.uploadFile(file)).thenReturn(fileUrl);

        Attachment attachment = Attachment.builder().fileType(fileUrl)
                .fileType(file.getContentType())
                .task(task).build();

        when(attachmentRepository.save(any(Attachment.class))).thenReturn(attachment);
        when(taskMapper.convertToDto(task)).thenReturn(taskDto);

        TaskDto result = taskService.addAttachmentToTask(taskId,file);

        assertEquals(taskId,result.id());
        verify(fileStorageService,times(1)).uploadFile(file);
        verify(attachmentRepository,times(1)).save(any(Attachment.class));
        verify(taskMapper,times(1)).convertToDto(task);

    }

    @Test
    void getTaskProgress_ShouldReturnTaskDto_WhenTaskExists(){

        when(taskRepository.findByIdAndDeletedFalse(taskId)).thenReturn(Optional.of(task));
        when(taskMapper.convertToDto(task)).thenReturn(taskDto);

        TaskDto result = taskService.getTaskProgress(taskId);

        assertEquals(taskId,result.id());
        verify(taskRepository,times(1)).findByIdAndDeletedFalse(taskId);
        verify(taskMapper,times(1)).convertToDto(task);
    }

    @Test
    void getTaskProgress_ShouldThrowException_WhenTaskNotFound(){

        when(taskRepository.findByIdAndDeletedFalse(taskId)).thenReturn(Optional.empty());

        assertThrows(TaskNotFoundException.class,()->taskService.getTaskProgress(taskId));

        verify(taskRepository,times(1)).findByIdAndDeletedFalse(taskId);
    }

    @Test
    void createTask_ShouldThrowException_WhenAssigneeNotFound() {

        when(userRepository.findByIdAndDeletedFalse(userId)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> taskService.createTask(taskCreateRequest));

        verify(userRepository, times(1)).findByIdAndDeletedFalse(userId);
    }

    @Test
    void assignTaskToUser_ShouldThrowException_WhenUserNotFound() {
        UUID nonExistentUserId = UUID.randomUUID();

        when(taskRepository.findByIdAndDeletedFalse(taskId)).thenReturn(Optional.of(task));
        when(userRepository.findByIdAndDeletedFalse(nonExistentUserId)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> taskService.assignTaskToUser(taskId, nonExistentUserId));

        verify(taskRepository, times(1)).findByIdAndDeletedFalse(taskId);
        verify(userRepository, times(1)).findByIdAndDeletedFalse(nonExistentUserId);
    }

    @Test
    void addAttachmentToTask_ShouldThrowException_WhenFileStorageFails() throws Exception {
        MultipartFile file = new MockMultipartFile("file", "testFile.txt", "text/plain", "content".getBytes());
        when(taskRepository.findByIdAndDeletedFalse(taskId)).thenReturn(Optional.of(task));
        when(fileStorageService.uploadFile(file)).thenThrow(new RuntimeException("File storage failed"));

        assertThrows(RuntimeException.class, () -> taskService.addAttachmentToTask(taskId, file));

        verify(fileStorageService, times(1)).uploadFile(file);
    }

    @Test
    void getTaskProgress_ShouldThrowException_WhenTaskIsDeleted() {
        task.setDeleted(true);
        when(taskRepository.findByIdAndDeletedFalse(taskId)).thenReturn(Optional.empty());

        assertThrows(TaskNotFoundException.class, () -> taskService.getTaskProgress(taskId));

        verify(taskRepository, times(1)).findByIdAndDeletedFalse(taskId);
    }

    @Test
    void assignPriority_ShouldThrowException_WhenTaskNotFound() {
        TaskPriority updatedPriority = TaskPriority.HIGH;

        when(taskRepository.findByIdAndDeletedFalse(taskId)).thenReturn(Optional.empty());

        assertThrows(TaskNotFoundException.class, () -> taskService.assignPriority(taskId, updatedPriority));

        verify(taskRepository, times(1)).findByIdAndDeletedFalse(taskId);
    }
    @Test
    void addCommentToTask_ShouldThrowException_WhenTaskIsDeleted() {
        String content = "Content";
        task.setDeleted(true);

        when(taskRepository.findByIdAndDeletedFalse(taskId)).thenReturn(Optional.empty());

        assertThrows(TaskNotFoundException.class, () -> taskService.addCommentToTask(taskId, content));

        verify(taskRepository, times(1)).findByIdAndDeletedFalse(taskId);
    }

    @Test
    void getTasks_ShouldReturnEmptyPage_WhenNoTasksExist() {
        Pageable pageable = Pageable.unpaged();
        Page<Task> emptyPage = new PageImpl<>(Collections.emptyList(), pageable, 0);

        when(taskRepository.findByDeletedFalse(pageable)).thenReturn(emptyPage);

        Page<TaskDto> result = taskService.getTasks(pageable);

        assertTrue(result.isEmpty());
        verify(taskRepository, times(1)).findByDeletedFalse(pageable);
    }

    @Test
    public void updateTaskStatus_ShouldReturnUpdatedTask() {
        UUID taskId = UUID.randomUUID();

        Task task = Task.builder().id(taskId)
                .priority(TaskPriority.LOW).state(TaskState.IN_DEVELOPMENT)
                .acceptanceCriteria("AC-1").assignee(user)
                .deleted(false)
                .build();
        task.setState(TaskState.IN_DEVELOPMENT);

        when(taskRepository.findByIdAndDeletedFalse(taskId)).thenReturn(Optional.of(task));

        TaskStatusUpdateRequest taskStatusUpdateRequest= new TaskStatusUpdateRequest(TaskState.COMPLETED,"");
        taskService.updateTaskState(taskId, taskStatusUpdateRequest);
        assertEquals(TaskState.COMPLETED, task.getState(), "Status should transition to IN_PROGRESS.");
    }

    @Test
    public void checkReasonRequiredForTask_ShouldThrowException_WhenStateIsCancelledOrBlockedAndReasonIsNullOrEmpty() {
        TaskState targetState = TaskState.CANCELLED;
        String reason = null;

        TaskReasonRequiredException exception = assertThrows(TaskReasonRequiredException.class, () -> {
            taskService.checkReasonRequiredForTask(targetState, reason);
        });

        assertEquals("Reason must be provided forCANCELLED", exception.getMessage());
    }

    @Test
    public void checkReasonRequiredForTask_ShouldThrowException_WhenStateIsCancelledAndReasonIsEmpty() {
        TaskState targetState = TaskState.CANCELLED;
        String reason = "";

        TaskReasonRequiredException exception = assertThrows(TaskReasonRequiredException.class, () -> {
            taskService.checkReasonRequiredForTask(targetState, reason);
        });

        assertEquals("Reason must be provided forCANCELLED", exception.getMessage());
    }

    @Test
    public void checkReasonRequiredForTask_ShouldNotThrowException_WhenStateIsNotCancelledOrBlocked() {
        TaskState targetState = TaskState.IN_DEVELOPMENT;
        String reason = null;

        assertDoesNotThrow(() -> {
            taskService.checkReasonRequiredForTask(targetState, reason);
        });
    }

    @Test
    public void checkReasonRequiredForTask_ShouldNotThrowException_WhenStateIsCancelledAndReasonIsProvided() {

        TaskState targetState = TaskState.CANCELLED;
        String reason = "Reason 1";

        assertDoesNotThrow(() -> {
            taskService.checkReasonRequiredForTask(targetState, reason);
        });
    }

    @Test
    public void checkReasonRequiredForTask_ShouldNotThrowException_WhenStateIsBlockedAndReasonIsProvided() {
        TaskState targetState = TaskState.BLOCKED;
        String reason = "Reason 2";

        assertDoesNotThrow(() -> {
            taskService.checkReasonRequiredForTask(targetState, reason);
        });
    }
    @Test
    public void getTasks_ShouldReturnTasksPage() {
        Pageable pageable = PageRequest.of(0, 10);

        UUID taskId1 = UUID.randomUUID();
        UUID taskId2 = UUID.randomUUID();

        Task task1 = Task.builder().id(taskId1)
                .priority(TaskPriority.CRITICAL).state(TaskState.IN_DEVELOPMENT)
                .acceptanceCriteria("AC-1").assignee(user)
                .deleted(false)
                .build();
        Task task2 = Task.builder().id(taskId2)
                .priority(TaskPriority.LOW).state(TaskState.BACKLOG)
                .acceptanceCriteria("AC-2").assignee(user)
                .deleted(false)
                .build();

        List<Task> taskList = List.of(task1, task2);
        Page<Task> taskPage = new PageImpl<>(taskList, pageable, taskList.size());

        when(taskRepository.findByDeletedFalse(pageable)).thenReturn(taskPage);
        when(taskMapper.convertToDto(task1)).thenReturn(any(TaskDto.class));
        when(taskMapper.convertToDto(task2)).thenReturn(any(TaskDto.class));

        Page<TaskDto> result = taskService.getTasks(pageable);

        assertNotNull(result, "The result should not be null.");
        assertEquals(2, result.getContent().size(), "The page should contain two tasks.");
        verify(taskRepository, times(1)).findByDeletedFalse(pageable);
        verify(taskMapper, times(2)).convertToDto(any(Task.class));
    }

    @Test
    public void getTasks_ShouldReturnEmptyPage_WhenNoTasks() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Task> taskPage = Page.empty(pageable);

        when(taskRepository.findByDeletedFalse(pageable)).thenReturn(taskPage);

        Page<TaskDto> result = taskService.getTasks(pageable);

        assertNotNull(result, "The result should not be null.");
        assertTrue(result.isEmpty(), "The result page should be empty.");
        verify(taskRepository, times(1)).findByDeletedFalse(pageable);
    }

    @Test
    public void getTasks_ShouldMapTaskCorrectly() {
        Pageable pageable = PageRequest.of(0, 10);
        Task task = new Task();
        List<Task> taskList = List.of(task);
        Page<Task> taskPage = new PageImpl<>(taskList, pageable, taskList.size());

        when(taskRepository.findByDeletedFalse(pageable)).thenReturn(taskPage);
        when(taskMapper.convertToDto(task)).thenReturn(any(TaskDto.class));

        Page<TaskDto> result = taskService.getTasks(pageable);

        assertNotNull(result, "The result should not be null.");
        assertEquals(1, result.getContent().size(), "The page should contain one task.");
        verify(taskMapper, times(1)).convertToDto(task);
    }

    @Test
    public void assignTaskToUser_ShouldThrowTaskNotFoundException_WhenTaskNotFound() {

        when(taskRepository.findByIdAndDeletedFalse(taskId)).thenReturn(Optional.empty());

        TaskNotFoundException exception = assertThrows(TaskNotFoundException.class, () -> {
            taskService.assignTaskToUser(taskId, userId);
        });
        assertEquals("Task not found", exception.getMessage());
    }

    @Test
    public void assignTaskToUser_ShouldThrowUserNotFoundException_WhenUserNotFound() {
        when(taskRepository.findByIdAndDeletedFalse(taskId)).thenReturn(Optional.of(task));
        when(userRepository.findByIdAndDeletedFalse(userId)).thenReturn(Optional.empty());

        UserNotFoundException exception = assertThrows(UserNotFoundException.class, () -> {
            taskService.assignTaskToUser(taskId, userId);
        });
        assertEquals("User not found", exception.getMessage());
    }

    @Test
    public void assignTaskToUser_ShouldReturnTaskDto_WhenTaskAlreadyAssignedToUser() {

        task.setAssignee(user);
        when(taskRepository.findByIdAndDeletedFalse(taskId)).thenReturn(Optional.of(task));
        when(userRepository.findByIdAndDeletedFalse(userId)).thenReturn(Optional.of(user));
        when(taskMapper.convertToDto(task)).thenReturn(taskDto);

        TaskDto result = taskService.assignTaskToUser(taskId, userId);

        assertNotNull(result, "TaskDto should not be null.");
        assertEquals(userId, task.getAssignee().getId(), "Task should remain assigned to the user.");
        verify(taskRepository, times(1)).findByIdAndDeletedFalse(taskId);
        verify(userRepository, times(1)).findByIdAndDeletedFalse(userId);
        verify(taskMapper, times(1)).convertToDto(task);
    }

    @Test
    public void checkReasonRequiredForTask_ShouldThrowException_WhenStateIsCancelledAndReasonIsNull() {
        final TaskState targetState = TaskState.CANCELLED;
        final String reason = null;
        TaskReasonRequiredException exception = assertThrows(TaskReasonRequiredException.class, () -> {
            taskService.checkReasonRequiredForTask(targetState, reason);
        });

        assertEquals("Reason must be provided forCANCELLED", exception.getMessage());

        final String emptyReason = "";
        exception = assertThrows(TaskReasonRequiredException.class, () -> {
            taskService.checkReasonRequiredForTask(targetState, emptyReason);
        });

        assertEquals("Reason must be provided forCANCELLED", exception.getMessage());    }

    @Test
    public void checkReasonRequiredForTask_ShouldNotThrowException_WhenStateIsCancelledAndReasonIsNotNullOrEmpty() {
        final TaskState targetState = TaskState.CANCELLED;
        final String reason = "Valid reason";

        assertDoesNotThrow(() -> {
            taskService.checkReasonRequiredForTask(targetState, reason);
        });
    }

    @Test
    public void checkReasonRequiredForTask_ShouldNotThrowException_WhenStateIsNotCancelled() {
        final TaskState targetState = TaskState.IN_DEVELOPMENT;
        final String reason = "Any reason";

        assertDoesNotThrow(() -> {
            taskService.checkReasonRequiredForTask(targetState, reason);
        });
    }

    @Test
    public void checkReasonRequiredForTask_ShouldThrowException_WhenStateIsCancelledAndReasonIsWhitespace() {
        final TaskState targetState = TaskState.CANCELLED;
        final String reason = "     ";

        TaskReasonRequiredException exception = assertThrows(TaskReasonRequiredException.class, () -> {
            taskService.checkReasonRequiredForTask(targetState, reason);
        });

        assertEquals("Reason must be provided forCANCELLED", exception.getMessage());
    }

    @Test
    public void checkReasonRequiredForTask_ShouldThrowException_WhenStateIsCancelledAndReasonIsEmptyString() {
        final TaskState targetState = TaskState.CANCELLED;
        final String reason = "";

        TaskReasonRequiredException exception = assertThrows(TaskReasonRequiredException.class, () -> {
            taskService.checkReasonRequiredForTask(targetState, reason);
        });

        assertEquals("Reason must be provided forCANCELLED", exception.getMessage());
    }

    @Test
    public void updateTaskState_ShouldThrowException_WhenStateIsAlreadyTheSame() {
        UUID taskId = UUID.randomUUID();
        Task task = new Task();
        task.setId(taskId);
        task.setState(TaskState.IN_DEVELOPMENT);

        TaskStatusUpdateRequest request = new TaskStatusUpdateRequest(TaskState.IN_DEVELOPMENT, "Already in progress");

        when(taskRepository.findByIdAndDeletedFalse(taskId)).thenReturn(Optional.of(task));

        TaskStateAlreadySameException exception = assertThrows(TaskStateAlreadySameException.class,
                () -> taskService.updateTaskState(taskId, request));
        assertEquals("Task is already in the IN_DEVELOPMENT", exception.getMessage());
    }

    @Test
    public void updateTaskState_ShouldThrowException_WhenReasonIsRequiredButNotProvided() {
        UUID taskId = UUID.randomUUID();
        Task task = new Task();
        task.setId(taskId);
        task.setState(TaskState.CANCELLED);

        TaskStatusUpdateRequest request = new TaskStatusUpdateRequest(TaskState.CANCELLED, null);

        when(taskRepository.findByIdAndDeletedFalse(taskId)).thenReturn(Optional.of(task));

        TaskStateAlreadySameException exception = assertThrows(TaskStateAlreadySameException.class,
                () -> taskService.updateTaskState(taskId, request));
        assertEquals("Task is already in the CANCELLED", exception.getMessage());
    }

    @Test
    public void updateTaskState_ShouldThrowException_WhenTaskNotFound() {
        UUID taskId = UUID.randomUUID();
        TaskStatusUpdateRequest request = new TaskStatusUpdateRequest(TaskState.COMPLETED, "Task completed");

        when(taskRepository.findByIdAndDeletedFalse(taskId)).thenReturn(Optional.empty());

        TaskNotFoundException exception = assertThrows(TaskNotFoundException.class,
                () -> taskService.updateTaskState(taskId, request));
        assertEquals("Task not found", exception.getMessage());
    }
    @Test
    public void assignTaskToUser_ShouldThrowException_WhenTaskNotFound() {
        UUID taskId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();

        when(taskRepository.findByIdAndDeletedFalse(taskId)).thenReturn(Optional.empty());

        TaskNotFoundException exception = assertThrows(TaskNotFoundException.class,
                () -> taskService.assignTaskToUser(taskId, userId));
        assertEquals("Task not found", exception.getMessage());
    }

    @Test
    public void assignTaskToUser_ShouldAssignTaskWhenUserIsNull() {
        UUID taskId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();

        Task task = new Task();
        task.setId(taskId);
        task.setState(TaskState.IN_DEVELOPMENT);

        User user = User.builder().id(userId).fullName("Irmak Tekin").build();

        when(taskRepository.findByIdAndDeletedFalse(taskId)).thenReturn(Optional.of(task));
        when(userRepository.findByIdAndDeletedFalse(userId)).thenReturn(Optional.of(user));

        when(taskMapper.convertToDto(task)).thenReturn(taskDto);

        TaskDto result = taskService.assignTaskToUser(taskId, userId);

        assertNotNull(result);
    }

    @Test
    void assignTaskToUser_ShouldThrowTaskNotFoundException_WhenTaskDoesNotExist() {
        when(taskRepository.findByIdAndDeletedFalse(taskId)).thenReturn(Optional.empty());

        assertThrows(TaskNotFoundException.class, () -> taskService.assignTaskToUser(taskId, userId));
    }

    @Test
    void assignTaskToUser_ShouldThrowUserNotFoundException_WhenUserDoesNotExist() {
        when(taskRepository.findByIdAndDeletedFalse(taskId)).thenReturn(Optional.of(task));
        when(userRepository.findByIdAndDeletedFalse(userId)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> taskService.assignTaskToUser(taskId, userId));
    }

    @Test
    void assignTaskToUser_ShouldReturnTaskDto_WhenTaskIsAlreadyAssignedToUser() {

        when(taskRepository.findByIdAndDeletedFalse(taskId)).thenReturn(Optional.of(task));
        when(userRepository.findByIdAndDeletedFalse(userId)).thenReturn(Optional.of(user));
        when(taskService.assignTaskToUser(taskId, userId)).thenReturn(taskDto);

        when(taskMapper.convertToDto(task)).thenReturn(taskDto);

        TaskDto result = taskService.assignTaskToUser(taskId, userId);

        assertEquals(taskDto, result);
        verify(taskRepository, never()).save(any(Task.class));
    }

    @Test
    void shouldAddAttachmentToTask_WhenTaskExists() throws Exception {
        UUID taskId = UUID.randomUUID();

        Task task = Task.builder()
                .id(taskId)
                .priority(TaskPriority.LOW)
                .state(TaskState.IN_DEVELOPMENT)
                .acceptanceCriteria("AC-1")
                .build();

        MockMultipartFile file = new MockMultipartFile(
                "file",
                "testfile.txt",
                "text/plain",
                "Dummy Content".getBytes()
        );

        when(taskRepository.findByIdAndDeletedFalse(taskId)).thenReturn(Optional.of(task));
        when(fileStorageService.uploadFile(file)).thenReturn("https://testurl.com/testfile.txt");
        when(taskMapper.convertToDto(task)).thenReturn(any(TaskDto.class));

        taskService.addAttachmentToTask(taskId, file);

        verify(taskRepository, times(1)).findByIdAndDeletedFalse(taskId);
        verify(fileStorageService, times(1)).uploadFile(file);
        verify(attachmentRepository, times(1)).save(any(Attachment.class));
        verify(taskMapper, times(1)).convertToDto(task);
    }

    @Test
    void shouldThrowIOException_WhenFileUploadFails() throws Exception {
        when(taskRepository.findByIdAndDeletedFalse(taskId)).thenReturn(Optional.of(task));
        when(fileStorageService.uploadFile(file)).thenThrow(new IOException("File upload failed"));

        assertThrows(IOException.class, () -> taskService.addAttachmentToTask(taskId, file));

        verify(taskRepository, times(1)).findByIdAndDeletedFalse(taskId);
        verify(fileStorageService, times(1)).uploadFile(file);
        verify(attachmentRepository, never()).save(any());
        verify(taskMapper, never()).convertToDto(any());
    }

    @Test
    void shouldThrowTaskNotFoundException_WhenTaskDoesNotExist() throws Exception{
        when(taskRepository.findByIdAndDeletedFalse(taskId)).thenReturn(Optional.empty());

        assertThrows(TaskNotFoundException.class, () -> taskService.addAttachmentToTask(taskId, file));

        verify(taskRepository, times(1)).findByIdAndDeletedFalse(taskId);
        verify(fileStorageService, never()).uploadFile(any());
        verify(attachmentRepository, never()).save(any());
        verify(taskMapper, never()).convertToDto(any());
    }

    @Test
    void shouldAddCommentToTask_WhenTaskExists() {
        String commentContent = "Test Content";
        when(taskRepository.findByIdAndDeletedFalse(taskId)).thenReturn(Optional.of(task));
        when(commentRepository.save(any(Comment.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Comment comment = taskService.addCommentToTask(taskId, commentContent);

        assertNotNull(comment);
        assertEquals(commentContent, comment.getContent());
        assertEquals(task, comment.getTask());

        verify(taskRepository, times(1)).findByIdAndDeletedFalse(taskId);
        verify(commentRepository, times(1)).save(any(Comment.class));
    }

    @Test
    void createTask_ShouldCreateTaskWhenRequestIsValid() throws Exception {

        UUID userId = UUID.randomUUID();
        User assignee = new User();
        assignee.setId(userId);
        assignee.setDeleted(false);

        Task task = new Task();
        task.setId(taskId);

        when(userRepository.findByIdAndDeletedFalse(any(UUID.class))).thenReturn(java.util.Optional.of(assignee));
        when(taskMapper.taskCreateRequestToTask(taskCreateRequest)).thenReturn(task);
        when(taskRepository.save(task)).thenReturn(task);
        when(taskMapper.convertToDto(task)).thenReturn(taskDto);

        TaskDto createdTaskDto = taskService.createTask(taskCreateRequest);

        assertEquals(task.getId(), createdTaskDto.id());
        Mockito.verify(taskRepository, Mockito.times(1)).save(task);
    }

}
