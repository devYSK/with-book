package com.ys.practice.config;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.GenericFilterBean;

import com.ys.practice.service.TotpService;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class TotpAuthFilter extends GenericFilterBean {

	private final TotpService totpService;
	private static final String ON_SUCCESS_URL = "/index";
	private static final String ON_FAILURE_URL = "/totp-login-error";

	private final RedirectStrategy redirectStrategy = new DefaultRedirectStrategy();

	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws
		IOException,
		ServletException {

		Authentication authentication = SecurityContextHolder.getContext()
															 .getAuthentication();

		String code = request.getParameter("totp_code");

		if (!requiresTotpAuthentication(authentication) || code == null) {
			chain.doFilter(request, response);
			return;
		}

		if (totpService.verifyCode(authentication.getName(), Integer.parseInt(code))) {
			Set<String> authorities = AuthorityUtils.authorityListToSet(authentication.getAuthorities());
			authorities.remove("TOTP_AUTH_AUTHORITY");
			authorities.add("ROLE_USER");

			authentication = new UsernamePasswordAuthenticationToken(authentication.getPrincipal(),
				authentication.getCredentials(), buildAuthorities(authorities));

			SecurityContextHolder.getContext()
								 .setAuthentication(authentication);

			redirectStrategy.sendRedirect((HttpServletRequest)request, (HttpServletResponse)response, ON_SUCCESS_URL);
		} else {
			redirectStrategy.sendRedirect((HttpServletRequest)request, (HttpServletResponse)response, ON_FAILURE_URL);
		}

	}

	private boolean requiresTotpAuthentication(Authentication authentication) {
		if (authentication == null) {
			return false;
		}

		Set<String> authorities = AuthorityUtils.authorityListToSet(authentication.getAuthorities());
		boolean hasTotpAuthority = authorities.contains("TOTP_AUTH_AUTHORITY");

		return hasTotpAuthority && authentication.isAuthenticated();
	}

	private List<GrantedAuthority> buildAuthorities(Collection<String> authorities) {
		List<GrantedAuthority> authList = new ArrayList<>(1);

		for (String authority : authorities) {
			authList.add(new SimpleGrantedAuthority(authority));
		}

		return authList;
	}

}
