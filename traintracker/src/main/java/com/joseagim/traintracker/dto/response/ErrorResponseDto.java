package com.joseagim.traintracker.dto.response;

import java.time.LocalDateTime;

public record ErrorResponseDto(
        String message,
        int status,
        LocalDateTime timestamp,
        String path,
        String method
) {
}
