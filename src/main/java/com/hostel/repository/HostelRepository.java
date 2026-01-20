package com.hostel.repository;

import com.hostel.entity.Hostel;
import com.hostel.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface HostelRepository extends JpaRepository<Hostel, Long> {

	List<Hostel> findByApproved(Boolean approved);

	List<Hostel> findByCity(String city);

	List<Hostel> findByCityAndApproved(String city, Boolean approved);

	List<Hostel> findByOwner(User owner);

	List<Hostel> findByOwner_UserId(Long ownerId);

	List<Hostel> findByHostelNameContainingIgnoreCase(String hostelName);

	@Query("SELECT h FROM Hostel h WHERE h.city = :city AND h.approved = true")
	List<Hostel> searchHostelsByCity(@Param("city") String city);

	List<Hostel> findByApprovedTrue();
}
