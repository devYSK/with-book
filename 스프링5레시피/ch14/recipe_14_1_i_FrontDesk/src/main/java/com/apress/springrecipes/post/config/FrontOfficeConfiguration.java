package com.apress.springrecipes.post.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.apress.springrecipes.post.FrontDeskImpl;

@Configuration
public class FrontOfficeConfiguration {

    @Bean
    public FrontDeskImpl frontDesk() {
        return new FrontDeskImpl();
    }
}
