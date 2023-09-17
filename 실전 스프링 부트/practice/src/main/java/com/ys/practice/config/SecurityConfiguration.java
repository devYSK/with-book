package com.ys.practice.config;

import org.springframework.boot.actuate.autoconfigure.security.servlet.EndpointRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.ys.practice.handler.CustomAuthenticationFailureHandler;

import lombok.RequiredArgsConstructor;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfiguration {

	private final CustomAuthenticationFailureHandler customAuthenticationFailureHandler;
	private final TotpAuthFilter totpAuthFilter;

	@Bean
	public UserDetailsService userDetailsService() {

		UserDetails peter = User.builder()
								.username("user")
								.password(passwordEncoder().encode("pass"))
								.roles("USER")
								.build();

		final var actuatorUser = User.builder()
									 .username("admin")
									 .password(passwordEncoder().encode("admin"))
									 .roles("ENDPOINT_ADMIN")
									 .build();

		return new InMemoryUserDetailsManager(peter);
	}

	@Bean
	public SecurityFilterChain httpSecurity(HttpSecurity http) throws Exception {

		http.addFilterBefore(totpAuthFilter, UsernamePasswordAuthenticationFilter.class);
		http.authorizeRequests()
			.antMatchers("/adduser", "/login", "/login-error", "/login-verified", "/verify/email", "/setup-totp",
				"/confirm-totp")
			.permitAll()
			.antMatchers("/totp-login", "/totp-login-error")
			.hasAuthority("TOTP_AUTH_AUTHORITY")
			.requestMatchers(EndpointRequest.to("health"))
			.hasAnyRole("USER", "ENDPOINT_ADMIN")
			.requestMatchers(EndpointRequest.toAnyEndpoint())
			.hasRole("ENDPOINT_ADMIN")
			.anyRequest()
			.hasRole("USER")
			.and()
			.formLogin()
			.loginPage("/login")
			.successHandler(new DefaultAuthenticationSuccessHandler())
			.failureUrl("/login-error");

		return http.build();
	}

	@Bean
	public WebSecurityCustomizer webSecurityCustomizer() {
		// antMatchers 부분도 deprecated 되어 requestMatchers로 대체
		return (web) -> web.ignoring()
						   .antMatchers("/webjars/**", "/images/*", "/css/*", "/h2-console/**", "/login", "/register",
							   "/adduser");
	}

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}
}
