package com.irmaktekin.task.management.system.repository;

import com.irmaktekin.task.management.system.dto.response.TaskDto;
import com.irmaktekin.task.management.system.dto.response.UserDto;
import com.irmaktekin.task.management.system.entity.Task;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface TaskRepository extends JpaRepository<Task, UUID> {
    @Query("""
            Select new com.irmaktekin.task.management.system.dto.response.TaskDto(
                u.id, t.userStoryDescription, t.acceptanceCriteria)
            From Task t
            """)
    Page<TaskDto> getTasks(Pageable pageable);
}
