package com.ys.a.jms.backoffice;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class BackOfficeMain {

	public static void main(String[] args) {
		new AnnotationConfigApplicationContext(KafkaBackOfficeConfiguration.class);
	}
}
