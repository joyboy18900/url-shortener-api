package com.urlshortener.api.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;

@Configuration
public class JwtConfig {

    @Bean
    public SecretKey secretKey() {
        String secretKeyString = "KrungSriConsumerPassedAllTestsJWTSecretKeyProductionReady1234";
        byte[] decodedKey = Base64.getEncoder().encode(secretKeyString.getBytes());
        return new SecretKeySpec(decodedKey, 0, decodedKey.length, "HmacSHA512");
    }
}