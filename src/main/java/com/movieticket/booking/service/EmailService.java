package com.movieticket.booking.service;

import com.movieticket.booking.entity.Booking;
import com.movieticket.booking.entity.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {

    private final JavaMailSender mailSender;

    // -------------------------
    // WELCOME EMAIL
    // -------------------------
    @Async
    public void sendWelcomeEmail(User user) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(user.getEmail());
            message.setSubject("Welcome to Movie Ticket Booking System!");
            message.setText(
                    "Dear " + user.getName() + ",\n\n" +
                    "Welcome to Movie Ticket Booking System!\n\n" +
                    "Your account has been created successfully.\n" +
                    "Email: " + user.getEmail() + "\n\n" +
                    "Happy Booking!\n\n" +
                    "Team Movie Booking"
            );

            mailSender.send(message);
            log.info("Welcome email sent to {}", user.getEmail());

        } catch (Exception e) {
            log.error("Failed to send welcome email to {}: {}", user.getEmail(), e.getMessage());
        }
    }

    // -------------------------
    // BOOKING CONFIRMATION EMAIL
    // -------------------------
    @Async
    public void sendBookingConfirmationEmail(User user, Booking booking) {
        try {
            if (user == null || booking == null) {
                log.warn("Skipping email: user or booking is null");
                return;
            }

            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(user.getEmail());
            message.setSubject("Booking Confirmation - " + booking.getBookingId());

            message.setText(
                    "Dear " + user.getName() + ",\n\n" +
                    "Your booking is confirmed!\n\n" +
                    "Booking ID: " + booking.getBookingId() + "\n" +
                    "Total Amount: Rs. " + booking.getTotalAmount() + "\n" +
                    "Status: " + booking.getStatus() + "\n\n" +
                    "Enjoy your movie!\n\n" +
                    "Team Movie Booking"
            );

            mailSender.send(message);
            log.info("Booking confirmation email sent to {}", user.getEmail());

        } catch (Exception e) {
            log.error("Failed to send booking email for bookingId {}: {}",
                    booking != null ? booking.getBookingId() : "NULL",
                    e.getMessage());
        }
    }

    // -------------------------
    // CANCELLATION EMAIL
    // -------------------------
    @Async
    public void sendBookingCancellationEmail(User user, Booking booking) {
        try {
            if (user == null || booking == null) return;

            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(user.getEmail());
            message.setSubject("Booking Cancelled - " + booking.getBookingId());

            message.setText(
                    "Dear " + user.getName() + ",\n\n" +
                    "Your booking has been cancelled.\n\n" +
                    "Booking ID: " + booking.getBookingId() + "\n" +
                    "Refund will be processed soon.\n\n" +
                    "Team Movie Booking"
            );

            mailSender.send(message);
            log.info("Cancellation email sent to {}", user.getEmail());

        } catch (Exception e) {
            log.error("Failed cancellation email: {}", e.getMessage());
        }
    }

    // -------------------------
    // PAYMENT SUCCESS EMAIL
    // -------------------------
    @Async
    public void sendPaymentSuccessEmail(User user, Booking booking) {
        try {
            if (user == null || booking == null) return;

            String txnId = (booking.getPayment() != null)
                    ? booking.getPayment().getTransactionId()
                    : "N/A";

            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(user.getEmail());
            message.setSubject("Payment Successful - " + booking.getBookingId());

            message.setText(
                    "Dear " + user.getName() + ",\n\n" +
                    "Payment successful!\n\n" +
                    "Booking ID: " + booking.getBookingId() + "\n" +
                    "Transaction ID: " + txnId + "\n" +
                    "Amount: Rs. " + booking.getTotalAmount() + "\n\n" +
                    "Enjoy your movie!\n\n" +
                    "Team Movie Booking"
            );

            mailSender.send(message);
            log.info("Payment email sent to {}", user.getEmail());

        } catch (Exception e) {
            log.error("Failed payment email: {}", e.getMessage());
        }
    }
}