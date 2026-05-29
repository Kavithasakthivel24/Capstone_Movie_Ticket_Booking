package com.movieticket.booking.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TheaterResponse {
    private Long id;
    private String theaterName;
    private String city;
    private String address;
    private Integer totalScreens;
    private String facilities;
}
