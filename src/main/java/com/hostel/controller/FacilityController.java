package com.hostel.controller;

import com.hostel.dto.request.FacilityRequest;
import com.hostel.dto.response.FacilityResponse;
import com.hostel.dto.response.ApiResponse;
import com.hostel.service.FacilityService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/v1/facilities")
@CrossOrigin(origins = "*")
public class FacilityController {

	@Autowired
	private FacilityService facilityService;

	@PostMapping
	public ResponseEntity<ApiResponse<FacilityResponse>> addFacility(@Valid @RequestBody FacilityRequest request) {
		FacilityResponse response = facilityService.addFacility(request);
		return ResponseEntity.status(HttpStatus.CREATED)
				.body(ApiResponse.success("Facility added successfully", response));
	}

	@GetMapping
	public ResponseEntity<ApiResponse<List<FacilityResponse>>> getAllFacilities() {
		List<FacilityResponse> facilities = facilityService.getAllFacilities();
		return ResponseEntity.ok(ApiResponse.success("Facilities retrieved successfully", facilities));
	}

	@GetMapping("/{facilityId}")
	public ResponseEntity<ApiResponse<FacilityResponse>> getFacilityById(@PathVariable Long facilityId) {
		FacilityResponse response = facilityService.getFacilityById(facilityId);
		return ResponseEntity.ok(ApiResponse.success("Facility retrieved successfully", response));
	}

	@PutMapping("/{facilityId}")
	public ResponseEntity<ApiResponse<FacilityResponse>> updateFacility(@PathVariable Long facilityId,
			@Valid @RequestBody FacilityRequest request) {
		FacilityResponse response = facilityService.updateFacility(facilityId, request);
		return ResponseEntity.ok(ApiResponse.success("Facility updated successfully", response));
	}

	@DeleteMapping("/{facilityId}")
	public ResponseEntity<ApiResponse<String>> deleteFacility(@PathVariable Long facilityId) {
		facilityService.deleteFacility(facilityId);
		return ResponseEntity.ok(ApiResponse.success("Facility deleted successfully", null));
	}
}
