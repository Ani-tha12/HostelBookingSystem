package com.hostel.test.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.hostel.dto.request.BookingRequest;
import com.hostel.dto.response.BookingResponse;
import com.hostel.dto.response.BookingStatisticsResponse;
import com.hostel.entity.Booking;
import com.hostel.entity.Hostel;
import com.hostel.entity.Room;
import com.hostel.entity.User;
import com.hostel.enums.BookingStatus;
import com.hostel.enums.RoomType;
import com.hostel.enums.UserRole;
import com.hostel.exception.BadRequestException;
import com.hostel.exception.ResourceNotFoundException;
import com.hostel.mapper.BookingMapper;
import com.hostel.repository.BookingRepository;
import com.hostel.repository.HostelRepository;
import com.hostel.repository.RoomRepository;
import com.hostel.repository.UserRepository;
import com.hostel.service.BookingService;

@ExtendWith(MockitoExtension.class)
@DisplayName("Booking Service Tests")
class BookingServiceTest {

	@Mock
	private BookingRepository bookingRepository;
	@Mock
	private UserRepository userRepository;
	@Mock
	private HostelRepository hostelRepository;
	@Mock
	private RoomRepository roomRepository;
	@Mock
	private BookingMapper bookingMapper;

	@InjectMocks
	private BookingService bookingService;

	private User testUser;
	private Hostel testHostel;
	private Room testRoom;
	private Booking testBooking;
	private BookingRequest bookingRequest;
	private BookingResponse bookingResponse;

	@BeforeEach
	void setUp() {

		testUser = new User();
		testUser.setUserId(1L);
		testUser.setName("Suresh Kumar");
		testUser.setEmail("suresh@gmail.com");
		testUser.setRole(UserRole.USER);

		User testOwner = new User();
		testOwner.setUserId(5L);
		testOwner.setName("Raj Kumar");
		testOwner.setRole(UserRole.OWNER);

		testHostel = new Hostel();
		testHostel.setHostelId(1L);
		testHostel.setHostelName("Sunshine Hostel");
		testHostel.setCity("Chennai");
		testHostel.setAddress("123 Beach Road");
		testHostel.setApproved(true);
		testHostel.setOwner(testOwner);

		testRoom = new Room();
		testRoom.setRoomId(10L);
		testRoom.setHostel(testHostel);
		testRoom.setRoomType(RoomType.DORM);
		testRoom.setTotalBeds(6);
		testRoom.setAvailableBeds(6);
		testRoom.setPricePerNight(300.0);

		bookingRequest = new BookingRequest();
		bookingRequest.setUserId(1L);
		bookingRequest.setHostelId(1L);
		bookingRequest.setRoomId(10L);
		bookingRequest.setCheckInDate(LocalDate.now().plusDays(5));
		bookingRequest.setCheckOutDate(LocalDate.now().plusDays(10));
		bookingRequest.setNumberOfBeds(2);

		testBooking = new Booking();
		testBooking.setBookingId(101L);
		testBooking.setUser(testUser);
		testBooking.setHostel(testHostel);
		testBooking.setRoom(testRoom);
		testBooking.setCheckInDate(bookingRequest.getCheckInDate());
		testBooking.setCheckOutDate(bookingRequest.getCheckOutDate());
		testBooking.setNumberOfBeds(2);
		testBooking.setTotalPrice(3000.0);
		testBooking.setBookingStatus(BookingStatus.CONFIRMED);
		testBooking.setBookingDate(LocalDateTime.now());

		bookingResponse = new BookingResponse();
		bookingResponse.setBookingId(101L);
		bookingResponse.setUserId(1L);
		bookingResponse.setUserName("Suresh Kumar");
		bookingResponse.setHostelId(1L);
		bookingResponse.setHostelName("Sunshine Hostel");
		bookingResponse.setRoomId(10L);
		bookingResponse.setRoomType(RoomType.DORM);
		bookingResponse.setCheckInDate(bookingRequest.getCheckInDate());
		bookingResponse.setCheckOutDate(bookingRequest.getCheckOutDate());
		bookingResponse.setNumberOfBeds(2);
		bookingResponse.setTotalPrice(3000.0);
		bookingResponse.setBookingStatus(BookingStatus.CONFIRMED);
	}

	@Test
	@DisplayName("SUCCESS: Create Booking - Should create booking, compute price, reduce availability")
	void testCreateBooking_Success() {

		when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
		when(hostelRepository.findById(1L)).thenReturn(Optional.of(testHostel));
		when(roomRepository.findById(10L)).thenReturn(Optional.of(testRoom));
		when(bookingRepository.findOverlappingBookings(anyLong(), any(LocalDate.class), any(LocalDate.class)))
				.thenReturn(new ArrayList<>());

		when(bookingMapper.toEntity(any(BookingRequest.class))).thenAnswer(inv -> {
			Booking b = new Booking();
			b.setCheckInDate(bookingRequest.getCheckInDate());
			b.setCheckOutDate(bookingRequest.getCheckOutDate());
			b.setNumberOfBeds(bookingRequest.getNumberOfBeds());
			return b;
		});

		when(bookingRepository.save(any(Booking.class))).thenAnswer(inv -> {
			Booking b = inv.getArgument(0);
			b.setBookingId(101L);
			return b;
		});

		when(roomRepository.save(any(Room.class))).thenAnswer(inv -> inv.getArgument(0));
		when(bookingMapper.toResponse(any(Booking.class))).thenReturn(bookingResponse);

		BookingResponse result = bookingService.createBooking(bookingRequest);

		assertNotNull(result);
		assertEquals(101L, result.getBookingId());
		assertEquals(BookingStatus.CONFIRMED, result.getBookingStatus());

		ArgumentCaptor<Booking> bookingCaptor = ArgumentCaptor.forClass(Booking.class);
		verify(bookingRepository).save(bookingCaptor.capture());
		Booking saved = bookingCaptor.getValue();

		long nights = ChronoUnit.DAYS.between(bookingRequest.getCheckInDate(), bookingRequest.getCheckOutDate());
		double expectedTotal = nights * testRoom.getPricePerNight() * bookingRequest.getNumberOfBeds();
		assertEquals(expectedTotal, saved.getTotalPrice(), 0.0001);

		assertEquals(4, testRoom.getAvailableBeds());

		InOrder inOrder = inOrder(roomRepository, bookingRepository);
		inOrder.verify(roomRepository).save(testRoom);
		inOrder.verify(bookingRepository).save(saved);
	}

	@Test
	@DisplayName("SUCCESS: Cancel Booking - Should cancel and restore room availability")
	void testCancelBooking_Success() {

		testRoom.setAvailableBeds(4);
		testBooking.setBookingStatus(BookingStatus.CONFIRMED);

		when(bookingRepository.findById(101L)).thenReturn(Optional.of(testBooking));
		when(bookingRepository.save(any(Booking.class))).thenAnswer(inv -> inv.getArgument(0));
		when(roomRepository.save(any(Room.class))).thenAnswer(inv -> inv.getArgument(0));
		when(bookingMapper.toResponse(any(Booking.class))).thenReturn(bookingResponse);

		BookingResponse result = bookingService.cancelBooking(101L, "Changed plans");

		assertNotNull(result);
		assertEquals(BookingStatus.CANCELLED, testBooking.getBookingStatus());
		assertEquals(6, testRoom.getAvailableBeds());
		verify(bookingRepository).findById(101L);
		verify(roomRepository).save(testRoom);
		verify(bookingRepository).save(testBooking);
	}

	@Test
	@DisplayName("SUCCESS: Get Bookings By User - Should return user's bookings")
	void testGetBookingsByUser_Success() {
		List<Booking> bookings = Arrays.asList(testBooking);

		when(bookingRepository.findByUser_UserId(1L)).thenReturn(bookings);
		when(bookingMapper.toResponse(any(Booking.class))).thenReturn(bookingResponse);

		List<BookingResponse> result = bookingService.getBookingsByUser(1L);

		assertNotNull(result);
		assertEquals(1, result.size());
		assertEquals("Suresh Kumar", result.get(0).getUserName());

		verify(bookingRepository).findByUser_UserId(1L);
	}

	@Test
	@DisplayName("SUCCESS: Get All Bookings - Should return all bookings")
	void testGetAllBookings_Success() {
		List<Booking> bookings = Arrays.asList(testBooking);

		when(bookingRepository.findAll()).thenReturn(bookings);
		when(bookingMapper.toResponse(any(Booking.class))).thenReturn(bookingResponse);

		List<BookingResponse> result = bookingService.getAllBookings();

		assertNotNull(result);
		assertEquals(1, result.size());

		verify(bookingRepository).findAll();
	}

	@Test
	@DisplayName("SUCCESS: Get Booking By ID - Should return booking details")
	void testGetBookingById_Success() {
		when(bookingRepository.findById(101L)).thenReturn(Optional.of(testBooking));
		when(bookingMapper.toResponse(any(Booking.class))).thenReturn(bookingResponse);

		BookingResponse result = bookingService.getBookingById(101L);

		assertNotNull(result);
		assertEquals(101L, result.getBookingId());
		assertEquals("Sunshine Hostel", result.getHostelName());

		verify(bookingRepository).findById(101L);
	}

	@Test
	@DisplayName("SUCCESS: Get Bookings By Hostel - Should return hostel bookings")
	void testGetBookingsByHostel_Success() {
		List<Booking> bookings = Arrays.asList(testBooking);

		when(bookingRepository.findByHostel_HostelId(1L)).thenReturn(bookings);
		when(bookingMapper.toResponse(any(Booking.class))).thenReturn(bookingResponse);

		List<BookingResponse> result = bookingService.getBookingsByHostel(1L);

		assertNotNull(result);
		assertEquals(1, result.size());

		verify(bookingRepository).findByHostel_HostelId(1L);
	}

	@Test
	@DisplayName("SUCCESS: Get Bookings By Owner - Should return owner's hostel bookings")
	void testGetBookingsByOwner_Success() {
		List<Booking> bookings = Arrays.asList(testBooking);

		when(bookingRepository.findByHostel_Owner_UserId(5L)).thenReturn(bookings);
		when(bookingMapper.toResponse(any(Booking.class))).thenReturn(bookingResponse);

		List<BookingResponse> result = bookingService.getBookingsByOwner(5L);

		assertNotNull(result);
		assertEquals(1, result.size());

		verify(bookingRepository).findByHostel_Owner_UserId(5L);
	}

	@Test
	@DisplayName("SUCCESS: Update Booking Status - Should update status")
	void testUpdateBookingStatus_Success() {
		when(bookingRepository.findById(101L)).thenReturn(Optional.of(testBooking));
		when(bookingRepository.save(any(Booking.class))).thenAnswer(inv -> inv.getArgument(0));
		when(bookingMapper.toResponse(any(Booking.class))).thenReturn(bookingResponse);

		BookingResponse result = bookingService.updateBookingStatus(101L, BookingStatus.COMPLETED);

		assertNotNull(result);
		assertEquals(BookingStatus.COMPLETED, testBooking.getBookingStatus());

		verify(bookingRepository).save(testBooking);
	}

	@Test
	@DisplayName("SUCCESS: Get Statistics - Should return booking statistics")
	void testGetStatistics_Success() {
		when(bookingRepository.count()).thenReturn(100L);
		when(bookingRepository.countByBookingStatus(BookingStatus.CONFIRMED)).thenReturn(60L);
		when(bookingRepository.countByBookingStatus(BookingStatus.CANCELLED)).thenReturn(20L);
		when(bookingRepository.countByBookingStatus(BookingStatus.COMPLETED)).thenReturn(20L);

		BookingStatisticsResponse result = bookingService.getStatistics();

		assertNotNull(result);
		assertEquals(100L, result.getTotal());
		assertEquals(60L, result.getConfirmed());
		assertEquals(20L, result.getCancelled());
		assertEquals(20L, result.getCompleted());
	}

	@Test
	@DisplayName("FAILURE: Create Booking - User not found")
	void testCreateBooking_UserNotFound_ThrowsException() {
		when(userRepository.findById(999L)).thenReturn(Optional.empty());
		bookingRequest.setUserId(999L);

		ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
			bookingService.createBooking(bookingRequest);
		});

		assertTrue(exception.getMessage().contains("User"));
		verify(bookingRepository, never()).save(any(Booking.class));
	}

	@Test
	@DisplayName("FAILURE: Create Booking - Hostel not found")
	void testCreateBooking_HostelNotFound_ThrowsException() {
		when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
		when(hostelRepository.findById(999L)).thenReturn(Optional.empty());
		bookingRequest.setHostelId(999L);

		ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
			bookingService.createBooking(bookingRequest);
		});

		assertTrue(exception.getMessage().contains("Hostel"));
	}

	@Test
	@DisplayName("FAILURE: Create Booking - Room not found")
	void testCreateBooking_RoomNotFound_ThrowsException() {
		when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
		when(hostelRepository.findById(1L)).thenReturn(Optional.of(testHostel));
		when(roomRepository.findById(999L)).thenReturn(Optional.empty());
		bookingRequest.setRoomId(999L);

		ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
			bookingService.createBooking(bookingRequest);
		});

		assertTrue(exception.getMessage().contains("Room"));
	}

	@Test
	@DisplayName("FAILURE: Create Booking - Check-in date in past")
	void testCreateBooking_CheckInDateInPast_ThrowsException() {
		bookingRequest.setCheckInDate(LocalDate.now().minusDays(1));

		when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
		when(hostelRepository.findById(1L)).thenReturn(Optional.of(testHostel));
		when(roomRepository.findById(10L)).thenReturn(Optional.of(testRoom));

		BadRequestException exception = assertThrows(BadRequestException.class, () -> {
			bookingService.createBooking(bookingRequest);
		});

		assertEquals("Check-in date cannot be in the past", exception.getMessage());
	}

	@Test
	@DisplayName("FAILURE: Create Booking - Check-out before check-in")
	void testCreateBooking_CheckOutBeforeCheckIn_ThrowsException() {
		bookingRequest.setCheckInDate(LocalDate.now().plusDays(10));
		bookingRequest.setCheckOutDate(LocalDate.now().plusDays(5));

		when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
		when(hostelRepository.findById(1L)).thenReturn(Optional.of(testHostel));
		when(roomRepository.findById(10L)).thenReturn(Optional.of(testRoom));

		BadRequestException exception = assertThrows(BadRequestException.class, () -> {
			bookingService.createBooking(bookingRequest);
		});

		assertEquals("Check-out date must be after check-in date", exception.getMessage());
	}

	@Test
	@DisplayName("FAILURE: Create Booking - Hostel not approved")
	void testCreateBooking_HostelNotApproved_ThrowsException() {
		testHostel.setApproved(false);

		when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
		when(hostelRepository.findById(1L)).thenReturn(Optional.of(testHostel));
		when(roomRepository.findById(10L)).thenReturn(Optional.of(testRoom));

		BadRequestException exception = assertThrows(BadRequestException.class, () -> {
			bookingService.createBooking(bookingRequest);
		});

		assertEquals("Hostel is not approved for bookings", exception.getMessage());
	}

	@Test
	@DisplayName("FAILURE: Create Booking - Not enough beds available")
	void testCreateBooking_NotEnoughBeds_ThrowsException() {
		testRoom.setAvailableBeds(1);
		bookingRequest.setNumberOfBeds(3);

		when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
		when(hostelRepository.findById(1L)).thenReturn(Optional.of(testHostel));
		when(roomRepository.findById(10L)).thenReturn(Optional.of(testRoom));

		BadRequestException exception = assertThrows(BadRequestException.class, () -> {
			bookingService.createBooking(bookingRequest);
		});

		assertTrue(exception.getMessage().contains("Not enough beds available"));
	}

	@Test
	@DisplayName("FAILURE: Create Booking - Room already booked for dates")
	void testCreateBooking_OverlappingBooking_ThrowsException() {
		List<Booking> overlappingBookings = Arrays.asList(testBooking);

		when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
		when(hostelRepository.findById(1L)).thenReturn(Optional.of(testHostel));
		when(roomRepository.findById(10L)).thenReturn(Optional.of(testRoom));
		when(bookingRepository.findOverlappingBookings(anyLong(), any(LocalDate.class), any(LocalDate.class)))
				.thenReturn(overlappingBookings);

		BadRequestException exception = assertThrows(BadRequestException.class, () -> {
			bookingService.createBooking(bookingRequest);
		});

		assertEquals("Room is already booked for selected dates", exception.getMessage());
	}

	@Test
	@DisplayName("FAILURE: Cancel Booking - Booking not found")
	void testCancelBooking_BookingNotFound_ThrowsException() {
		when(bookingRepository.findById(999L)).thenReturn(Optional.empty());

		ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
			bookingService.cancelBooking(999L, "reason");
		});

		assertTrue(exception.getMessage().contains("Booking"));
	}

	@Test
	@DisplayName("FAILURE: Cancel Booking - Already completed")
	void testCancelBooking_AlreadyCompleted_ThrowsException() {
		testBooking.setBookingStatus(BookingStatus.COMPLETED);
		when(bookingRepository.findById(101L)).thenReturn(Optional.of(testBooking));

		BadRequestException exception = assertThrows(BadRequestException.class, () -> {
			bookingService.cancelBooking(101L, "reason");
		});

		assertEquals("Cannot cancel completed booking", exception.getMessage());
	}

	@Test
	@DisplayName("FAILURE: Cancel Booking - Already cancelled")
	void testCancelBooking_AlreadyCancelled_ThrowsException() {
		testBooking.setBookingStatus(BookingStatus.CANCELLED);
		when(bookingRepository.findById(101L)).thenReturn(Optional.of(testBooking));

		BadRequestException exception = assertThrows(BadRequestException.class, () -> {
			bookingService.cancelBooking(101L, "reason");
		});

		assertEquals("Booking is already cancelled", exception.getMessage());
	}

	@Test
	@DisplayName("FAILURE: Get Booking By ID - Not found")
	void testGetBookingById_NotFound_ThrowsException() {
		when(bookingRepository.findById(999L)).thenReturn(Optional.empty());

		ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
			bookingService.getBookingById(999L);
		});

		assertTrue(exception.getMessage().contains("Booking"));
	}

	@Test
	@DisplayName("FAILURE: Update Booking Status - Booking not found")
	void testUpdateBookingStatus_NotFound_ThrowsException() {
		when(bookingRepository.findById(999L)).thenReturn(Optional.empty());

		assertThrows(ResourceNotFoundException.class, () -> {
			bookingService.updateBookingStatus(999L, BookingStatus.COMPLETED);
		});
	}

	@Test
	@DisplayName("FAILURE: Create Booking - Unexpected exception is wrapped with 'Booking creation failed'")
	void testCreateBooking_UnexpectedExceptionWrapped() {
		when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
		when(hostelRepository.findById(1L)).thenReturn(Optional.of(testHostel));
		when(roomRepository.findById(10L)).thenReturn(Optional.of(testRoom));
		when(bookingRepository.findOverlappingBookings(anyLong(), any(LocalDate.class), any(LocalDate.class)))
				.thenReturn(Collections.emptyList());
		when(bookingMapper.toEntity(any(BookingRequest.class))).thenReturn(new Booking());

		when(roomRepository.save(any(Room.class))).thenThrow(new RuntimeException("DB down"));

		RuntimeException ex = assertThrows(RuntimeException.class, () -> bookingService.createBooking(bookingRequest));
		assertEquals("Booking creation failed", ex.getMessage());
		assertNotNull(ex.getCause());
		assertTrue(ex.getCause().getMessage().contains("DB down"));
	}
}
