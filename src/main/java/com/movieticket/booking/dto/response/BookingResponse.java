package com.movieticket.booking.dto.response;

import com.movieticket.booking.entity.BookingStatus;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class BookingResponse {
    private Long id;
    private String bookingId;
    private Long userId;
    private String userName;
    private String userEmail;
    private Long showId;
    private String movieTitle;
    private String theaterName;
    private String theaterCity;
    private String screenName;
    private String showDate;
    private String startTime;
    private List<String> seatNumbers;
    private Double totalAmount;
    private BookingStatus status;
    private LocalDateTime bookedAt;
    private PaymentResponse payment;
}
