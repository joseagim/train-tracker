package com.joseagim.traintracker.dto.response;

public record TrainResponseDto(
        Long id,
        String type,
        int bogeys,
        int seatsByBogey
) {
}
