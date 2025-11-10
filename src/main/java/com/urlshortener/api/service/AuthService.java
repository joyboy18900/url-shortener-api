package com.urlshortener.api.service;

import com.urlshortener.api.entity.User;
import com.urlshortener.api.exception.BadRequestException;
import com.urlshortener.api.exception.ResourceNotFoundException;
import com.urlshortener.api.repository.UserRepository;
import com.urlshortener.api.request.UserRequest;
import com.urlshortener.api.response.LoginResponse;
import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;

@Service
public class AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final SecretKey secretKey;
    private final long jwtExpiration;

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder,
                      SecretKey secretKey, @Value("${jwt.expiration}") long jwtExpiration) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.secretKey = secretKey;
        this.jwtExpiration = jwtExpiration;
    }

    public LoginResponse login(UserRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new BadRequestException("Invalid credentials");
        }

        String token = generateToken(user);
        return new LoginResponse(token, user.getId(), user.getEmail());
    }

    private String generateToken(User user) {
        long jwtExpirationMs = 3600000L; // 1 hour
        return Jwts.builder()
                .subject(user.getEmail())
                .claim("userId", user.getId())
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + jwtExpirationMs))
                .signWith(secretKey, Jwts.SIG.HS512)
                .compact();
    }
}