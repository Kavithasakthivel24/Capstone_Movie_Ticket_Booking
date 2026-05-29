package com.movieticket.booking.controller;

import com.movieticket.booking.dto.response.SeatResponse;
import com.movieticket.booking.service.SeatService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/seats")
@RequiredArgsConstructor
@Tag(name = "Seats", description = "Seat Management APIs")
public class SeatController {

    private final SeatService seatService;

    @GetMapping("/show/{showId}")
    @Operation(summary = "Get all seats for a show")
    public ResponseEntity<List<SeatResponse>> getSeatsByShow(@PathVariable Long showId) {
        return ResponseEntity.ok(seatService.getSeatsByShow(showId));
    }

    @GetMapping("/show/{showId}/available")
    @Operation(summary = "Get available seats for a show")
    public ResponseEntity<List<SeatResponse>> getAvailableSeats(@PathVariable Long showId) {
        return ResponseEntity.ok(seatService.getAvailableSeatsByShow(showId));
    }
}
