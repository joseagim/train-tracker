package com.joseagim.traintracker.controller;

import com.joseagim.traintracker.dto.request.TripRequestDto;
import com.joseagim.traintracker.dto.response.TripResponseDto;
import com.joseagim.traintracker.service.TripService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/trips")
public class TripController {

    private final TripService tripService;

    public TripController(TripService tripService) {
        this.tripService = tripService;
    }

    @PostMapping
    public ResponseEntity<TripResponseDto> create(@Valid @RequestBody TripRequestDto tripRequest) {
        return ResponseEntity.status(HttpStatus.CREATED).body(tripService.create(tripRequest));
    }

    @GetMapping("/{id}")
    public ResponseEntity<TripResponseDto> findById(@PathVariable Long id) {
        return ResponseEntity.ok(tripService.findById(id));
    }

    @GetMapping
    public ResponseEntity<List<TripResponseDto>> findAll() {
        return ResponseEntity.ok(tripService.findAll());
    }

    @PutMapping("/{id}")
    public ResponseEntity<TripResponseDto> update(
            @PathVariable Long id, @Valid @RequestBody TripRequestDto tripRequest) {
        return ResponseEntity.ok(tripService.update(id, tripRequest));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        tripService.delete(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
    
}
