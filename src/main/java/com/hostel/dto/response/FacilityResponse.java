package com.hostel.dto.response;

public class FacilityResponse {
    
    private Long facilityId;
    private String facilityName;
    
    // Constructors
    public FacilityResponse() {}
    
    public FacilityResponse(Long facilityId, String facilityName) {
        this.facilityId = facilityId;
        this.facilityName = facilityName;
    }
    
    // Getters and Setters
    public Long getFacilityId() {
        return facilityId;
    }
    
    public void setFacilityId(Long facilityId) {
        this.facilityId = facilityId;
    }
    
    public String getFacilityName() {
        return facilityName;
    }
    
    public void setFacilityName(String facilityName) {
        this.facilityName = facilityName;
    }
}
