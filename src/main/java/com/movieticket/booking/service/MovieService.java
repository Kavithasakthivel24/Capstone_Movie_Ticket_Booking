package com.movieticket.booking.service;

import com.movieticket.booking.dto.request.MovieRequest;
import com.movieticket.booking.dto.response.MovieResponse;
import com.movieticket.booking.entity.Movie;
import com.movieticket.booking.entity.MovieStatus;
import com.movieticket.booking.exception.ResourceNotFoundException;
import com.movieticket.booking.repository.MovieRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MovieService {

    private final MovieRepository movieRepository;

    public MovieResponse createMovie(MovieRequest request) {
        Movie movie = Movie.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .genre(request.getGenre())
                .language(request.getLanguage())
                .duration(request.getDuration())
                .releaseDate(request.getReleaseDate())
                .rating(request.getRating())
                .posterUrl(request.getPosterUrl())
                .trailerUrl(request.getTrailerUrl())
                .status(request.getStatus() != null ? request.getStatus() : MovieStatus.NOW_SHOWING)
                .build();
        return toResponse(movieRepository.save(movie));
    }

    public MovieResponse getMovieById(Long id) {
        return toResponse(findById(id));
    }

    public Page<MovieResponse> getAllMovies(Pageable pageable) {
        return movieRepository.findAll(pageable).map(this::toResponse);
    }

    public Page<MovieResponse> searchMovies(String title, String genre, String language,
                                             MovieStatus status, Pageable pageable) {
        return movieRepository.searchMovies(title, genre, language, status, pageable).map(this::toResponse);
    }

    public List<MovieResponse> getTrendingMovies() {
        return movieRepository.findTop10ByStatusOrderByRatingDesc(MovieStatus.NOW_SHOWING)
                .stream().map(this::toResponse).collect(Collectors.toList());
    }

    public MovieResponse updateMovie(Long id, MovieRequest request) {
        Movie movie = findById(id);
        movie.setTitle(request.getTitle());
        movie.setDescription(request.getDescription());
        movie.setGenre(request.getGenre());
        movie.setLanguage(request.getLanguage());
        movie.setDuration(request.getDuration());
        movie.setReleaseDate(request.getReleaseDate());
        movie.setRating(request.getRating());
        movie.setPosterUrl(request.getPosterUrl());
        movie.setTrailerUrl(request.getTrailerUrl());
        if (request.getStatus() != null) movie.setStatus(request.getStatus());
        return toResponse(movieRepository.save(movie));
    }

    public void deleteMovie(Long id) {
        Movie movie = findById(id);
        movieRepository.delete(movie);
    }

    private Movie findById(Long id) {
        return movieRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Movie not found with id: " + id));
    }

    public MovieResponse toResponse(Movie movie) {
        return MovieResponse.builder()
                .id(movie.getId())
                .title(movie.getTitle())
                .description(movie.getDescription())
                .genre(movie.getGenre())
                .language(movie.getLanguage())
                .duration(movie.getDuration())
                .releaseDate(movie.getReleaseDate())
                .rating(movie.getRating())
                .posterUrl(movie.getPosterUrl())
                .trailerUrl(movie.getTrailerUrl())
                .status(movie.getStatus())
                .createdAt(movie.getCreatedAt())
                .build();
    }
}
