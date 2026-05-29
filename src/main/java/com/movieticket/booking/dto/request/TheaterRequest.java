package com.movieticket.booking.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class TheaterRequest {

    @NotBlank(message = "Theater name is required")
    private String theaterName;

    @NotBlank(message = "City is required")
    private String city;

    private String address;
    private Integer totalScreens;
    private String facilities;
}
