package com.joseagim.traintracker.controller;

import com.joseagim.traintracker.dto.request.StationRequestDto;
import com.joseagim.traintracker.dto.response.StationResponseDto;
import com.joseagim.traintracker.service.StationService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/stations")
public class StationController {

    private final StationService stationService;

    public StationController(StationService stationService) {
        this.stationService = stationService;
    }

    @PostMapping
    public ResponseEntity<StationResponseDto> create(@Valid @RequestBody StationRequestDto stationRequest) {
        return ResponseEntity.status(HttpStatus.CREATED).body(stationService.create(stationRequest));
    }

    @GetMapping
    public ResponseEntity<Page<StationResponseDto>> findAll(Pageable pageable) {
        return ResponseEntity.ok(stationService.findAll(pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<StationResponseDto> findById(@PathVariable Long id) {
        return ResponseEntity.ok(stationService.findById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<StationResponseDto> update(@PathVariable Long id, @Valid @RequestBody StationRequestDto stationRequest) {
        return ResponseEntity.ok(stationService.update(id, stationRequest));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        stationService.delete(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

}
