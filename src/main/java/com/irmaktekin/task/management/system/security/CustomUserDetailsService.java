package com.irmaktekin.task.management.system.security;

import com.irmaktekin.task.management.system.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        Optional<com.irmaktekin.task.management.system.entity.User> user = userRepository.findByUsername(username);

        if(user.isPresent()) {
            var userObj = user.get();
            return org.springframework.security.core.userdetails.User.builder()
                    .username(userObj.getUsername())
                    .password(userObj.getPassword())
                    .roles(getRoles(userObj))
                    .build();
        }
        else{
            throw new UsernameNotFoundException(username);
        }
    }
    public String [] getRoles(com.irmaktekin.task.management.system.entity.User user){
        if(user.getRoles()==null || user.getRoles().isEmpty()){
            return new String[]{"MEMBER"};
        }
        return user.getRoles().stream()
                .map(role->role.getRoleType().name())
                .toArray(String[]::new);
    }
}
