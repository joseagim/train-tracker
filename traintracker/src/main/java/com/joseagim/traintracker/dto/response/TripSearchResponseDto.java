package com.joseagim.traintracker.dto.response;

import com.joseagim.traintracker.entity.Trip;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

public record TripSearchResponseDto(
        Long id,
        TrainResponseDto train,
        LocalDateTime departureTime,
        String seats,
        Long routeId,
        List<TripSearchStationResponseDto> stations
) {

    public static TripSearchResponseDto from(Trip trip) {
        return new TripSearchResponseDto(
                trip.getId(),
                TrainResponseDto.from(trip.getTrain()),
                trip.getDepartureTime(),
                trip.getSeats(),
                trip.getRoute().getId(),
                trip.getRoute().getRouteStations().stream()
                        .map(rs -> TripSearchStationResponseDto.from(rs, trip.getDepartureTime()))
                        .collect(Collectors.toList())
        );
    }

}
