package com.ys.practice.event;

import org.springframework.context.ApplicationEvent;

import com.ys.practice.entity.User;

public class UserRegistrationEvent extends ApplicationEvent {

	private static final long serialVersionUID = -2685172945219633123L;
	
	private User User;

    public UserRegistrationEvent(User User) {
        super(User);
        this.User = User;
    }

    public User getUser() {
        return User;
    }
}
