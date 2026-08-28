package com.joseagim.traintracker.controller;

import com.joseagim.traintracker.dto.request.TicketRequestDto;
import com.joseagim.traintracker.dto.response.TicketResponseDto;
import com.joseagim.traintracker.entity.User;
import com.joseagim.traintracker.service.TicketService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tickets")
public class TicketController {

    private final TicketService ticketService;

    public TicketController(TicketService ticketService) {
        this.ticketService = ticketService;
    }

    @GetMapping("/my-tickets")
    public ResponseEntity<List<TicketResponseDto>> findMyTickets(
            @AuthenticationPrincipal User user) {

        return ResponseEntity.ok(ticketService.findByUser(user));

    }

    @PostMapping
    public ResponseEntity<TicketResponseDto> purchase(
            @Valid @RequestBody TicketRequestDto ticketRequest,
            @AuthenticationPrincipal User user) {

        return ResponseEntity.status(HttpStatus.CREATED).body(ticketService.purchase(user, ticketRequest));

    }

}
