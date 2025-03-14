package com.irmaktekin.task.management.system.service;

import com.irmaktekin.task.management.system.common.exception.TaskNotFoundException;
import com.irmaktekin.task.management.system.dto.request.TaskCreateRequest;
import com.irmaktekin.task.management.system.entity.Task;
import com.irmaktekin.task.management.system.entity.User;
import com.irmaktekin.task.management.system.enums.TaskPriority;
import com.irmaktekin.task.management.system.enums.TaskState;
import com.irmaktekin.task.management.system.repository.TaskRepository;
import com.irmaktekin.task.management.system.service.impl.TaskServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TaskServiceImplTest {
    @InjectMocks
    private TaskServiceImpl taskService;

    @Mock
    private TaskRepository taskRepository;

    private Task task;
    private UUID taskId;
    private User user;
    private UUID userId;
    @BeforeEach
    public void setUp(){
        userId = UUID.randomUUID();
        user = User.builder().id(userId).fullName("Irmak Tekin")
                .email("irmaktekin@gmail.com").password("testpassword123.")
                .isActive(true).build();

        taskId = UUID.randomUUID();
        task = Task.builder().id(taskId)
                .taskPriority(TaskPriority.LOW).taskState(TaskState.IN_DEVELOPMENT)
                .acceptanceCriteria("AC-1").assignee(user)
                .build();
    }
    @Test
    public void shouldCreateTask_whenRequestIsValid(){
        var request = new TaskCreateRequest("Description Test",TaskPriority.HIGH,TaskState.IN_DEVELOPMENT, userId,"AC1");
        UUID id3 = UUID.randomUUID();
        Task task = Task.builder().id(id3).userStoryDescription(request.description()).taskPriority(request.taskPriority()).taskState(request.taskState()).assignee(user).build();

        when(taskRepository.save(any(Task.class))).thenReturn(task);
        taskService.createTask(request);

        verify(taskRepository,times(1)).save(any(Task.class));
    }

    @Test
    public void shouldUpdateTask_whenTaskExists(){
        when(taskRepository.findById(taskId)).thenReturn(Optional.of(task));
        when(taskRepository.save(task)).thenReturn(task);

        Task updatedTask = taskService.updateTask(task);

        assertNotNull(updatedTask);
        assertEquals(task.getTaskState(),updatedTask.getTaskState());

        verify(taskRepository,times(1)).findById(taskId);
        verify(taskRepository,times(1)).save(task);
    }

    @Test
    public void shouldReturnAllTasks(){
        var page = 0;
        var size = 20;

        taskService.getTasks(page,size);

        verify(taskRepository,times(1)).getTasks(PageRequest.of(page,size));
    }

    @Test
    public void shouldReturnTask_whenTaskExist(){
        when(taskRepository.findById(taskId)).thenReturn(Optional.of(task));

        Task foundTask = taskService.findTaskById(taskId);

        assertNotNull(foundTask);
        assertEquals(task.getId(),foundTask.getId());

        verify(taskRepository,times(1)).findById(taskId);
    }

    @Test
    public void shouldThrowException_whenTaskNotFound(){
        when(taskRepository.findById(taskId)).thenReturn(Optional.empty());

        assertThrows(TaskNotFoundException.class,()->taskService.findTaskById(taskId));
    }
}
