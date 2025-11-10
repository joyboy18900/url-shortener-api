package com.urlshortener.api.controller;

import com.urlshortener.api.entity.User;
import com.urlshortener.api.request.ShortenRequest;
import com.urlshortener.api.response.ShortenResponse;
import com.urlshortener.api.response.ShortUrlResponse;
import com.urlshortener.api.service.ShortUrlService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class UrlController {
    private final ShortUrlService shortUrlService;

    @PostMapping("/shorten")
    public ResponseEntity<ShortenResponse> shorten(@RequestBody ShortenRequest request, Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        ShortenResponse response = shortUrlService.shorten(request, user);
        return ResponseEntity.status(201).body(response);
    }

    @GetMapping("/urls")
    public ResponseEntity<List<ShortUrlResponse>> getUserUrls(Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        List<ShortUrlResponse> urls = shortUrlService.getUserUrls(user.getId());
        return ResponseEntity.ok(urls);
    }

    @DeleteMapping("/urls/{id}")
    public ResponseEntity<Void> deleteUrl(@PathVariable Long id, Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        shortUrlService.deleteUrl(id, user.getId());
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/r/{shortCode}")
    public ResponseEntity<Void> redirect(@PathVariable String shortCode) {
        return shortUrlService.getOriginalUrl(shortCode)
                .map(url -> ResponseEntity.status(301).header("Location", url).<Void>build())
                .orElseGet(() -> ResponseEntity.<Void>notFound().build());
    }
}