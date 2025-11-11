package com.urlshortener.api.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;

@Configuration
public class JwtConfig {

    @Value("${jwt.secret-key}")
    private String secretKeyString;

    @Value("${jwt.algorithm}")
    private String algorithm;

    @Bean
    public SecretKey secretKey() {
        byte[] decodedKey = Base64.getEncoder().encode(secretKeyString.getBytes());
        return new SecretKeySpec(decodedKey, 0, decodedKey.length, algorithm);
    }
}