package com.joseagim.traintracker.dto.response;

import com.joseagim.traintracker.entity.Station;

public record StationResponseDto(
        Long id,
        String name,
        String city
) {

    public static StationResponseDto from(Station station) {
        return new StationResponseDto(station.getId(), station.getName(), station.getCity());
    }

}
