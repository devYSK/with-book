package com.ys.practice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import reactor.core.publisher.Flux;

@SpringBootApplication
public class PracticeApplication {

	public static void main(String[] args) {
		// SpringApplication.run(PracticeApplication.class, args);

		final var flux = Flux.just(
			"Apple", "Orange", "Graph"
		);

		flux.subscribe(f -> System.out.println("good : " + f));
	}

}
