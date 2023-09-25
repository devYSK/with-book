package com.apress.springrecipes.social;

import org.springframework.social.UserIdSource;

public class StaticUserIdSource implements UserIdSource {

    private static final String DEFAULT_USERID = "anonymous";

    private String userId = DEFAULT_USERID;

    @Override
    public String getUserId() {
        return this.userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
