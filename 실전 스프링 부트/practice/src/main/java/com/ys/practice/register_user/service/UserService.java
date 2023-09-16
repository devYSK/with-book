package com.ys.practice.register_user.service;

import com.ys.practice.register_user.ApplicationUser;
import com.ys.practice.register_user.UserDto;

public interface UserService {
    ApplicationUser createUser(UserDto userDto);
    ApplicationUser findByUsername(String username);

    ApplicationUser save(ApplicationUser applicationUser);
}
