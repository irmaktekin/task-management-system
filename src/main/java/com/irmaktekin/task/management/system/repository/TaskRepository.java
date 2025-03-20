package com.irmaktekin.task.management.system.repository;

import com.irmaktekin.task.management.system.entity.Task;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface TaskRepository extends JpaRepository<Task, UUID> {
    @Query("Select t From Task t")
    Page<Task> getTasks(Pageable pageable);
    Page<Task> findByDeletedFalse(Pageable pageable);
    Optional<Task> findByIdAndDeletedFalse(UUID taskId);

}
