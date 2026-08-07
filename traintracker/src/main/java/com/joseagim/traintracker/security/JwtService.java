package com.joseagim.traintracker.security;

import com.joseagim.traintracker.entity.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;

@Service
public class JwtService {

    @Value("${jwt.key}")
    private String key;

    @Value("${jwt.expiration-minutes}")
    private Long expirationMinutes;

    public String generateToken(User user) {

        Date now = new Date();
        Date expiration = new Date(now.getTime() + expirationMinutes * 60 * 1000);

        return Jwts.builder()
                .subject(user.getEmail())
                .claim("role", user.getRole().name())
                .issuedAt(now)
                .expiration(expiration)
                .signWith(getSecretKey())
                .compact();

    }

    public Claims extractClaims(String token) {

        return Jwts.parser()
                .verifyWith(getSecretKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();

    }

    private SecretKey getSecretKey() {
        return Keys.hmacShaKeyFor(key.getBytes());
    }

}
