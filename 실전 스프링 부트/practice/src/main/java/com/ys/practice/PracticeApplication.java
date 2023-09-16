package com.ys.practice;

import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.ys.practice.passay.Password;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@SpringBootApplication
public class PracticeApplication implements CommandLineRunner {

	public static void main(String[] args) {
		SpringApplication.run(PracticeApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {

		final var user = new User("test", "test");

		Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

		Set<ConstraintViolation<User>> violations = validator.validate(user);
		log.error("user1의 비밀번호가 비밀번호 정책을 준수하지 않습니다.");
		violations.forEach(constraintViolation -> log.error("Violation details: [{}].", constraintViolation.getMessage()));

	}


	public static class User {

		private String userName;

		@Password
		private String password;

		public User(String userName, String password) {
			this.userName = userName;
			this.password = password;
		}

		public String getUserName() {
			return userName;
		}

		public String getPassword() {
			return password;
		}

		@Override
		public String toString() {
			return "User{" +
				"userName='" + userName + '\'' +
				", password='" + password + '\'' +
				'}';
		}

	}

}
