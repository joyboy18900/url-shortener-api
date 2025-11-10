package com.urlshortener.api.service;

import com.urlshortener.api.entity.ShortUrl;
import com.urlshortener.api.entity.User;
import com.urlshortener.api.exception.BadRequestException;
import com.urlshortener.api.exception.ResourceNotFoundException;
import com.urlshortener.api.repository.ShortUrlRepository;
import com.urlshortener.api.request.ShortenRequest;
import com.urlshortener.api.response.ShortenResponse;
import com.urlshortener.api.response.ShortUrlResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ShortUrlService {
    private final ShortUrlRepository shortUrlRepository;

    public ShortenResponse shorten(ShortenRequest request, User user) {
        validateUrl(request.getOriginal_url());

        String shortCode = generateShortCode();
        ShortUrl shortUrl = new ShortUrl();
        shortUrl.setOriginalUrl(request.getOriginal_url());
        shortUrl.setShortCode(shortCode);
        shortUrl.setUser(user);
        shortUrlRepository.save(shortUrl);

        return new ShortenResponse("http://localhost:8080/r/" + shortCode);
    }

    public Optional<String> getOriginalUrl(String shortCode) {
        return shortUrlRepository.findByShortCode(shortCode)
                .filter(ShortUrl::getActive)
                .map(ShortUrl::getOriginalUrl);
    }

    public List<ShortUrlResponse> getUserUrls(Long userId) {
        List<ShortUrl> urls = shortUrlRepository.findByUserId(userId);
        return urls.stream()
                .filter(ShortUrl::getActive)
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public void deleteUrl(Long urlId, Long userId) {
        ShortUrl shortUrl = shortUrlRepository.findById(urlId)
                .orElseThrow(() -> new ResourceNotFoundException("URL not found"));

        User owner = shortUrl.getUser();
        if (owner == null || !owner.getId().equals(userId)) {
            throw new BadRequestException("Unauthorized to delete this URL");
        }

        shortUrlRepository.deleteById(urlId);
    }

    private String generateShortCode() {
        String shortCode;
        do {
            shortCode = generateRandomCode();
        } while (shortUrlRepository.existsByShortCode(shortCode));
        return shortCode;
    }

    private String generateRandomCode() {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        StringBuilder code = new StringBuilder();
        for (int i = 0; i < 6; i++) {
            code.append(chars.charAt((int) (Math.random() * chars.length())));
        }
        return code.toString();
    }

    private ShortUrlResponse mapToResponse(ShortUrl shortUrl) {
        return new ShortUrlResponse(shortUrl.getId(), shortUrl.getShortCode(),
                shortUrl.getOriginalUrl(), shortUrl.getCreatedAt(), shortUrl.getActive());
    }

    private void validateUrl(String url) {
        if (url == null || url.trim().isEmpty()) {
            throw new BadRequestException("URL cannot be empty");
        }

        if (!url.startsWith("http://") && !url.startsWith("https://")) {
            throw new BadRequestException("URL must start with http:// or https://");
        }

        if (url.length() > 2048) {
            throw new BadRequestException("URL is too long");
        }
    }
}