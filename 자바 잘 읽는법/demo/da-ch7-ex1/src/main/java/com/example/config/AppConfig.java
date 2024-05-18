package com.example.config;

import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.cloud.commons.httpclient.HttpClientConfiguration;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.cloud.openfeign.FeignAutoConfiguration;
import org.springframework.context.annotation.Configuration;

@ImportAutoConfiguration({FeignAutoConfiguration.class, HttpClientConfiguration.class})
@Configuration
@EnableFeignClients(basePackages = "com.example.proxy")
public class AppConfig {
}
