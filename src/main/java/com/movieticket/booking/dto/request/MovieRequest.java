package com.movieticket.booking.dto.request;

import com.movieticket.booking.entity.MovieStatus;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import java.time.LocalDate;

@Data
public class MovieRequest {

    @NotBlank(message = "Title is required")
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
}
