package com.joseagim.traintracker.dto.response;

public record RouteStationResponseDto(
        Long id,
        StationResponseDto station,
        int stopOrder,
        int minutesFromStart
) {
}
