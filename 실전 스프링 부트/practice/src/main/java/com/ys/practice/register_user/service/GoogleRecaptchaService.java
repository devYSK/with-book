package com.ys.practice.register_user.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;

@Service
public class GoogleRecaptchaService {

	private static final String VERIFY_URL = "https://www.google.com/recaptcha/api/siteverify";

	private final WebClient webClient;
	private final String secretKey;

	public GoogleRecaptchaService(@Value("${captcha.secret.key}") String secretKey,
		WebClient.Builder webClientBuilder) {
		this.secretKey = secretKey;
		this.webClient = webClientBuilder.baseUrl(VERIFY_URL)
										 .build();
	}

	public RecaptchaDto verify(String ip, String recaptchaResponse) {
		Map<String, String> params = new HashMap<>();
		params.put("remoteip", ip);
		params.put("secret", secretKey);
		params.put("response", recaptchaResponse);

		Map<String, Object> body = webClient.get()
											.uri(uriBuilder -> uriBuilder
												.queryParam("remoteip", "{remoteip}")
												.queryParam("secret", "{secret}")
												.queryParam("response", "{response}")
												.build(params))
											.retrieve()
											.bodyToMono(Map.class)
											.block();

		if (body == null) {
			throw new RuntimeException("Recaptcha verification failed!");
		}

		boolean success = (Boolean)body.get("success");
		RecaptchaDto recaptchaDto = new RecaptchaDto();
		recaptchaDto.setSuccess(success);

		if (!success) {
			recaptchaDto.setErrors((List<String>)body.get("error-codes"));
		}

		return recaptchaDto;
	}
}