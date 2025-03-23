package com.irmaktekin.task.management.system.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractAuthenticationFilterConfigurer;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final UserDetailsService userDetailsService;
    private final JwtFilter jwtFilter;

    public SecurityConfig(UserDetailsService userDetailsService, JwtFilter jwtFilter) {
        this.userDetailsService = userDetailsService;
        this.jwtFilter = jwtFilter;
    }

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
    @Bean
    public AuthenticationManager authenticationManager(HttpSecurity http) throws Exception {
        AuthenticationManagerBuilder authenticationManagerBuilder =
                http.getSharedObject(AuthenticationManagerBuilder.class);
        authenticationManagerBuilder.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder());
        return authenticationManagerBuilder.build();
    }
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http.csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/v1/auth/**","/swagger-ui/**","/v3/api-docs/**").permitAll()
                        .requestMatchers("/api/v1/tasks/{taskId}/assignee/{assigneeId}").hasAnyRole("PROJECT_MANAGER","TEAM_LEADER")
                        .requestMatchers(HttpMethod.GET,"/api/v1/tasks/{taskId}/status").hasAnyRole("PROJECT_MANAGER","TEAM_LEADER")
                        .requestMatchers(HttpMethod.PATCH,"/api/v1/tasks/{taskId}/details").hasAnyRole("PROJECT_MANAGER","TEAM_LEADER")
                        .requestMatchers(HttpMethod.PUT,"/api/v1/tasks/{taskId}/status").hasAnyRole("PROJECT_MANAGER","TEAM_LEADER","MEMBER")
                        .requestMatchers(HttpMethod.PUT,"/api/v1/tasks/{taskId}/priority").hasAnyRole("PROJECT_MANAGER","TEAM_LEADER")
                        .requestMatchers( "/api/v1/projects/**").hasRole("PROJECT_MANAGER")
                        .anyRequest().authenticated()
        )
                .formLogin(AbstractAuthenticationFilterConfigurer::disable)
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

}