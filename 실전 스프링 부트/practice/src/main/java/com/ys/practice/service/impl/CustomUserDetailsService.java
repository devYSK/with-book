package com.ys.practice.service.impl;

import java.util.Arrays;
import java.util.List;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.ys.practice.entity.CustomUser;
import com.ys.practice.entity.User;
import com.ys.practice.repository.UserRepository;
import com.ys.practice.service.UserService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

	private final UserRepository userRepository;

	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		User user = userRepository.findByUsername(username);

		if (user == null) {
			throw new UsernameNotFoundException(username);
		}

		SimpleGrantedAuthority simpleGrantedAuthority = null;

		if (user.isTotpEnabled()) {
			simpleGrantedAuthority = new SimpleGrantedAuthority("TOTP_AUTH_AUTHORITY");
		} else {
			simpleGrantedAuthority = new SimpleGrantedAuthority("ROLE_USER");
		}

		CustomUser customUser = new CustomUser(user.getUsername(), user.getPassword(), true, true, true, true,
			List.of(simpleGrantedAuthority));

		customUser.setTotpEnabled(user.isTotpEnabled());

		return customUser;
	}

}
