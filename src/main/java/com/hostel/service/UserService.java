package com.hostel.service;

import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hostel.dto.request.LoginRequest;
import com.hostel.dto.request.UserRequest;
import com.hostel.dto.response.LoginResponse;
import com.hostel.dto.response.UserResponse;
import com.hostel.entity.User;
import com.hostel.enums.UserRole;
import com.hostel.enums.UserStatus;
import com.hostel.exception.BadRequestException;
import com.hostel.exception.ResourceNotFoundException;
import com.hostel.exception.UnauthorizedException;
import com.hostel.config.JwtService;
import com.hostel.mapper.UserMapper;
import com.hostel.repository.UserRepository;

import jakarta.validation.Valid;

@Service
@Transactional
public class UserService {
    
    
    private static final Logger logger = LoggerFactory.getLogger(UserService.class);
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private UserMapper userMapper;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @Autowired
    private JwtService jwtService;
    
   
    public UserResponse registerUser(UserRequest request) {
        logger.info("Attempting to register user with email: {}", request.getEmail());
        
        try {
            
            if (userRepository.existsByEmail(request.getEmail())) {
                logger.warn("Registration failed: Email already exists - {}", request.getEmail());
                throw new BadRequestException("Email already exists");
            }
            
            
            User user = userMapper.toEntity(request);
            
          
            user.setPassword(passwordEncoder.encode(request.getPassword()));
            logger.debug("Password encoded for user: {}", request.getEmail());
            
          
            if (request.getRole() == UserRole.OWNER) {
                user.setStatus(UserStatus.PENDING);
                logger.info("Owner registration - Status set to PENDING for: {}", request.getEmail());
            } else {
                user.setStatus(UserStatus.ACTIVE);
                logger.info("User registration - Status set to ACTIVE for: {}", request.getEmail());
            }
            
           
            User savedUser = userRepository.save(user);
            logger.info("User registered successfully with ID: {} and email: {}", 
                       savedUser.getUserId(), savedUser.getEmail());
            
            
            return userMapper.toResponse(savedUser);
            
        } catch (BadRequestException e) {
            logger.error("User registration failed for email: {} - {}", request.getEmail(), e.getMessage());
            throw e;
        } catch (Exception e) {
            logger.error("Unexpected error during user registration for email: {}", request.getEmail(), e);
            throw new RuntimeException("Registration failed", e);
        }
    }
    
    public LoginResponse login(LoginRequest request) {
        logger.info("Login attempt for email: {}", request.getEmail());
        
        try {
          
            User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> {
                    logger.warn("Login failed: User not found with email - {}", request.getEmail());
                    return new UnauthorizedException("Invalid email or password");
                });
          
            if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
                logger.warn("Login failed: Invalid password for email - {}", request.getEmail());
                throw new UnauthorizedException("Invalid email or password");
            }
            
          
            if (user.getStatus() != UserStatus.ACTIVE && user.getStatus() != UserStatus.APPROVED) {
                logger.warn("Login failed: Account not active for email: {} - Status: {}", 
                           request.getEmail(), user.getStatus());
                throw new UnauthorizedException("Account is not active. Status: " + user.getStatus());
            }
            
         
            String token = jwtService.generateToken(user.getEmail(), user.getRole().name());
            logger.info("Login successful for user: {} (ID: {}) with role: {}", 
                       user.getEmail(), user.getUserId(), user.getRole());
            
           
            return new LoginResponse(
                user.getUserId(),
                user.getName(),
                user.getEmail(),
                user.getRole(),
                token
            );
            
        } catch (UnauthorizedException e) {
            logger.error("Login failed for email: {} - {}", request.getEmail(), e.getMessage());
            throw e;
        } catch (Exception e) {
            logger.error("Unexpected error during login for email: {}", request.getEmail(), e);
            throw new RuntimeException("Login failed", e);
        }
    }
    
  
    public UserResponse getUserById(Long userId) {
        logger.debug("Fetching user with ID: {}", userId);
        
        User user = userRepository.findById(userId)
            .orElseThrow(() -> {
                logger.error("User not found with ID: {}", userId);
                return new ResourceNotFoundException("User", "userId", userId);
            });
        
        logger.info("User retrieved successfully: {} (ID: {})", user.getEmail(), userId);
        return userMapper.toResponse(user);
    }
    
    
    public List<UserResponse> getAllUsers() {
        logger.info("Fetching all users");
        
        List<UserResponse> users = userRepository.findAll().stream()
            .map(userMapper::toResponse)
            .collect(Collectors.toList());
        
        logger.info("Retrieved {} users", users.size());
        return users;
    }
    
   
    public UserResponse approveOwner(Long userId) {
        logger.info("Attempting to approve owner with ID: {}", userId);
        
        User user = userRepository.findById(userId)
            .orElseThrow(() -> {
                logger.error("Owner approval failed: User not found with ID: {}", userId);
                return new ResourceNotFoundException("User", "userId", userId);
            });
        
        if (user.getRole() != UserRole.OWNER) {
            logger.warn("Owner approval failed: User is not an owner - ID: {}, Role: {}", 
                       userId, user.getRole());
            throw new BadRequestException("User is not an owner");
        }
        
        user.setStatus(UserStatus.APPROVED);
        User updatedUser = userRepository.save(user);
        
        logger.info("Owner approved successfully - ID: {}, Email: {}", userId, user.getEmail());
        return userMapper.toResponse(updatedUser);
    }
    
    
    public void deleteUser(Long userId) {
        logger.info("Attempting to delete user with ID: {}", userId);
        
        User user = userRepository.findById(userId)
            .orElseThrow(() -> {
                logger.error("Delete failed: User not found with ID: {}", userId);
                return new ResourceNotFoundException("User", "userId", userId);
            });
        
   
        if (user.getBookings() != null && !user.getBookings().isEmpty()) {
            logger.warn("Delete failed: User has {} active bookings - ID: {}", 
                       user.getBookings().size(), userId);
            throw new BadRequestException("Cannot delete user with active bookings");
        }
        
        userRepository.delete(user);
        logger.info("User deleted successfully - ID: {}, Email: {}", userId, user.getEmail());
    }
    
   
    public void changePassword(Long userId, String currentPassword, String newPassword) {
        logger.info("Attempting to change password for user ID: {}", userId);
        
        User user = userRepository.findById(userId)
            .orElseThrow(() -> {
                logger.error("Password change failed: User not found with ID: {}", userId);
                return new ResourceNotFoundException("User", "userId", userId);
            });
        
        
        if (!passwordEncoder.matches(currentPassword, user.getPassword())) {
            logger.warn("Password change failed: Incorrect current password for user ID: {}", userId);
            throw new UnauthorizedException("Current password is incorrect");
        }
        
        
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
        
        logger.info("Password changed successfully for user ID: {}", userId);
    }

	public List<UserResponse> getUsersByRole(UserRole role) {
		
		    logger.info("Fetching users with role: {}", role);

		    List<UserResponse> users = userRepository.findAll().stream()
		            .filter(user -> user.getRole() == role)
		            .map(userMapper::toResponse)
		            .collect(Collectors.toList());

		    logger.info("Retrieved {} users with role {}", users.size(), role);
		    return users;
		}

	public UserResponse updateUser(Long userId, @Valid UserRequest request) {
		
		    logger.info("Attempting to update user with ID: {}", userId);

		    User user = userRepository.findById(userId)
		            .orElseThrow(() -> {
		                logger.error("Update failed: User not found with ID: {}", userId);
		                return new ResourceNotFoundException("User", "userId", userId);
		            });

		    
		    user.setName(request.getName());
		    user.setEmail(request.getEmail());
		    user.setRole(request.getRole());

		   
		    if (request.getPassword() != null && !request.getPassword().isEmpty()) {
		        user.setPassword(passwordEncoder.encode(request.getPassword()));
		        logger.debug("Password updated for user ID: {}", userId);
		    }

		    User updatedUser = userRepository.save(user);
		    logger.info("User updated successfully - ID: {}, Email: {}", userId, updatedUser.getEmail());

		    return userMapper.toResponse(updatedUser);
		}

	public List<UserResponse> getPendingOwners() {
		
		    logger.info("Fetching all pending owners");

		    List<UserResponse> pendingOwners = userRepository.findAll().stream()
		            .filter(user -> user.getRole() == UserRole.OWNER && user.getStatus() == UserStatus.PENDING)
		            .map(userMapper::toResponse)
		            .collect(Collectors.toList());

		    logger.info("Retrieved {} pending owners", pendingOwners.size());
		    return pendingOwners;
		}

	public UserResponse rejectOwner(Long userId, String reason) {
		
		    logger.info("Attempting to reject owner with ID: {} for reason: {}", userId, reason);

		    User user = userRepository.findById(userId)
		            .orElseThrow(() -> {
		                logger.error("Reject failed: User not found with ID: {}", userId);
		                return new ResourceNotFoundException("User", "userId", userId);
		            });

		    if (user.getRole() != UserRole.OWNER) {
		        logger.warn("Reject failed: User is not an owner - ID: {}, Role: {}", userId, user.getRole());
		        throw new BadRequestException("User is not an owner");
		    }

		   
		    user.setStatus(UserStatus.REJECTED);

		   
		    User updatedUser = userRepository.save(user);
		    logger.info("Owner rejected successfully - ID: {}, Email: {}", userId, updatedUser.getEmail());

		    return userMapper.toResponse(updatedUser);
		}

	public UserResponse updateUserRole(Long userId, UserRole newRole) {
		
		
		    logger.info("Attempting to update role for user ID: {} to {}", userId, newRole);

		    User user = userRepository.findById(userId)
		            .orElseThrow(() -> {
		                logger.error("Role update failed: User not found with ID: {}", userId);
		                return new ResourceNotFoundException("User", "userId", userId);
		            });

		    user.setRole(newRole);

		  
		    if (newRole == UserRole.OWNER) {
		        user.setStatus(UserStatus.PENDING);
		        logger.info("Role updated to OWNER - Status set to PENDING for user ID: {}", userId);
		    } else {
		        user.setStatus(UserStatus.ACTIVE);
		        logger.info("Role updated to {} - Status set to ACTIVE for user ID: {}", newRole, userId);
		    }

		    User updatedUser = userRepository.save(user);
		    logger.info("User role updated successfully - ID: {}, New Role: {}", userId, newRole);

		    return userMapper.toResponse(updatedUser);
		}
	
	}
	
	
