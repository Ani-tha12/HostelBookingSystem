package com.hostel.controller;

import com.hostel.dto.request.HostelRequest;
import com.hostel.dto.response.HostelResponse;
import com.hostel.dto.response.ApiResponse;
import com.hostel.service.HostelService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/v1/hostels")
@CrossOrigin(origins = "*")
public class HostelController {
    
    @Autowired
    private HostelService hostelService;
    
   
    @PostMapping
    public ResponseEntity<ApiResponse<HostelResponse>> addHostel(
            @Valid @RequestBody HostelRequest request) {
        HostelResponse response = hostelService.addHostel(request);
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(ApiResponse.success("Hostel added successfully. Pending admin approval.", response));
    }
    
   
    @GetMapping
    public ResponseEntity<ApiResponse<List<HostelResponse>>> getAllHostels() {
        List<HostelResponse> hostels = hostelService.getApprovedHostels();
        return ResponseEntity.ok(ApiResponse.success("Hostels retrieved successfully", hostels));
    }
    
    
    @GetMapping("/{hostelId}")
    public ResponseEntity<ApiResponse<HostelResponse>> getHostelById(@PathVariable Long hostelId) {
        HostelResponse response = hostelService.getHostelById(hostelId);
        return ResponseEntity.ok(ApiResponse.success("Hostel retrieved successfully", response));
    }
    
    @GetMapping("/search")
    public ResponseEntity<ApiResponse<List<HostelResponse>>> searchHostelsByCity(
            @RequestParam String city) {
        List<HostelResponse> hostels = hostelService.searchHostelsByCity(city);
        return ResponseEntity.ok(ApiResponse.success("Search results retrieved", hostels));
    }
    
    
    @GetMapping("/owner/{ownerId}")
    public ResponseEntity<ApiResponse<List<HostelResponse>>> getHostelsByOwner(
            @PathVariable Long ownerId) {
        List<HostelResponse> hostels = hostelService.getHostelsByOwner(ownerId);
        return ResponseEntity.ok(ApiResponse.success("Owner hostels retrieved", hostels));
    }
    
    
    @PutMapping("/{hostelId}")
    public ResponseEntity<ApiResponse<HostelResponse>> updateHostel(
            @PathVariable Long hostelId,
            @Valid @RequestBody HostelRequest request) {
        HostelResponse response = hostelService.updateHostel(hostelId, request);
        return ResponseEntity.ok(ApiResponse.success("Hostel updated successfully", response));
    }
    
    
    @PostMapping("/{hostelId}/facilities")
    public ResponseEntity<ApiResponse<HostelResponse>> assignFacilities(
            @PathVariable Long hostelId,
            @RequestBody List<Long> facilityIds) {
        HostelResponse response = hostelService.assignFacilities(hostelId, facilityIds);
        return ResponseEntity.ok(
            ApiResponse.success("Facilities assigned successfully", response)
        );
    }
    
    
    @DeleteMapping("/{hostelId}/facilities/{facilityId}")
    public ResponseEntity<ApiResponse<HostelResponse>> removeFacility(
            @PathVariable Long hostelId,
            @PathVariable Long facilityId) {
        HostelResponse response = hostelService.removeFacility(hostelId, facilityId);
        return ResponseEntity.ok(
            ApiResponse.success("Facility removed successfully", response)
        );
    }
    
    
    @DeleteMapping("/{hostelId}")
    public ResponseEntity<ApiResponse<String>> deleteHostel(@PathVariable Long hostelId) {
        hostelService.deleteHostel(hostelId);
        return ResponseEntity.ok(ApiResponse.success("Hostel deleted successfully", null));
    }
    
    
    @GetMapping("/admin/pending")
    public ResponseEntity<ApiResponse<List<HostelResponse>>> getPendingHostels() {
        List<HostelResponse> hostels = hostelService.getPendingHostels();
        return ResponseEntity.ok(
            ApiResponse.success("Pending hostels retrieved successfully", hostels)
        );
    }
    
   
    @PutMapping("/admin/{hostelId}/approve")
    public ResponseEntity<ApiResponse<HostelResponse>> approveHostel(@PathVariable Long hostelId) {
        HostelResponse response = hostelService.approveHostel(hostelId);
        return ResponseEntity.ok(ApiResponse.success("Hostel approved successfully", response));
    }
    
   
    @PutMapping("/admin/{hostelId}/reject")
    public ResponseEntity<ApiResponse<String>> rejectHostel(
            @PathVariable Long hostelId,
            @RequestParam String reason) {
        hostelService.rejectHostel(hostelId, reason);
        return ResponseEntity.ok(ApiResponse.success("Hostel rejected", null));
    }
}

