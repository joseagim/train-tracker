package com.joseagim.traintracker.dto.request;

import jakarta.validation.constraints.NotBlank;

public record StationRequestDto(
        @NotBlank String name,
        @NotBlank String city
) {
}
