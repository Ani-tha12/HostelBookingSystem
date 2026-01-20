package com.hostel.entity;

import com.hostel.enums.PaymentMethod;
import com.hostel.enums.PaymentStatus;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "payments")
public class Payment {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long paymentId;

	@OneToOne
	@JoinColumn(name = "booking_id", nullable = false)
	private Booking booking;

	@Column(nullable = false)
	private Double amount;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private PaymentStatus paymentStatus = PaymentStatus.PENDING;

	@Enumerated(EnumType.STRING)
	private PaymentMethod paymentMethod;

	@Column(name = "transaction_id", unique = true)
	private String transactionId;

	@Column(name = "payment_date")
	private LocalDateTime paymentDate;

	@Column(name = "created_date", nullable = false)
	private LocalDateTime createdDate = LocalDateTime.now();

	@Column(name = "failure_reason")
	private String failureReason;

	// Constructors
	public Payment() {
	}

	public Payment(Booking booking, Double amount, PaymentMethod paymentMethod) {
		this.booking = booking;
		this.amount = amount;
		this.paymentMethod = paymentMethod;
	}

	// Getters and Setters
	public Long getPaymentId() {
		return paymentId;
	}

	public void setPaymentId(Long paymentId) {
		this.paymentId = paymentId;
	}

	public Booking getBooking() {
		return booking;
	}

	public void setBooking(Booking booking) {
		this.booking = booking;
	}

	public Double getAmount() {
		return amount;
	}

	public void setAmount(Double amount) {
		this.amount = amount;
	}

	public PaymentStatus getPaymentStatus() {
		return paymentStatus;
	}

	public void setPaymentStatus(PaymentStatus paymentStatus) {
		this.paymentStatus = paymentStatus;
	}

	public PaymentMethod getPaymentMethod() {
		return paymentMethod;
	}

	public void setPaymentMethod(PaymentMethod paymentMethod) {
		this.paymentMethod = paymentMethod;
	}

	public String getTransactionId() {
		return transactionId;
	}

	public void setTransactionId(String transactionId) {
		this.transactionId = transactionId;
	}

	public LocalDateTime getPaymentDate() {
		return paymentDate;
	}

	public void setPaymentDate(LocalDateTime paymentDate) {
		this.paymentDate = paymentDate;
	}

	public LocalDateTime getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(LocalDateTime createdDate) {
		this.createdDate = createdDate;
	}

	public String getFailureReason() {
		return failureReason;
	}

	public void setFailureReason(String failureReason) {
		this.failureReason = failureReason;
	}

	// ✅ REMOVED THE DUPLICATE getPaymentStatus() method that was returning null
}