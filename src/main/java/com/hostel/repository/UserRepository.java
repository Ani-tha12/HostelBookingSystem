package com.hostel.repository;

import com.hostel.entity.User;
import com.hostel.enums.UserRole;
import com.hostel.enums.UserStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

	Optional<User> findByEmail(String email);

	boolean existsByEmail(String email);

	List<User> findByRole(UserRole role);

	List<User> findByStatus(UserStatus status);

//    List<User> findByRoleAndStatus(UserRole role, UserStatus status);

	List<User> findByRoleAndStatus(UserRole role, UserStatus status);

	
//    Optional<User> findByResetToken(String resetToken);
	Optional<User> findByEmailAndName(String email, String name);

	Optional<User> findByResetToken(String resetToken);

}