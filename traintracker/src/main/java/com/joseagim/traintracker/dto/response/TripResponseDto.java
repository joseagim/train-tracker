package com.joseagim.traintracker.dto.response;

import com.joseagim.traintracker.entity.Trip;
import com.joseagim.traintracker.entity.TripStatus;

import java.time.LocalDateTime;

public record TripResponseDto(
        Long id,
        RouteResponseDto route,
        TrainResponseDto train,
        LocalDateTime departureTime,
        TripStatus status,
        String seats
) {

    public static TripResponseDto from(Trip trip) {
        return new TripResponseDto(
                trip.getId(),
                RouteResponseDto.from(trip.getRoute()),
                TrainResponseDto.from(trip.getTrain()),
                trip.getDepartureTime(),
                trip.getStatus(),
                trip.getSeats()
        );
    }

}
