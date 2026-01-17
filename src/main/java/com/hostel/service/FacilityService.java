package com.hostel.service;

import com.hostel.entity.Facility;
import com.hostel.dto.request.FacilityRequest;
import com.hostel.dto.response.FacilityResponse;
import com.hostel.repository.FacilityRepository;

import jakarta.validation.Valid;

import com.hostel.mapper.FacilityMapper;
import com.hostel.exception.ResourceNotFoundException;
import com.hostel.exception.BadRequestException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class FacilityService {
    
    // ⭐ ADD THIS: Logger declaration
    private static final Logger logger = LoggerFactory.getLogger(FacilityService.class);
    
    @Autowired
    private FacilityRepository facilityRepository;
    
    @Autowired
    private FacilityMapper facilityMapper;
    
    // Add new facility
    public FacilityResponse addFacility(FacilityRequest request) {
        logger.info("Adding new facility - Name: {}", request.getFacilityName());
        
        try {
            // Check if facility already exists
            if (facilityRepository.existsByFacilityName(request.getFacilityName())) {
                logger.warn("Facility creation failed: Facility already exists - Name: {}", 
                           request.getFacilityName());
                throw new BadRequestException("Facility already exists");
            }
            
            // Create facility
            Facility facility = facilityMapper.toEntity(request);
            
            // Save facility
            Facility savedFacility = facilityRepository.save(facility);
            
            logger.info("Facility created successfully - ID: {}, Name: {}", 
                       savedFacility.getFacilityId(), savedFacility.getFacilityName());
            
            return facilityMapper.toResponse(savedFacility);
            
        } catch (BadRequestException e) {
            logger.error("Facility creation failed: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            logger.error("Unexpected error during facility creation", e);
            throw new RuntimeException("Facility creation failed", e);
        }
    }
    
    // Get all facilities
    public List<FacilityResponse> getAllFacilities() {
        logger.info("Fetching all facilities");
        
        List<FacilityResponse> facilities = facilityRepository.findAll().stream()
            .map(facilityMapper::toResponse)
            .collect(Collectors.toList());
        
        logger.info("Retrieved {} facilities", facilities.size());
        return facilities;
    }
    
    // Delete facility
    public void deleteFacility(Long facilityId) {
        logger.info("Attempting to delete facility - ID: {}", facilityId);
        
        Facility facility = facilityRepository.findById(facilityId)
            .orElseThrow(() -> {
                logger.error("Facility deletion failed: Facility not found - ID: {}", facilityId);
                return new ResourceNotFoundException("Facility", "facilityId", facilityId);
            });
        
        // Check if facility is assigned to any hostel
        if (facility.getHostels() != null && !facility.getHostels().isEmpty()) {
            logger.warn("Facility deletion failed: Assigned to {} hostels - ID: {}", 
                       facility.getHostels().size(), facilityId);
            throw new BadRequestException("Cannot delete facility assigned to hostels");
        }
        
        facilityRepository.delete(facility);
        logger.info("Facility deleted successfully - ID: {}, Name: {}", 
                   facilityId, facility.getFacilityName());
    }

	public FacilityResponse getFacilityById(Long facilityId) {
		 logger.info("Fetching facility with ID: {}", facilityId);
		    Facility facility = facilityRepository.findById(facilityId)
		            .orElseThrow(() -> new ResourceNotFoundException("Facility", "facilityId", facilityId));
		    return facilityMapper.toResponse(facility);

	}

	public FacilityResponse updateFacility(Long facilityId, @Valid FacilityRequest request) {
		 logger.info("Updating facility with ID: {}", facilityId);
		    Facility facility = facilityRepository.findById(facilityId)
		            .orElseThrow(() -> new ResourceNotFoundException("Facility", "facilityId", facilityId));

		    facility.setFacilityName(request.getFacilityName());

		    Facility updatedFacility = facilityRepository.save(facility);
		    logger.info("Facility updated successfully - ID: {}, Name: {}", facilityId, updatedFacility.getFacilityName());

		    return facilityMapper.toResponse(updatedFacility);

	}
}
