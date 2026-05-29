package com.movieticket.booking.service;

import com.movieticket.booking.dto.request.ShowRequest;

import com.movieticket.booking.dto.response.ShowResponse;
import com.movieticket.booking.entity.*;
import com.movieticket.booking.exception.ResourceNotFoundException;
import com.movieticket.booking.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.movieticket.booking.entity.Seat;
import com.movieticket.booking.entity.SeatStatus;
import com.movieticket.booking.repository.SeatRepository;

import java.util.ArrayList;

@Service
@RequiredArgsConstructor
public class ShowService {

    private final ShowRepository showRepository;
    private final MovieRepository movieRepository;
    private final TheaterRepository theaterRepository;
    private final ScreenRepository screenRepository;
    private final SeatRepository seatRepository;

    @Transactional
    public ShowResponse createShow(ShowRequest request) {

        try {

            System.out.println("Creating show...");
            System.out.println("Movie ID: " + request.getMovieId());
            System.out.println("Theater ID: " + request.getTheaterId());
            System.out.println("Screen ID: " + request.getScreenId());

            Movie movie = movieRepository.findById(request.getMovieId())
                    .orElseThrow(() -> new ResourceNotFoundException("Movie not found"));

            Theater theater = theaterRepository.findById(request.getTheaterId())
                    .orElseThrow(() -> new ResourceNotFoundException("Theater not found"));

            Screen screen = screenRepository.findById(request.getScreenId())
                    .orElseThrow(() -> new ResourceNotFoundException("Screen not found"));

            System.out.println("Screen seats: " + screen.getTotalSeats());

            Show show = Show.builder()
                    .movie(movie)
                    .theater(theater)
                    .screen(screen)
                    .showDate(request.getShowDate())
                    .startTime(request.getStartTime())
                    .endTime(request.getEndTime())
                    .ticketPrice(request.getTicketPrice())
                    .status(ShowStatus.ACTIVE)
                    .build();

            show = showRepository.save(show);

            System.out.println("Show saved successfully. ID: " + show.getId());

            generateSeats(show, screen.getTotalSeats(), request.getTicketPrice());

            System.out.println("Seats generated successfully.");

            return toResponse(show);

        } catch (Exception e) {

            e.printStackTrace();

            throw new RuntimeException("CREATE SHOW FAILED: " + e.getMessage());
        }
    }
    
    private void generateSeats(Show show, int totalSeats, double basePrice) {
        List<Seat> seats = new ArrayList<>();
        int silverCount = (int) (totalSeats * 0.5);
        int goldCount = (int) (totalSeats * 0.3);
        int platinumCount = totalSeats - silverCount - goldCount;

        for (int i = 1; i <= silverCount; i++) {
            seats.add(Seat.builder()
                    .seatNumber("S" + i)
                    .category(SeatCategory.SILVER)
                    .status(SeatStatus.AVAILABLE)
                    .price(basePrice)
                    .show(show)
                    .build());
        }
        for (int i = 1; i <= goldCount; i++) {
            seats.add(Seat.builder()
                    .seatNumber("G" + i)
                    .category(SeatCategory.GOLD)
                    .status(SeatStatus.AVAILABLE)
                    .price(basePrice * 1.5)
                    .show(show)
                    .build());
        }
        for (int i = 1; i <= platinumCount; i++) {
            seats.add(Seat.builder()
                    .seatNumber("P" + i)
                    .category(SeatCategory.PLATINUM)
                    .status(SeatStatus.AVAILABLE)
                    .price(basePrice * 2.0)
                    .show(show)
                    .build());
        }
        seatRepository.saveAll(seats);
    }

    @Transactional(readOnly = true)
    public ShowResponse getShowById(Long id) {
        return toResponse(findById(id));
    }

    @Transactional(readOnly = true)
    public List<ShowResponse> getShowsByMovie(Long movieId, LocalDate date) {
        List<Show> shows = date != null
                ? showRepository.findByMovieIdAndShowDateAndStatus(movieId, date, ShowStatus.ACTIVE)
                : showRepository.findByMovieIdAndStatus(movieId, ShowStatus.ACTIVE);
        return shows.stream().map(this::toResponse).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ShowResponse> getShowsByDate(LocalDate date) {
        return showRepository.findByShowDateAndStatus(date, ShowStatus.ACTIVE)
                .stream().map(this::toResponse).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ShowResponse> getAllShows() {
        return showRepository.findAll().stream().map(this::toResponse).collect(Collectors.toList());
    }

    public void deleteShow(Long id) {
        showRepository.delete(findById(id));
    }

    public Show findById(Long id) {
        return showRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Show not found with id: " + id));
    }

    public ShowResponse toResponse(Show show) {
        long availableSeats = seatRepository.countByShowIdAndStatus(show.getId(), SeatStatus.AVAILABLE);
        long totalSeats = seatRepository.countByShowIdAndStatus(show.getId(), SeatStatus.AVAILABLE)
                + seatRepository.countByShowIdAndStatus(show.getId(), SeatStatus.BOOKED)
                + seatRepository.countByShowIdAndStatus(show.getId(), SeatStatus.LOCKED);

        return ShowResponse.builder()
                .id(show.getId())
                .movieId(show.getMovie().getId())
                .movieTitle(show.getMovie().getTitle())
                .moviePosterUrl(show.getMovie().getPosterUrl())
                .theaterId(show.getTheater().getId())
                .theaterName(show.getTheater().getTheaterName())
                .theaterCity(show.getTheater().getCity())
                .screenId(show.getScreen().getId())
                .screenName(show.getScreen().getScreenName())
                .showDate(show.getShowDate())
                .startTime(show.getStartTime())
                .endTime(show.getEndTime())
                .ticketPrice(show.getTicketPrice())
                .status(show.getStatus())
                .availableSeats(availableSeats)
                .totalSeats(totalSeats)
                .build();
    }
}
