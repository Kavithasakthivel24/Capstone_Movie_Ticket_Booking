package com.movieticket.booking.repository;

import com.movieticket.booking.entity.Seat;
import com.movieticket.booking.entity.SeatStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SeatRepository extends JpaRepository<Seat, Long> {
    List<Seat> findByShowId(Long showId);
    List<Seat> findByShowIdAndStatus(Long showId, SeatStatus status);
    long countByShowIdAndStatus(Long showId, SeatStatus status);
}
