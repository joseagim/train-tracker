package com.joseagim.traintracker.controller;

import com.joseagim.traintracker.dto.request.RegisterRequestDto;
import com.joseagim.traintracker.dto.response.RegisterResponseDto;
import com.joseagim.traintracker.security.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity<RegisterResponseDto> register(
            @Valid @RequestBody RegisterRequestDto registerRequest) {

        return ResponseEntity.status(HttpStatus.CREATED).body(authService.register(registerRequest));

    }

}
