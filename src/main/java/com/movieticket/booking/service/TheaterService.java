package com.movieticket.booking.service;

import com.movieticket.booking.dto.request.TheaterRequest;
import com.movieticket.booking.dto.response.TheaterResponse;
import com.movieticket.booking.entity.Theater;
import com.movieticket.booking.exception.ResourceNotFoundException;
import com.movieticket.booking.repository.TheaterRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TheaterService {

    private final TheaterRepository theaterRepository;

    public TheaterResponse createTheater(TheaterRequest request) {
        Theater theater = Theater.builder()
                .theaterName(request.getTheaterName())
                .city(request.getCity())
                .address(request.getAddress())
                .totalScreens(request.getTotalScreens())
                .facilities(request.getFacilities())
                .build();
        return toResponse(theaterRepository.save(theater));
    }

    public TheaterResponse getTheaterById(Long id) {
        return toResponse(findById(id));
    }

    public List<TheaterResponse> getAllTheaters() {
        return theaterRepository.findAll().stream().map(this::toResponse).collect(Collectors.toList());
    }

    public List<TheaterResponse> getTheatersByCity(String city) {
        return theaterRepository.findByCityIgnoreCase(city).stream()
                .map(this::toResponse).collect(Collectors.toList());
    }

    public TheaterResponse updateTheater(Long id, TheaterRequest request) {
        Theater theater = findById(id);
        theater.setTheaterName(request.getTheaterName());
        theater.setCity(request.getCity());
        theater.setAddress(request.getAddress());
        theater.setTotalScreens(request.getTotalScreens());
        theater.setFacilities(request.getFacilities());
        return toResponse(theaterRepository.save(theater));
    }

    public void deleteTheater(Long id) {
        Theater theater = findById(id);
        theaterRepository.delete(theater);
    }

    public Theater findById(Long id) {
        return theaterRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Theater not found with id: " + id));
    }

    public TheaterResponse toResponse(Theater theater) {
        return TheaterResponse.builder()
                .id(theater.getId())
                .theaterName(theater.getTheaterName())
                .city(theater.getCity())
                .address(theater.getAddress())
                .totalScreens(theater.getTotalScreens())
                .facilities(theater.getFacilities())
                .build();
    }
}
