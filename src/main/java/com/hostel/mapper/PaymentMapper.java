package com.hostel.mapper;

import com.hostel.entity.Payment;
import com.hostel.dto.response.PaymentResponse;
import org.springframework.stereotype.Component;

@Component
public class PaymentMapper {
    
    public PaymentResponse toResponse(Payment payment) {
        PaymentResponse response = new PaymentResponse();
        response.setPaymentId(payment.getPaymentId());
        response.setBookingId(payment.getBooking().getBookingId());
        response.setAmount(payment.getAmount());
        response.setPaymentStatus(payment.getPaymentStatus());
        response.setPaymentMethod(payment.getPaymentMethod());
        response.setTransactionId(payment.getTransactionId());
        response.setPaymentDate(payment.getPaymentDate());
        response.setCreatedDate(payment.getCreatedDate());
        response.setFailureReason(payment.getFailureReason());
        return response;
    }
}