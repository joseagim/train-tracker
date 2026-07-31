package com.joseagim.traintracker.dto.response;

import com.joseagim.traintracker.entity.RouteStation;

import java.time.LocalDateTime;

public record TripSearchStationResponseDto(
        String name,
        LocalDateTime estimatedTime
) {

    public static TripSearchStationResponseDto from(RouteStation rs, LocalDateTime departureTime) {
        return new TripSearchStationResponseDto(
                rs.getStation().getName(),
                departureTime.plusMinutes(rs.getMinutesFromStart())
        );
    }

}
