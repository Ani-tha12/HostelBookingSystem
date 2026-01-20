package com.hostel.repository;

import com.hostel.entity.Booking;
import com.hostel.entity.User;
import com.hostel.entity.Hostel;
import com.hostel.entity.Room;
import com.hostel.enums.BookingStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.springframework.data.repository.query.Param;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {

	List<Booking> findByUser(User user);

	List<Booking> findByUser_UserId(Long userId);

	List<Booking> findByHostel(Hostel hostel);

	List<Booking> findByHostel_HostelId(Long hostelId);

	List<Booking> findByRoom(Room room);

	List<Booking> findByBookingStatus(BookingStatus status);

	List<Booking> findByUserAndBookingStatus(User user, BookingStatus status);

	@Query("SELECT b FROM Booking b WHERE b.hostel.owner.userId = :ownerId")
	List<Booking> findBookingsByOwner(@Param("ownerId") Long ownerId);

	@Query("SELECT b FROM Booking b WHERE b.room.roomId = :roomId " + "AND b.bookingStatus != 'CANCELLED' "
			+ "AND ((b.checkInDate <= :checkOut AND b.checkOutDate >= :checkIn))")
	List<Booking> findOverlappingBookings(@Param("roomId") Long roomId, @Param("checkIn") LocalDate checkIn,
			@Param("checkOut") LocalDate checkOut);

	@Query("SELECT COUNT(b) FROM Booking b")
	Long countTotalBookings();

	Long countByBookingStatus(BookingStatus status);

	// Optional<User> findByHostel_Owner_UserId(Long ownerId);

	List<Booking> findByHostel_Owner_UserId(Long ownerId);

}
