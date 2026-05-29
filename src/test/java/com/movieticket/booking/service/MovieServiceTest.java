package com.movieticket.booking.service;

import com.movieticket.booking.dto.request.MovieRequest;
import com.movieticket.booking.dto.response.MovieResponse;
import com.movieticket.booking.entity.Movie;
import com.movieticket.booking.entity.MovieStatus;
import com.movieticket.booking.exception.ResourceNotFoundException;
import com.movieticket.booking.repository.MovieRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MovieServiceTest {

    @Mock
    private MovieRepository movieRepository;

    @InjectMocks
    private MovieService movieService;

    private Movie movie;
    private MovieRequest movieRequest;

    @BeforeEach
    void setUp() {
        movie = Movie.builder()
                .id(1L)
                .title("Test Movie")
                .genre("Action")
                .language("English")
                .duration(120)
                .releaseDate(LocalDate.now())
                .rating(8.0)
                .status(MovieStatus.NOW_SHOWING)
                .build();

        movieRequest = new MovieRequest();
        movieRequest.setTitle("Test Movie");
        movieRequest.setGenre("Action");
        movieRequest.setLanguage("English");
        movieRequest.setDuration(120);
        movieRequest.setRating(8.0);
    }

    @Test
    void createMovie_ShouldReturnMovieResponse() {
        when(movieRepository.save(any(Movie.class))).thenReturn(movie);
        MovieResponse response = movieService.createMovie(movieRequest);
        assertNotNull(response);
        assertEquals("Test Movie", response.getTitle());
    }

    @Test
    void getMovieById_ShouldReturnMovieWhenExists() {
        when(movieRepository.findById(1L)).thenReturn(Optional.of(movie));
        MovieResponse response = movieService.getMovieById(1L);
        assertNotNull(response);
        assertEquals(1L, response.getId());
    }

    @Test
    void getMovieById_ShouldThrowWhenNotFound() {
        when(movieRepository.findById(999L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> movieService.getMovieById(999L));
    }

    @Test
    void getAllMovies_ShouldReturnPagedMovies() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Movie> page = new PageImpl<>(List.of(movie));
        when(movieRepository.findAll(pageable)).thenReturn(page);

        Page<MovieResponse> response = movieService.getAllMovies(pageable);
        assertNotNull(response);
        assertEquals(1, response.getTotalElements());
    }

    @Test
    void deleteMovie_ShouldDeleteSuccessfully() {
        when(movieRepository.findById(1L)).thenReturn(Optional.of(movie));
        doNothing().when(movieRepository).delete(any(Movie.class));
        assertDoesNotThrow(() -> movieService.deleteMovie(1L));
        verify(movieRepository).delete(movie);
    }

    @Test
    void getTrendingMovies_ShouldReturnList() {
        when(movieRepository.findTop10ByStatusOrderByRatingDesc(MovieStatus.NOW_SHOWING))
                .thenReturn(List.of(movie));
        List<MovieResponse> trending = movieService.getTrendingMovies();
        assertFalse(trending.isEmpty());
    }
}
