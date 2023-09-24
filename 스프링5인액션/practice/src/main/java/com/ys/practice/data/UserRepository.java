package com.ys.practice.data;

import org.springframework.data.repository.CrudRepository;

import com.ys.practice.domain.User;

public interface UserRepository extends CrudRepository<User, Long> {
	User findByUsername(String username);
}