package com.hostel.test.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.SecurityFilterAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hostel.controller.UserController;
import com.hostel.dto.request.LoginRequest;
import com.hostel.dto.request.UserRequest;
import com.hostel.dto.response.LoginResponse;
import com.hostel.dto.response.UserResponse;
import com.hostel.enums.UserRole;
import com.hostel.enums.UserStatus;
import com.hostel.service.UserService;

import org.springframework.context.annotation.Import;
import com.hostel.exception.GlobalExceptionHandler;

// ✅ FIX: Add excludeAutoConfiguration parameter
@WebMvcTest(
    controllers = UserController.class,
    excludeAutoConfiguration = {
        SecurityAutoConfiguration.class,
        SecurityFilterAutoConfiguration.class
    }
)
@AutoConfigureMockMvc(addFilters = false)
@Import(GlobalExceptionHandler.class)
@DisplayName("User Controller Tests")
class UserControllerTest {
    
    @Autowired
    private MockMvc mockMvc;
    
    @Autowired
    private ObjectMapper objectMapper;
    
    @MockBean
    private UserService userService;
    
    // ✅ OPTIONAL FIX: If you have JWT components, mock them
    // Only add these if you're still getting JWT-related errors
    // @MockBean
    // private JwtUtil jwtUtil;
    
    // @MockBean
    // private JwtAuthenticationFilter jwtAuthenticationFilter;
    
    // @MockBean
    // private PasswordEncoder passwordEncoder;
    
    private UserRequest userRequest;
    private UserResponse userResponse;
    private LoginRequest loginRequest;
    private LoginResponse loginResponse;
    
    @BeforeEach
    void setUp() {
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
        
        // Setup login request
        loginRequest = new LoginRequest();
        loginRequest.setEmail("suresh@gmail.com");
        loginRequest.setPassword("securePassword123");
        
        // Setup login response
        loginResponse = new LoginResponse();
        loginResponse.setUserId(1L);
        loginResponse.setName("Suresh Kumar");
        loginResponse.setEmail("suresh@gmail.com");
        loginResponse.setRole(UserRole.USER);
        loginResponse.setToken("jwt-token-123");
    }
    
    // ==========================================
    // SUCCESS TEST CASES
    // ==========================================
    
    @Test
    @DisplayName("SUCCESS: Register User - Should return 201 CREATED")
    void testRegisterUser_Success() throws Exception {
        // Arrange
        when(userService.registerUser(any(UserRequest.class))).thenReturn(userResponse);
        
        // Act & Assert
        mockMvc.perform(post("/api/v1/users/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.userId").value(1))
                .andExpect(jsonPath("$.email").value("suresh@gmail.com"))
                .andExpect(jsonPath("$.role").value("USER"));
        
        verify(userService, times(1)).registerUser(any(UserRequest.class));
    }
    
    @Test
    @DisplayName("SUCCESS: Login User - Should return 200 OK with token")
    void testLoginUser_Success() throws Exception {
        // Arrange
        when(userService.login(any(LoginRequest.class))).thenReturn(loginResponse);
        
        // Act & Assert
        mockMvc.perform(post("/api/v1/users/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value(1))
                .andExpect(jsonPath("$.email").value("suresh@gmail.com"))
                .andExpect(jsonPath("$.token").value("jwt-token-123"));
        
        verify(userService, times(1)).login(any(LoginRequest.class));
    }
    
    @Test
    @DisplayName("SUCCESS: Get User By ID - Should return user details")
    void testGetUserById_Success() throws Exception {
        // Arrange
        when(userService.getUserById(1L)).thenReturn(userResponse);
        
        // Act & Assert
        mockMvc.perform(get("/api/v1/users/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value(1))
                .andExpect(jsonPath("$.email").value("suresh@gmail.com"));
        
        verify(userService, times(1)).getUserById(1L);
    }
    
    @Test
    @DisplayName("SUCCESS: Get All Users - Should return list of users")
    void testGetAllUsers_Success() throws Exception {
        // Arrange
        List<UserResponse> users = Arrays.asList(userResponse);
        when(userService.getAllUsers()).thenReturn(users);
        
        // Act & Assert
        mockMvc.perform(get("/api/v1/users/all"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].email").value("suresh@gmail.com"));
        
        verify(userService, times(1)).getAllUsers();
    }
    
    @Test
    @DisplayName("SUCCESS: Approve Owner - Should return 200 OK")
    void testApproveOwner_Success() throws Exception {
        // Arrange
        userResponse.setStatus(UserStatus.APPROVED);
        when(userService.approveOwner(10L)).thenReturn(userResponse);
        
        // Act & Assert
        mockMvc.perform(put("/api/v1/users/10/approve"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("APPROVED"));
        
        verify(userService, times(1)).approveOwner(10L);
    }
    
    @Test
    @DisplayName("SUCCESS: Update User - Should return updated user")
    void testUpdateUser_Success() throws Exception {
        // Arrange
        when(userService.updateUser(anyLong(), any(UserRequest.class))).thenReturn(userResponse);
        
        // Act & Assert
        mockMvc.perform(put("/api/v1/users/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value(1));
        
        verify(userService, times(1)).updateUser(anyLong(), any(UserRequest.class));
    }
    
    @Test
    @DisplayName("SUCCESS: Delete User - Should return 200 OK")
    void testDeleteUser_Success() throws Exception {
        // Arrange
        doNothing().when(userService).deleteUser(1L);
        
        // Act & Assert
        mockMvc.perform(delete("/api/v1/users/1"))
                .andExpect(status().isOk());
        
        verify(userService, times(1)).deleteUser(1L);
    }
    
    // ==========================================
    // FAILURE TEST CASES
    // ==========================================
    
    @Test
    @DisplayName("FAILURE: Register User - Invalid email format")
    void testRegisterUser_InvalidEmail_ReturnsBadRequest() throws Exception {
        // Arrange
        userRequest.setEmail("invalid-email");
        
        // Act & Assert
        mockMvc.perform(post("/api/v1/users/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userRequest)))
                .andExpect(status().isBadRequest());
        
        verify(userService, never()).registerUser(any(UserRequest.class));
    }
    
    @Test
    @DisplayName("FAILURE: Register User - Empty name")
    void testRegisterUser_EmptyName_ReturnsBadRequest() throws Exception {
        // Arrange
        userRequest.setName("");
        
        // Act & Assert
        mockMvc.perform(post("/api/v1/users/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userRequest)))
                .andExpect(status().isBadRequest());
    }
    
    @Test
    @DisplayName("FAILURE: Register User - Password too short")
    void testRegisterUser_ShortPassword_ReturnsBadRequest() throws Exception {
        // Arrange
        userRequest.setPassword("123");
        
        // Act & Assert
        mockMvc.perform(post("/api/v1/users/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userRequest)))
                .andExpect(status().isBadRequest());
    }
    
    @Test
    @DisplayName("FAILURE: Login - Empty email")
    void testLogin_EmptyEmail_ReturnsBadRequest() throws Exception {
        // Arrange
        loginRequest.setEmail("");
        
        // Act & Assert
        mockMvc.perform(post("/api/v1/users/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isBadRequest());
    }
}
