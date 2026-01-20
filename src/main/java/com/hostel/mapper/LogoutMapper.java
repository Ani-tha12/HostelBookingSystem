package com.hostel.mapper;

import com.hostel.dto.response.LogoutResponse;
import com.hostel.entity.Logout;
import org.springframework.stereotype.Component;

@Component
public class LogoutMapper {

	public LogoutResponse toResponse(Logout logout) {
		LogoutResponse response = new LogoutResponse();
		response.setSuccess(true);
		response.setMessage("Logout successful");
		response.setEmail(logout.getEmail());
		response.setLogoutTime(logout.getBlacklistedAt());

		return response;
	}
}