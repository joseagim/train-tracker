package com.joseagim.traintracker.dto.request;

import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;

public record RouteStationRequestDto(
        @Positive Long stationId,
        @Positive int stopOrder,
        @PositiveOrZero int minutesFromStart
) {
}
