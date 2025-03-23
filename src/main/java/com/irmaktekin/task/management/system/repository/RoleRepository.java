package com.irmaktekin.task.management.system.repository;

import com.irmaktekin.task.management.system.entity.Role;
import com.irmaktekin.task.management.system.enums.RoleType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface RoleRepository extends JpaRepository<Role, UUID> {
    Optional<Role> findByRoleType(RoleType roleType);
    boolean existsByRoleType(RoleType roleType);
}
