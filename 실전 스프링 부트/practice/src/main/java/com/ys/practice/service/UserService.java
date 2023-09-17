package com.ys.practice.service;

import com.ys.practice.entity.User;
import com.ys.practice.dto.UserDto;

public interface UserService {
	User createUser(UserDto userDto);

	User findByUsername(String username);

	User save(User User);
}
