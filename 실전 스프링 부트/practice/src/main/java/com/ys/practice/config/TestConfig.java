package com.ys.practice.config;

import javax.annotation.PostConstruct;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;

import lombok.RequiredArgsConstructor;

@Configuration
@PropertySource("classpath:test.yml")
@RequiredArgsConstructor
public class TestConfig {

	private final Environment environment;


	@PostConstruct
	void init() {
		System.out.println(environment.getProperty("a"));
		System.out.println(environment.getProperty("b"));
	}
}
