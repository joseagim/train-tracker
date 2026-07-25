package com.joseagim.traintracker.dto.response;

import com.joseagim.traintracker.entity.Train;

public record TrainResponseDto(
        Long id,
        String type,
        int bogeys,
        int seatsByBogey
) {

    public static TrainResponseDto from(Train train) {
        return new TrainResponseDto(
                train.getId(),
                train.getType(),
                train.getBogeys(),
                train.getSeatsByBogey()
        );
    }

}
