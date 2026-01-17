package com.hostel.test.service;

import com.hostel.config.JwtService;
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
import com.hostel.mapper.UserMapper;
import com.hostel.repository.UserRepository;
import com.hostel.service.UserService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("User Service Tests")
class UserServiceTest {
    
    @Mock
    private UserRepository userRepository;
    
    @Mock
    private UserMapper userMapper;
    
    @Mock
    private PasswordEncoder passwordEncoder;
    
    @Mock
    private JwtService jwtService;
    
    @InjectMocks
    private UserService userService;
    
    private User testUser;
    private User testOwner;
    private UserRequest userRequest;
    private UserResponse userResponse;
    
    @BeforeEach
    void setUp() {
        // Setup test user
        testUser = new User();
        testUser.setUserId(1L);
        testUser.setName("Suresh Kumar");
        testUser.setEmail("suresh@gmail.com");
        testUser.setPhone("9876543210");
        testUser.setPassword("encodedPassword");
        testUser.setRole(UserRole.USER);
        testUser.setStatus(UserStatus.ACTIVE);
        testUser.setBookings(new ArrayList<>());
        
        // Setup test owner
        testOwner = new User();
        testOwner.setUserId(10L);
        testOwner.setName("Raj Kumar");
        testOwner.setEmail("raj@gmail.com");
        testOwner.setPhone("9988776655");
        testOwner.setPassword("encodedPassword");
        testOwner.setRole(UserRole.OWNER);
        testOwner.setStatus(UserStatus.PENDING);
        testOwner.setBookings(new ArrayList<>());
        
        // Setup user request
        userRequest = new UserRequest();
        userRequest.setName("Suresh Kumar");
        userRequest.setEmail("suresh@gmail.com");
        userRequest.setPhone("9876543210");
        userRequest.setPassword("securePassword123");
        userRequest.setRole(UserRole.USER);
        
        // Setup user response
        userResponse = new UserResponse();
        userResponse.setUserId(1L);
        userResponse.setName("Suresh Kumar");
        userResponse.setEmail("suresh@gmail.com");
        userResponse.setRole(UserRole.USER);
        userResponse.setStatus(UserStatus.ACTIVE);
    }
    
    // ==========================================
    // SUCCESS TEST CASES
    // ==========================================
    
    @Test
    @DisplayName("SUCCESS: Register User - Should create user with ACTIVE status")
    void testRegisterUser_Success() {
        // Arrange
        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(userMapper.toEntity(any(UserRequest.class))).thenReturn(testUser);
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(testUser);
        when(userMapper.toResponse(any(User.class))).thenReturn(userResponse);
        
        // Act
        UserResponse result = userService.registerUser(userRequest);
        
        // Assert
        assertNotNull(result);
        assertEquals("suresh@gmail.com", result.getEmail());
        assertEquals(UserRole.USER, result.getRole());
        assertEquals(UserStatus.ACTIVE, result.getStatus());
        
        verify(userRepository, times(1)).existsByEmail("suresh@gmail.com");
        verify(passwordEncoder, times(1)).encode("securePassword123");
        verify(userRepository, times(1)).save(any(User.class));
        verify(userMapper, times(1)).toResponse(testUser);
    }
    
    @Test
    @DisplayName("SUCCESS: Register Owner - Should create owner with PENDING status")
    void testRegisterOwner_Success() {
        // Arrange
        UserRequest ownerRequest = new UserRequest();
        ownerRequest.setName("Raj Kumar");
        ownerRequest.setEmail("raj@gmail.com");
        ownerRequest.setPassword("ownerPass123");
        ownerRequest.setRole(UserRole.OWNER);
        
        UserResponse ownerResponse = new UserResponse();
        ownerResponse.setUserId(10L);
        ownerResponse.setEmail("raj@gmail.com");
        ownerResponse.setRole(UserRole.OWNER);
        ownerResponse.setStatus(UserStatus.PENDING);
        
        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(userMapper.toEntity(any(UserRequest.class))).thenReturn(testOwner);
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(testOwner);
        when(userMapper.toResponse(any(User.class))).thenReturn(ownerResponse);
        
        // Act
        UserResponse result = userService.registerUser(ownerRequest);
        
        // Assert
        assertNotNull(result);
        assertEquals(UserRole.OWNER, result.getRole());
        assertEquals(UserStatus.PENDING, result.getStatus());
        
        verify(userRepository, times(1)).save(any(User.class));
    }
    
    @Test
    @DisplayName("SUCCESS: Login - Should return JWT token and user details")
    void testLogin_Success() {
        // Arrange
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setEmail("suresh@gmail.com");
        loginRequest.setPassword("securePassword123");
        
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches(anyString(), anyString())).thenReturn(true);
        when(jwtService.generateToken(anyString(), anyString())).thenReturn("jwt-token-123");
        
        // Act
        LoginResponse result = userService.login(loginRequest);
        
        // Assert
        assertNotNull(result);
        assertEquals(1L, result.getUserId());
        assertEquals("Suresh Kumar", result.getName());
        assertEquals("suresh@gmail.com", result.getEmail());
        assertEquals(UserRole.USER, result.getRole());
        assertEquals("jwt-token-123", result.getToken());
        
        verify(jwtService, times(1)).generateToken("suresh@gmail.com", "USER");
    }
    
    @Test
    @DisplayName("SUCCESS: Get User By ID - Should return user response")
    void testGetUserById_Success() {
        // Arrange
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(userMapper.toResponse(any(User.class))).thenReturn(userResponse);
        
        // Act
        UserResponse result = userService.getUserById(1L);
        
        // Assert
        assertNotNull(result);
        assertEquals(1L, result.getUserId());
        assertEquals("suresh@gmail.com", result.getEmail());
        
        verify(userRepository, times(1)).findById(1L);
        verify(userMapper, times(1)).toResponse(testUser);
    }
    
    @Test
    @DisplayName("SUCCESS: Get All Users - Should return list of users")
    void testGetAllUsers_Success() {
        // Arrange
        List<User> users = Arrays.asList(testUser, testOwner);
        List<UserResponse> responses = Arrays.asList(userResponse, new UserResponse());
        
        when(userRepository.findAll()).thenReturn(users);
        when(userMapper.toResponse(any(User.class))).thenReturn(userResponse);
        
        // Act
        List<UserResponse> result = userService.getAllUsers();
        
        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        
        verify(userRepository, times(1)).findAll();
    }
    
    @Test
    @DisplayName("SUCCESS: Approve Owner - Should change status to APPROVED")
    void testApproveOwner_Success() {
        // Arrange
        UserResponse approvedResponse = new UserResponse();
        approvedResponse.setUserId(10L);
        approvedResponse.setStatus(UserStatus.APPROVED);
        
        when(userRepository.findById(10L)).thenReturn(Optional.of(testOwner));
        when(userRepository.save(any(User.class))).thenReturn(testOwner);
        when(userMapper.toResponse(any(User.class))).thenReturn(approvedResponse);
        
        // Act
        UserResponse result = userService.approveOwner(10L);
        
        // Assert
        assertNotNull(result);
        assertEquals(UserStatus.APPROVED, testOwner.getStatus());
        
        verify(userRepository, times(1)).save(testOwner);
    }
    
    @Test
    @DisplayName("SUCCESS: Get Users By Role - Should return users with specific role")
    void testGetUsersByRole_Success() {
        // Arrange
        List<User> users = Arrays.asList(testUser);
        when(userRepository.findAll()).thenReturn(users);
        when(userMapper.toResponse(any(User.class))).thenReturn(userResponse);
        
        // Act
        List<UserResponse> result = userService.getUsersByRole(UserRole.USER);
        
        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        
        verify(userRepository, times(1)).findAll();
    }
    
    @Test
    @DisplayName("SUCCESS: Update User - Should update user details")
    void testUpdateUser_Success() {
        // Arrange
        UserRequest updateRequest = new UserRequest();
        updateRequest.setName("Suresh Kumar Updated");
        updateRequest.setEmail("suresh.new@gmail.com");
        updateRequest.setRole(UserRole.USER);
        
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(userRepository.save(any(User.class))).thenReturn(testUser);
        when(userMapper.toResponse(any(User.class))).thenReturn(userResponse);
        
        // Act
        UserResponse result = userService.updateUser(1L, updateRequest);
        
        // Assert
        assertNotNull(result);
        verify(userRepository, times(1)).save(testUser);
    }
    
    @Test
    @DisplayName("SUCCESS: Get Pending Owners - Should return pending owners")
    void testGetPendingOwners_Success() {
        // Arrange
        List<User> allUsers = Arrays.asList(testUser, testOwner);
        when(userRepository.findAll()).thenReturn(allUsers);
        when(userMapper.toResponse(testOwner)).thenReturn(new UserResponse());
        
        // Act
        List<UserResponse> result = userService.getPendingOwners();
        
        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
    }
    
    @Test
    @DisplayName("SUCCESS: Reject Owner - Should set status to REJECTED")
    void testRejectOwner_Success() {
        // Arrange
        when(userRepository.findById(10L)).thenReturn(Optional.of(testOwner));
        when(userRepository.save(any(User.class))).thenReturn(testOwner);
        when(userMapper.toResponse(any(User.class))).thenReturn(new UserResponse());
        
        // Act
        UserResponse result = userService.rejectOwner(10L, "Invalid documents");
        
        // Assert
        assertNotNull(result);
        assertEquals(UserStatus.REJECTED, testOwner.getStatus());
        
        verify(userRepository, times(1)).save(testOwner);
    }
    
    @Test
    @DisplayName("SUCCESS: Update User Role - Should update role and set appropriate status")
    void testUpdateUserRole_Success() {
        // Arrange
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(userRepository.save(any(User.class))).thenReturn(testUser);
        when(userMapper.toResponse(any(User.class))).thenReturn(userResponse);
        
        // Act
        UserResponse result = userService.updateUserRole(1L, UserRole.OWNER);
        
        // Assert
        assertNotNull(result);
        assertEquals(UserRole.OWNER, testUser.getRole());
        assertEquals(UserStatus.PENDING, testUser.getStatus());
        
        verify(userRepository, times(1)).save(testUser);
    }
    
    @Test
    @DisplayName("SUCCESS: Change Password - Should update password")
    void testChangePassword_Success() {
        // Arrange
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches(anyString(), anyString())).thenReturn(true);
        when(passwordEncoder.encode(anyString())).thenReturn("newEncodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(testUser);
        
        // Act
        userService.changePassword(1L, "oldPassword", "newPassword");
        
        // Assert
        verify(passwordEncoder, times(1)).encode("newPassword");
        verify(userRepository, times(1)).save(testUser);
    }
    
    @Test
    @DisplayName("SUCCESS: Delete User - Should delete user without bookings")
    void testDeleteUser_Success() {
        // Arrange
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        doNothing().when(userRepository).delete(any(User.class));
        
        // Act
        userService.deleteUser(1L);
        
        // Assert
        verify(userRepository, times(1)).delete(testUser);
    }
    
    // ==========================================
    // FAILURE TEST CASES
    // ==========================================
    
    @Test
    @DisplayName("FAILURE: Register User - Email already exists")
    void testRegisterUser_EmailExists_ThrowsException() {
        // Arrange
        when(userRepository.existsByEmail(anyString())).thenReturn(true);
        
        // Act & Assert
        BadRequestException exception = assertThrows(BadRequestException.class, () -> {
            userService.registerUser(userRequest);
        });
        
        assertEquals("Email already exists", exception.getMessage());
        verify(userRepository, never()).save(any(User.class));
    }
    
    @Test
    @DisplayName("FAILURE: Login - User not found")
    void testLogin_UserNotFound_ThrowsException() {
        // Arrange
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setEmail("nonexistent@gmail.com");
        loginRequest.setPassword("password");
        
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());
        
        // Act & Assert
        UnauthorizedException exception = assertThrows(UnauthorizedException.class, () -> {
            userService.login(loginRequest);
        });
        
        assertEquals("Invalid email or password", exception.getMessage());
    }
    
    @Test
    @DisplayName("FAILURE: Login - Invalid password")
    void testLogin_InvalidPassword_ThrowsException() {
        // Arrange
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setEmail("suresh@gmail.com");
        loginRequest.setPassword("wrongPassword");
        
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches(anyString(), anyString())).thenReturn(false);
        
        // Act & Assert
        UnauthorizedException exception = assertThrows(UnauthorizedException.class, () -> {
            userService.login(loginRequest);
        });
        
        assertEquals("Invalid email or password", exception.getMessage());
    }
    
    @Test
    @DisplayName("FAILURE: Login - User status not active")
    void testLogin_UserNotActive_ThrowsException() {
        // Arrange
        testUser.setStatus(UserStatus.PENDING);
        
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setEmail("suresh@gmail.com");
        loginRequest.setPassword("password");
        
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches(anyString(), anyString())).thenReturn(true);
        
        // Act & Assert
        UnauthorizedException exception = assertThrows(UnauthorizedException.class, () -> {
            userService.login(loginRequest);
        });
        
        assertTrue(exception.getMessage().contains("Account is not active"));
    }
    
    @Test
    @DisplayName("FAILURE: Get User By ID - User not found")
    void testGetUserById_NotFound_ThrowsException() {
        // Arrange
        when(userRepository.findById(999L)).thenReturn(Optional.empty());
        
        // Act & Assert
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
            userService.getUserById(999L);
        });
        
        assertTrue(exception.getMessage().contains("User"));
        assertTrue(exception.getMessage().contains("999"));
    }
    
    @Test
    @DisplayName("FAILURE: Approve Owner - User not found")
    void testApproveOwner_UserNotFound_ThrowsException() {
        // Arrange
        when(userRepository.findById(999L)).thenReturn(Optional.empty());
        
        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> {
            userService.approveOwner(999L);
        });
    }
    
    @Test
    @DisplayName("FAILURE: Approve Owner - User is not owner")
    void testApproveOwner_NotOwner_ThrowsException() {
        // Arrange
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser)); // testUser is USER, not OWNER
        
        // Act & Assert
        BadRequestException exception = assertThrows(BadRequestException.class, () -> {
            userService.approveOwner(1L);
        });
        
        assertEquals("User is not an owner", exception.getMessage());
    }
    
    @Test
    @DisplayName("FAILURE: Update User - User not found")
    void testUpdateUser_NotFound_ThrowsException() {
        // Arrange
        when(userRepository.findById(999L)).thenReturn(Optional.empty());
        
        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> {
            userService.updateUser(999L, userRequest);
        });
    }
    
    @Test
    @DisplayName("FAILURE: Reject Owner - User is not owner")
    void testRejectOwner_NotOwner_ThrowsException() {
        // Arrange
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        
        // Act & Assert
        BadRequestException exception = assertThrows(BadRequestException.class, () -> {
            userService.rejectOwner(1L, "reason");
        });
        
        assertEquals("User is not an owner", exception.getMessage());
    }
    
    @Test
    @DisplayName("FAILURE: Update User Role - User not found")
    void testUpdateUserRole_NotFound_ThrowsException() {
        // Arrange
        when(userRepository.findById(999L)).thenReturn(Optional.empty());
        
        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> {
            userService.updateUserRole(999L, UserRole.OWNER);
        });
    }
    
    @Test
    @DisplayName("FAILURE: Change Password - User not found")
    void testChangePassword_UserNotFound_ThrowsException() {
        // Arrange
        when(userRepository.findById(999L)).thenReturn(Optional.empty());
        
        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> {
            userService.changePassword(999L, "old", "new");
        });
    }
    
    @Test
    @DisplayName("FAILURE: Change Password - Incorrect current password")
    void testChangePassword_WrongPassword_ThrowsException() {
        // Arrange
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches(anyString(), anyString())).thenReturn(false);
        
        // Act & Assert
        UnauthorizedException exception = assertThrows(UnauthorizedException.class, () -> {
            userService.changePassword(1L, "wrongPassword", "newPassword");
        });
        
        assertEquals("Current password is incorrect", exception.getMessage());
    }
    
    @Test
    @DisplayName("FAILURE: Delete User - User not found")
    void testDeleteUser_NotFound_ThrowsException() {
        // Arrange
        when(userRepository.findById(999L)).thenReturn(Optional.empty());
        
        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> {
            userService.deleteUser(999L);
        });
    }
    
    @Test
    @DisplayName("FAILURE: Delete User - Has active bookings")
    void testDeleteUser_HasBookings_ThrowsException() {
        // Arrange
        testUser.setBookings(Arrays.asList(new com.hostel.entity.Booking())); // Add mock booking
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        
        // Act & Assert
        BadRequestException exception = assertThrows(BadRequestException.class, () -> {
            userService.deleteUser(1L);
        });
        
        assertEquals("Cannot delete user with active bookings", exception.getMessage());
        verify(userRepository, never()).delete(any(User.class));
    }
}