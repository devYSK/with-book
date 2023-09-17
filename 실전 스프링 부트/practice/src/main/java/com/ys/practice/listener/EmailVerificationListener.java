package com.ys.practice.listener;

import java.util.Base64;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.ys.practice.entity.User;
import com.ys.practice.event.UserRegistrationEvent;
import com.ys.practice.service.EmailVerificationService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class EmailVerificationListener {

	private final JavaMailSender mailSender;
	private final EmailVerificationService verificationService;

	@Autowired
	public EmailVerificationListener(JavaMailSender mailSender, EmailVerificationService verificationService) {
		this.mailSender = mailSender;
		this.verificationService = verificationService;
	}

	@Async
	@EventListener(UserRegistrationEvent.class)
	public void onApplicationEvent(UserRegistrationEvent event) {
		log.info("on event! ");

		User user = event.getUser();
		String username = user.getUsername();
		String verificationId = verificationService.generateVerification(username);
		String email = event.getUser()
							.getEmail();

		SimpleMailMessage message = new SimpleMailMessage();
		message.setSubject("Course Tracker Account Verification");
		message.setText(getText(user, verificationId));
		message.setTo(email);
		mailSender.send(message);
	}

	private String getText(User user, String verificationId) {
		String encodedVerificationId = new String(Base64.getEncoder()
														.encode(verificationId.getBytes()));
		StringBuffer buffer = new StringBuffer();
		buffer.append("Dear ")
			  .append(user.getFirstName())
			  .append(" ")
			  .append(user.getLastName())
			  .append(",")
			  .append(System.lineSeparator())
			  .append(System.lineSeparator());
		buffer.append("Your account has been successfully created in the Course Tracker application. ");

		buffer.append("Activate your account by clicking the following link: http://localhost:8080/verify/email?id=")
			  .append(encodedVerificationId);
		buffer.append(System.lineSeparator())
			  .append(System.lineSeparator());
		buffer.append("Regards,")
			  .append(System.lineSeparator())
			  .append("Course Tracker Team");
		return buffer.toString();
	}
}
