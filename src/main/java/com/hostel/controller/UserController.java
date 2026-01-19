package com.hostel.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.hostel.dto.request.LoginRequest;
import com.hostel.dto.request.UserRequest;
import com.hostel.dto.response.ApiResponse;
import com.hostel.dto.response.LoginResponse;
import com.hostel.dto.response.UserResponse;
import com.hostel.enums.UserRole;
import com.hostel.service.UserService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/users")
@CrossOrigin(origins = "*")
public class UserController {
    
    @Autowired
    private UserService userService;
    
    
    @PostMapping("/register")
    public ResponseEntity<ApiResponse<UserResponse>> registerUser(
            @Valid @RequestBody UserRequest request) {
        UserResponse response = userService.registerUser(request);
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(ApiResponse.success("User registered successfully", response));
    }
    
    
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginResponse>> login(
            @Valid @RequestBody LoginRequest request) {
        LoginResponse response = userService.login(request);
        return ResponseEntity.ok(ApiResponse.success("Login successful", response));
    }
    
   
    @GetMapping("/{userId}")
    public ResponseEntity<ApiResponse<UserResponse>> getUserById(@PathVariable Long userId) {
        UserResponse response = userService.getUserById(userId);
        return ResponseEntity.ok(ApiResponse.success("User retrieved successfully", response));
    }
    
    
    @GetMapping
    public ResponseEntity<ApiResponse<List<UserResponse>>> getAllUsers() {
        List<UserResponse> users = userService.getAllUsers();
        return ResponseEntity.ok(ApiResponse.success("Users retrieved successfully", users));
    }
    
    
    @GetMapping("/role/{role}")
    public ResponseEntity<ApiResponse<List<UserResponse>>> getUsersByRole(
            @PathVariable UserRole role) {
        List<UserResponse> users = userService.getUsersByRole(role);
        return ResponseEntity.ok(ApiResponse.success("Users retrieved successfully", users));
    }
    
   
    @PutMapping("/{userId}")
    public ResponseEntity<ApiResponse<UserResponse>> updateUser(
            @PathVariable Long userId,
            @Valid @RequestBody UserRequest request) {
        UserResponse response = userService.updateUser(userId, request);
        return ResponseEntity.ok(ApiResponse.success("User updated successfully", response));
    }
    
    
    @PutMapping("/{userId}/change-password")
    public ResponseEntity<ApiResponse<String>> changePassword(
            @PathVariable Long userId,
            @RequestParam String currentPassword,
            @RequestParam String newPassword) {
        userService.changePassword(userId, currentPassword, newPassword);
        return ResponseEntity.ok(ApiResponse.success("Password changed successfully", null));
    }
    
  
    @DeleteMapping("/{userId}")
    public ResponseEntity<ApiResponse<String>> deleteUser(@PathVariable Long userId) {
        userService.deleteUser(userId);
        return ResponseEntity.ok(ApiResponse.success("User deleted successfully", null));
    }
    
   
    @GetMapping("/admin/owners/pending")
    public ResponseEntity<ApiResponse<List<UserResponse>>> getPendingOwners() {
        List<UserResponse> pendingOwners = userService.getPendingOwners();
        return ResponseEntity.ok(
            ApiResponse.success("Pending owners retrieved successfully", pendingOwners)
        );
    }
    
   
    @PutMapping("/admin/owners/{userId}/approve")
    public ResponseEntity<ApiResponse<UserResponse>> approveOwner(@PathVariable Long userId) {
        UserResponse response = userService.approveOwner(userId);
        return ResponseEntity.ok(ApiResponse.success("Owner approved successfully", response));
    }
    
   
    @PutMapping("/admin/owners/{userId}/reject")
    public ResponseEntity<ApiResponse<UserResponse>> rejectOwner(
            @PathVariable Long userId,
            @RequestParam String reason) {
        UserResponse response = userService.rejectOwner(userId, reason);
        return ResponseEntity.ok(ApiResponse.success("Owner rejected", response));
    }
    
    
    @PutMapping("/admin/{userId}/role")
    public ResponseEntity<ApiResponse<UserResponse>> updateUserRole(
            @PathVariable Long userId,
            @RequestParam UserRole newRole) {
        UserResponse response = userService.updateUserRole(userId, newRole);
        return ResponseEntity.ok(ApiResponse.success("User role updated successfully", response));
    }
}