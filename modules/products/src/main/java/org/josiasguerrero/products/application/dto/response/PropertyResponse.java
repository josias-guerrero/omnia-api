package org.josiasguerrero.products.application.dto.response;

import java.time.LocalDateTime;

public record PropertyResponse(Integer id, String name, LocalDateTime createdAt, LocalDateTime updatedAt) {
}
