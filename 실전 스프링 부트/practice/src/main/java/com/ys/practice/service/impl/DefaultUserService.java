package com.ys.practice.service.impl;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.ys.practice.entity.User;
import com.ys.practice.dto.UserDto;
import com.ys.practice.repository.UserRepository;
import com.ys.practice.service.UserService;

@Service
public class DefaultUserService implements UserService {

	private final UserRepository userRepository;

	private final PasswordEncoder passwordEncoder;

	public DefaultUserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
		this.userRepository = userRepository;
		this.passwordEncoder = passwordEncoder;
	}

	public User createUser(UserDto userDto) {
		User User = new User();
		User.setFirstName(userDto.getFirstName());
		User.setLastName(userDto.getLastName());
		User.setEmail(userDto.getEmail());
		User.setUsername(userDto.getUsername());
		User.setPassword(passwordEncoder.encode(userDto.getPassword()));

		return userRepository.save(User);
	}

	public User save(User User) {
		return userRepository.save(User);
	}

	public User findByUsername(String username) {
		return userRepository.findByUsername(username);
	}
}
