package com.irmaktekin.task.management.system.controller;

import com.irmaktekin.task.management.system.dto.response.UserDto;
import com.irmaktekin.task.management.system.entity.User;
import com.irmaktekin.task.management.system.service.UserService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping(value = "api/v1/users",produces = MediaType.APPLICATION_JSON_VALUE)
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public Page<UserDto> getUsers(@RequestParam(defaultValue = "0")int page,
                                  @RequestParam(defaultValue = "20") int size){
        return userService.getUsers(page,size);
    }

    @GetMapping("/{id}")
    public ResponseEntity<User> getUserById(@PathVariable UUID id){
        return ResponseEntity.ok(userService.findUserById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<User> updateUser(@PathVariable UUID id, @Valid @RequestBody User user){
        return ResponseEntity.ok(userService.updateUser(user));
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<Void> softDeleteUser(@PathVariable UUID userId){
        userService.softDeleteUser(userId);
        return ResponseEntity.noContent().build();
    }
}
