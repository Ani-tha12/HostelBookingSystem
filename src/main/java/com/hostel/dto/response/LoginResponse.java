package com.hostel.dto.response;

import com.hostel.enums.UserRole;
import java.time.LocalDateTime;

public class LoginResponse {
    
    private Long userId;
    private String name;
    private String email;
    private UserRole role;
    private String token;
    private LocalDateTime loginTime;
    
   
    public LoginResponse() {
        this.loginTime = LocalDateTime.now();
    }
    
    public LoginResponse(Long userId, String name, String email, UserRole role, String token) {
        this.userId = userId;
        this.name = name;
        this.email = email;
        this.role = role;
        this.token = token;
        this.loginTime = LocalDateTime.now();
    }
    
    
    public Long getUserId() {
        return userId;
    }
    
    public void setUserId(Long userId) {
        this.userId = userId;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getEmail() {
        return email;
    }
    
    public void setEmail(String email) {
        this.email = email;
    }
    
    public UserRole getRole() {
        return role;
    }
    
    public void setRole(UserRole role) {
        this.role = role;
    }
    
    public String getToken() {
        return token;
    }
    
    public void setToken(String token) {
        this.token = token;
    }
    
    public LocalDateTime getLoginTime() {
        return loginTime;
    }
    
    public void setLoginTime(LocalDateTime loginTime) {
        this.loginTime = loginTime;
    }
}
