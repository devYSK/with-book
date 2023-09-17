package com.ys.practice.controller;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import com.ys.practice.entity.User;
import com.ys.practice.dto.UserDto;
import com.ys.practice.event.UserRegistrationEvent;
import com.ys.practice.service.GoogleRecaptchaService;
import com.ys.practice.dto.RecaptchaDto;
import com.ys.practice.service.UserService;

@Controller
public class RegistrationController {

	private final UserService userService;
	private final GoogleRecaptchaService captchaService;

	private final ApplicationEventPublisher eventPublisher;

	@Value("${app.email.verification:N}")
	private String emailVerification;

	public RegistrationController(UserService userService, GoogleRecaptchaService captchaService,
		ApplicationEventPublisher eventPublisher) {
		this.userService = userService;
		this.captchaService = captchaService;
		this.eventPublisher = eventPublisher;
	}

	@GetMapping("/adduser")
	public String register(Model model) {
		model.addAttribute("user", new UserDto());
		return "add-user";
	}

	@PostMapping("/adduser")
	public String register(@Valid @ModelAttribute("user") UserDto userDto, HttpServletRequest httpServletRequest,
		BindingResult result) {
		if (result.hasErrors()) {
			return "add-user";
		}
		String response = httpServletRequest.getParameter("g-recaptcha-response");

		if (response == null) {
			return "add-user";
		}
		String ip = httpServletRequest.getRemoteAddr();
		RecaptchaDto recaptchaDto = captchaService.verify(ip, response);
		if (!recaptchaDto.isSuccess()) {
			return "redirect:adduser?incorrectCaptcha";
		}

		User User = userService.createUser(userDto);
		if ("Y".equalsIgnoreCase(emailVerification)) {
			eventPublisher.publishEvent(new UserRegistrationEvent(User));
			return "redirect:adduser?validate";
		}
		return "redirect:adduser?success";

	}

}
