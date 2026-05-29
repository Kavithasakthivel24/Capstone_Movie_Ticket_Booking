package com.movieticket.booking.repository;

import com.movieticket.booking.entity.Show;
import com.movieticket.booking.entity.ShowStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface ShowRepository extends JpaRepository<Show, Long> {
    List<Show> findByMovieIdAndShowDateAndStatus(Long movieId, LocalDate showDate, ShowStatus status);
    List<Show> findByMovieIdAndStatus(Long movieId, ShowStatus status);
    List<Show> findByTheaterIdAndShowDateAndStatus(Long theaterId, LocalDate showDate, ShowStatus status);
    List<Show> findByShowDateAndStatus(LocalDate showDate, ShowStatus status);
}
