package com.hostel.dto.request;

import com.hostel.enums.RoomType;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public class RoomRequest {
    
    @NotNull(message = "Hostel ID is required")
    private Long hostelId;
    
    @NotNull(message = "Room type is required")
    private RoomType roomType;
    
    @NotNull(message = "Total beds is required")
    @Min(value = 1, message = "Total beds must be at least 1")
    private Integer totalBeds;
    
    private Integer AvailableBeds;
    
    @NotNull(message = "Price per night is required")
    @Min(value = 0, message = "Price must be positive")
    private Double pricePerNight;
    
    private String description;
    
    // Constructors
    public RoomRequest() {}
    
    // Getters and Setters
    public Long getHostelId() {
        return hostelId;
    }
    
    public void setHostelId(Long hostelId) {
        this.hostelId = hostelId;
    }
    
    public RoomType getRoomType() {
        return roomType;
    }
    
    public void setRoomType(RoomType roomType) {
        this.roomType = roomType;
    }
    
    public Integer getTotalBeds() {
        return totalBeds;
    }
    
    public void setTotalBeds(Integer totalBeds) {
        this.totalBeds = totalBeds;
    }
    
    public Double getPricePerNight() {
        return pricePerNight;
    }
    
    public void setPricePerNight(Double pricePerNight) {
        this.pricePerNight = pricePerNight;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }

	public Integer  getAvailableBeds() {
		return AvailableBeds;
	}
	public void setAvailableBeds(Integer AvailbaleBeds)
	{
		this.AvailableBeds=AvailableBeds;
	}
}