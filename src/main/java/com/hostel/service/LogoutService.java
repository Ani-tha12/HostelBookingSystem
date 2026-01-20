package com.hostel.service;

import com.hostel.config.JwtService;
import com.hostel.dto.request.LogoutRequest;
import com.hostel.dto.response.LogoutResponse;
import com.hostel.entity.Logout;
import com.hostel.exception.BadRequestException;
import com.hostel.mapper.LogoutMapper;
import com.hostel.repository.LogoutRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.hostel.dto.request.LogoutRequest;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

@Service
@Transactional
public class LogoutService {

	private static final Logger logger = LoggerFactory.getLogger(LogoutService.class);

	@Autowired
	private LogoutRepository logoutRepository;

	@Autowired
	private JwtService jwtService;

	@Autowired
	private LogoutMapper logoutMapper;

	public LogoutResponse logout(LogoutRequest request) {
		logger.info("Processing logout request");

		String token = request.getToken();

		try {
			if (jwtService.isTokenExpired(token)) {
				logger.error("Token is already expired");
				throw new BadRequestException("Token is expired");
			}
		} catch (Exception e) {
			logger.error("Invalid token provided for logout");
			throw new BadRequestException("Invalid token");
		}

		if (logoutRepository.existsByToken(token)) {
			logger.warn("Token already blacklisted (user already logged out)");
			throw new BadRequestException("User already logged out");
		}

		String email = jwtService.extractEmail(token);

		Date expirationDate = jwtService.extractExpiration(token);
		LocalDateTime expiresAt = expirationDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();

		Logout blacklistedToken = new Logout(token, email, expiresAt);
		Logout savedToken = logoutRepository.save(blacklistedToken);

		logger.info("User logged out successfully - Email: {}", email);

		return logoutMapper.toResponse(savedToken);
	}

	public boolean isTokenBlacklisted(String token) {
		return logoutRepository.existsByToken(token);
	}

	public void cleanupExpiredTokens() {
		logger.info("Starting cleanup of expired tokens");
		int deletedCount = logoutRepository.deleteExpiredTokens(LocalDateTime.now());
		logger.info("Expired tokens cleanup completed - Deleted {} tokens", deletedCount);
	}
}