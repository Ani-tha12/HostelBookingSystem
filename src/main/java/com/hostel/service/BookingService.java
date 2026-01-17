package com.hostel.service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hostel.dto.request.BookingRequest;
import com.hostel.dto.response.BookingResponse;
import com.hostel.dto.response.BookingStatisticsResponse;
import com.hostel.entity.Booking;
import com.hostel.entity.Hostel;
import com.hostel.entity.Room;
import com.hostel.entity.User;
import com.hostel.enums.BookingStatus;
import com.hostel.exception.BadRequestException;
import com.hostel.exception.ResourceNotFoundException;
import com.hostel.mapper.BookingMapper;
import com.hostel.repository.BookingRepository;
import com.hostel.repository.HostelRepository;
import com.hostel.repository.RoomRepository;
import com.hostel.repository.UserRepository;

@Service
@Transactional
public class BookingService {
    
    // ⭐ ADD THIS: Logger declaration
    private static final Logger logger = LoggerFactory.getLogger(BookingService.class);
    
    @Autowired
    private BookingRepository bookingRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private HostelRepository hostelRepository;
    
    @Autowired
    private RoomRepository roomRepository;
    
    @Autowired
    private BookingMapper bookingMapper;
   
    
    // Create booking
    public BookingResponse createBooking(BookingRequest request) {
        logger.info("Creating booking - User ID: {}, Hostel ID: {}, Room ID: {}", 
                   request.getUserId(), request.getHostelId(), request.getRoomId());
        
        try {
            // Find user
            User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> {
                    logger.error("Booking creation failed: User not found - ID: {}", request.getUserId());
                    return new ResourceNotFoundException("User", "userId", request.getUserId());
                });
            
            // Find hostel
            Hostel hostel = hostelRepository.findById(request.getHostelId())
                .orElseThrow(() -> {
                    logger.error("Booking creation failed: Hostel not found - ID: {}", request.getHostelId());
                    return new ResourceNotFoundException("Hostel", "hostelId", request.getHostelId());
                });
            
            // Find room
            Room room = roomRepository.findById(request.getRoomId())
                .orElseThrow(() -> {
                    logger.error("Booking creation failed: Room not found - ID: {}", request.getRoomId());
                    return new ResourceNotFoundException("Room", "roomId", request.getRoomId());
                });
            
            logger.debug("Booking entities validated - User: {}, Hostel: {}, Room: {}", 
                        user.getEmail(), hostel.getHostelName(), room.getRoomId());
            
            // Validate dates
            if (request.getCheckInDate().isBefore(LocalDate.now())) {
                logger.warn("Booking validation failed: Check-in date is in the past - {}", 
                           request.getCheckInDate());
                throw new BadRequestException("Check-in date cannot be in the past");
            }
            
            if (request.getCheckOutDate().isBefore(request.getCheckInDate())) {
                logger.warn("Booking validation failed: Check-out date before check-in - CheckIn: {}, CheckOut: {}", 
                           request.getCheckInDate(), request.getCheckOutDate());
                throw new BadRequestException("Check-out date must be after check-in date");
            }
            
            // Check if hostel is approved
            if (!hostel.getApproved()) {
                logger.warn("Booking failed: Hostel not approved - ID: {}, Name: {}", 
                           hostel.getHostelId(), hostel.getHostelName());
                throw new BadRequestException("Hostel is not approved for bookings");
            }
            
            // Check room availability
            logger.debug("Checking room availability - Available: {}, Required: {}", 
                        room.getAvailableBeds(), request.getNumberOfBeds());
            
            if (room.getAvailableBeds() < request.getNumberOfBeds()) {
                logger.warn("Booking failed: Insufficient beds - Available: {}, Required: {}", 
                           room.getAvailableBeds(), request.getNumberOfBeds());
                throw new BadRequestException("Not enough beds available. Only " + 
                    room.getAvailableBeds() + " beds available");
            }
            
            // Check for overlapping bookings
            List<Booking> overlappingBookings = bookingRepository.findOverlappingBookings(
                request.getRoomId(), 
                request.getCheckInDate(), 
                request.getCheckOutDate()
            );
            
            if (!overlappingBookings.isEmpty()) {
                logger.warn("Booking failed: Room already booked for selected dates - Room ID: {}, Overlapping bookings: {}", 
                           request.getRoomId(), overlappingBookings.size());
                throw new BadRequestException("Room is already booked for selected dates");
            }
            
            // Calculate total price
            long numberOfNights = ChronoUnit.DAYS.between(
                request.getCheckInDate(), 
                request.getCheckOutDate()
            );
            double totalPrice = numberOfNights * room.getPricePerNight() * request.getNumberOfBeds();
            
            logger.debug("Booking calculation - Nights: {}, Price per night: {}, Total beds: {}, Total price: {}", 
                        numberOfNights, room.getPricePerNight(), request.getNumberOfBeds(), totalPrice);
            
            // Create booking
            Booking booking = bookingMapper.toEntity(request);
            booking.setUser(user);
            booking.setHostel(hostel);
            booking.setRoom(room);
            booking.setTotalPrice(totalPrice);
            booking.setBookingStatus(BookingStatus.CONFIRMED);
            
            // Update room availability
            int previousAvailability = room.getAvailableBeds();
            room.setAvailableBeds(room.getAvailableBeds() - request.getNumberOfBeds());
            roomRepository.save(room);
            
            logger.debug("Room availability updated - Room ID: {}, Previous: {}, New: {}", 
                        room.getRoomId(), previousAvailability, room.getAvailableBeds());
            
            // Save booking
            Booking savedBooking = bookingRepository.save(booking);
            
            logger.info("Booking created successfully - Booking ID: {}, User: {}, Hostel: {}, Total Price: {}", 
                       savedBooking.getBookingId(), user.getEmail(), hostel.getHostelName(), totalPrice);
            
            return bookingMapper.toResponse(savedBooking);
            
        } catch (ResourceNotFoundException | BadRequestException e) {
            logger.error("Booking creation failed: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            logger.error("Unexpected error during booking creation", e);
            throw new RuntimeException("Booking creation failed", e);
        }
    }
    
    // Cancel booking
    public BookingResponse cancelBooking(Long bookingId, String reason) {
        logger.info("Attempting to cancel booking - ID: {}, Reason: {}", bookingId, reason);
        
        Booking booking = bookingRepository.findById(bookingId)
            .orElseThrow(() -> {
                logger.error("Booking cancellation failed: Booking not found - ID: {}", bookingId);
                return new ResourceNotFoundException("Booking", "bookingId", bookingId);
            });
        
        // Check if booking can be cancelled
        if (booking.getBookingStatus() == BookingStatus.COMPLETED) {
            logger.warn("Cancellation failed: Booking already completed - ID: {}", bookingId);
            throw new BadRequestException("Cannot cancel completed booking");
        }
        
        if (booking.getBookingStatus() == BookingStatus.CANCELLED) {
            logger.warn("Cancellation failed: Booking already cancelled - ID: {}", bookingId);
            throw new BadRequestException("Booking is already cancelled");
        }
        
        // Update booking status
        booking.setBookingStatus(BookingStatus.CANCELLED);
        
        // Restore room availability
        Room room = booking.getRoom();
        int previousAvailability = room.getAvailableBeds();
        room.setAvailableBeds(room.getAvailableBeds() + booking.getNumberOfBeds());
        roomRepository.save(room);
        
        logger.debug("Room availability restored - Room ID: {}, Previous: {}, New: {}", 
                    room.getRoomId(), previousAvailability, room.getAvailableBeds());
        
        Booking updatedBooking = bookingRepository.save(booking);
        
        logger.info("Booking cancelled successfully - ID: {}, User: {}, Beds restored: {}", 
                   bookingId, booking.getUser().getEmail(), booking.getNumberOfBeds());
        
        return bookingMapper.toResponse(updatedBooking);
    }
    

	public List<BookingResponse> getAllBookings() {
		 logger.info("Fetching all bookings");
		    return bookingRepository.findAll().stream()
		            .map(bookingMapper::toResponse)
		            .collect(Collectors.toList());

	}

	public BookingResponse getBookingById(Long bookingId) {
		 logger.info("Fetching booking with ID: {}", bookingId);
		    Booking booking = bookingRepository.findById(bookingId)
		            .orElseThrow(() -> new ResourceNotFoundException("Booking", "bookingId", bookingId));
		    return bookingMapper.toResponse(booking);

	}

	public List<BookingResponse> getBookingsByHostel(Long hostelId) {
		logger.info("Fetching bookings for hostel ID: {}", hostelId);
	    return bookingRepository.findByHostel_HostelId(hostelId).stream()
	            .map(bookingMapper::toResponse)
	            .collect(Collectors.toList());

	}

	public List<BookingResponse> getBookingsByOwner(Long ownerId) {
		 logger.info("Fetching bookings for owner ID: {}", ownerId);
		 return bookingRepository.findByHostel_Owner_UserId(ownerId).stream()
			        .map(bookingMapper::toResponse)
			        .collect(Collectors.toList());

	}

	public BookingResponse updateBookingStatus(Long bookingId, BookingStatus status) {
		logger.info("Updating booking status - ID: {}, New Status: {}", bookingId, status);
	    Booking booking = bookingRepository.findById(bookingId)
	            .orElseThrow(() -> new ResourceNotFoundException("Booking", "bookingId", bookingId));

	    booking.setBookingStatus(status);
	    Booking updatedBooking = bookingRepository.save(booking);

	    logger.info("Booking status updated successfully - ID: {}, Status: {}", bookingId, status);
	    return bookingMapper.toResponse(updatedBooking);

	}

	public BookingStatisticsResponse getStatistics() {
	    long total = bookingRepository.count();
	    long confirmed = bookingRepository.countByBookingStatus(BookingStatus.CONFIRMED);
	    long cancelled = bookingRepository.countByBookingStatus(BookingStatus.CANCELLED);
	    long completed = bookingRepository.countByBookingStatus(BookingStatus.COMPLETED);

	    return new BookingStatisticsResponse(total, confirmed, cancelled, completed);
	}

	public List<BookingResponse> getBookingsByUser(long userId) {
		 logger.info("Fetching bookings for userId={}", userId);
	        List<Booking> bookings = bookingRepository.findByUser_UserId(userId);
	        if (bookings.isEmpty()) {
	            logger.warn("No bookings found for userId={}", userId);
	            return Collections.emptyList();
	        }
	        return bookings.stream()
	                       .map(bookingMapper::toResponse)
	                       .collect(Collectors.toList());
	    }



	}
	
	

