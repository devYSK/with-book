package com.ys.practice.register_user.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.ys.practice.register_user.ApplicationUser;

@Repository
public interface UserRepository extends CrudRepository<ApplicationUser, Long> {

    ApplicationUser findByUsername(String username);
}
