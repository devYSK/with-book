package com.ys.practice.register_user.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ys.practice.register_user.EmailVerification;
import com.ys.practice.register_user.repository.EmailVerificationRepository;

@Service
public class EmailVerificationService {

    private final EmailVerificationRepository repository;

    @Autowired
    public EmailVerificationService(EmailVerificationRepository repository) {
        this.repository = repository;
    }

    public String generateVerification(String username) {
        if (!repository.existsByUsername(username)) {
            EmailVerification verification = new EmailVerification(username);
            verification = repository.save(verification);
            return verification.getVerificationId();
        }
        return getVerificationIdByUsername(username);
    }

    public String getVerificationIdByUsername(String username) {
        EmailVerification verification = repository.findByUsername(username);
        if(verification != null) {
            return verification.getVerificationId();
        }
        return null;
    }

    public String getUsernameForVerificationId(String verificationId) {
        Optional<EmailVerification> verification = repository.findById(verificationId);
        if(verification.isPresent()) {
            return verification.get().getUsername();
        }
        return null;
    }
}
