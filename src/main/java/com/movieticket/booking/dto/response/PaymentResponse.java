package com.movieticket.booking.dto.response;

import com.movieticket.booking.entity.PaymentMethod;
import com.movieticket.booking.entity.PaymentStatus;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class PaymentResponse {
    private Long id;
    private String transactionId;
    private Double amount;
    private PaymentStatus status;
    private PaymentMethod method;
    private LocalDateTime paidAt;
}
