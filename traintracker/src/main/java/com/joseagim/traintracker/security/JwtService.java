package com.joseagim.traintracker.security;

import com.joseagim.traintracker.entity.User;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;

@Service
public class JwtService {

    @Value("${jwt.key}")
    private String key;

    @Value("${jwt.expiration-minutes")
    private Long expirationMinutes;

    public String generateToken(User user) {

        SecretKey secretKey = Keys.hmacShaKeyFor(key.getBytes());


        return "";
    }

}
