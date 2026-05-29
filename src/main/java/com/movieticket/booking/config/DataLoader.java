package com.movieticket.booking.config;

import com.movieticket.booking.entity.*;
import com.movieticket.booking.repository.*;

//import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Component

public class DataLoader implements CommandLineRunner {
	
	private static final Logger log =
	        LoggerFactory.getLogger(DataLoader.class);

    private final UserRepository userRepository;
    private final MovieRepository movieRepository;
    private final TheaterRepository theaterRepository;
    private final ScreenRepository screenRepository;
    private final ShowRepository showRepository;
    private final SeatRepository seatRepository;
    private final PasswordEncoder passwordEncoder;

    // MANUAL CONSTRUCTOR
    public DataLoader(
            UserRepository userRepository,
            MovieRepository movieRepository,
            TheaterRepository theaterRepository,
            ScreenRepository screenRepository,
            ShowRepository showRepository,
            SeatRepository seatRepository,
            PasswordEncoder passwordEncoder
    ) {
        this.userRepository = userRepository;
        this.movieRepository = movieRepository;
        this.theaterRepository = theaterRepository;
        this.screenRepository = screenRepository;
        this.showRepository = showRepository;
        this.seatRepository = seatRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {

        if (userRepository.count() > 0) {
            log.info("Database already seeded");
            return;
        }

        log.info("Seeding database...");

        // ADMIN USER
        User admin = User.builder()
                .name("Admin")
                .email("admin@gmail.com")
                .password(passwordEncoder.encode("Admin@123"))
                .role(Role.ROLE_ADMIN)
                .active(true)
                .build();

        // NORMAL USER
        User user = User.builder()
                .name("John")
                .email("user@gmail.com")
                .password(passwordEncoder.encode("User@123"))
                .role(Role.ROLE_USER)
                .active(true)
                .build();

        userRepository.save(admin);
        userRepository.save(user);

        // MOVIE
        Movie movie = Movie.builder()
                .title("Avengers Endgame")
                .description("Marvel Movie")
                .genre("Action")
                .language("English")
                .duration(180)
                .releaseDate(LocalDate.now())
                .rating(8.5)
                .posterUrl("poster.jpg")
                .status(MovieStatus.NOW_SHOWING)
                .build();

        movieRepository.save(movie);

        // THEATER
        Theater theater = Theater.builder()
                .theaterName("PVR Cinemas")
                .city("Chennai")
                .address("VR Mall")
                .totalScreens(2)
                .facilities("Dolby Atmos")
                .build();

        theaterRepository.save(theater);

        // SCREEN
        Screen screen = Screen.builder()
                .screenName("Screen 1")
                .totalSeats(50)
                .theater(theater)
                .build();

        screenRepository.save(screen);

        // SHOW
        Show show = Show.builder()
                .movie(movie)
                .theater(theater)
                .screen(screen)
                .showDate(LocalDate.now())
                .startTime(LocalTime.of(10, 0))
                .endTime(LocalTime.of(13, 0))
                .ticketPrice(250.0)
                .status(ShowStatus.ACTIVE)
                .build();

        showRepository.save(show);

        // SEATS
        for (int i = 1; i <= 50; i++) {

            Seat seat = Seat.builder()
                    .seatNumber("A" + i)
                    .category(SeatCategory.SILVER)
                    .status(SeatStatus.AVAILABLE)
                    .price(250.0)
                    .show(show)
                    .build();

            seatRepository.save(seat);
        }

        log.info("Database seeded successfully");
    }
}