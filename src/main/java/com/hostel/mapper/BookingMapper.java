package com.hostel.mapper;

import com.hostel.entity.Booking;
import com.hostel.dto.request.BookingRequest;
import com.hostel.dto.response.BookingResponse;
import org.springframework.stereotype.Component;

@Component
public class BookingMapper {
    
   
    public Booking toEntity(BookingRequest request) {
        Booking booking = new Booking();
        booking.setCheckInDate(request.getCheckInDate());
        booking.setCheckOutDate(request.getCheckOutDate());
        booking.setNumberOfBeds(request.getNumberOfBeds());
        return booking;
    }
    
   
    public BookingResponse toResponse(Booking booking) {
        BookingResponse response = new BookingResponse();
        response.setBookingId(booking.getBookingId());
        response.setCheckInDate(booking.getCheckInDate());
        response.setCheckOutDate(booking.getCheckOutDate());
        response.setNumberOfBeds(booking.getNumberOfBeds());
        response.setTotalPrice(booking.getTotalPrice());
        response.setBookingStatus(booking.getBookingStatus());
        response.setBookingDate(booking.getBookingDate());
        
        if (booking.getUser() != null) {
            response.setUserId(booking.getUser().getUserId());
            response.setUserName(booking.getUser().getName());
        }
        
        if (booking.getHostel() != null) {
            response.setHostelId(booking.getHostel().getHostelId());
            response.setHostelName(booking.getHostel().getHostelName());
        }
        
        if (booking.getRoom() != null) {
            response.setRoomId(booking.getRoom().getRoomId());
            response.setRoomType(booking.getRoom().getRoomType());
        }
        
        return response;
    }
}
