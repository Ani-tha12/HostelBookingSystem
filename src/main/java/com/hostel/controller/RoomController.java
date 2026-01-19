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
    
    
    @PostMapping
    public ResponseEntity<ApiResponse<RoomResponse>> addRoom(
            @Valid @RequestBody RoomRequest request) {
        RoomResponse response = roomService.addRoom(request);
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(ApiResponse.success("Room added successfully", response));
    }
    
   
    @GetMapping
    public ResponseEntity<ApiResponse<List<RoomResponse>>> getAllRooms() {
        List<RoomResponse> rooms = roomService.getAllRooms();
        return ResponseEntity.ok(ApiResponse.success("Rooms retrieved successfully", rooms));
    }
    
   
    @GetMapping("/{roomId}")
    public ResponseEntity<ApiResponse<RoomResponse>> getRoomById(@PathVariable Long roomId) {
        RoomResponse response = roomService.getRoomById(roomId);
        return ResponseEntity.ok(ApiResponse.success("Room retrieved successfully", response));
    }
    
    
    @GetMapping("/hostel/{hostelId}")
    public ResponseEntity<ApiResponse<List<RoomResponse>>> getRoomsByHostel(
            @PathVariable Long hostelId) {
        List<RoomResponse> rooms = roomService.getRoomsByHostel(hostelId);
        return ResponseEntity.ok(ApiResponse.success("Hostel rooms retrieved", rooms));
    }
    
    
    @GetMapping("/available")
    public ResponseEntity<ApiResponse<List<RoomResponse>>> getAvailableRooms() {
        List<RoomResponse> rooms = roomService.getAvailableRooms();
        return ResponseEntity.ok(ApiResponse.success("Available rooms retrieved", rooms));
    }
    
    
    @GetMapping("/hostel/{hostelId}/available")
    public ResponseEntity<ApiResponse<List<RoomResponse>>> getAvailableRoomsByHostel(
            @PathVariable Long hostelId) {
        List<RoomResponse> rooms = roomService.getAvailableRoomsByHostel(hostelId);
        return ResponseEntity.ok(
            ApiResponse.success("Available rooms for hostel retrieved", rooms)
        );
    }
    
    
    @GetMapping("/{roomId}/availability")
    public ResponseEntity<ApiResponse<Boolean>> checkAvailability(
            @PathVariable Long roomId,
            @RequestParam Integer requiredBeds) {
        boolean available = roomService.checkAvailability(roomId, requiredBeds);
        return ResponseEntity.ok(
            ApiResponse.success("Availability checked", available)
        );
    }
    
    
    @PutMapping("/{roomId}")
    public ResponseEntity<ApiResponse<RoomResponse>> updateRoom(
            @PathVariable Long roomId,
            @Valid @RequestBody RoomRequest request) {
        RoomResponse response = roomService.updateRoom(roomId, request);
        return ResponseEntity.ok(ApiResponse.success("Room updated successfully", response));
    }
    
   
    @PutMapping("/{roomId}/availability")
    public ResponseEntity<ApiResponse<RoomResponse>> updateAvailability(
            @PathVariable Long roomId,
            @RequestParam Integer availableBeds) {
        RoomResponse response = roomService.updateAvailability(roomId, availableBeds);
        return ResponseEntity.ok(
            ApiResponse.success("Room availability updated", response)
        );
    }
    
   
    @DeleteMapping("/{roomId}")
    public ResponseEntity<ApiResponse<String>> deleteRoom(@PathVariable Long roomId) {
        roomService.deleteRoom(roomId);
        return ResponseEntity.ok(ApiResponse.success("Room deleted successfully", null));
    }
}
