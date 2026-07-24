package com.joseagim.traintracker.controller;

import com.joseagim.traintracker.dto.request.RouteRequestDto;
import com.joseagim.traintracker.dto.response.RouteResponseDto;
import com.joseagim.traintracker.service.RouteService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/routes")
public class RouteController {

    private final RouteService routeService;

    public RouteController(RouteService routeService) {
        this.routeService = routeService;
    }

    @PostMapping
    public ResponseEntity<RouteResponseDto> create(@Valid @RequestBody RouteRequestDto routeRequest) {
        return ResponseEntity.status(HttpStatus.CREATED).body(routeService.create(routeRequest));
    }

    @GetMapping
    public ResponseEntity<List<RouteResponseDto>> findAll() {
        return ResponseEntity.ok(routeService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<RouteResponseDto> findById(@PathVariable Long id) {
        return ResponseEntity.ok(routeService.findById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<RouteResponseDto> update(
            @PathVariable Long id, @Valid @RequestBody RouteRequestDto routeRequest) {
        return ResponseEntity.ok(routeService.update(id, routeRequest));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        routeService.delete(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

}
