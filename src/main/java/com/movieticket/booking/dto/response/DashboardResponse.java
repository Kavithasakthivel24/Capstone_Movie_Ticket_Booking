package com.movieticket.booking.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class DashboardResponse {
    private long totalUsers;
    private long totalMovies;
    private long totalTheaters;
    private long totalShows;
    private long totalBookings;
    private long confirmedBookings;
    private long cancelledBookings;
    private double totalRevenue;
}
