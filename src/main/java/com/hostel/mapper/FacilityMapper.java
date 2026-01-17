package com.hostel.mapper;

import com.hostel.entity.Facility;
import com.hostel.dto.request.FacilityRequest;
import com.hostel.dto.response.FacilityResponse;
import org.springframework.stereotype.Component;

@Component
public class FacilityMapper {
    
    
    public Facility toEntity(FacilityRequest request) {
        Facility facility = new Facility();
        facility.setFacilityName(request.getFacilityName());
        return facility;
    }
    
   
    public FacilityResponse toResponse(Facility facility) {
        FacilityResponse response = new FacilityResponse();
        response.setFacilityId(facility.getFacilityId());
        response.setFacilityName(facility.getFacilityName());
        return response;
    }
    
   
    public void updateEntity(Facility facility, FacilityRequest request) {
        if (request.getFacilityName() != null) {
            facility.setFacilityName(request.getFacilityName());
        }
    }
}
