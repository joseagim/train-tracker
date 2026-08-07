package com.joseagim.traintracker.security;

import com.joseagim.traintracker.dto.request.RegisterRequestDto;
import com.joseagim.traintracker.dto.response.RegisterResponseDto;
import com.joseagim.traintracker.entity.User;
import com.joseagim.traintracker.entity.UserRole;
import com.joseagim.traintracker.exception.DuplicateResourceException;
import com.joseagim.traintracker.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public RegisterResponseDto register(RegisterRequestDto registerRequest) {

        if (userRepository.findByEmail(registerRequest.email()).isPresent())
            throw new DuplicateResourceException("Already exists user with email: " + registerRequest.email());

        if (userRepository.findByPhoneNumber(registerRequest.phoneNumber()).isPresent())
            throw new DuplicateResourceException("Already exists user with phone number: " + registerRequest.phoneNumber());

        if (userRepository.findByDni(registerRequest.dni()).isPresent())
            throw new DuplicateResourceException("Already exists user with dni: " + registerRequest.dni());

        User user = new User();
        user.setRole(UserRole.ROLE_USER);
        user.setFirstName(registerRequest.firstName());
        user.setLastName(registerRequest.lastName());
        user.setEmail(registerRequest.email());
        user.setPhoneNumber(registerRequest.phoneNumber());
        user.setDni(registerRequest.dni());
        user.setPassword(passwordEncoder.encode(registerRequest.password()));

        User saved = userRepository.save(user);

        return RegisterResponseDto.from(saved);

    }

}
