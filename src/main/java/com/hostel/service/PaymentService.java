package com.hostel.service;

import com.hostel.dto.request.PaymentRequest;
import com.hostel.dto.response.PaymentResponse;
import com.hostel.entity.Booking;
import com.hostel.entity.Payment;
import com.hostel.enums.BookingStatus;
import com.hostel.enums.PaymentStatus;
import com.hostel.exception.BadRequestException;
import com.hostel.exception.ResourceNotFoundException;
import com.hostel.mapper.PaymentMapper;
import com.hostel.repository.BookingRepository;
import com.hostel.repository.PaymentRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional
public class PaymentService {

	private static final Logger logger = LoggerFactory.getLogger(PaymentService.class);

	@Autowired
	private PaymentRepository paymentRepository;

	@Autowired
	private BookingRepository bookingRepository;

	@Autowired
	private PaymentMapper paymentMapper;

	public PaymentResponse processPayment(PaymentRequest request) {
		logger.info("Processing payment for booking ID: {}", request.getBookingId());

		Booking booking = bookingRepository.findById(request.getBookingId()).orElseThrow(() -> {
			logger.error("Booking not found - ID: {}", request.getBookingId());
			return new ResourceNotFoundException("Booking", "bookingId", request.getBookingId());
		});

		if (booking.getBookingStatus() != BookingStatus.PENDING_PAYMENT) {
			logger.error("Booking not in PENDING_PAYMENT status - Current: {}", booking.getBookingStatus());
			throw new BadRequestException("Booking is not awaiting payment");
		}

		if (booking.getPayment() != null) {
			logger.error("Payment already exists for booking ID: {}", request.getBookingId());
			throw new BadRequestException("Payment already processed for this booking");
		}

		Payment payment = new Payment();
		payment.setBooking(booking);
		payment.setAmount(booking.getTotalPrice());
		payment.setPaymentMethod(request.getPaymentMethod());

		boolean paymentSuccess = simulatePaymentGateway(request);

		if (paymentSuccess) {

			payment.setPaymentStatus(PaymentStatus.COMPLETED);
			payment.setTransactionId("TXN-" + UUID.randomUUID().toString());
			payment.setPaymentDate(LocalDateTime.now());

			booking.setBookingStatus(BookingStatus.CONFIRMED);

			logger.info("Payment successful - Transaction ID: {}", payment.getTransactionId());
		} else {

			payment.setPaymentStatus(PaymentStatus.FAILED);
			payment.setFailureReason("Payment gateway declined the transaction");

			logger.warn("Payment failed for booking ID: {}", request.getBookingId());
		}

		Payment savedPayment = paymentRepository.save(payment);
		booking.setPayment(savedPayment);
		bookingRepository.save(booking);

		return paymentMapper.toResponse(savedPayment);
	}

	public PaymentResponse getPaymentByBooking(Long bookingId) {
		logger.info("Fetching payment for booking ID: {}", bookingId);

		Payment payment = paymentRepository.findByBooking_BookingId(bookingId).orElseThrow(() -> {
			logger.error("Payment not found for booking ID: {}", bookingId);
			return new ResourceNotFoundException("Payment", "bookingId", bookingId);
		});

		return paymentMapper.toResponse(payment);
	}

	public PaymentResponse getPaymentById(Long paymentId) {
		logger.info("Fetching payment with ID: {}", paymentId);

		Payment payment = paymentRepository.findById(paymentId).orElseThrow(() -> {
			logger.error("Payment not found with ID: {}", paymentId);
			return new ResourceNotFoundException("Payment", "paymentId", paymentId);
		});

		return paymentMapper.toResponse(payment);
	}

	public List<PaymentResponse> getPaymentsByUser(Long userId) {
		logger.info("Fetching payments for user ID: {}", userId);

		return paymentRepository.findByBooking_User_UserId(userId).stream().map(paymentMapper::toResponse)
				.collect(Collectors.toList());
	}

	public PaymentResponse refundPayment(Long paymentId) {
		logger.info("Processing refund for payment ID: {}", paymentId);

		Payment payment = paymentRepository.findById(paymentId).orElseThrow(() -> {
			logger.error("Payment not found with ID: {}", paymentId);
			return new ResourceNotFoundException("Payment", "paymentId", paymentId);
		});

		if (payment.getPaymentStatus() != PaymentStatus.COMPLETED) {
			throw new BadRequestException("Cannot refund payment that was not completed");
		}

		payment.setPaymentStatus(PaymentStatus.REFUNDED);

		Payment updatedPayment = paymentRepository.save(payment);

		logger.info("Refund processed successfully for payment ID: {}", paymentId);

		return paymentMapper.toResponse(updatedPayment);
	}

	private boolean simulatePaymentGateway(PaymentRequest request) {

		return Math.random() < 0.9;

	}
}