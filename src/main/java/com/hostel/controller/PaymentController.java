package com.hostel.controller;

import com.hostel.dto.request.PaymentRequest;
import com.hostel.dto.response.ApiResponse;
import com.hostel.dto.response.PaymentResponse;
import com.hostel.service.PaymentService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/payments")
@CrossOrigin(origins = "*")
public class PaymentController {

	@Autowired
	private PaymentService paymentService;

	@PostMapping
	public ResponseEntity<ApiResponse<PaymentResponse>> processPayment(@Valid @RequestBody PaymentRequest request) {
		PaymentResponse response = paymentService.processPayment(request);
		return ResponseEntity.status(HttpStatus.CREATED)
				.body(ApiResponse.success("Payment processed successfully", response));
	}

	@GetMapping("/{paymentId}")
	public ResponseEntity<ApiResponse<PaymentResponse>> getPaymentById(@PathVariable Long paymentId) {
		PaymentResponse response = paymentService.getPaymentById(paymentId);
		return ResponseEntity.ok(ApiResponse.success("Payment retrieved successfully", response));
	}

	@GetMapping("/booking/{bookingId}")
	public ResponseEntity<ApiResponse<PaymentResponse>> getPaymentByBooking(@PathVariable Long bookingId) {
		PaymentResponse response = paymentService.getPaymentByBooking(bookingId);
		return ResponseEntity.ok(ApiResponse.success("Payment retrieved successfully", response));
	}

	@GetMapping("/user/{userId}")
	public ResponseEntity<ApiResponse<List<PaymentResponse>>> getPaymentsByUser(@PathVariable Long userId) {
		List<PaymentResponse> payments = paymentService.getPaymentsByUser(userId);
		return ResponseEntity.ok(ApiResponse.success("User payments retrieved successfully", payments));
	}

	@PutMapping("/{paymentId}/refund")
	public ResponseEntity<ApiResponse<PaymentResponse>> refundPayment(@PathVariable Long paymentId) {
		PaymentResponse response = paymentService.refundPayment(paymentId);
		return ResponseEntity.ok(ApiResponse.success("Payment refunded successfully", response));
	}
}