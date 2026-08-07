package com.joseagim.traintracker.dto.response;

import com.joseagim.traintracker.entity.User;

public record RegisterResponseDto(
        Long id,
        String firstName,
        String lastName,
        String email,
        String phoneNumber,
        String dni
) {

    public static RegisterResponseDto from(User user) {
        return new RegisterResponseDto(
                user.getId(),
                user.getFirstName(),
                user.getLastName(),
                user.getEmail(),
                user.getPhoneNumber(),
                user.getDni()
        );
    }

}
