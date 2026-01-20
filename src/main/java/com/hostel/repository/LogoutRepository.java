package com.hostel.repository;

import com.hostel.entity.Logout;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;

public interface LogoutRepository extends JpaRepository<Logout, Long> {

	boolean existsByToken(String token);

	@Modifying
	@Query("DELETE FROM Logout l WHERE l.expiresAt < :now")
	int deleteExpiredTokens(@Param("now") LocalDateTime now);
}