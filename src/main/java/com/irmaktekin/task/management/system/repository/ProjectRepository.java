package com.irmaktekin.task.management.system.repository;

import com.irmaktekin.task.management.system.dto.response.ProjectDto;
import com.irmaktekin.task.management.system.entity.Project;
import com.irmaktekin.task.management.system.entity.Task;
import com.irmaktekin.task.management.system.enums.ProjectStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ProjectRepository extends JpaRepository<Project, UUID> {
    @Query("Select p From Project p")
    Page<Project> getProjects(Pageable pageable);

    Page<ProjectDto> findByDeletedFalse(Pageable pageable);

    List<Project> findByDepartmentNameAndIsDeletedFalse(String departmentName);

    @Query("SELECT CASE WHEN COUNT(p)>0 Then true ELSE False END " +
            "FROM Project p JOIN p.members m Where m.id IN :memberIds " +
            "And p.status =:status")
    boolean existMemberInProject(@Param("memberIds")List<UUID> memberIds, @Param("status")ProjectStatus status);
}
