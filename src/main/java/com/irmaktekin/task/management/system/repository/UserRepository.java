package com.irmaktekin.task.management.system.repository;

import com.irmaktekin.task.management.system.dto.response.UserDto;
import com.irmaktekin.task.management.system.entity.Task;
import com.irmaktekin.task.management.system.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {
    @Query(" Select u From User u")
    Page<UserDto> getUsers(Pageable pageable);
    Optional<User> findByUsername(String username);
    Optional<User> findByIdAndDeletedFalse(UUID taskId);

}
