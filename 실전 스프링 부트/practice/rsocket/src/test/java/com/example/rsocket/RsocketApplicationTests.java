package com.example.rsocket;

import static org.assertj.core.api.AssertionsForClassTypes.*;

import java.time.Duration;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.rsocket.server.LocalRSocketServerPort;
import org.springframework.messaging.rsocket.RSocketRequester;
import org.springframework.messaging.rsocket.RSocketStrategies;

import com.example.rsocket.model.Course;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@SpringBootTest
class RsocketApplicationTests {
	private static RSocketRequester requester;

	@BeforeAll
	static void setUpOnce(
		@Autowired RSocketRequester.Builder builder,
		@LocalRSocketServerPort Integer port,
		@Autowired RSocketStrategies rSocketStrategies) {
		// 초기 설정 메소드
		requester = builder.tcp("localhost", port);
	}

	@Test
	void testRequestResponse() {
		// 요청-응답 테스트: Spring이라는 코스를 요청하고, 응답을 검증합니다.
		Mono<Course> courseMono = requester.route("request-response")
										   .data(new Course("Spring"))
										   .retrieveMono(Course.class);

		StepVerifier.create(courseMono)
					.consumeNextWith(course -> assertThat(course.getCourseName()).isEqualTo("Your course name: Spring"))
					.verifyComplete();
	}

	@Test
	void testFireAndForget() {
		// Fire-and-Forget 테스트: 요청을 보내고, 어떠한 응답도 기다리지 않습니다.
		Mono<Course> courseMono = requester.route("fire-and-forget")
										   .data(new Course("Spring"))
										   .retrieveMono(Course.class);

		StepVerifier.create(courseMono)
					.verifyComplete();
	}

	@Test
	void testRequestStream() {
		// 요청 스트림 테스트: Spring이라는 코스에 대한 스트림 요청을 보내고, 응답 스트림을 검증합니다.
		Flux<Course> courseFlux = requester.route("request-stream")
										   .data(new Course("Spring"))
										   .retrieveFlux(Course.class);

		StepVerifier.create(courseFlux)
					.consumeNextWith(
						course -> assertThat(course.getCourseName()).isEqualTo("Your course name: Spring. Response #0"))
					.expectNextCount(0)
					.consumeNextWith(
						course -> assertThat(course.getCourseName()).isEqualTo("Your course name: Spring. Response #1"))
					.thenCancel()
					.verify();
	}

	@Test
	void testChannel() {
		// 채널 테스트: 설정을 기반으로 스트림 요청을 보내고, 응답 스트림을 검증합니다.
		Mono<Integer> setting1 = Mono.just(2)
									 .delayElement(Duration.ofSeconds(0));
		Mono<Integer> setting2 = Mono.just(1)
									 .delayElement(Duration.ofSeconds(3));

		Flux<Integer> settings = Flux.concat(setting1, setting2);

		Flux<Course> stream = requester.route("stream-stream")
									   .data(settings)
									   .retrieveFlux(Course.class);

		StepVerifier
			.create(stream)
			.consumeNextWith(course -> assertThat(course.getCourseName()).isEqualTo("Spring. Response #0"))
			.consumeNextWith(course -> assertThat(course.getCourseName()).isEqualTo("Spring. Response #0"))
			.thenCancel()
			.verify();
	}

}
