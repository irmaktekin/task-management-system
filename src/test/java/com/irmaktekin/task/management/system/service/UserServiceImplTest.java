package com.irmaktekin.task.management.system.service;

import com.irmaktekin.task.management.system.common.exception.UserNotFoundException;
import com.irmaktekin.task.management.system.entity.User;
import com.irmaktekin.task.management.system.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
public class UserServiceImplTest {

    @InjectMocks
    private UserService userService;

    @Mock
    private UserRepository userRepository;

    private User user1;
    private User user2;
    private UUID userId1;
    private UUID userId2;

    @BeforeEach
    public void setUp(){
        MockitoAnnotations.openMocks(this);
        userId1 = UUID.randomUUID();
        userId2 = UUID.randomUUID();
        user1 = User.builder().id(userId1).fullName("Irmak Tekin")
                .email("irmaktekin@gmail.com").password("testpassword123.")
                .isActive(true).build();

        user2 = User.builder().id(userId2).fullName("Irmak Tekin")
                .email("aysetekin@gmail.com").password("testpasswor456.")
                .isActive(true).build();
    }
    @Test
    public void shouldSaveUser_whenUserExists(){
        when(userRepository.save(user1)).thenReturn(user1);

        User createdUser = userService.createUser(user1);

        assertNotNull(createdUser);
        assertEquals("Irmak Tekin",createdUser.getFullName());
        assertEquals("irmaktekin@gmail.com",createdUser.getEmail());
        assertEquals("testpassword123.",createdUser.getPassword());
        assertEquals(true,createdUser.isActive());

        verify(userRepository,times(1)).save(createdUser);
    }

    @Test
    public void shouldUpdateUser_whenUserExists(){
        when(userRepository.findById(userId1)).thenReturn(Optional.of(user1));
        when(userRepository.save(user1)).thenReturn(user1);

        User updatedUser = userService.updateUser(userId1, user1);

        assertNotNull(updatedUser);
        assertEquals(user1.getFullName(),updatedUser.getFullName());

        verify(userRepository,times(1)).findById(userId1);
        verify(userRepository,times(1)).save(user1);

    }

    @Test
    public void shouldReturnAllUsers(){
        List<User> users = List.of(user1, user2);
        when(userRepository.findAll()).thenReturn(users);

        List<User> result = userService.getUsers();

        assertNotNull(result);
        assertEquals(2,result.size());

        verify(userRepository,times(1)).findAll();
    }

    @Test
    public void shouldReturnUser_whenUserExist() throws Exception{
        when(userRepository.findById(userId1)).thenReturn(Optional.of(user1));

        User foundUser = userService.findUserById(userId1);

        assertNotNull(foundUser);
        assertEquals(user1.getId(),foundUser.getId());

        verify(userRepository,times(1)).findById(userId1);
    }

    @Test
    public void shouldThrowException_whenUserNotFoundById(){
        when(userRepository.findById(userId1)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class,()->userService.findUserById(userId1));
    }

    @Test
    public void shouldDeleteUserWhenUserExist(){
        when(userRepository.findById(userId1)).thenReturn(Optional.of(user1));

        userService.deleteUser(user1.getId());

        verify(userRepository,times(1)).delete(user1);
    }
}
