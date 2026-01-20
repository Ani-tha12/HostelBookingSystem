package com.hostel.test.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.hostel.dto.request.HostelRequest;
import com.hostel.dto.response.HostelResponse;
import com.hostel.entity.Facility;
import com.hostel.entity.Hostel;
import com.hostel.entity.User;
import com.hostel.enums.UserRole;
import com.hostel.enums.UserStatus;
import com.hostel.exception.BadRequestException;
import com.hostel.exception.ResourceNotFoundException;
import com.hostel.mapper.HostelMapper;
import com.hostel.repository.FacilityRepository;
import com.hostel.repository.HostelRepository;
import com.hostel.repository.UserRepository;
import com.hostel.service.HostelService;

@ExtendWith(MockitoExtension.class)
@DisplayName("Hostel Service Tests")
class HostelServiceTest {

	@Mock
	private HostelRepository hostelRepository;

	@Mock
	private UserRepository userRepository;

	@Mock
	private FacilityRepository facilityRepository;

	@Mock
	private HostelMapper hostelMapper;

	@InjectMocks
	private HostelService hostelService;

	private User testOwner;
	private Hostel testHostel;
	private HostelRequest hostelRequest;
	private HostelResponse hostelResponse;
	private Facility testFacility;

	@BeforeEach
	void setUp() {

		testOwner = new User();
		testOwner.setUserId(5L);
		testOwner.setName("Raj Kumar");
		testOwner.setEmail("raj@gmail.com");
		testOwner.setRole(UserRole.OWNER);
		testOwner.setStatus(UserStatus.APPROVED);

		testHostel = new Hostel();
		testHostel.setHostelId(1L);
		testHostel.setHostelName("Sunshine Hostel");
		testHostel.setCity("Chennai");
		testHostel.setAddress("123 Beach Road");
		testHostel.setDescription("Budget-friendly hostel");
		testHostel.setApproved(false);
		testHostel.setOwner(testOwner);
		testHostel.setBookings(new ArrayList<>());
		testHostel.setFacilities(new HashSet<>());

		testFacility = new Facility();
		testFacility.setFacilityId(1L);
		testFacility.setFacilityName("WiFi");

		hostelRequest = new HostelRequest();
		hostelRequest.setOwnerId(5L);
		hostelRequest.setHostelName("Sunshine Hostel");
		hostelRequest.setCity("Chennai");
		hostelRequest.setAddress("123 Beach Road");
		hostelRequest.setDescription("Budget-friendly hostel");
		hostelRequest.setFacilityIds(Arrays.asList(1L));

		hostelResponse = new HostelResponse();
		hostelResponse.setHostelId(1L);
		hostelResponse.setHostelName("Sunshine Hostel");
		hostelResponse.setCity("Chennai");
		hostelResponse.setApproved(false);
		hostelResponse.setOwnerId(5L);
		hostelResponse.setOwnerName("Raj Kumar");
	}

	@Test
	@DisplayName("SUCCESS: Add Hostel - Should create hostel with PENDING approval")
	void testAddHostel_Success() {

		when(userRepository.findById(5L)).thenReturn(Optional.of(testOwner));
		when(facilityRepository.findById(1L)).thenReturn(Optional.of(testFacility));
		when(hostelMapper.toEntity(any(HostelRequest.class))).thenReturn(testHostel);
		when(hostelRepository.save(any(Hostel.class))).thenReturn(testHostel);
		when(hostelMapper.toResponse(any(Hostel.class))).thenReturn(hostelResponse);

		HostelResponse result = hostelService.addHostel(hostelRequest);

		assertNotNull(result);
		assertEquals("Sunshine Hostel", result.getHostelName());
		assertEquals(false, result.getApproved());
		assertEquals(5L, result.getOwnerId());

		verify(userRepository, times(1)).findById(5L);
		verify(hostelRepository, times(1)).save(any(Hostel.class));
	}

	@Test
	@DisplayName("SUCCESS: Get Approved Hostels - Should return only approved hostels")
	void testGetApprovedHostels_Success() {

		testHostel.setApproved(true);
		List<Hostel> hostels = Arrays.asList(testHostel);

		when(hostelRepository.findByApproved(true)).thenReturn(hostels);
		when(hostelMapper.toResponse(any(Hostel.class))).thenReturn(hostelResponse);

		List<HostelResponse> result = hostelService.getApprovedHostels();

		assertNotNull(result);
		assertEquals(1, result.size());

		verify(hostelRepository, times(1)).findByApproved(true);
	}

	@Test
	@DisplayName("SUCCESS: Search Hostels By City - Should return hostels in city")
	void testSearchHostelsByCity_Success() {

		testHostel.setApproved(true);
		List<Hostel> hostels = Arrays.asList(testHostel);

		when(hostelRepository.findByCityAndApproved("Chennai", true)).thenReturn(hostels);
		when(hostelMapper.toResponse(any(Hostel.class))).thenReturn(hostelResponse);

		List<HostelResponse> result = hostelService.searchHostelsByCity("Chennai");

		assertNotNull(result);
		assertEquals(1, result.size());
		assertEquals("Chennai", hostelResponse.getCity());

		verify(hostelRepository, times(1)).findByCityAndApproved("Chennai", true);
	}

	@Test
	@DisplayName("SUCCESS: Approve Hostel - Should set approved to true")
	void testApproveHostel_Success() {

		when(hostelRepository.findById(1L)).thenReturn(Optional.of(testHostel));
		when(hostelRepository.save(any(Hostel.class))).thenReturn(testHostel);
		when(hostelMapper.toResponse(any(Hostel.class))).thenReturn(hostelResponse);

		HostelResponse result = hostelService.approveHostel(1L);

		assertNotNull(result);
		assertTrue(testHostel.getApproved());

		verify(hostelRepository, times(1)).save(testHostel);
	}

	@Test
	@DisplayName("SUCCESS: Get Hostel By ID - Should return hostel details")
	void testGetHostelById_Success() {

		when(hostelRepository.findById(1L)).thenReturn(Optional.of(testHostel));
		when(hostelMapper.toResponse(any(Hostel.class))).thenReturn(hostelResponse);

		HostelResponse result = hostelService.getHostelById(1L);

		assertNotNull(result);
		assertEquals(1L, result.getHostelId());
		assertEquals("Sunshine Hostel", result.getHostelName());
	}

	@Test
	@DisplayName("SUCCESS: Get Hostels By Owner - Should return owner's hostels")
	void testGetHostelsByOwner_Success() {

		List<Hostel> hostels = Arrays.asList(testHostel);

		when(hostelRepository.findByOwner_UserId(5L)).thenReturn(hostels);
		when(hostelMapper.toResponse(any(Hostel.class))).thenReturn(hostelResponse);

		List<HostelResponse> result = hostelService.getHostelsByOwner(5L);

		assertNotNull(result);
		assertEquals(1, result.size());
	}

	@Test
	@DisplayName("SUCCESS: Update Hostel - Should update hostel details")
	void testUpdateHostel_Success() {

		HostelRequest updateRequest = new HostelRequest();
		updateRequest.setHostelName("Sunshine Premium Hostel");
		updateRequest.setCity("Chennai");
		updateRequest.setAddress("456 New Address");

		when(hostelRepository.findById(1L)).thenReturn(Optional.of(testHostel));
		when(hostelRepository.save(any(Hostel.class))).thenReturn(testHostel);
		when(hostelMapper.toResponse(any(Hostel.class))).thenReturn(hostelResponse);

		HostelResponse result = hostelService.updateHostel(1L, updateRequest);

		assertNotNull(result);
		verify(hostelRepository, times(1)).save(testHostel);
	}

	@Test
	@DisplayName("SUCCESS: Assign Facilities - Should assign facilities to hostel")
	void testAssignFacilities_Success() {

		List<Long> facilityIds = Arrays.asList(1L);

		when(hostelRepository.findById(1L)).thenReturn(Optional.of(testHostel));
		when(facilityRepository.findById(1L)).thenReturn(Optional.of(testFacility));
		when(hostelRepository.save(any(Hostel.class))).thenReturn(testHostel);
		when(hostelMapper.toResponse(any(Hostel.class))).thenReturn(hostelResponse);

		HostelResponse result = hostelService.assignFacilities(1L, facilityIds);

		assertNotNull(result);
		verify(hostelRepository, times(1)).save(testHostel);
	}

	@Test
	@DisplayName("SUCCESS: Remove Facility - Should remove facility from hostel")
	void testRemoveFacility_Success() {

		testHostel.getFacilities().add(testFacility);

		when(hostelRepository.findById(1L)).thenReturn(Optional.of(testHostel));
		when(facilityRepository.findById(1L)).thenReturn(Optional.of(testFacility));
		when(hostelRepository.save(any(Hostel.class))).thenReturn(testHostel);
		when(hostelMapper.toResponse(any(Hostel.class))).thenReturn(hostelResponse);

		HostelResponse result = hostelService.removeFacility(1L, 1L);

		assertNotNull(result);
		verify(hostelRepository, times(1)).save(testHostel);
	}

	@Test
	@DisplayName("SUCCESS: Get Pending Hostels - Should return pending hostels")
	void testGetPendingHostels_Success() {

		List<Hostel> hostels = Arrays.asList(testHostel);

		when(hostelRepository.findByApproved(false)).thenReturn(hostels);
		when(hostelMapper.toResponse(any(Hostel.class))).thenReturn(hostelResponse);

		List<HostelResponse> result = hostelService.getPendingHostels();

		assertNotNull(result);
		assertEquals(1, result.size());
	}

	@Test
	@DisplayName("SUCCESS: Reject Hostel - Should keep approved as false")
	void testRejectHostel_Success() {

		when(hostelRepository.findById(1L)).thenReturn(Optional.of(testHostel));
		when(hostelRepository.save(any(Hostel.class))).thenReturn(testHostel);

		hostelService.rejectHostel(1L, "Invalid information");

		assertFalse(testHostel.getApproved());
		verify(hostelRepository, times(1)).save(testHostel);
	}

	@Test
	@DisplayName("SUCCESS: Delete Hostel - Should delete hostel without bookings")
	void testDeleteHostel_Success() {

		when(hostelRepository.findById(1L)).thenReturn(Optional.of(testHostel));
		doNothing().when(hostelRepository).delete(any(Hostel.class));

		hostelService.deleteHostel(1L);

		verify(hostelRepository, times(1)).delete(testHostel);
	}

	@Test
	@DisplayName("FAILURE: Add Hostel - Owner not found")
	void testAddHostel_OwnerNotFound_ThrowsException() {

		when(userRepository.findById(999L)).thenReturn(Optional.empty());
		hostelRequest.setOwnerId(999L);

		ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
			hostelService.addHostel(hostelRequest);
		});

		assertTrue(exception.getMessage().contains("User"));
		verify(hostelRepository, never()).save(any(Hostel.class));
	}

	@Test
	@DisplayName("FAILURE: Add Hostel - Owner not approved")
	void testAddHostel_OwnerNotApproved_ThrowsException() {

		testOwner.setStatus(UserStatus.PENDING);

		when(userRepository.findById(5L)).thenReturn(Optional.of(testOwner));

		BadRequestException exception = assertThrows(BadRequestException.class, () -> {
			hostelService.addHostel(hostelRequest);
		});

		assertEquals("Owner account is not approved", exception.getMessage());
	}

	@Test
	@DisplayName("FAILURE: Add Hostel - Facility not found")
	void testAddHostel_FacilityNotFound_ThrowsException() {

		when(userRepository.findById(5L)).thenReturn(Optional.of(testOwner));
		when(hostelMapper.toEntity(any(HostelRequest.class))).thenReturn(testHostel);
		when(facilityRepository.findById(999L)).thenReturn(Optional.empty());

		hostelRequest.setFacilityIds(Arrays.asList(999L));

		assertThrows(ResourceNotFoundException.class, () -> {
			hostelService.addHostel(hostelRequest);
		});
	}

	@Test
	@DisplayName("FAILURE: Get Hostel By ID - Not found")
	void testGetHostelById_NotFound_ThrowsException() {

		when(hostelRepository.findById(999L)).thenReturn(Optional.empty());

		ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
			hostelService.getHostelById(999L);
		});

		assertTrue(exception.getMessage().contains("Hostel"));
	}

	@Test
	@DisplayName("FAILURE: Approve Hostel - Not found")
	void testApproveHostel_NotFound_ThrowsException() {

		when(hostelRepository.findById(999L)).thenReturn(Optional.empty());

		assertThrows(ResourceNotFoundException.class, () -> {
			hostelService.approveHostel(999L);
		});
	}

	@Test
	@DisplayName("FAILURE: Update Hostel - Not found")
	void testUpdateHostel_NotFound_ThrowsException() {

		when(hostelRepository.findById(999L)).thenReturn(Optional.empty());

		assertThrows(ResourceNotFoundException.class, () -> {
			hostelService.updateHostel(999L, hostelRequest);
		});
	}

	@Test
	@DisplayName("FAILURE: Delete Hostel - Has active bookings")
	void testDeleteHostel_HasBookings_ThrowsException() {

		testHostel.setBookings(Arrays.asList(new com.hostel.entity.Booking()));
		when(hostelRepository.findById(1L)).thenReturn(Optional.of(testHostel));

		BadRequestException exception = assertThrows(BadRequestException.class, () -> {
			hostelService.deleteHostel(1L);
		});

		assertEquals("Cannot delete hostel with active bookings", exception.getMessage());
		verify(hostelRepository, never()).delete(any(Hostel.class));
	}

	@Test
	@DisplayName("FAILURE: Assign Facilities - Hostel not found")
	void testAssignFacilities_HostelNotFound_ThrowsException() {

		when(hostelRepository.findById(999L)).thenReturn(Optional.empty());

		assertThrows(ResourceNotFoundException.class, () -> {
			hostelService.assignFacilities(999L, Arrays.asList(1L));
		});
	}

	@Test
	@DisplayName("FAILURE: Remove Facility - Facility not found")
	void testRemoveFacility_FacilityNotFound_ThrowsException() {

		when(hostelRepository.findById(1L)).thenReturn(Optional.of(testHostel));
		when(facilityRepository.findById(999L)).thenReturn(Optional.empty());

		assertThrows(ResourceNotFoundException.class, () -> {
			hostelService.removeFacility(1L, 999L);
		});
	}
}