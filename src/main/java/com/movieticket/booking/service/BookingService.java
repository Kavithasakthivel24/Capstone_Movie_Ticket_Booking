package com.movieticket.booking.service;

import com.movieticket.booking.dto.request.BookingRequest;
import com.movieticket.booking.dto.response.BookingResponse;
import com.movieticket.booking.dto.response.PaymentResponse;
import com.movieticket.booking.entity.*;
import com.movieticket.booking.exception.BadRequestException;
import com.movieticket.booking.exception.ResourceNotFoundException;
import com.movieticket.booking.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BookingService {

    private final BookingRepository bookingRepository;
    private final ShowRepository showRepository;
    private final SeatRepository seatRepository;
    private final UserRepository userRepository;
    private final PaymentRepository paymentRepository;
    private final EmailService emailService;

    // -------------------------
    // CREATE BOOKING
    // -------------------------
    @Transactional
    public BookingResponse createBooking(BookingRequest request) {

        String email = SecurityContextHolder.getContext().getAuthentication().getName();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Show show = showRepository.findById(request.getShowId())
                .orElseThrow(() -> new ResourceNotFoundException("Show not found"));

        List<Seat> seats = seatRepository.findAllById(request.getSeatIds());

        if (seats.size() != request.getSeatIds().size()) {
            throw new BadRequestException("Some seats not found");
        }

        for (Seat seat : seats) {
            if (seat.getStatus() != SeatStatus.AVAILABLE) {
                throw new BadRequestException("Seat " + seat.getSeatNumber() + " is not available");
            }
        }

        double totalAmount = seats.stream()
                .mapToDouble(Seat::getPrice)
                .sum();

        // -------------------------
        // LOCK SEATS
        // -------------------------
        seats.forEach(s -> s.setStatus(SeatStatus.BOOKED));
        seatRepository.saveAll(seats);

        // -------------------------
        // CREATE BOOKING
        // -------------------------
        Booking booking = Booking.builder()
                .bookingId("BK" + UUID.randomUUID().toString().substring(0, 8).toUpperCase())
                .user(user)
                .show(show)
                .seatIds(request.getSeatIds())
                .totalAmount(totalAmount)
                .status(BookingStatus.CONFIRMED)
                .build();

        booking = bookingRepository.save(booking);

        // -------------------------
        // PAYMENT
        // -------------------------
        Payment payment = Payment.builder()
                .transactionId("TXN" + UUID.randomUUID().toString().substring(0, 10).toUpperCase())
                .booking(booking)
                .amount(totalAmount)
                .status(PaymentStatus.SUCCESS)
                .method(parsePaymentMethod(request.getPaymentMethod()))
                .build();

        payment = paymentRepository.save(payment);

        // link payment
        booking.setPayment(payment);
        booking = bookingRepository.save(booking);

        // -------------------------
        // EMAIL (IMPORTANT)
        // -------------------------
        emailService.sendBookingConfirmationEmail(user, booking);

        return toResponse(booking, seats);
    }

    // -------------------------
    // GET MY BOOKINGS
    // -------------------------
    @Transactional(readOnly = true)
    public List<BookingResponse> getMyBookings() {

        String email = SecurityContextHolder.getContext().getAuthentication().getName();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        return bookingRepository.findByUserId(user.getId())
                .stream()
                .map(b -> toResponse(b, null))
                .collect(Collectors.toList());
    }

    // -------------------------
    // CANCEL BOOKING
    // -------------------------
    @Transactional
    public BookingResponse cancelBooking(Long id) {

        String email = SecurityContextHolder.getContext().getAuthentication().getName();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found"));

        if (!booking.getUser().getId().equals(user.getId())) {
            throw new BadRequestException("Not authorized");
        }

        if (booking.getStatus() == BookingStatus.CANCELLED) {
            throw new BadRequestException("Already cancelled");
        }

        booking.setStatus(BookingStatus.CANCELLED);

        List<Seat> seats = seatRepository.findAllById(booking.getSeatIds());
        seats.forEach(s -> s.setStatus(SeatStatus.AVAILABLE));
        seatRepository.saveAll(seats);

        if (booking.getPayment() != null) {
            booking.getPayment().setStatus(PaymentStatus.REFUNDED);
            paymentRepository.save(booking.getPayment());
        }

        bookingRepository.save(booking);

        emailService.sendBookingCancellationEmail(user, booking);

        return toResponse(booking, seats);
    }

    // -------------------------
    // GET ALL BOOKINGS
    // -------------------------
    @Transactional(readOnly = true)
    public List<BookingResponse> getAllBookings() {
        return bookingRepository.findAll()
                .stream()
                .map(b -> toResponse(b, null))
                .collect(Collectors.toList());
    }

    // -------------------------
    // PAYMENT PARSER
    // -------------------------
    private PaymentMethod parsePaymentMethod(String method) {
        if (method == null) return PaymentMethod.CREDIT_CARD;
        try {
            return PaymentMethod.valueOf(method.toUpperCase());
        } catch (Exception e) {
            return PaymentMethod.CREDIT_CARD;
        }
    }

    // -------------------------
    // RESPONSE MAPPER
    // -------------------------
    public BookingResponse toResponse(Booking booking, List<Seat> seats) {

        List<String> seatNumbers = (seats != null)
                ? seats.stream().map(Seat::getSeatNumber).toList()
                : seatRepository.findAllById(booking.getSeatIds())
                    .stream().map(Seat::getSeatNumber).toList();

        PaymentResponse paymentResponse = null;

        if (booking.getPayment() != null) {
            Payment p = booking.getPayment();

            paymentResponse = PaymentResponse.builder()
                    .id(p.getId())
                    .transactionId(p.getTransactionId())
                    .amount(p.getAmount())
                    .status(p.getStatus())
                    .method(p.getMethod())
                    .paidAt(p.getPaidAt())
                    .build();
        }

        return BookingResponse.builder()
                .id(booking.getId())
                .bookingId(booking.getBookingId())
                .userId(booking.getUser().getId())
                .userName(booking.getUser().getName())
                .userEmail(booking.getUser().getEmail())
                .showId(booking.getShow().getId())
                .movieTitle(booking.getShow().getMovie().getTitle())
                .theaterName(booking.getShow().getTheater().getTheaterName())
                .theaterCity(booking.getShow().getTheater().getCity())
                .screenName(booking.getShow().getScreen().getScreenName())
                .showDate(booking.getShow().getShowDate().toString())
                .startTime(booking.getShow().getStartTime().toString())
                .seatNumbers(seatNumbers)
                .totalAmount(booking.getTotalAmount())
                .status(booking.getStatus())
                .bookedAt(booking.getBookedAt())
                .payment(paymentResponse)
                .build();
    }
}