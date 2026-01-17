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

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.hostel.dto.request.RoomRequest;
import com.hostel.dto.response.RoomResponse;
import com.hostel.entity.Hostel;
import com.hostel.entity.Room;
import com.hostel.entity.User;
import com.hostel.enums.RoomType;
import com.hostel.enums.UserRole;
import com.hostel.exception.BadRequestException;
import com.hostel.exception.ResourceNotFoundException;
import com.hostel.mapper.RoomMapper;
import com.hostel.repository.HostelRepository;
import com.hostel.repository.RoomRepository;
import com.hostel.service.RoomService;

@ExtendWith(MockitoExtension.class)
@DisplayName("Room Service Tests")
class RoomServiceTest {
    
    @Mock
    private RoomRepository roomRepository;
    
    @Mock
    private HostelRepository hostelRepository;
    
    @Mock
    private RoomMapper roomMapper;
    
    @InjectMocks
    private RoomService roomService;
    
    private Hostel testHostel;
    private Room testRoom;
    private RoomRequest roomRequest;
    private RoomResponse roomResponse;
    
    @BeforeEach
    void setUp() {
        // Setup test owner
        User testOwner = new User();
        testOwner.setUserId(5L);
        testOwner.setName("Raj Kumar");
        testOwner.setRole(UserRole.OWNER);
        
        // Setup test hostel
        testHostel = new Hostel();
        testHostel.setHostelId(1L);
        testHostel.setHostelName("Sunshine Hostel");
        testHostel.setCity("Chennai");
        testHostel.setApproved(true);
        testHostel.setOwner(testOwner);
        
        // Setup test room
        testRoom = new Room();
        testRoom.setRoomId(10L);
        testRoom.setHostel(testHostel);
        testRoom.setRoomType(RoomType.DORM);
        testRoom.setTotalBeds(6);
        testRoom.setAvailableBeds(6);
        testRoom.setPricePerNight(300.0);
        testRoom.setDescription("6-bed dormitory");
        
        // Setup room request
        roomRequest = new RoomRequest();
        roomRequest.setHostelId(1L);
        roomRequest.setRoomType(RoomType.DORM);
        roomRequest.setTotalBeds(6);
        roomRequest.setAvailableBeds(6);
        roomRequest.setPricePerNight(300.0);
        roomRequest.setDescription("6-bed dormitory");
        
        // Setup room response
        roomResponse = new RoomResponse();
        roomResponse.setRoomId(10L);
        roomResponse.setHostelId(1L);
        roomResponse.setHostelName("Sunshine Hostel");
        roomResponse.setRoomType(RoomType.DORM);
        roomResponse.setTotalBeds(6);
        roomResponse.setAvailableBeds(6);
        roomResponse.setPricePerNight(300.0);
    }
    
    // ==========================================
    // SUCCESS TEST CASES
    // ==========================================
    
    @Test
    @DisplayName("SUCCESS: Add Room - Should create room for approved hostel")
    void testAddRoom_Success() {
        // Arrange
        when(hostelRepository.findById(1L)).thenReturn(Optional.of(testHostel));
        when(roomMapper.toEntity(any(RoomRequest.class))).thenReturn(testRoom);
        when(roomRepository.save(any(Room.class))).thenReturn(testRoom);
        when(roomMapper.toResponse(any(Room.class))).thenReturn(roomResponse);
        
        // Act
        RoomResponse result = roomService.addRoom(roomRequest);
        
        // Assert
        assertNotNull(result);
        assertEquals(10L, result.getRoomId());
        assertEquals(RoomType.DORM, result.getRoomType());
        assertEquals(6, result.getTotalBeds());
        assertEquals(300.0, result.getPricePerNight());
        
        verify(hostelRepository, times(1)).findById(1L);
        verify(roomRepository, times(1)).save(any(Room.class));
    }
    
    @Test
    @DisplayName("SUCCESS: Get Available Rooms By Hostel - Should return available rooms")
    void testGetAvailableRoomsByHostel_Success() {
        // Arrange
        List<Room> rooms = Arrays.asList(testRoom);
        
        when(roomRepository.findAvailableRoomsByHostel(1L)).thenReturn(rooms);
        when(roomMapper.toResponse(any(Room.class))).thenReturn(roomResponse);
        
        // Act
        List<RoomResponse> result = roomService.getAvailableRoomsByHostel(1L);
        
        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(6, result.get(0).getAvailableBeds());
        
        verify(roomRepository, times(1)).findAvailableRoomsByHostel(1L);
    }
    
    @Test
    @DisplayName("SUCCESS: Update Availability - Should update available beds")
    void testUpdateAvailability_Success() {
        // Arrange
        when(roomRepository.findById(10L)).thenReturn(Optional.of(testRoom));
        when(roomRepository.save(any(Room.class))).thenReturn(testRoom);
        when(roomMapper.toResponse(any(Room.class))).thenReturn(roomResponse);
        
        // Act
        RoomResponse result = roomService.updateAvailability(10L, 4);
        
        // Assert
        assertNotNull(result);
        assertEquals(4, testRoom.getAvailableBeds());
        
        verify(roomRepository, times(1)).save(testRoom);
    }
    
    @Test
    @DisplayName("SUCCESS: Check Availability - Should return true when beds available")
    void testCheckAvailability_Success() {
        // Arrange
        when(roomRepository.findById(10L)).thenReturn(Optional.of(testRoom));
        
        // Act
        boolean result = roomService.checkAvailability(10L, 3);
        
        // Assert
        assertTrue(result);
        
        verify(roomRepository, times(1)).findById(10L);
    }
    
    @Test
    @DisplayName("SUCCESS: Get All Rooms - Should return all rooms")
    void testGetAllRooms_Success() {
        // Arrange
        List<Room> rooms = Arrays.asList(testRoom);
        
        when(roomRepository.findAll()).thenReturn(rooms);
        when(roomMapper.toResponse(any(Room.class))).thenReturn(roomResponse);
        
        // Act
        List<RoomResponse> result = roomService.getAllRooms();
        
        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        
        verify(roomRepository, times(1)).findAll();
    }
    
    @Test
    @DisplayName("SUCCESS: Get Room By ID - Should return room details")
    void testGetRoomById_Success() {
        // Arrange
        when(roomRepository.findById(10L)).thenReturn(Optional.of(testRoom));
        when(roomMapper.toResponse(any(Room.class))).thenReturn(roomResponse);
        
        // Act
        RoomResponse result = roomService.getRoomById(10L);
        
        // Assert
        assertNotNull(result);
        assertEquals(10L, result.getRoomId());
        assertEquals("Sunshine Hostel", result.getHostelName());
    }
    
    @Test
    @DisplayName("SUCCESS: Get Rooms By Hostel - Should return hostel rooms")
    void testGetRoomsByHostel_Success() {
        // Arrange
        List<Room> rooms = Arrays.asList(testRoom);
        
        when(roomRepository.findByHostel_HostelId(1L)).thenReturn(rooms);
        when(roomMapper.toResponse(any(Room.class))).thenReturn(roomResponse);
        
        // Act
        List<RoomResponse> result = roomService.getRoomsByHostel(1L);
        
        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
    }
    
    @Test
    @DisplayName("SUCCESS: Get Available Rooms - Should return rooms with available beds")
    void testGetAvailableRooms_Success() {
        // Arrange
        testRoom.setAvailableBeds(3);
        List<Room> rooms = Arrays.asList(testRoom);
        
        when(roomRepository.findAll()).thenReturn(rooms);
        when(roomMapper.toResponse(any(Room.class))).thenReturn(roomResponse);
        
        // Act
        List<RoomResponse> result = roomService.getAvailableRooms();
        
        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
    }
    
    @Test
    @DisplayName("SUCCESS: Update Room - Should update room details")
    void testUpdateRoom_Success() {
        // Arrange
        RoomRequest updateRequest = new RoomRequest();
        updateRequest.setRoomType(RoomType.PRIVATE);
        updateRequest.setTotalBeds(2);
        updateRequest.setAvailableBeds(2);
        updateRequest.setPricePerNight(800.0);
        
        when(roomRepository.findById(10L)).thenReturn(Optional.of(testRoom));
        when(roomRepository.save(any(Room.class))).thenReturn(testRoom);
        when(roomMapper.toResponse(any(Room.class))).thenReturn(roomResponse);
        
        // Act
        RoomResponse result = roomService.updateRoom(10L, updateRequest);
        
        // Assert
        assertNotNull(result);
        verify(roomRepository, times(1)).save(testRoom);
    }
    
    @Test
    @DisplayName("SUCCESS: Delete Room - Should delete room")
    void testDeleteRoom_Success() {
        // Arrange
        when(roomRepository.findById(10L)).thenReturn(Optional.of(testRoom));
        doNothing().when(roomRepository).delete(any(Room.class));
        
        // Act
        roomService.deleteRoom(10L);
        
        // Assert
        verify(roomRepository, times(1)).delete(testRoom);
    }
    
    // ==========================================
    // FAILURE TEST CASES
    // ==========================================
    
    @Test
    @DisplayName("FAILURE: Add Room - Hostel not found")
    void testAddRoom_HostelNotFound_ThrowsException() {
        // Arrange
        when(hostelRepository.findById(999L)).thenReturn(Optional.empty());
        roomRequest.setHostelId(999L);
        
        // Act & Assert
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
            roomService.addRoom(roomRequest);
        });
        
        assertTrue(exception.getMessage().contains("Hostel"));
        verify(roomRepository, never()).save(any(Room.class));
    }
    
    @Test
    @DisplayName("FAILURE: Add Room - Hostel not approved")
    void testAddRoom_HostelNotApproved_ThrowsException() {
        // Arrange
        testHostel.setApproved(false);
        
        when(hostelRepository.findById(1L)).thenReturn(Optional.of(testHostel));
        
        // Act & Assert
        BadRequestException exception = assertThrows(BadRequestException.class, () -> {
            roomService.addRoom(roomRequest);
        });
        
        assertEquals("Cannot add room to unapproved hostel", exception.getMessage());
    }
    
    @Test
    @DisplayName("FAILURE: Update Availability - Room not found")
    void testUpdateAvailability_RoomNotFound_ThrowsException() {
        // Arrange
        when(roomRepository.findById(999L)).thenReturn(Optional.empty());
        
        // Act & Assert
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
            roomService.updateAvailability(999L, 5);
        });
        
        assertTrue(exception.getMessage().contains("Room"));
    }
    
    @Test
    @DisplayName("FAILURE: Update Availability - Invalid beds count (negative)")
    void testUpdateAvailability_NegativeBeds_ThrowsException() {
        // Arrange
        when(roomRepository.findById(10L)).thenReturn(Optional.of(testRoom));
        
        // Act & Assert
        BadRequestException exception = assertThrows(BadRequestException.class, () -> {
            roomService.updateAvailability(10L, -1);
        });
        
        assertEquals("Invalid available beds count", exception.getMessage());
    }
    
    @Test
    @DisplayName("FAILURE: Update Availability - Exceeds total beds")
    void testUpdateAvailability_ExceedsTotalBeds_ThrowsException() {
        // Arrange
        when(roomRepository.findById(10L)).thenReturn(Optional.of(testRoom));
        
        // Act & Assert
        BadRequestException exception = assertThrows(BadRequestException.class, () -> {
            roomService.updateAvailability(10L, 10); // Total is 6
        });
        
        assertEquals("Invalid available beds count", exception.getMessage());
    }
    
    @Test
    @DisplayName("FAILURE: Check Availability - Room not found")
    void testCheckAvailability_RoomNotFound_ThrowsException() {
        // Arrange
        when(roomRepository.findById(999L)).thenReturn(Optional.empty());
        
        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> {
            roomService.checkAvailability(999L, 2);
        });
    }
    
    @Test
    @DisplayName("FAILURE: Check Availability - Not enough beds")
    void testCheckAvailability_NotEnoughBeds_ReturnsFalse() {
        // Arrange
        testRoom.setAvailableBeds(2);
        when(roomRepository.findById(10L)).thenReturn(Optional.of(testRoom));
        
        // Act
        boolean result = roomService.checkAvailability(10L, 5);
        
        // Assert
        assertFalse(result);
    }
    
    @Test
    @DisplayName("FAILURE: Get Room By ID - Not found")
    void testGetRoomById_NotFound_ThrowsException() {
        // Arrange
        when(roomRepository.findById(999L)).thenReturn(Optional.empty());
        
        // Act & Assert
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
            roomService.getRoomById(999L);
        });
        
        assertTrue(exception.getMessage().contains("Room"));
    }
    
    @Test
    @DisplayName("FAILURE: Update Room - Room not found")
    void testUpdateRoom_NotFound_ThrowsException() {
        // Arrange
        when(roomRepository.findById(999L)).thenReturn(Optional.empty());
        
        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> {
            roomService.updateRoom(999L, roomRequest);
        });
    }
    
    @Test
    @DisplayName("FAILURE: Delete Room - Room not found")
    void testDeleteRoom_NotFound_ThrowsException() {
        // Arrange
        when(roomRepository.findById(999L)).thenReturn(Optional.empty());
        
        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> {
            roomService.deleteRoom(999L);
        });
    }
}