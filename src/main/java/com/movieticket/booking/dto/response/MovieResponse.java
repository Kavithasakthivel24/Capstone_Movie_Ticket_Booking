package com.movieticket.booking.dto.response;

import com.movieticket.booking.entity.MovieStatus;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
public class MovieResponse {
    private Long id;
    private String title;
    private String description;
    private String genre;
    private String language;
    private Integer duration;
    private LocalDate releaseDate;
    private Double rating;
    private String posterUrl;
    private String trailerUrl;
    private MovieStatus status;
    private LocalDateTime createdAt;
}
