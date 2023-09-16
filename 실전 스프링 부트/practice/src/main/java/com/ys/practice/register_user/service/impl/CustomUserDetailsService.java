package com.ys.practice.register_user.service.impl;

import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.ys.practice.register_user.ApplicationUser;
import com.ys.practice.register_user.service.UserService;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserService userService;

	public CustomUserDetailsService(UserService userService) {
		this.userService = userService;
	}

	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    	
    	ApplicationUser applicationUser = userService.findByUsername(username);
    	if(applicationUser == null) {
    		throw new UsernameNotFoundException("User with username "+username+" does not exists");
    	}
    	
    	return User.withUsername(username).password(applicationUser.getPassword()).roles("USER").disabled(false).build();
    	
    }
}
