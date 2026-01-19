package com.hostel.controller;

import com.hostel.dto.request.BookingRequest;
import com.hostel.dto.response.BookingResponse;
import com.hostel.dto.response.BookingStatisticsResponse;
import com.hostel.dto.response.ApiResponse;
import com.hostel.enums.BookingStatus;
import com.hostel.service.BookingService;
//import com.hostel.service.BookingService.BookingStatistics;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/v1/bookings")
@CrossOrigin(origins = "*")
public class BookingController {
    
    @Autowired
    private BookingService bookingService;
    
    
    @PostMapping
    public ResponseEntity<ApiResponse<BookingResponse>> createBooking(
            @Valid @RequestBody BookingRequest request) {
        BookingResponse response = bookingService.createBooking(request);
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(ApiResponse.success("Booking created successfully", response));
    }
    
   
    @GetMapping
    public ResponseEntity<ApiResponse<List<BookingResponse>>> getAllBookings() {
        List<BookingResponse> bookings = bookingService.getAllBookings();
        return ResponseEntity.ok(ApiResponse.success("Bookings retrieved successfully", bookings));
    }
    
    
    @GetMapping("/{bookingId}")
    public ResponseEntity<ApiResponse<BookingResponse>> getBookingById(
            @PathVariable Long bookingId) {
        BookingResponse response = bookingService.getBookingById(bookingId);
        return ResponseEntity.ok(ApiResponse.success("Booking retrieved successfully", response));
    }
    
    
    @GetMapping("/user/{userId}")
    public ResponseEntity<ApiResponse<List<BookingResponse>>> getBookingsByUser(
            @PathVariable Long userId) {
        List<BookingResponse> bookings = bookingService.getBookingsByUser(userId);
        return ResponseEntity.ok(
            ApiResponse.success("User bookings retrieved successfully", bookings)
        );
    }
    
   
    @GetMapping("/hostel/{hostelId}")
    public ResponseEntity<ApiResponse<List<BookingResponse>>> getBookingsByHostel(
            @PathVariable Long hostelId) {
        List<BookingResponse> bookings = bookingService.getBookingsByHostel(hostelId);
        return ResponseEntity.ok(
            ApiResponse.success("Hostel bookings retrieved successfully", bookings)
        );
    }
    
    @GetMapping("/owner/{ownerId}")
    public ResponseEntity<ApiResponse<List<BookingResponse>>> getBookingsByOwner(
            @PathVariable Long ownerId) {
        List<BookingResponse> bookings = bookingService.getBookingsByOwner(ownerId);
        return ResponseEntity.ok(
            ApiResponse.success("Owner bookings retrieved successfully", bookings)
        );
    }
    
    
    @PutMapping("/{bookingId}/cancel")
    public ResponseEntity<ApiResponse<BookingResponse>> cancelBooking(
            @PathVariable Long bookingId,
            @RequestParam(required = false) String reason) {
        BookingResponse response = bookingService.cancelBooking(bookingId, reason);
        return ResponseEntity.ok(
            ApiResponse.success("Booking cancelled successfully", response)
        );
    }
    
   
    @PutMapping("/{bookingId}/status")
    public ResponseEntity<ApiResponse<BookingResponse>> updateBookingStatus(
            @PathVariable Long bookingId,
            @RequestParam BookingStatus status) {
        BookingResponse response = bookingService.updateBookingStatus(bookingId, status);
        return ResponseEntity.ok(
            ApiResponse.success("Booking status updated successfully", response)
        );
    }
    
    
    @GetMapping("/admin/statistics")
    public ResponseEntity<ApiResponse<BookingStatisticsResponse>> getStatistics() {
        BookingStatisticsResponse stats = bookingService.getStatistics();
        return ResponseEntity.ok(ApiResponse.success("Statistics retrieved successfully", stats));
    }
}
