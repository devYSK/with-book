package com.ys.practice.register_user.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.ys.practice.register_user.EmailVerification;

@Repository
public interface EmailVerificationRepository extends CrudRepository<EmailVerification, String> {

    EmailVerification findByUsername(String userName);
    boolean existsByUsername(String userName);
}
