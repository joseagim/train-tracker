package com.joseagim.traintracker.dto.response;

import com.joseagim.traintracker.entity.Route;

import java.util.List;
import java.util.stream.Collectors;

public record RouteResponseDto(
        Long id,
        String name,
        List<RouteStationResponseDto> routeStations
) {

    public static RouteResponseDto from(Route route) {
        return new RouteResponseDto(
                route.getId(),
                route.getName(),
                route.getRouteStations().stream()
                        .map(RouteStationResponseDto::from)
                        .collect(Collectors.toList())
        );
    }

}
