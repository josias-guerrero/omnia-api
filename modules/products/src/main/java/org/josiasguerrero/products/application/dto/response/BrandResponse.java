package org.josiasguerrero.products.application.dto.response;

import java.time.LocalDateTime;

public record BrandResponse(Integer id, String name, LocalDateTime createdAt, LocalDateTime updatedAt) {
}
