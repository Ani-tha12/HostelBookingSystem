package com.hostel.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.List;

public class HostelRequest {
    
    @NotNull(message = "Owner ID is required")
    private Long ownerId;
    
    @NotBlank(message = "Hostel name is required")
    @Size(min = 3, max = 100, message = "Hostel name must be between 3 and 100 characters")
    private String hostelName;
    
    @NotBlank(message = "City is required")
    private String city;
    
    @NotBlank(message = "Address is required")
    @Size(min = 10, max = 200, message = "Address must be between 10 and 200 characters")
    private String address;
    
    @Size(max = 1000, message = "Description cannot exceed 1000 characters")
    private String description;
    
    private List<Long> facilityIds; // List of facility IDs to assign
    
    // Constructors
    public HostelRequest() {}
    
    // Getters and Setters
    public Long getOwnerId() {
        return ownerId;
    }
    
    public void setOwnerId(Long ownerId) {
        this.ownerId = ownerId;
    }
    
    public String getHostelName() {
        return hostelName;
    }
    
    public void setHostelName(String hostelName) {
        this.hostelName = hostelName;
    }
    
    public String getCity() {
        return city;
    }
    
    public void setCity(String city) {
        this.city = city;
    }
    
    public String getAddress() {
        return address;
    }
    
    public void setAddress(String address) {
        this.address = address;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public List<Long> getFacilityIds() {
        return facilityIds;
    }
    
    public void setFacilityIds(List<Long> facilityIds) {
        this.facilityIds = facilityIds;
    }
}
