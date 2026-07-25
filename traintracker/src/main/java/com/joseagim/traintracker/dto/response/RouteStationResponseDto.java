package com.joseagim.traintracker.dto.response;

import com.joseagim.traintracker.entity.RouteStation;

public record RouteStationResponseDto(
        Long id,
        StationResponseDto station,
        int stopOrder,
        int minutesFromStart
) {

    public static RouteStationResponseDto from(RouteStation rs) {
        return new RouteStationResponseDto(
                rs.getId(),
                StationResponseDto.from(rs.getStation()),
                rs.getStopOrder(),
                rs.getMinutesFromStart()
        );
    }

}
