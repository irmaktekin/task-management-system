package com.irmaktekin.task.management.system.service;

import com.irmaktekin.task.management.system.common.exception.RoleNotFoundException;
import com.irmaktekin.task.management.system.common.exception.UserNotFoundException;
import com.irmaktekin.task.management.system.entity.Role;
import com.irmaktekin.task.management.system.entity.Task;
import com.irmaktekin.task.management.system.entity.User;
import com.irmaktekin.task.management.system.enums.RoleType;
import com.irmaktekin.task.management.system.enums.TaskPriority;
import com.irmaktekin.task.management.system.enums.TaskState;
import com.irmaktekin.task.management.system.repository.RoleRepository;
import com.irmaktekin.task.management.system.repository.UserRepository;
import com.irmaktekin.task.management.system.service.impl.UserServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceImplTest {

    @InjectMocks
    private UserServiceImpl userService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private RoleRepository roleRepository;

    private User user;
    private UUID userId;
    private Task task;
    private UUID taskId;
    Role mockRole;
    @BeforeEach
    public void setUp(){
        userId = UUID.randomUUID();

        userId = UUID.randomUUID();
        user = User.builder().id(userId).fullName("Irmak Tekin")
                .username("irmaktekin")
                .password("testpassword123.")
                .active(true).build();

        taskId = UUID.randomUUID();
        task = Task.builder().id(taskId)
                .priority(TaskPriority.LOW).state(TaskState.IN_DEVELOPMENT)
                .acceptanceCriteria("AC-1").assignee(user)
                .build();
        mockRole = mock(Role.class);

    }

    @Test
    public void shouldUpdateUser_whenUserExists(){
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(userRepository.save(user)).thenReturn(user);

        User updatedUser = userService.updateUser(user);

        assertNotNull(updatedUser);
        assertEquals(user.getFullName(),updatedUser.getFullName());

        verify(userRepository,times(1)).findById(userId);
        verify(userRepository,times(1)).save(user);
    }

    @Test
    public void shouldReturnAllUsers(){
        var page = 0;
        var size = 20;

        userService.getUsers(page,size);

        verify(userRepository,times(1)).getUsers(PageRequest.of(page,size));
    }

    @Test
    public void shouldReturnUser_whenUserExist(){
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        User foundUser = userService.findUserById(userId);

        assertNotNull(foundUser);
        assertEquals(user.getId(),foundUser.getId());

        verify(userRepository,times(1)).findById(userId);
    }

    @Test
    public void shouldThrowException_whenUserNotFound(){
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class,()->userService.findUserById(userId));
    }

    @Test
    public void softDeleteUser_ShouldSetDeletedTrue_WhenUserExists() throws Exception{
        UUID userId = UUID.randomUUID();
        User user = new User(userId,"irmaktekin","Irmak Tekin", "12345678",true, List.of(task), Set.of(mockRole),false);
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(userRepository.save(user)).thenReturn(user);

        User deletedUser = userService.softDeleteUser(userId);

        assertTrue(deletedUser.isDeleted());

        verify(userRepository,times(1)).findById(userId);
        verify(userRepository,times(1)).save(user);
    }

    @Test
    public void softDeleteUser_ShouldThrowUserNotFoundException_WhenUserDoesNotExists(){
        UUID userId = UUID.randomUUID();
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class,()->userService.softDeleteUser(userId));
    }

    @Test
    public void getUserByName_ShouldReturnUser_WhenUserExists() throws Exception{
        String username = "irmaktekin";
        User user = new User(userId,"Irmak Tekin","irmaktekin", "12345678",true, List.of(task), Set.of(mockRole),false);

        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));

        User foundUser = userService.getUserByName(username);

        assertEquals(user.getUsername(),foundUser.getUsername());
    }

    @Test
    void updateUser_ShouldThrowUserNotFoundException_WhenUserDoesNotExists(){
        UUID userId = UUID.randomUUID();
        User updatedUser = new User(userId,"Irmak Tekin","irmaktekin", "12345678",true, List.of(task), Set.of(mockRole),false);

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class,()->userService.updateUser(updatedUser));

    }

    @Test
    public void deleteUser_ShouldSetActiveFalse_WhenUserExists() {
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        userService.softDeleteUser(userId);

        verify(userRepository, times(1)).save(user);
    }


    @Test
    public void deleteUser_ShouldThrowUserNotFoundException_WhenUserDoesNotExist() {
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> userService.softDeleteUser(userId));
    }

    @Test
    public void assignRoleToUser_ShouldAddRole_WhenUserAndRoleExist() {
        UUID userId = UUID.randomUUID();
        User user = User.builder()
                .id(userId)
                .fullName("Irmak Tekin")
                .password("testpassword123.")
                .active(true)
                .roles(new HashSet<>())
                .build();

        RoleType roleType = RoleType.PROJECT_MANAGER;
        Role role = new Role(UUID.randomUUID(), roleType);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(roleRepository.findByRoleType(roleType)).thenReturn(Optional.of(role));
        when(userRepository.save(user)).thenReturn(user);

        User updatedUser = userService.assignRoleToUser(userId, roleType);

        assertTrue(updatedUser.getRoles().contains(role));
        verify(userRepository, times(1)).save(user);
    }

    @Test
    public void assignRoleToUser_ShouldThrowUserNotFoundException_WhenUserDoesNotExist() {
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> userService.assignRoleToUser(userId, RoleType.TEAM_LEADER));
    }

    @Test
    public void assignRoleToUser_ShouldThrowIllegalArgumentException_WhenRoleDoesNotExist() {
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(roleRepository.findByRoleType(RoleType.TEAM_LEADER)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> userService.assignRoleToUser(userId, RoleType.TEAM_LEADER));
    }

    @Test
    public void getUserByName_ShouldThrowUserNotFoundException_WhenUserDoesNotExist() {
        String username = "nonexistentUser";
        when(userRepository.findByUsername(username)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> userService.getUserByName(username));
    }

    @Test
    public void softDeleteUser_ShouldNotChangeDeleted_WhenUserIsAlreadyDeleted() {
        user.setDeleted(true);
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenReturn(user);

        User deletedUser = userService.softDeleteUser(userId);

        assertTrue(deletedUser.isDeleted());
        verify(userRepository, times(1)).save(user);
    }

    @Test
    public void getUsers_ShouldReturnEmptyPage_WhenNoUsersExist() {
        var page = 0;
        var size = 10;

        when(userRepository.getUsers(PageRequest.of(page, size))).thenReturn(Page.empty());

        var result = userService.getUsers(page, size);

        assertTrue(result.isEmpty());
    }

    @Test
    public void updateUser_ShouldNotChangeUser_WhenSameDataProvided() {
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(userRepository.save(user)).thenReturn(user);

        User updatedUser = userService.updateUser(user);

        assertEquals(user, updatedUser);
        verify(userRepository, times(1)).save(user);
    }
    @Test
    public void getUserShouldHandleNullUsername() {
        String username = null;
        when(userRepository.findByUsername(username)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> userService.getUserByName(username));
    }
    @Test
    public void shouldThrowUserNotFoundException_WhenRoleIsNull() {
        assertThrows(UserNotFoundException.class, () -> userService.assignRoleToUser(userId, null));
    }

    @Test
    public void shouldSoftDeleteUser_whenUserExists() {

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(userRepository.save(user)).thenReturn(user);

        User deletedUser = userService.softDeleteUser(userId);

        assertTrue(deletedUser.isDeleted());
        verify(userRepository, times(1)).save(user);
    }

    @Test
    public void shouldThrowUserNotFoundException_whenUserNotFoundForSoftDelete() {
        UUID userId = UUID.randomUUID();
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> userService.softDeleteUser(userId));
    }
    @Test
    public void shouldReturnUser_WhenUserExistsByUsername() {
        String username = "irmaktekin";
        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));

        User foundUser = userService.getUserByName(username);

        assertNotNull(foundUser);
        assertEquals(username, foundUser.getUsername());
    }
    @Test
    public void shouldThrowUserNotFoundException_whenUserNotFoundForUpdate() {
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> userService.updateUser(user));
    }

    @Test
    public void shouldReturnAuthenticatedUser_whenAuthenticatedUserExists() {
        String username = "irmaktekin";

        UserDetails userDetails = mock(UserDetails.class);
        when(userDetails.getUsername()).thenReturn(username);
        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(new UsernamePasswordAuthenticationToken(userDetails, null));
        SecurityContextHolder.setContext(securityContext);

        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));

        User authenticatedUser = userService.getAuthenticatedUser();

        assertNotNull(authenticatedUser);
        assertEquals(username, authenticatedUser.getUsername());
    }

    @Test
    public void shouldThrowUsernameNotFoundException_whenNoAuthenticatedUser() {
        String username = "irmaktekin";

        UserDetails userDetails = mock(UserDetails.class);
        when(userDetails.getUsername()).thenReturn(username);

        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(new UsernamePasswordAuthenticationToken(userDetails, null));
        SecurityContextHolder.setContext(securityContext);

        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));

        User authenticatedUser = userService.getAuthenticatedUser();

        assertNotNull(authenticatedUser);
        assertEquals(username, authenticatedUser.getUsername());
    }
    @Test
    public void shouldThrowUsernameNotFoundException_whenPrincipalIsNotUserDetails() {
        Object unexpectedPrincipal = new Object();

        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(new UsernamePasswordAuthenticationToken(unexpectedPrincipal, null));
        SecurityContextHolder.setContext(securityContext);

        assertThrows(UsernameNotFoundException.class, () -> userService.getAuthenticatedUser());
    }


}
