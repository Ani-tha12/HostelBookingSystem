package com.hostel.controller;

import com.hostel.dto.request.LogoutRequest;
import com.hostel.dto.response.LogoutResponse;
import com.hostel.service.LogoutService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
public class LogoutController {

	@Autowired
	private LogoutService logoutService;

	@PostMapping("/logout")
	public ResponseEntity<LogoutResponse> logout(@Valid @RequestBody LogoutRequest request) {
		LogoutResponse response = logoutService.logout(request);
		return ResponseEntity.ok(response);
	}
}