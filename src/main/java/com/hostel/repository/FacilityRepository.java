package com.hostel.repository;

import com.hostel.entity.Facility;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;
import java.util.List;

@Repository
public interface FacilityRepository extends JpaRepository<Facility, Long> {

	Optional<Facility> findByFacilityName(String facilityName);

	boolean existsByFacilityName(String facilityName);

	List<Facility> findByFacilityNameContainingIgnoreCase(String facilityName);

	List<Facility> findAllByOrderByFacilityNameAsc();
}
