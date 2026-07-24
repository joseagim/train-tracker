package com.joseagim.traintracker.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.List;

public record RouteRequestDto(
        @NotBlank String name,
        @Size(min = 2) List<RouteStationRequestDto> routeStations
) {
}
