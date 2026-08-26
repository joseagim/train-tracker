package com.joseagim.traintracker.dto.request;

import jakarta.validation.constraints.NotNull;

public record TicketRequestDto(
        @NotNull Long tripId,
        @NotNull Long origin,
        @NotNull Long destination
) {
}
