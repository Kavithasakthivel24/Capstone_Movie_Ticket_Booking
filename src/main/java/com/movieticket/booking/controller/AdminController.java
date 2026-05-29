package com.movieticket.booking.controller;

import com.movieticket.booking.dto.response.BookingResponse;
import com.movieticket.booking.dto.response.DashboardResponse;
import com.movieticket.booking.dto.response.UserResponse;
import com.movieticket.booking.entity.BookingStatus;
import com.movieticket.booking.repository.*;
import com.movieticket.booking.service.BookingService;
import com.movieticket.booking.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@PreAuthorize("hasAuthority('ROLE_ADMIN')")
@SecurityRequirement(name = "Bearer Authentication")
@Tag(name = "Admin", description = "Admin Management APIs")
public class AdminController {

    private final UserService userService;
    private final BookingService bookingService;
    private final UserRepository userRepository;
    private final MovieRepository movieRepository;
    private final TheaterRepository theaterRepository;
    private final ShowRepository showRepository;
    private final BookingRepository bookingRepository;
    private final PaymentRepository paymentRepository;

    @GetMapping("/dashboard")
    @Operation(summary = "Get admin dashboard statistics")
    public ResponseEntity<DashboardResponse> getDashboard() {
        double totalRevenue = paymentRepository.findAll().stream()
                .mapToDouble(p -> p.getAmount())
                .sum();

        DashboardResponse dashboard = DashboardResponse.builder()
                .totalUsers(userRepository.count())
                .totalMovies(movieRepository.count())
                .totalTheaters(theaterRepository.count())
                .totalShows(showRepository.count())
                .totalBookings(bookingRepository.count())
                .confirmedBookings(bookingRepository.countByStatus(BookingStatus.CONFIRMED))
                .cancelledBookings(bookingRepository.countByStatus(BookingStatus.CANCELLED))
                .totalRevenue(totalRevenue)
                .build();

        return ResponseEntity.ok(dashboard);
    }

    @GetMapping("/users")
    @Operation(summary = "Get all users")
    public ResponseEntity<List<UserResponse>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    @GetMapping("/users/{id}")
    @Operation(summary = "Get user by ID")
    public ResponseEntity<UserResponse> getUserById(@PathVariable Long id) {
        return ResponseEntity.ok(userService.getUserById(id));
    }

    @PutMapping("/users/{id}/toggle-status")
    @Operation(summary = "Toggle user active/inactive status")
    public ResponseEntity<String> toggleUserStatus(@PathVariable Long id) {
        userService.toggleUserStatus(id);
        return ResponseEntity.ok("User status updated");
    }

    @GetMapping("/bookings")
    @Operation(summary = "Get all bookings")
    public ResponseEntity<List<BookingResponse>> getAllBookings() {
        return ResponseEntity.ok(bookingService.getAllBookings());
    }
}
