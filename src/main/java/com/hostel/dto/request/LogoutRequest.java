package com.hostel.dto.request;

import jakarta.validation.constraints.NotBlank;

public class LogoutRequest {

	@NotBlank(message = "Token is required")
	private String token;

	public LogoutRequest() {
	}

	public LogoutRequest(String token) {
		this.token = token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public String getToken() {

		return token;
	}
}