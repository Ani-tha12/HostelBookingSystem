package com.hostel.config;

import java.io.IOException;
import java.util.Collections;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.hostel.service.LogoutService;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

	private final JwtService jwtService;

	@Autowired
	private LogoutService logoutService;

	public JwtAuthenticationFilter(JwtService jwtService) {
		this.jwtService = jwtService;
	}

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {

		String authHeader = request.getHeader("Authorization");

		if (authHeader == null || !authHeader.startsWith("Bearer ")) {
			filterChain.doFilter(request, response);
			return;
		}

		try {

			String token = authHeader.substring(7);

			String email = jwtService.extractEmail(token);
			String role = jwtService.extractRole(token);

			if (email != null && SecurityContextHolder.getContext().getAuthentication() == null) {

				if (jwtService.validateToken(token, email)) {

					SimpleGrantedAuthority authority = new SimpleGrantedAuthority("ROLE_" + role);
					UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(email, null,
							Collections.singletonList(authority));

					authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

					SecurityContextHolder.getContext().setAuthentication(authToken);
				}
			}
		} catch (Exception e) {

			logger.error("JWT authentication failed: " + e.getMessage());
		}

		filterChain.doFilter(request, response);
	}
}