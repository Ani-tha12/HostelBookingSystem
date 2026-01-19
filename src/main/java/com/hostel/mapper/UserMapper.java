package com.hostel.mapper;

import com.hostel.entity.User;
import com.hostel.dto.request.UserRequest;
import com.hostel.dto.response.UserResponse;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {
    
    
    public User toEntity(UserRequest request) {
        User user = new User();
        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setPhone(request.getPhone());
        user.setPassword(request.getPassword()); 
        user.setRole(request.getRole());
        user.setBusinessLicense(request.getBusinessLicense());
        return user;
    }
    
  
    public UserResponse toResponse(User user) {
        UserResponse response = new UserResponse();
        response.setUserId(user.getUserId());
        response.setName(user.getName());
        response.setEmail(user.getEmail());
        response.setPhone(user.getPhone());
        response.setRole(user.getRole());
        response.setStatus(user.getStatus());
        response.setRegistrationDate(user.getRegistrationDate());
        response.setBusinessLicense(user.getBusinessLicense());
        return response;
    }
    
  
    public void updateEntity(User user, UserRequest request) {
        if (request.getName() != null) {
            user.setName(request.getName());
        }
        if (request.getPhone() != null) {
            user.setPhone(request.getPhone());
        }
        if (request.getPassword() != null) {
            user.setPassword(request.getPassword());
        }
    }
}
