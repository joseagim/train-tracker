package com.joseagim.traintracker.controller;

import com.joseagim.traintracker.dto.request.TrainRequestDto;
import com.joseagim.traintracker.dto.response.TrainResponseDto;
import com.joseagim.traintracker.service.TrainService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/trains")
public class TrainController {

    private final TrainService trainService;

    public TrainController(TrainService trainService) {
        this.trainService = trainService;
    }

    @PostMapping
    public ResponseEntity<TrainResponseDto> create(@Valid @RequestBody TrainRequestDto trainRequest) {
        return ResponseEntity.status(HttpStatus.CREATED).body(trainService.create(trainRequest));
    }

    @GetMapping
    public ResponseEntity<List<TrainResponseDto>> findAll() {
        return ResponseEntity.ok(trainService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<TrainResponseDto> findById(@PathVariable Long id) {
        return ResponseEntity.ok(trainService.findById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<TrainResponseDto> edit(
            @PathVariable Long id, @Valid @RequestBody TrainRequestDto trainRequest) {
        return ResponseEntity.ok(trainService.edit(id, trainRequest));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        trainService.delete(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
