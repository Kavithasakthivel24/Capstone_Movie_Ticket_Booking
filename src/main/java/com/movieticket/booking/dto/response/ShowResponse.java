package com.movieticket.booking.dto.response;

import com.movieticket.booking.entity.ShowStatus;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
@Builder
public class ShowResponse {
    private Long id;
    private Long movieId;
    private String movieTitle;
    private String moviePosterUrl;
    private Long theaterId;
    private String theaterName;
    private String theaterCity;
    private Long screenId;
    private String screenName;
    private LocalDate showDate;
    private LocalTime startTime;
    private LocalTime endTime;
    private Double ticketPrice;
    private ShowStatus status;
    private Long availableSeats;
    private Long totalSeats;
}
