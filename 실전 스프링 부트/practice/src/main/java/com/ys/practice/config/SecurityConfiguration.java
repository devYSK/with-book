package com.ys.practice.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
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

import com.ys.practice.register_user.handler.CustomAuthenticationFailureHandler;

@Configuration
@EnableWebSecurity
public class SecurityConfiguration {

	private final CustomAuthenticationFailureHandler customAuthenticationFailureHandler;

	public SecurityConfiguration(CustomAuthenticationFailureHandler customAuthenticationFailureHandler) {
		this.customAuthenticationFailureHandler = customAuthenticationFailureHandler;
	}

	@Bean
	public UserDetailsService userDetailsService() {

		UserDetails peter = User.builder()
								.username("user")
								.password(passwordEncoder().encode("pass"))
								.roles("USER")
								.build();

		return new InMemoryUserDetailsManager(peter);
	}

	@Bean
	public SecurityFilterChain httpSecurity(HttpSecurity http) throws Exception {
		// http.requiresChannel()
		// 	.anyRequest()
		// 	.requiresSecure() // 강제로 모든 요청에 https 리다이렉트
		// 	.and()
		// 	.authorizeRequests()
		// 	.antMatchers("/login")
		// 	.permitAll()
		// 	.anyRequest()
		// 	.authenticated()
		// 	.and()
		// 	.formLogin()
		// 	.loginPage("/login");

		http.requiresChannel().anyRequest().requiresSecure().and().authorizeRequests()
			.antMatchers("/adduser", "/login", "/login-error", "/login-verified", "/login-disabled", "/verify/email", "/login-locked").permitAll()
			.anyRequest().authenticated().and().formLogin().loginPage("/login").failureHandler(customAuthenticationFailureHandler);

		return http.build();
	}

	@Bean
	public WebSecurityCustomizer webSecurityCustomizer() {
		// antMatchers 부분도 deprecated 되어 requestMatchers로 대체
		return (web) -> web.ignoring()
						   .antMatchers("/webjars/**", "/images/*", "/css/*", "/h2-console/**", "/login", "/register", "/adduser");
	}

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}
}
