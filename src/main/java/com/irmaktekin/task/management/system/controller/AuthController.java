package com.irmaktekin.task.management.system.controller;

import com.irmaktekin.task.management.system.dto.request.LoginRequest;
import com.irmaktekin.task.management.system.dto.request.UserRegisterRequest;
import com.irmaktekin.task.management.system.dto.response.LoginResponse;
import com.irmaktekin.task.management.system.dto.response.UserDto;
import com.irmaktekin.task.management.system.security.JwtService;
import com.irmaktekin.task.management.system.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping(value = "api/v1/auth",produces = MediaType.APPLICATION_JSON_VALUE)
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final UserDetailsService customUserDetailsService;
    private final AuthService authService;

    public AuthController(AuthenticationManager authenticationManager, JwtService jwtService, UserDetailsService customUserDetailsService, AuthService authService) {
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.customUserDetailsService = customUserDetailsService;
        this.authService = authService;
    }

    @PostMapping
    public ResponseEntity<LoginResponse> authenticateAndGetToken(@Valid @RequestBody LoginRequest loginRequest){

        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                loginRequest.username(),loginRequest.password()
        ));

        if(authentication.isAuthenticated()){
            String token = jwtService.generateToken(customUserDetailsService.loadUserByUsername(loginRequest.username()));
            LoginResponse loginResponse = new LoginResponse(loginRequest.username(),token);
            return ResponseEntity.ok(loginResponse);
        }
        else{
            throw new BadCredentialsException("Invalid Credentials");
        }
    }
    @PostMapping("/register")
    public ResponseEntity<UserDto> createUser( @Valid @RequestBody UserRegisterRequest userRegisterRequest) throws Exception {
        UserDto createdUser = authService.createUser(userRegisterRequest);

        URI location = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("/api/v1/auth/register")
                .buildAndExpand(createdUser.id())
                .toUri();

        return ResponseEntity.created(location).body(createdUser);
    }

}
