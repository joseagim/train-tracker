package com.joseagim.traintracker.dto.response;

import com.joseagim.traintracker.entity.User;

public record LoginResponseDto(
        String token,
        String firstName,
        String lastName,
        String email,
        String role
) {

    public static LoginResponseDto from(User user, String token) {
        return new LoginResponseDto(
                token,
                user.getFirstName(),
                user.getLastName(),
                user.getEmail(),
                user.getRole().name()
        );
    }

}
