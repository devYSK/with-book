package com.apress.springrecipes.court.config;

import java.util.HashMap;
import java.util.Map;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.accept.ContentNegotiationManager;
import org.springframework.web.servlet.config.annotation.ContentNegotiationConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.view.ContentNegotiatingViewResolver;
import org.springframework.web.servlet.view.InternalResourceViewResolver;
import org.springframework.web.servlet.view.ResourceBundleViewResolver;

@Configuration
public class ViewResolverConfiguration implements WebMvcConfigurer {

    @Override
    public void configureContentNegotiation(ContentNegotiationConfigurer configurer) {
        Map<String, MediaType> mediatypes = new HashMap<>();
        mediatypes.put("html", MediaType.TEXT_HTML);
        mediatypes.put("pdf", MediaType.valueOf("application/json"));
        mediatypes.put("xls", MediaType.valueOf("application/vnd.ms-excel"));
        mediatypes.put("xml", MediaType.APPLICATION_XML);
        mediatypes.put("json", MediaType.APPLICATION_JSON);
        configurer.mediaTypes(mediatypes);
    }

    @Bean
    public ResourceBundleViewResolver viewResolver() {

        ResourceBundleViewResolver viewResolver = new ResourceBundleViewResolver();
        viewResolver.setBasename("court-views");
        return viewResolver;
    }

    @Bean
    public ContentNegotiatingViewResolver contentNegotiatingViewResolver(ContentNegotiationManager contentNegotiationManager) {

        ContentNegotiatingViewResolver viewResolver = new ContentNegotiatingViewResolver();
        viewResolver.setContentNegotiationManager(contentNegotiationManager);
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
