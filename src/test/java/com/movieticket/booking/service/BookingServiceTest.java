package com.movieticket.booking.service;

import com.movieticket.booking.dto.request.BookingRequest;
import com.movieticket.booking.dto.response.BookingResponse;
import com.movieticket.booking.entity.*;
import com.movieticket.booking.exception.BadRequestException;
import com.movieticket.booking.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookingServiceTest {

    @Mock private BookingRepository bookingRepository;
    @Mock private ShowRepository showRepository;
    @Mock private SeatRepository seatRepository;
    @Mock private UserRepository userRepository;
    @Mock private PaymentRepository paymentRepository;
    @Mock private EmailService emailService;

    @InjectMocks
    private BookingService bookingService;

    private User user;
    private Show show;
    private Seat seat;

    @BeforeEach
    void setUp() {

        user = User.builder()
                .id(1L)
                .name("Test User")
                .email("test@example.com")
                .role(Role.ROLE_USER)
                .build();

        Movie movie = Movie.builder()
                .id(1L)
                .title("Test Movie")
                .build();

        Theater theater = Theater.builder()
                .id(1L)
                .theaterName("Test Theater")
                .city("Mumbai")
                .build();

        Screen screen = Screen.builder()
                .id(1L)
                .screenName("Screen 1")
                .theater(theater)
                .build();

        show = Show.builder()
                .id(1L)
                .movie(movie)
                .theater(theater)
                .screen(screen)
                .showDate(LocalDate.now().plusDays(1))
                .startTime(LocalTime.of(10, 0))
                .ticketPrice(250.0)
                .build();

        seat = Seat.builder()
                .id(1L)
                .seatNumber("S1")
                .category(SeatCategory.SILVER)
                .status(SeatStatus.AVAILABLE)
                .price(250.0)
                .show(show)
                .build();

        Authentication authentication = mock(Authentication.class);
        when(authentication.getName()).thenReturn("test@example.com");

        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);

        SecurityContextHolder.setContext(securityContext);
    }

    @Test
    void createBooking_ShouldSucceed_WhenSeatsAvailable() {

        BookingRequest request = new BookingRequest();
        request.setShowId(1L);
        request.setSeatIds(List.of(1L));
        request.setPaymentMethod("CREDIT_CARD");

        when(userRepository.findByEmail(anyString()))
                .thenReturn(Optional.of(user));

        when(showRepository.findById(anyLong()))
                .thenReturn(Optional.of(show));

        when(seatRepository.findAllById(anyList()))
                .thenReturn(List.of(seat));

        when(seatRepository.saveAll(anyList()))
                .thenReturn(List.of(seat));

        when(bookingRepository.save(any(Booking.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        when(paymentRepository.save(any(Payment.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        doNothing().when(emailService)
                .sendBookingConfirmationEmail(any(User.class), any(Booking.class));

        BookingResponse response = bookingService.createBooking(request);

        assertNotNull(response);
        assertEquals("Test User", response.getUserName());
        assertEquals(1L, response.getShowId());

        // ✅ IMPORTANT: save() is called twice in service
        verify(bookingRepository, times(2)).save(any(Booking.class));

        verify(paymentRepository, times(1)).save(any(Payment.class));
        verify(seatRepository, times(1)).saveAll(anyList());

        verify(emailService, times(1))
                .sendBookingConfirmationEmail(any(User.class), any(Booking.class));
    }

    @Test
    void createBooking_ShouldThrow_WhenSeatNotAvailable() {

        seat.setStatus(SeatStatus.BOOKED);

        BookingRequest request = new BookingRequest();
        request.setShowId(1L);
        request.setSeatIds(List.of(1L));

        when(userRepository.findByEmail(anyString()))
                .thenReturn(Optional.of(user));

        when(showRepository.findById(anyLong()))
                .thenReturn(Optional.of(show));

        when(seatRepository.findAllById(anyList()))
                .thenReturn(List.of(seat));

        assertThrows(BadRequestException.class,
                () -> bookingService.createBooking(request));

        verify(bookingRepository, never()).save(any());
        verify(paymentRepository, never()).save(any());
    }

    @Test
    void getMyBookings_ShouldReturnList() {

        when(userRepository.findByEmail(anyString()))
                .thenReturn(Optional.of(user));

        when(bookingRepository.findByUserId(1L))
                .thenReturn(List.of());

        var result = bookingService.getMyBookings();

        assertNotNull(result);
        assertTrue(result.isEmpty());

        verify(bookingRepository, times(1)).findByUserId(1L);
    }
}