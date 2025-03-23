package com.irmaktekin.task.management.system.config;

import com.irmaktekin.task.management.system.entity.Role;
import com.irmaktekin.task.management.system.enums.RoleType;
import com.irmaktekin.task.management.system.repository.RoleRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;

import java.util.Set;

@Configuration
public class RoleInitializer implements CommandLineRunner {

    private final RoleRepository roleRepository;

    public RoleInitializer(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        Set < RoleType> roles  = Set.of(RoleType.TEAM_LEADER,RoleType.MEMBER,RoleType.PROJECT_MANAGER);
        roles.stream()
                .filter(roleType -> !roleRepository.existsByRoleType(roleType))
                .map(roleType -> Role.builder().roleType(roleType).build())
                .forEach(roleRepository::save);
    }
}
