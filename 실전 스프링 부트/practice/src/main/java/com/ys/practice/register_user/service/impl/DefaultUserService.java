package com.ys.practice.register_user.service.impl;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.ys.practice.register_user.ApplicationUser;
import com.ys.practice.register_user.UserDto;
import com.ys.practice.register_user.repository.UserRepository;
import com.ys.practice.register_user.service.UserService;

@Service
public class DefaultUserService implements UserService {

    private final UserRepository userRepository;
    
    private final PasswordEncoder passwordEncoder;

    public DefaultUserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public ApplicationUser createUser(UserDto userDto) {
       ApplicationUser applicationUser = new ApplicationUser();
       applicationUser.setFirstName(userDto.getFirstName());
       applicationUser.setLastName(userDto.getLastName());
       applicationUser.setEmail(userDto.getEmail());
       applicationUser.setUsername(userDto.getUsername());
       applicationUser.setPassword(passwordEncoder.encode(userDto.getPassword()));

       return userRepository.save(applicationUser);
    }

    public ApplicationUser save(ApplicationUser applicationUser) {
        return userRepository.save(applicationUser);
    }

    public ApplicationUser findByUsername(String username) {
        return userRepository.findByUsername(username);
    }
}
