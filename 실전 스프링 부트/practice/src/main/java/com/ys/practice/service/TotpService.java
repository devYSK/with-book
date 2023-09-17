package com.ys.practice.service;

import javax.transaction.Transactional;

import org.springframework.stereotype.Service;

import com.warrenstrange.googleauth.GoogleAuthenticator;
import com.warrenstrange.googleauth.GoogleAuthenticatorKey;
import com.warrenstrange.googleauth.GoogleAuthenticatorQRGenerator;
import com.ys.practice.config.InvalidVerificationCode;
import com.ys.practice.entity.TotpDetails;
import com.ys.practice.entity.User;
import com.ys.practice.repository.TotpRepository;
import com.ys.practice.repository.UserRepository;

@Service
public class TotpService {

	private final GoogleAuthenticator googleAuth = new GoogleAuthenticator();
	private final TotpRepository totpRepository;
	private final UserRepository userRepository;
	private static final String ISSUER = "CourseTracker";

	public TotpService(TotpRepository totpRepository, UserRepository userRepository) {
		this.totpRepository = totpRepository;
		this.userRepository = userRepository;
	}

	@Transactional
	public String generateAuthenticationQrUrl(String username) {
		GoogleAuthenticatorKey authenticationKey = googleAuth.createCredentials();
		String secret = authenticationKey.getKey();
		totpRepository.deleteByUsername(username);
		totpRepository.save(new TotpDetails(username, secret));
		return GoogleAuthenticatorQRGenerator.getOtpAuthURL(ISSUER, username, authenticationKey);
	}

	public boolean isTotpEnabled(String userName) {
		return userRepository.findByUsername(userName)
							 .isTotpEnabled();
	}

	public void enableTotpForUser(String username, int code) {
		if (!verifyCode(username, code)) {
			throw new InvalidVerificationCode("Invalid verification code");
		}

		User user = userRepository.findByUsername(username);
		user.setTotpEnabled(true);
		userRepository.save(user);
	}

	public boolean verifyCode(String userName, int verificationCode) {
		TotpDetails totpDetails = totpRepository.findByUsername(userName);
		return googleAuth.authorize(totpDetails.getSecret(), verificationCode);
	}
}
