package org.josiasguerrero.products.application.dto.response;

import java.time.LocalDateTime;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Full category information")
public record CategoryResponse(
    @Schema(description = "Category ID", example = "1") Integer id,
    @Schema(description = "Category name", example = "Electronics") String name,
    @Schema(description = "Category description", example = "Electronic devices and accessories") String description,
    @Schema(description = "Creation timestamp") LocalDateTime createdAt,
    @Schema(description = "Last update timestamp") LocalDateTime updatedAt) {
}
