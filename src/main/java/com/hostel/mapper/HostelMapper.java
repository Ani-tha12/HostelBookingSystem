package com.hostel.mapper;

import com.hostel.entity.Hostel;
import com.hostel.entity.Facility;
import com.hostel.dto.request.HostelRequest;
import com.hostel.dto.response.HostelResponse;
import org.springframework.stereotype.Component;
import java.util.stream.Collectors;

@Component
public class HostelMapper {
    
   
    public Hostel toEntity(HostelRequest request) {
        Hostel hostel = new Hostel();
        hostel.setHostelName(request.getHostelName());
        hostel.setCity(request.getCity());
        hostel.setAddress(request.getAddress());
        hostel.setDescription(request.getDescription());
        return hostel;
    }
    
  
    public HostelResponse toResponse(Hostel hostel) {
        HostelResponse response = new HostelResponse();
        response.setHostelId(hostel.getHostelId());
        response.setHostelName(hostel.getHostelName());
        response.setCity(hostel.getCity());
        response.setAddress(hostel.getAddress());
        response.setDescription(hostel.getDescription());
        response.setApproved(hostel.getApproved());
        response.setCreatedDate(hostel.getCreatedDate());
        
        if (hostel.getOwner() != null) {
            response.setOwnerId(hostel.getOwner().getUserId());
            response.setOwnerName(hostel.getOwner().getName());
        }
        
        if (hostel.getFacilities() != null) {
            response.setFacilities(
                hostel.getFacilities().stream()
                    .map(Facility::getFacilityName)
                    .collect(Collectors.toList())
            );
        }
        
        return response;
    }
    
 
    public void updateEntity(Hostel hostel, HostelRequest request) {
        if (request.getHostelName() != null) {
            hostel.setHostelName(request.getHostelName());
        }
        if (request.getCity() != null) {
            hostel.setCity(request.getCity());
        }
        if (request.getAddress() != null) {
            hostel.setAddress(request.getAddress());
        }
        if (request.getDescription() != null) {
            hostel.setDescription(request.getDescription());
        }
    }
}
