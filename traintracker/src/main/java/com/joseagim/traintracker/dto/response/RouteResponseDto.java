package com.joseagim.traintracker.dto.response;

import java.util.List;

public record RouteResponseDto(
        Long id,
        String name,
        List<RouteStationResponseDto> routeStations
) {
}
