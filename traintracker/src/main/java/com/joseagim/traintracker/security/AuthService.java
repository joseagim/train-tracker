package com.joseagim.traintracker.security;

import com.joseagim.traintracker.dto.request.LoginRequestDto;
import com.joseagim.traintracker.dto.request.RegisterRequestDto;
import com.joseagim.traintracker.dto.response.LoginResponseDto;
import com.joseagim.traintracker.dto.response.RegisterResponseDto;
import com.joseagim.traintracker.entity.User;
import com.joseagim.traintracker.entity.UserRole;
import com.joseagim.traintracker.exception.DuplicateResourceException;
import com.joseagim.traintracker.repository.UserRepository;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    private final JwtService jwtService;

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtService jwtService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
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

    public LoginResponseDto login(LoginRequestDto loginRequest) {

        User user = userRepository.findByEmail(loginRequest.email())
                .orElseThrow(() -> new BadCredentialsException("Invalid email or password"));

        if (!passwordEncoder.matches(loginRequest.password(), user.getPassword()))
            throw new BadCredentialsException("Invalid email or password");

        String token = jwtService.generateToken(user);

        return LoginResponseDto.from(user, token);

    }

}
