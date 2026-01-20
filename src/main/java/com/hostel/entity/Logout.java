package com.hostel.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
public class Logout {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private String token;

	private String email;

	private LocalDateTime expiresAt;

	private LocalDateTime blacklistedAt = LocalDateTime.now();

	public Logout() {
	}

	public Logout(String token, String email, LocalDateTime expiresAt) {
		this.token = token;
		this.email = email;
		this.expiresAt = expiresAt;
		this.blacklistedAt = LocalDateTime.now();
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public LocalDateTime getExpiresAt() {
		return expiresAt;
	}

	public void setExpiresAt(LocalDateTime expiresAt) {
		this.expiresAt = expiresAt;
	}

	public LocalDateTime getBlacklistedAt() {
		return blacklistedAt;
	}

	public void setBlacklistedAt(LocalDateTime blacklistedAt) {
		this.blacklistedAt = blacklistedAt;
	}

}