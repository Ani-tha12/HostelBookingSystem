package com.hostel.repository;

import com.hostel.entity.Payment;
import com.hostel.enums.PaymentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {
    
    Optional<Payment> findByBooking_BookingId(Long bookingId);
    
    Optional<Payment> findByTransactionId(String transactionId);
    
    List<Payment> findByPaymentStatus(PaymentStatus status);
    
    List<Payment> findByBooking_User_UserId(Long userId);
}