package com.hostel.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class FacilityRequest {

	@NotBlank(message = "Facility name is required")
	@Size(min = 2, max = 50, message = "Facility name must be between 2 and 50 characters")
	private String facilityName;

	public FacilityRequest() {
	}

	public String getFacilityName() {
		return facilityName;
	}

	public void setFacilityName(String facilityName) {
		this.facilityName = facilityName;
	}
}
