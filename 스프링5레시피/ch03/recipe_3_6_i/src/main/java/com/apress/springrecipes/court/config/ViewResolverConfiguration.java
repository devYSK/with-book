package com.apress.springrecipes.court.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ResourceLoader;
import org.springframework.web.servlet.view.InternalResourceViewResolver;
import org.springframework.web.servlet.view.XmlViewResolver;

@Configuration
public class ViewResolverConfiguration {


    private final ResourceLoader resourceLoader;

    public ViewResolverConfiguration(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }

    @Bean
    public XmlViewResolver viewResolver() {

        XmlViewResolver viewResolver = new XmlViewResolver();
        viewResolver.setLocation(resourceLoader.getResource("/WEB-INF/court-views.xml"));
        return viewResolver;
    }

    @Bean
    public InternalResourceViewResolver internalResourceViewResolver() {

        InternalResourceViewResolver viewResolver = new InternalResourceViewResolver();
        viewResolver.setPrefix("/WEB-INF/jsp/");
        viewResolver.setSuffix(".jsp");
        return viewResolver;
    }
}
