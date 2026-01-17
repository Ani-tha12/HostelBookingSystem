package com.hostel.mapper;

import com.hostel.entity.Room;
import com.hostel.dto.request.RoomRequest;
import com.hostel.dto.response.RoomResponse;
import org.springframework.stereotype.Component;

@Component
public class RoomMapper {
    
    
    public Room toEntity(RoomRequest request) {
        Room room = new Room();
        room.setRoomType(request.getRoomType());
        room.setTotalBeds(request.getTotalBeds());
        room.setAvailableBeds(request.getTotalBeds()); // Initially all beds available
        room.setPricePerNight(request.getPricePerNight());
        room.setDescription(request.getDescription());
        return room;
    }
    
   
    public RoomResponse toResponse(Room room) {
        RoomResponse response = new RoomResponse();
        response.setRoomId(room.getRoomId());
        response.setRoomType(room.getRoomType());
        response.setTotalBeds(room.getTotalBeds());
        response.setAvailableBeds(room.getAvailableBeds());
        response.setPricePerNight(room.getPricePerNight());
        response.setDescription(room.getDescription());
        
        if (room.getHostel() != null) {
            response.setHostelId(room.getHostel().getHostelId());
            response.setHostelName(room.getHostel().getHostelName());
        }
        
        return response;
    }
    
   
    public void updateEntity(Room room, RoomRequest request) {
        if (request.getTotalBeds() != null) {
            room.setTotalBeds(request.getTotalBeds());
        }
        if (request.getPricePerNight() != null) {
            room.setPricePerNight(request.getPricePerNight());
        }
        if (request.getDescription() != null) {
            room.setDescription(request.getDescription());
        }
    }
}