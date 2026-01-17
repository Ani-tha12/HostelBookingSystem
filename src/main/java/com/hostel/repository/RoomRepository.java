package com.hostel.repository;

import com.hostel.entity.Room;
import com.hostel.entity.Hostel;
import com.hostel.enums.RoomType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface RoomRepository extends JpaRepository<Room, Long> {
    
    
    List<Room> findByHostel(Hostel hostel);
    
   
    List<Room> findByHostel_HostelId(Long hostelId);
    
 
    List<Room> findByRoomType(RoomType roomType);
    
    
    List<Room> findByHostelAndRoomType(Hostel hostel, RoomType roomType);
    
 
    @Query("SELECT r FROM Room r WHERE r.availableBeds > 0")
    List<Room> findAvailableRooms();
    
   
    @Query("SELECT r FROM Room r WHERE r.hostel.hostelId = :hostelId AND r.availableBeds > 0")
    List<Room> findAvailableRoomsByHostel(@Param("hostelId") Long hostelId);
    
   
    @Query("SELECT r FROM Room r WHERE r.pricePerNight BETWEEN :minPrice AND :maxPrice")
    List<Room> findByPriceRange(@Param("minPrice") Double minPrice, @Param("maxPrice") Double maxPrice);
}
