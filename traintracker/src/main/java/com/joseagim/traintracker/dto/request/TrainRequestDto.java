package com.joseagim.traintracker.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;

public record TrainRequestDto(
        @NotBlank String type,
        @Positive int bogeys,
        @Positive int seatsByBogey
) {
}
