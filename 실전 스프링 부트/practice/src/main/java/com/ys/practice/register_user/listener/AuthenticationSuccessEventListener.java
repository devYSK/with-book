package com.ys.practice.register_user.listener;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Component;

import com.ys.practice.register_user.service.LoginAttemptService;

@Component
public class AuthenticationSuccessEventListener implements ApplicationListener<AuthenticationSuccessEvent> {
	
    private final LoginAttemptService loginAttemptService;

    public AuthenticationSuccessEventListener(LoginAttemptService loginAttemptService) {
        this.loginAttemptService = loginAttemptService;
    }

    @Override
    public void onApplicationEvent(AuthenticationSuccessEvent authenticationSuccessEvent) {
    	 User user= (User) authenticationSuccessEvent.getAuthentication().getPrincipal();
         loginAttemptService.loginSuccess(user.getUsername());
    }
}
