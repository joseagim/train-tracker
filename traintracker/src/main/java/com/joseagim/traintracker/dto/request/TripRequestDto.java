package com.joseagim.traintracker.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.time.LocalDateTime;

public record TripRequestDto(
        @Positive Long routeId,
        @Positive Long trainId,
        @NotNull LocalDateTime departureTime
        ) {
}
