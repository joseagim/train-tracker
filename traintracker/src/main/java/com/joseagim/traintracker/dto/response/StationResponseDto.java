package com.joseagim.traintracker.dto.response;

import jakarta.validation.constraints.NotBlank;

public record StationResponseDto(
        Long id,
        @NotBlank String name,
        @NotBlank String city
) {
}
