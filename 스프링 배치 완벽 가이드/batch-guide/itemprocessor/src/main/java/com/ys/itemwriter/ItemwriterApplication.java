package com.ys.itemwriter;

import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@EnableBatchProcessing
@SpringBootApplication
public class ItemwriterApplication {

	public static void main(String[] args) {
		SpringApplication.run(ItemwriterApplication.class, args);
	}

}
