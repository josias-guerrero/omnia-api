package org.josiasguerrero.products.application.dto.response;

import java.time.LocalDateTime;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Full brand information")
public record BrandResponse(
    @Schema(description = "Brand ID", example = "1") Integer id,
    @Schema(description = "Brand name", example = "Logitech") String name,
    @Schema(description = "Creation timestamp") LocalDateTime createdAt,
    @Schema(description = "Last update timestamp") LocalDateTime updatedAt) {
}
