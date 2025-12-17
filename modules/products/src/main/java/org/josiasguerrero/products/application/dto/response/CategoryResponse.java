package org.josiasguerrero.products.application.dto.response;

import java.time.LocalDateTime;

public record CategoryResponse(Integer id, String name, String description, LocalDateTime createdAt,
    LocalDateTime updatedAt) {
}
