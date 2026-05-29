package com.movieticket.booking.dto.response;

import com.movieticket.booking.entity.SeatCategory;
import com.movieticket.booking.entity.SeatStatus;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SeatResponse {
    private Long id;
    private String seatNumber;
    private SeatCategory category;
    private SeatStatus status;
    private Double price;
    private Long showId;
}
