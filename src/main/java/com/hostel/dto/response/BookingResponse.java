package com.hostel.dto.response;

import com.hostel.enums.BookingStatus;
import com.hostel.enums.RoomType;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class BookingResponse {
    
    private Long bookingId;
    private Long userId;
    private String userName;
    private Long hostelId;
    private String hostelName;
    private Long roomId;
    private RoomType roomType;
    private LocalDate checkInDate;
    private LocalDate checkOutDate;
    private Integer numberOfBeds;
    private Double totalPrice;
    private BookingStatus bookingStatus;
    private LocalDateTime bookingDate;
    
    // Constructors
    public BookingResponse() {}
    
    // Getters and Setters
    public Long getBookingId() {
        return bookingId;
    }
    
    public void setBookingId(Long bookingId) {
        this.bookingId = bookingId;
    }
    
    public Long getUserId() {
        return userId;
    }
    
    public void setUserId(Long userId) {
        this.userId = userId;
    }
    
    public String getUserName() {
        return userName;
    }
    
    public void setUserName(String userName) {
        this.userName = userName;
    }
    
    public Long getHostelId() {
        return hostelId;
    }
    
    public void setHostelId(Long hostelId) {
        this.hostelId = hostelId;
    }
    
    public String getHostelName() {
        return hostelName;
    }
    
    public void setHostelName(String hostelName) {
        this.hostelName = hostelName;
    }
    
    public Long getRoomId() {
        return roomId;
    }
    
    public void setRoomId(Long roomId) {
        this.roomId = roomId;
    }
    
    public RoomType getRoomType() {
        return roomType;
    }
    
    public void setRoomType(RoomType roomType) {
        this.roomType = roomType;
    }
    
    public LocalDate getCheckInDate() {
        return checkInDate;
    }
    
    public void setCheckInDate(LocalDate checkInDate) {
        this.checkInDate = checkInDate;
    }
    
    public LocalDate getCheckOutDate() {
        return checkOutDate;
    }
    
    public void setCheckOutDate(LocalDate checkOutDate) {
        this.checkOutDate = checkOutDate;
    }
    
    public Integer getNumberOfBeds() {
        return numberOfBeds;
    }
    
    public void setNumberOfBeds(Integer numberOfBeds) {
        this.numberOfBeds = numberOfBeds;
    }
    
    public Double getTotalPrice() {
        return totalPrice;
    }
    
    public void setTotalPrice(Double totalPrice) {
        this.totalPrice = totalPrice;
    }
    
    public BookingStatus getBookingStatus() {
        return bookingStatus;
    }
    
    public void setBookingStatus(BookingStatus bookingStatus) {
        this.bookingStatus = bookingStatus;
    }
    
    public LocalDateTime getBookingDate() {
        return bookingDate;
    }
    
    public void setBookingDate(LocalDateTime bookingDate) {
        this.bookingDate = bookingDate;
    }
}
