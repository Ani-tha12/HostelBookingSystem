package com.hostel.dto.response;

import com.hostel.enums.UserRole;
import com.hostel.enums.UserStatus;
import java.time.LocalDateTime;

public class UserResponse {
    
    private Long userId;
    private String name;
    private String email;
    private String phone;
    private UserRole role;
    private UserStatus status;
    private LocalDateTime registrationDate;
    private String businessLicense;
    
    // Constructors
    public UserResponse() {}
    
    public UserResponse(Long userId, String name, String email, String phone, 
                       UserRole role, UserStatus status, LocalDateTime registrationDate) {
        this.userId = userId;
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.role = role;
        this.status = status;
        this.registrationDate = registrationDate;
    }
    
    // Getters and Setters
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
    
    public String getPhone() {
        return phone;
    }
    
    public void setPhone(String phone) {
        this.phone = phone;
    }
    
    public UserRole getRole() {
        return role;
    }
    
    public void setRole(UserRole role) {
        this.role = role;
    }
    
    public UserStatus getStatus() {
        return status;
    }
    
    public void setStatus(UserStatus status) {
        this.status = status;
    }
    
    public LocalDateTime getRegistrationDate() {
        return registrationDate;
    }
    
    public void setRegistrationDate(LocalDateTime registrationDate) {
        this.registrationDate = registrationDate;
    }
    
    public String getBusinessLicense() {
        return businessLicense;
    }
    
    public void setBusinessLicense(String businessLicense) {
        this.businessLicense = businessLicense;
    }
}
