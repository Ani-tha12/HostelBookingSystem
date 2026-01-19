package com.hostel.service;

import com.hostel.entity.Room;
import com.hostel.entity.Hostel;
import com.hostel.dto.request.RoomRequest;
import com.hostel.dto.response.RoomResponse;
import com.hostel.repository.RoomRepository;

import jakarta.validation.Valid;

import com.hostel.repository.HostelRepository;
import com.hostel.mapper.RoomMapper;
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
public class RoomService {
    
  
    private static final Logger logger = LoggerFactory.getLogger(RoomService.class);
    
    @Autowired
    private RoomRepository roomRepository;
    
    @Autowired
    private HostelRepository hostelRepository;
    
    @Autowired
    private RoomMapper roomMapper;
    
  
    public RoomResponse addRoom(RoomRequest request) {
        logger.info("Adding new room - Hostel ID: {}, Type: {}, Beds: {}, Price: {}", 
                   request.getHostelId(), request.getRoomType(), 
                   request.getTotalBeds(), request.getPricePerNight());
        
        try {
          
            Hostel hostel = hostelRepository.findById(request.getHostelId())
                .orElseThrow(() -> {
                    logger.error("Room creation failed: Hostel not found - ID: {}", request.getHostelId());
                    return new ResourceNotFoundException("Hostel", "hostelId", request.getHostelId());
                });
            
            logger.debug("Hostel validated - ID: {}, Name: {}, Approved: {}", 
                        hostel.getHostelId(), hostel.getHostelName(), hostel.getApproved());
            
           
            if (!hostel.getApproved()) {
                logger.warn("Room creation failed: Hostel not approved - Hostel ID: {}", 
                           request.getHostelId());
                throw new BadRequestException("Cannot add room to unapproved hostel");
            }
            
           
            Room room = roomMapper.toEntity(request);
            room.setHostel(hostel);
            
           
            Room savedRoom = roomRepository.save(room);
            
            logger.info("Room created successfully - ID: {}, Hostel: {}, Type: {}, Beds: {}", 
                       savedRoom.getRoomId(), hostel.getHostelName(), 
                       savedRoom.getRoomType(), savedRoom.getTotalBeds());
            
            return roomMapper.toResponse(savedRoom);
            
        } catch (ResourceNotFoundException | BadRequestException e) {
            logger.error("Room creation failed: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            logger.error("Unexpected error during room creation", e);
            throw new RuntimeException("Room creation failed", e);
        }
    }
    
   
    public List<RoomResponse> getAvailableRoomsByHostel(Long hostelId) {
        logger.info("Fetching available rooms for hostel ID: {}", hostelId);
        
        List<RoomResponse> rooms = roomRepository.findAvailableRoomsByHostel(hostelId).stream()
            .map(roomMapper::toResponse)
            .collect(Collectors.toList());
        
        logger.info("Retrieved {} available rooms for hostel ID: {}", rooms.size(), hostelId);
        return rooms;
    }
    
    
    public RoomResponse updateAvailability(Long roomId, Integer availableBeds) {
        logger.info("Updating room availability - Room ID: {}, New availability: {}", 
                   roomId, availableBeds);
        
        Room room = roomRepository.findById(roomId)
            .orElseThrow(() -> {
                logger.error("Availability update failed: Room not found - ID: {}", roomId);
                return new ResourceNotFoundException("Room", "roomId", roomId);
            });
        
        int previousAvailability = room.getAvailableBeds();
        
       
        if (availableBeds < 0 || availableBeds > room.getTotalBeds()) {
            logger.warn("Availability update failed: Invalid value - Room ID: {}, Value: {}, Total beds: {}", 
                       roomId, availableBeds, room.getTotalBeds());
            throw new BadRequestException("Invalid available beds count");
        }
        
        room.setAvailableBeds(availableBeds);
        Room updatedRoom = roomRepository.save(room);
        
        logger.info("Room availability updated - Room ID: {}, Previous: {}, New: {}", 
                   roomId, previousAvailability, availableBeds);
        
        return roomMapper.toResponse(updatedRoom);
    }
    

    public boolean checkAvailability(Long roomId, Integer requiredBeds) {
        logger.debug("Checking room availability - Room ID: {}, Required beds: {}", 
                    roomId, requiredBeds);
        
        Room room = roomRepository.findById(roomId)
            .orElseThrow(() -> {
                logger.error("Availability check failed: Room not found - ID: {}", roomId);
                return new ResourceNotFoundException("Room", "roomId", roomId);
            });
        
        boolean available = room.getAvailableBeds() >= requiredBeds;
        
        logger.debug("Room availability check result - Room ID: {}, Available: {}, Required: {}, Result: {}", 
                    roomId, room.getAvailableBeds(), requiredBeds, available);
        
        return available;
    }


	public List<RoomResponse> getAllRooms() {
	    logger.info("Fetching all rooms");
	    return roomRepository.findAll().stream()
	            .map(roomMapper::toResponse)
	            .collect(Collectors.toList());
	}


	public RoomResponse getRoomById(Long roomId) {
	    logger.info("Fetching room with ID: {}", roomId);
	    Room room = roomRepository.findById(roomId)
	            .orElseThrow(() -> new ResourceNotFoundException("Room", "roomId", roomId));
	    return roomMapper.toResponse(room);
	}


	public List<RoomResponse> getRoomsByHostel(Long hostelId) {
	    logger.info("Fetching rooms for hostel ID: {}", hostelId);
	    List<RoomResponse> rooms = roomRepository.findByHostel_HostelId(hostelId).stream()
	            .map(roomMapper::toResponse)
	            .collect(Collectors.toList());
	    return rooms;
	}

	public List<RoomResponse> getAvailableRooms() {
	    logger.info("Fetching all available rooms");
	    List<RoomResponse> rooms = roomRepository.findAll().stream()
	            .filter(room -> room.getAvailableBeds() > 0)
	            .map(roomMapper::toResponse)
	            .collect(Collectors.toList());
	    return rooms;
	}


	public RoomResponse updateRoom(Long roomId, RoomRequest request) {
	    logger.info("Updating room with ID: {}", roomId);
	    Room room = roomRepository.findById(roomId)
	            .orElseThrow(() -> new ResourceNotFoundException("Room", "roomId", roomId));

	  
	    room.setRoomType(request.getRoomType());
	    room.setTotalBeds(request.getTotalBeds());
	    room.setPricePerNight(request.getPricePerNight());
	    room.setAvailableBeds(request.getAvailableBeds());

	    Room updatedRoom = roomRepository.save(room);
	    return roomMapper.toResponse(updatedRoom);
	}

	public void deleteRoom(Long roomId) {
	    logger.info("Deleting room with ID: {}", roomId);
	    Room room = roomRepository.findById(roomId)
	            .orElseThrow(() -> new ResourceNotFoundException("Room", "roomId", roomId));
	    roomRepository.delete(room);
	}
}
