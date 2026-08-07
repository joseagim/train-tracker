package com.joseagim.traintracker.security;

import com.joseagim.traintracker.entity.User;
import com.joseagim.traintracker.entity.UserRole;
import io.jsonwebtoken.Claims;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
public class JwtServiceTest {

    @Autowired
    private JwtService jwtService;

    @Test
    void generateToken_checksDecodeToken() {

        User user = new User();
        user.setEmail("prueba@gmail.com");
        user.setRole(UserRole.ROLE_USER);

        String token = jwtService.generateToken(user);

        Claims claims = jwtService.extractClaims(token);

        assertEquals("prueba@gmail.com", claims.getSubject());
        assertEquals(UserRole.ROLE_USER.name(), claims.get("role"));

    }

}
