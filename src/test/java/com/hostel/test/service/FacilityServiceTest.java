package com.hostel.test.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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

import com.hostel.dto.request.FacilityRequest;
import com.hostel.dto.response.FacilityResponse;
import com.hostel.entity.Facility;
import com.hostel.entity.Hostel;
import com.hostel.exception.BadRequestException;
import com.hostel.exception.ResourceNotFoundException;
import com.hostel.mapper.FacilityMapper;
import com.hostel.repository.FacilityRepository;
import com.hostel.service.FacilityService;

@ExtendWith(MockitoExtension.class)
@DisplayName("Facility Service Tests")
class FacilityServiceTest {

	@Mock
	private FacilityRepository facilityRepository;

	@Mock
	private FacilityMapper facilityMapper;

	@InjectMocks
	private FacilityService facilityService;

	private Facility testFacility;
	private FacilityRequest facilityRequest;
	private FacilityResponse facilityResponse;

	@BeforeEach
	void setUp() {

		testFacility = new Facility();
		testFacility.setFacilityId(1L);
		testFacility.setFacilityName("WiFi");
		testFacility.setHostels(new HashSet<>());

		facilityRequest = new FacilityRequest();
		facilityRequest.setFacilityName("WiFi");

		facilityResponse = new FacilityResponse();
		facilityResponse.setFacilityId(1L);
		facilityResponse.setFacilityName("WiFi");
	}

	@Test
	@DisplayName("SUCCESS: Add Facility - Should create new facility")
	void testAddFacility_Success() {

		when(facilityRepository.existsByFacilityName(anyString())).thenReturn(false);
		when(facilityMapper.toEntity(any(FacilityRequest.class))).thenReturn(testFacility);
		when(facilityRepository.save(any(Facility.class))).thenReturn(testFacility);
		when(facilityMapper.toResponse(any(Facility.class))).thenReturn(facilityResponse);

		FacilityResponse result = facilityService.addFacility(facilityRequest);

		assertNotNull(result);
		assertEquals(1L, result.getFacilityId());
		assertEquals("WiFi", result.getFacilityName());

		verify(facilityRepository, times(1)).existsByFacilityName("WiFi");
		verify(facilityRepository, times(1)).save(any(Facility.class));
	}

	@Test
	@DisplayName("SUCCESS: Get All Facilities - Should return all facilities")
	void testGetAllFacilities_Success() {

		List<Facility> facilities = Arrays.asList(testFacility);

		when(facilityRepository.findAll()).thenReturn(facilities);
		when(facilityMapper.toResponse(any(Facility.class))).thenReturn(facilityResponse);

		List<FacilityResponse> result = facilityService.getAllFacilities();

		assertNotNull(result);
		assertEquals(1, result.size());
		assertEquals("WiFi", result.get(0).getFacilityName());

		verify(facilityRepository, times(1)).findAll();
	}

	@Test
	@DisplayName("SUCCESS: Get Facility By ID - Should return facility details")
	void testGetFacilityById_Success() {

		when(facilityRepository.findById(1L)).thenReturn(Optional.of(testFacility));
		when(facilityMapper.toResponse(any(Facility.class))).thenReturn(facilityResponse);

		FacilityResponse result = facilityService.getFacilityById(1L);

		assertNotNull(result);
		assertEquals(1L, result.getFacilityId());
		assertEquals("WiFi", result.getFacilityName());

		verify(facilityRepository, times(1)).findById(1L);
	}

	@Test
	@DisplayName("SUCCESS: Update Facility - Should update facility name")
	void testUpdateFacility_Success() {

		FacilityRequest updateRequest = new FacilityRequest();
		updateRequest.setFacilityName("High-Speed WiFi");

		when(facilityRepository.findById(1L)).thenReturn(Optional.of(testFacility));
		when(facilityRepository.save(any(Facility.class))).thenReturn(testFacility);
		when(facilityMapper.toResponse(any(Facility.class))).thenReturn(facilityResponse);

		FacilityResponse result = facilityService.updateFacility(1L, updateRequest);

		assertNotNull(result);
		assertEquals("High-Speed WiFi", testFacility.getFacilityName());

		verify(facilityRepository, times(1)).save(testFacility);
	}

	@Test
	@DisplayName("SUCCESS: Delete Facility - Should delete facility without hostels")
	void testDeleteFacility_Success() {

		when(facilityRepository.findById(1L)).thenReturn(Optional.of(testFacility));
		doNothing().when(facilityRepository).delete(any(Facility.class));

		facilityService.deleteFacility(1L);

		verify(facilityRepository, times(1)).delete(testFacility);
	}

	@Test
	@DisplayName("FAILURE: Add Facility - Facility already exists")
	void testAddFacility_AlreadyExists_ThrowsException() {

		when(facilityRepository.existsByFacilityName(anyString())).thenReturn(true);

		BadRequestException exception = assertThrows(BadRequestException.class, () -> {
			facilityService.addFacility(facilityRequest);
		});

		assertEquals("Facility already exists", exception.getMessage());
		verify(facilityRepository, never()).save(any(Facility.class));
	}

	@Test
	@DisplayName("FAILURE: Get Facility By ID - Not found")
	void testGetFacilityById_NotFound_ThrowsException() {

		when(facilityRepository.findById(999L)).thenReturn(Optional.empty());

		ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
			facilityService.getFacilityById(999L);
		});

		assertTrue(exception.getMessage().contains("Facility"));
	}

	@Test
	@DisplayName("FAILURE: Update Facility - Not found")
	void testUpdateFacility_NotFound_ThrowsException() {

		when(facilityRepository.findById(999L)).thenReturn(Optional.empty());

		assertThrows(ResourceNotFoundException.class, () -> {
			facilityService.updateFacility(999L, facilityRequest);
		});
	}

	@Test
	@DisplayName("FAILURE: Delete Facility - Not found")
	void testDeleteFacility_NotFound_ThrowsException() {

		when(facilityRepository.findById(999L)).thenReturn(Optional.empty());

		ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
			facilityService.deleteFacility(999L);
		});

		assertTrue(exception.getMessage().contains("Facility"));
	}

	@Test
	@DisplayName("FAILURE: Delete Facility - Assigned to hostels")
	void testDeleteFacility_AssignedToHostels_ThrowsException() {

		Hostel hostel = new Hostel();
		hostel.setHostelId(1L);
		testFacility.setHostels(new HashSet<>(Arrays.asList(hostel)));

		when(facilityRepository.findById(1L)).thenReturn(Optional.of(testFacility));

		BadRequestException exception = assertThrows(BadRequestException.class, () -> {
			facilityService.deleteFacility(1L);
		});

		assertEquals("Cannot delete facility assigned to hostels", exception.getMessage());
		verify(facilityRepository, never()).delete(any(Facility.class));
	}
}