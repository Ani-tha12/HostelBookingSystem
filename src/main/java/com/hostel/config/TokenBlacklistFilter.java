package com.hostel.config;

import com.hostel.service.LogoutService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class TokenBlacklistFilter extends OncePerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(TokenBlacklistFilter.class);

    @Autowired
    private LogoutService logoutService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String authHeader = request.getHeader("Authorization");

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);

            
            if (logoutService.isTokenBlacklisted(token)) {
                logger.warn("Attempted to use blacklisted token (user logged out)");
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.setContentType("application/json");
                response.getWriter().write("{\"success\":false,\"message\":\"Token has been invalidated. Please login again.\"}");
                return;
            }
        }

        filterChain.doFilter(request, response);
    }
}