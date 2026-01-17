package com.hostel.controller;

import com.hostel.dto.request.RoomRequest;
import com.hostel.dto.response.RoomResponse;
import com.hostel.dto.response.ApiResponse;
import com.hostel.service.RoomService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/v1/rooms")
@CrossOrigin(origins = "*")
public class RoomController {
    
    @Autowired
    private RoomService roomService;
    
    // POST: Add new room (Owner only)
    @PostMapping
    public ResponseEntity<ApiResponse<RoomResponse>> addRoom(
            @Valid @RequestBody RoomRequest request) {
        RoomResponse response = roomService.addRoom(request);
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(ApiResponse.success("Room added successfully", response));
    }
    
    // GET: Get all rooms (Public)
    @GetMapping
    public ResponseEntity<ApiResponse<List<RoomResponse>>> getAllRooms() {
        List<RoomResponse> rooms = roomService.getAllRooms();
        return ResponseEntity.ok(ApiResponse.success("Rooms retrieved successfully", rooms));
    }
    
    // GET: Get room by ID (Public)
    @GetMapping("/{roomId}")
    public ResponseEntity<ApiResponse<RoomResponse>> getRoomById(@PathVariable Long roomId) {
        RoomResponse response = roomService.getRoomById(roomId);
        return ResponseEntity.ok(ApiResponse.success("Room retrieved successfully", response));
    }
    
    // GET: Get rooms by hostel (Public)
    @GetMapping("/hostel/{hostelId}")
    public ResponseEntity<ApiResponse<List<RoomResponse>>> getRoomsByHostel(
            @PathVariable Long hostelId) {
        List<RoomResponse> rooms = roomService.getRoomsByHostel(hostelId);
        return ResponseEntity.ok(ApiResponse.success("Hostel rooms retrieved", rooms));
    }
    
    // GET: Get available rooms (Public)
    @GetMapping("/available")
    public ResponseEntity<ApiResponse<List<RoomResponse>>> getAvailableRooms() {
        List<RoomResponse> rooms = roomService.getAvailableRooms();
        return ResponseEntity.ok(ApiResponse.success("Available rooms retrieved", rooms));
    }
    
    // GET: Get available rooms by hostel (Public)
    @GetMapping("/hostel/{hostelId}/available")
    public ResponseEntity<ApiResponse<List<RoomResponse>>> getAvailableRoomsByHostel(
            @PathVariable Long hostelId) {
        List<RoomResponse> rooms = roomService.getAvailableRoomsByHostel(hostelId);
        return ResponseEntity.ok(
            ApiResponse.success("Available rooms for hostel retrieved", rooms)
        );
    }
    
    // GET: Check room availability (Public)
    @GetMapping("/{roomId}/availability")
    public ResponseEntity<ApiResponse<Boolean>> checkAvailability(
            @PathVariable Long roomId,
            @RequestParam Integer requiredBeds) {
        boolean available = roomService.checkAvailability(roomId, requiredBeds);
        return ResponseEntity.ok(
            ApiResponse.success("Availability checked", available)
        );
    }
    
    // PUT: Update room (Owner only)
    @PutMapping("/{roomId}")
    public ResponseEntity<ApiResponse<RoomResponse>> updateRoom(
            @PathVariable Long roomId,
            @Valid @RequestBody RoomRequest request) {
        RoomResponse response = roomService.updateRoom(roomId, request);
        return ResponseEntity.ok(ApiResponse.success("Room updated successfully", response));
    }
    
    // PUT: Update room availability (Owner only)
    @PutMapping("/{roomId}/availability")
    public ResponseEntity<ApiResponse<RoomResponse>> updateAvailability(
            @PathVariable Long roomId,
            @RequestParam Integer availableBeds) {
        RoomResponse response = roomService.updateAvailability(roomId, availableBeds);
        return ResponseEntity.ok(
            ApiResponse.success("Room availability updated", response)
        );
    }
    
    // DELETE: Delete room (Owner/Admin only)
    @DeleteMapping("/{roomId}")
    public ResponseEntity<ApiResponse<String>> deleteRoom(@PathVariable Long roomId) {
        roomService.deleteRoom(roomId);
        return ResponseEntity.ok(ApiResponse.success("Room deleted successfully", null));
    }
}
