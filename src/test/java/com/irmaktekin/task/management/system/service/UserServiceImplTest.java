package com.irmaktekin.task.management.system.service;

import com.irmaktekin.task.management.system.common.exception.UserNotFoundException;
import com.irmaktekin.task.management.system.dto.request.UserCreateRequest;
import com.irmaktekin.task.management.system.entity.User;
import com.irmaktekin.task.management.system.repository.UserRepository;
import com.irmaktekin.task.management.system.service.impl.UserServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceImplTest {

    @InjectMocks
    private UserServiceImpl userService;

    @Mock
    private UserRepository userRepository;

    private User user1;
    private UUID userId1;

    @BeforeEach
    public void setUp(){
        userId1 = UUID.randomUUID();

        user1 = User.builder().id(userId1).fullName("Irmak Tekin")
                .email("irmaktekin@gmail.com").password("testpassword123.")
                .isActive(true).build();
    }

    @Test
    public void shouldCreateUser_whenRequestIsValid(){
        var request = new UserCreateRequest("Irmak Tekin","irmaktekin@test.com","128310",true);
        UUID id3 = UUID.randomUUID();
        User user = User.builder().id(id3).fullName(request.fullName()).email(request.email()).password(request.password()).isActive(request.isActive()).build();

        when(userRepository.save(any(User.class))).thenReturn(user);
        userService.createUser(request);

       verify(userRepository,times(1)).save(any(User.class));
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
        var page = 0;
        var size = 20;

        userService.getUsers(page,size);

        verify(userRepository,times(1)).getUsers(PageRequest.of(page,size));
    }

    @Test
    public void shouldReturnUser_whenUserExist(){
        when(userRepository.findById(userId1)).thenReturn(Optional.of(user1));

        User foundUser = userService.findUserById(userId1);

        assertNotNull(foundUser);
        assertEquals(user1.getId(),foundUser.getId());

        verify(userRepository,times(1)).findById(userId1);
    }

    @Test
    public void shouldThrowException_whenUserNotFound(){
        when(userRepository.findById(userId1)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class,()->userService.findUserById(userId1));
    }

    @Test
    public void shouldDeleteUserWhenUserExist(){
        when(userRepository.findById(userId1)).thenReturn(Optional.of(user1));

        userService.deleteUser(userId1);

        verify(userRepository,times(1)).findById(userId1);
        verify(userRepository,times(1)).save(user1);
    }
}
