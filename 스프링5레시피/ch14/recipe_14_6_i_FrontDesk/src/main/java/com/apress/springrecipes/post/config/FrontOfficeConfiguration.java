package com.apress.springrecipes.post.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

import com.apress.springrecipes.post.FrontDeskImpl;

@Configuration
@EnableScheduling
public class FrontOfficeConfiguration {

    @Bean
    public FrontDeskImpl frontDesk() {
        FrontDeskImpl frontDesk = new FrontDeskImpl();
        return frontDesk;
    }

}
