package com.movieticket.booking.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.movieticket.booking.dto.response.MovieResponse;
import com.movieticket.booking.entity.MovieStatus;
import com.movieticket.booking.security.CustomUserDetailsService;
import com.movieticket.booking.security.JwtAuthenticationFilter;
import com.movieticket.booking.service.AuthService;
import com.movieticket.booking.service.MovieService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(MovieController.class)
@AutoConfigureMockMvc(addFilters = false)
class MovieControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private MovieService movieService;

    @MockBean
    private AuthService authService;

    @MockBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @MockBean
    private CustomUserDetailsService customUserDetailsService;

    @Test
    void getAllMovies_ShouldReturn200() throws Exception {

        MovieResponse movie = MovieResponse.builder()
                .id(1L)
                .title("Test Movie")
                .genre("Action")
                .status(MovieStatus.NOW_SHOWING)
                .build();

        Page<MovieResponse> page = new PageImpl<>(
                List.of(movie),
                PageRequest.of(0, 10),
                1
        );

        when(movieService.getAllMovies(any(Pageable.class)))
                .thenReturn(page);

        when(movieService.searchMovies(any(), any(), any(), any(), any(Pageable.class)))
                .thenReturn(page);

        mockMvc.perform(get("/api/movies")
                        .param("page", "0")
                        .param("size", "10")
                        .param("sortBy", "id"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].title").value("Test Movie"));
    }

    @Test
    void getMovieById_ShouldReturn200() throws Exception {
        MovieResponse movie = MovieResponse.builder()
                .id(1L)
                .title("Test Movie")
                .build();

        when(movieService.getMovieById(1L)).thenReturn(movie);

        mockMvc.perform(get("/api/movies/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L));
    }

    @Test
    void getTrendingMovies_ShouldReturn200() throws Exception {
        when(movieService.getTrendingMovies()).thenReturn(List.of());
        mockMvc.perform(get("/api/movies/trending"))
                .andExpect(status().isOk());
    }
}
