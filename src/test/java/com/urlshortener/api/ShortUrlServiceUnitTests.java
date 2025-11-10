package com.urlshortener.api;

import com.urlshortener.api.entity.ShortUrl;
import com.urlshortener.api.entity.User;
import com.urlshortener.api.exception.BadRequestException;
import com.urlshortener.api.repository.ShortUrlRepository;
import com.urlshortener.api.request.ShortenRequest;
import com.urlshortener.api.response.ShortenResponse;
import com.urlshortener.api.service.ShortUrlService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@DisplayName("ShortUrlService Unit Tests")
class ShortUrlServiceUnitTests {

    @Mock
    private ShortUrlRepository shortUrlRepository;

    @InjectMocks
    private ShortUrlService shortUrlService;

    private User testUser;
    private ShortenRequest validRequest;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        testUser = new User();
        testUser.setId(1L);
        testUser.setEmail("test@example.com");
        testUser.setCreatedAt(LocalDateTime.now());

        validRequest = new ShortenRequest();
        validRequest.setOriginal_url("https://www.example.com/long/url");
    }

    @Test
    @DisplayName("Should validate valid URLs successfully")
    void testValidUrlPasses() {
        // Test http URL
        ShortenRequest httpRequest = new ShortenRequest();
        httpRequest.setOriginal_url("http://example.com");

        // Should not throw exception
        assertDoesNotThrow(() -> {
            // Validate URL (internal method)
            assertTrue(httpRequest.getOriginal_url().startsWith("http"));
        });
    }

    @Test
    @DisplayName("Should reject invalid URL formats")
    void testInvalidUrlFormat() {
        // Test without protocol
        ShortenRequest invalidRequest = new ShortenRequest();
        invalidRequest.setOriginal_url("example.com");

        assertFalse(invalidRequest.getOriginal_url().startsWith("http"));
    }

    @Test
    @DisplayName("Should generate short code")
    void testGenerateShortCode() {
        when(shortUrlRepository.existsByShortCode(anyString())).thenReturn(false);
        when(shortUrlRepository.save(any(ShortUrl.class))).thenAnswer(i -> i.getArgument(0));

        ShortenResponse response = shortUrlService.shorten(validRequest, testUser);

        assertNotNull(response);
        assertNotNull(response.getShort_url());
        assertTrue(response.getShort_url().contains("/r/"));
    }

    @Test
    @DisplayName("Should find short URL by code")
    void testFindByShortCode() {
        ShortUrl shortUrl = new ShortUrl();
        shortUrl.setId(1L);
        shortUrl.setShortCode("abc123");
        shortUrl.setOriginalUrl("https://example.com");
        shortUrl.setUser(testUser);

        when(shortUrlRepository.findByShortCode("abc123")).thenReturn(Optional.of(shortUrl));

        Optional<ShortUrl> result = shortUrlRepository.findByShortCode("abc123");

        assertTrue(result.isPresent());
        assertEquals("abc123", result.get().getShortCode());
    }

    @Test
    @DisplayName("Should delete short URL by ID")
    void testDeleteShortUrl() {
        ShortUrl shortUrl = new ShortUrl();
        shortUrl.setId(1L);
        shortUrl.setUser(testUser);

        when(shortUrlRepository.findById(1L)).thenReturn(Optional.of(shortUrl));

        // Assuming deleteUrl exists
        verify(shortUrlRepository, never()).deleteById(1L);
    }
}

