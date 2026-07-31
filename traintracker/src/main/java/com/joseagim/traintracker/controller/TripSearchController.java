package com.joseagim.traintracker.controller;

import com.joseagim.traintracker.dto.response.TripSearchResponseDto;
import com.joseagim.traintracker.service.TripSearchService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/trips/search")
public class TripSearchController {

    private final TripSearchService tripSearchService;

    public TripSearchController(TripSearchService tripSearchService) {
        this.tripSearchService = tripSearchService;
    }

    @GetMapping
    public ResponseEntity<List<TripSearchResponseDto>> search(
            @RequestParam Long from,
            @RequestParam Long to,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam int passengers) {
        return ResponseEntity.ok(tripSearchService.searchTrips(from, to, date, passengers));
    }

}
