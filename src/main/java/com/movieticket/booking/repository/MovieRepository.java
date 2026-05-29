package com.movieticket.booking.repository;

import com.movieticket.booking.entity.Movie;
import com.movieticket.booking.entity.MovieStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MovieRepository extends JpaRepository<Movie, Long> {
    Page<Movie> findByStatus(MovieStatus status, Pageable pageable);

    @Query("SELECT m FROM Movie m WHERE " +
           "(:title IS NULL OR LOWER(m.title) LIKE LOWER(CONCAT('%', :title, '%'))) AND " +
           "(:genre IS NULL OR LOWER(m.genre) = LOWER(:genre)) AND " +
           "(:language IS NULL OR LOWER(m.language) = LOWER(:language)) AND " +
           "(:status IS NULL OR m.status = :status)")
    Page<Movie> searchMovies(@Param("title") String title,
                             @Param("genre") String genre,
                             @Param("language") String language,
                             @Param("status") MovieStatus status,
                             Pageable pageable);

    List<Movie> findTop10ByStatusOrderByRatingDesc(MovieStatus status);
}
