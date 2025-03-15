package com.irmaktekin.task.management.system.repository;

import com.irmaktekin.task.management.system.dto.response.UserDto;
import com.irmaktekin.task.management.system.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {
    @Query("""
            Select new com.irmaktekin.task.management.system.dto.response.UserDto(
                u.id, u.fullName, u.email, u.password,u.isActive)
            From User u
            """)
    Page<UserDto> getUsers(Pageable pageable);
}
