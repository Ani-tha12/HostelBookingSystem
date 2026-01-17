package com.hostel.test.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDate;
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
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hostel.controller.BookingController;
import com.hostel.dto.request.BookingRequest;
import com.hostel.dto.response.BookingResponse;
import com.hostel.dto.response.BookingStatisticsResponse;
import com.hostel.enums.BookingStatus;
import com.hostel.enums.RoomType;
import com.hostel.exception.GlobalExceptionHandler;
import com.hostel.service.BookingService;

// ✅ FIX: Add excludeAutoConfiguration parameter
@WebMvcTest(
    controllers = BookingController.class,
    excludeAutoConfiguration = {
        SecurityAutoConfiguration.class,
        SecurityFilterAutoConfiguration.class
    }
)
@AutoConfigureMockMvc(addFilters = false)
@Import(GlobalExceptionHandler.class) 
@DisplayName("Booking Controller Tests")
class BookingControllerTest {
    
    @Autowired
    private MockMvc mockMvc;
    
    @Autowired
    private ObjectMapper objectMapper;
    
    @MockBean
    private BookingService bookingService;
    
    private BookingRequest bookingRequest;
    private BookingResponse bookingResponse;
    
    @BeforeEach
    void setUp() {
        // Setup booking request
        bookingRequest = new BookingRequest();
        bookingRequest.setUserId(1L);
        bookingRequest.setHostelId(1L);
        bookingRequest.setRoomId(10L);
        bookingRequest.setCheckInDate(LocalDate.now().plusDays(5));
        bookingRequest.setCheckOutDate(LocalDate.now().plusDays(10));
        bookingRequest.setNumberOfBeds(2);
        
        // Setup booking response
        bookingResponse = new BookingResponse();
        bookingResponse.setBookingId(101L);
        bookingResponse.setUserId(1L);
        bookingResponse.setUserName("Suresh Kumar");
        bookingResponse.setHostelId(1L);
        bookingResponse.setHostelName("Sunshine Hostel");
        bookingResponse.setRoomId(10L);
        bookingResponse.setRoomType(RoomType.DORM);
        bookingResponse.setCheckInDate(bookingRequest.getCheckInDate());
        bookingResponse.setCheckOutDate(bookingRequest.getCheckOutDate());
        bookingResponse.setNumberOfBeds(2);
        bookingResponse.setTotalPrice(3000.0);
        bookingResponse.setBookingStatus(BookingStatus.CONFIRMED);
    }
    
    // ==========================================
    // SUCCESS TEST CASES
    // ==========================================
    
    @Test
    @DisplayName("SUCCESS: Create Booking - Should return 201 CREATED")
    void testCreateBooking_Success() throws Exception {
        // Arrange
        when(bookingService.createBooking(any(BookingRequest.class))).thenReturn(bookingResponse);
        
        // Act & Assert
        mockMvc.perform(post("/api/v1/bookings")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(bookingRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.bookingId").value(101))
                .andExpect(jsonPath("$.totalPrice").value(3000.0))
                .andExpect(jsonPath("$.bookingStatus").value("CONFIRMED"));
        
        verify(bookingService, times(1)).createBooking(any(BookingRequest.class));
    }
    
    @Test
    @DisplayName("SUCCESS: Get Booking By ID - Should return booking details")
    void testGetBookingById_Success() throws Exception {
        // Arrange
        when(bookingService.getBookingById(101L)).thenReturn(bookingResponse);
        
        // Act & Assert
        mockMvc.perform(get("/api/v1/bookings/101"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.bookingId").value(101))
                .andExpect(jsonPath("$.hostelName").value("Sunshine Hostel"));
        
        verify(bookingService, times(1)).getBookingById(101L);
    }
    
    @Test
    @DisplayName("SUCCESS: Get All Bookings - Should return list of bookings")
    void testGetAllBookings_Success() throws Exception {
        // Arrange
        List<BookingResponse> bookings = Arrays.asList(bookingResponse);
        when(bookingService.getAllBookings()).thenReturn(bookings);
        
        // Act & Assert
        mockMvc.perform(get("/api/v1/bookings/all"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].bookingId").value(101));
        
        verify(bookingService, times(1)).getAllBookings();
    }
    
    @Test
    @DisplayName("SUCCESS: Get Bookings By User - Should return user bookings")
    void testGetBookingsByUser_Success() throws Exception {
        // Arrange
        List<BookingResponse> bookings = Arrays.asList(bookingResponse);
        when(bookingService.getBookingsByUser(1L)).thenReturn(bookings);
        
        // Act & Assert
        mockMvc.perform(get("/api/v1/bookings/user/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].userName").value("Suresh Kumar"));
        
        verify(bookingService, times(1)).getBookingsByUser(1L);
    }
    
    @Test
    @DisplayName("SUCCESS: Cancel Booking - Should return cancelled booking")
    void testCancelBooking_Success() throws Exception {
        // Arrange
        bookingResponse.setBookingStatus(BookingStatus.CANCELLED);
        when(bookingService.cancelBooking(anyLong(), anyString())).thenReturn(bookingResponse);
        
        // Act & Assert
        mockMvc.perform(put("/api/v1/bookings/101/cancel")
                .param("reason", "Changed plans"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.bookingStatus").value("CANCELLED"));
        
        verify(bookingService, times(1)).cancelBooking(anyLong(), anyString());
    }
    
    @Test
    @DisplayName("SUCCESS: Get Statistics - Should return booking statistics")
    void testGetStatistics_Success() throws Exception {
        // Arrange
        BookingStatisticsResponse stats = new BookingStatisticsResponse(100L, 60L, 20L, 20L);
        when(bookingService.getStatistics()).thenReturn(stats);
        
        // Act & Assert
        mockMvc.perform(get("/api/v1/bookings/statistics"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.total").value(100))
                .andExpect(jsonPath("$.confirmed").value(60));
        
        verify(bookingService, times(1)).getStatistics();
    }
    
    // ==========================================
    // FAILURE TEST CASES
    // ==========================================
    
    @Test
    @DisplayName("FAILURE: Create Booking - Invalid check-in date (past)")
    void testCreateBooking_PastDate_ReturnsBadRequest() throws Exception {
        // Arrange
        bookingRequest.setCheckInDate(LocalDate.now().minusDays(1));
        
        // Act & Assert
        mockMvc.perform(post("/api/v1/bookings")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(bookingRequest)))
                .andExpect(status().isBadRequest());
    }
    
    @Test
    @DisplayName("FAILURE: Create Booking - Missing required fields")
    void testCreateBooking_MissingFields_ReturnsBadRequest() throws Exception {
        // Arrange
        bookingRequest.setUserId(null);
        
        // Act & Assert
        mockMvc.perform(post("/api/v1/bookings")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(bookingRequest)))
                .andExpect(status().isBadRequest());
    }
}