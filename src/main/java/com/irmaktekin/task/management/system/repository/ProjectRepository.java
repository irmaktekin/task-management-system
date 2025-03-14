package com.irmaktekin.task.management.system.repository;

import com.irmaktekin.task.management.system.dto.response.ProjectDto;
import com.irmaktekin.task.management.system.dto.response.TaskDto;
import com.irmaktekin.task.management.system.entity.Project;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ProjectRepository extends JpaRepository<Project, UUID> {
    @Query("""
            Select new com.irmaktekin.task.management.system.dto.response.ProjectDto(
                p.id, p.description, p.title, p.status)
            From Project p
            """)
    Page<ProjectDto> getProjects(Pageable pageable);
}
