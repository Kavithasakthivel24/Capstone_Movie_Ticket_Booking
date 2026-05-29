package com.movieticket.booking.service;

import com.movieticket.booking.dto.response.SeatResponse;
import com.movieticket.booking.entity.Seat;
import com.movieticket.booking.entity.SeatStatus;
import com.movieticket.booking.exception.ResourceNotFoundException;
import com.movieticket.booking.repository.SeatRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SeatService {

    private final SeatRepository seatRepository;

    @Transactional(readOnly = true)
    public List<SeatResponse> getSeatsByShow(Long showId) {
        return seatRepository.findByShowId(showId)
                .stream().map(this::toResponse).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<SeatResponse> getAvailableSeatsByShow(Long showId) {
        return seatRepository.findByShowIdAndStatus(showId, SeatStatus.AVAILABLE)
                .stream().map(this::toResponse).collect(Collectors.toList());
    }

    public Seat findById(Long id) {
        return seatRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Seat not found with id: " + id));
    }

    public SeatResponse toResponse(Seat seat) {
        return SeatResponse.builder()
                .id(seat.getId())
                .seatNumber(seat.getSeatNumber())
                .category(seat.getCategory())
                .status(seat.getStatus())
                .price(seat.getPrice())
                .showId(seat.getShow().getId())
                .build();
    }
}
