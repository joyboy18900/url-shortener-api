package com.urlshortener.api.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ShortUrlResponse {
    private Long id;
    private String shortCode;
    private String originalUrl;
    private LocalDateTime createdAt;
    private Boolean active;
}
