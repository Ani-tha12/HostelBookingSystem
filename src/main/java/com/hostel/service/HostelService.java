package com.hostel.service;

import com.hostel.entity.Hostel;
import com.hostel.entity.User;
import com.hostel.entity.Facility;
import com.hostel.dto.request.HostelRequest;
import com.hostel.dto.response.HostelResponse;
import com.hostel.repository.HostelRepository;
import com.hostel.repository.UserRepository;

import jakarta.validation.Valid;

import com.hostel.repository.FacilityRepository;
import com.hostel.mapper.HostelMapper;
import com.hostel.exception.ResourceNotFoundException;
import com.hostel.exception.BadRequestException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Set;
import java.util.HashSet;
import java.util.stream.Collectors;

@Service
@Transactional
public class HostelService {

	private static final Logger logger = LoggerFactory.getLogger(HostelService.class);

	@Autowired
	private HostelRepository hostelRepository;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private FacilityRepository facilityRepository;

	@Autowired
	private HostelMapper hostelMapper;

	public HostelResponse addHostel(HostelRequest request) {
		logger.info("Adding new hostel - Name: {}, City: {}, Owner ID: {}", request.getHostelName(), request.getCity(),
				request.getOwnerId());

		try {

			User owner = userRepository.findById(request.getOwnerId()).orElseThrow(() -> {
				logger.error("Hostel creation failed: Owner not found - ID: {}", request.getOwnerId());
				return new ResourceNotFoundException("User", "userId", request.getOwnerId());
			});

			logger.debug("Owner validated - ID: {}, Name: {}, Role: {}", owner.getUserId(), owner.getName(),
					owner.getRole());

			if (owner.getRole().name().equals("OWNER") && !owner.getStatus().name().equals("APPROVED")) {
				logger.warn("Hostel creation failed: Owner not approved - ID: {}, Status: {}", owner.getUserId(),
						owner.getStatus());
				throw new BadRequestException("Owner account is not approved");
			}

			Hostel hostel = hostelMapper.toEntity(request);
			hostel.setOwner(owner);
			hostel.setApproved(false);

			logger.debug("Hostel entity created - Name: {}, Approved: {}", hostel.getHostelName(),
					hostel.getApproved());

			if (request.getFacilityIds() != null && !request.getFacilityIds().isEmpty()) {
				Set<Facility> facilities = new HashSet<>();
				for (Long facilityId : request.getFacilityIds()) {
					Facility facility = facilityRepository.findById(facilityId).orElseThrow(() -> {
						logger.error("Facility not found - ID: {}", facilityId);
						return new ResourceNotFoundException("Facility", "facilityId", facilityId);
					});
					facilities.add(facility);
				}
				hostel.setFacilities(facilities);
				logger.debug("Added {} facilities to hostel", facilities.size());
			}

			Hostel savedHostel = hostelRepository.save(hostel);

			logger.info("Hostel created successfully - ID: {}, Name: {}, Owner: {}, Requires approval",
					savedHostel.getHostelId(), savedHostel.getHostelName(), owner.getName());

			return hostelMapper.toResponse(savedHostel);

		} catch (ResourceNotFoundException | BadRequestException e) {
			logger.error("Hostel creation failed: {}", e.getMessage());
			throw e;
		} catch (Exception e) {
			logger.error("Unexpected error during hostel creation", e);
			throw new RuntimeException("Hostel creation failed", e);
		}
	}

	public List<HostelResponse> getApprovedHostels() {
		logger.info("Fetching all approved hostels");

		List<HostelResponse> hostels = hostelRepository.findByApproved(true).stream().map(hostelMapper::toResponse)
				.collect(Collectors.toList());

		logger.info("Retrieved {} approved hostels", hostels.size());
		return hostels;
	}

	public List<HostelResponse> searchHostelsByCity(String city) {
		logger.info("Searching hostels in city: {}", city);

		List<HostelResponse> hostels = hostelRepository.findByCityAndApproved(city, true).stream()
				.map(hostelMapper::toResponse).collect(Collectors.toList());

		logger.info("Found {} hostels in city: {}", hostels.size(), city);
		return hostels;
	}

	public HostelResponse approveHostel(Long hostelId) {
		logger.info("Attempting to approve hostel - ID: {}", hostelId);

		Hostel hostel = hostelRepository.findById(hostelId).orElseThrow(() -> {
			logger.error("Hostel approval failed: Hostel not found - ID: {}", hostelId);
			return new ResourceNotFoundException("Hostel", "hostelId", hostelId);
		});

		hostel.setApproved(true);
		Hostel updatedHostel = hostelRepository.save(hostel);

		logger.info("Hostel approved successfully - ID: {}, Name: {}, Owner: {}", hostelId, hostel.getHostelName(),
				hostel.getOwner().getName());

		return hostelMapper.toResponse(updatedHostel);
	}

	public void deleteHostel(Long hostelId) {
		logger.info("Attempting to delete hostel - ID: {}", hostelId);

		Hostel hostel = hostelRepository.findById(hostelId).orElseThrow(() -> {
			logger.error("Hostel deletion failed: Hostel not found - ID: {}", hostelId);
			return new ResourceNotFoundException("Hostel", "hostelId", hostelId);
		});

		if (hostel.getBookings() != null && !hostel.getBookings().isEmpty()) {
			logger.warn("Hostel deletion failed: Has {} active bookings - ID: {}", hostel.getBookings().size(),
					hostelId);
			throw new BadRequestException("Cannot delete hostel with active bookings");
		}

		hostelRepository.delete(hostel);
		logger.info("Hostel deleted successfully - ID: {}, Name: {}", hostelId, hostel.getHostelName());
	}

	public HostelResponse getHostelById(Long hostelId) {
		logger.info("Fetching hostel with ID: {}", hostelId);
		Hostel hostel = hostelRepository.findById(hostelId)
				.orElseThrow(() -> new ResourceNotFoundException("Hostel", "hostelId", hostelId));
		return hostelMapper.toResponse(hostel);

	}

	public List<HostelResponse> getHostelsByOwner(Long ownerId) {
		logger.info("Fetching hostels for owner ID: {}", ownerId);
		List<HostelResponse> hostels = hostelRepository.findByOwner_UserId(ownerId).stream()
				.map(hostelMapper::toResponse).collect(Collectors.toList());
		return hostels;

	}

	public HostelResponse updateHostel(Long hostelId, @Valid HostelRequest request) {
		logger.info("Updating hostel with ID: {}", hostelId);
		Hostel hostel = hostelRepository.findById(hostelId)
				.orElseThrow(() -> new ResourceNotFoundException("Hostel", "hostelId", hostelId));

		hostel.setHostelName(request.getHostelName());
		hostel.setCity(request.getCity());
		hostel.setAddress(request.getAddress());

		Hostel updatedHostel = hostelRepository.save(hostel);
		return hostelMapper.toResponse(updatedHostel);

	}

	public HostelResponse assignFacilities(Long hostelId, List<Long> facilityIds) {
		logger.info("Assigning facilities to hostel ID: {}", hostelId);
		Hostel hostel = hostelRepository.findById(hostelId)
				.orElseThrow(() -> new ResourceNotFoundException("Hostel", "hostelId", hostelId));

		Set<Facility> facilities = facilityIds.stream()
				.map(id -> facilityRepository.findById(id)
						.orElseThrow(() -> new ResourceNotFoundException("Facility", "facilityId", id)))
				.collect(Collectors.toSet());

		hostel.setFacilities(facilities);
		Hostel updatedHostel = hostelRepository.save(hostel);
		return hostelMapper.toResponse(updatedHostel);

	}

	public HostelResponse removeFacility(Long hostelId, Long facilityId) {
		logger.info("Removing facility ID: {} from hostel ID: {}", facilityId, hostelId);
		Hostel hostel = hostelRepository.findById(hostelId)
				.orElseThrow(() -> new ResourceNotFoundException("Hostel", "hostelId", hostelId));

		Facility facility = facilityRepository.findById(facilityId)
				.orElseThrow(() -> new ResourceNotFoundException("Facility", "facilityId", facilityId));

		hostel.getFacilities().remove(facility);
		Hostel updatedHostel = hostelRepository.save(hostel);
		return hostelMapper.toResponse(updatedHostel);

	}

	public List<HostelResponse> getPendingHostels() {
		logger.info("Fetching pending hostels");
		List<HostelResponse> hostels = hostelRepository.findByApproved(false).stream().map(hostelMapper::toResponse)
				.collect(Collectors.toList());
		return hostels;
	}

	public void rejectHostel(Long hostelId, String reason) {
		logger.info("Rejecting hostel ID: {} for reason: {}", hostelId, reason);
		Hostel hostel = hostelRepository.findById(hostelId)
				.orElseThrow(() -> new ResourceNotFoundException("Hostel", "hostelId", hostelId));

		hostel.setApproved(false);

		hostelRepository.save(hostel);

	}

}