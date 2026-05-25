package org.josiasguerrero.products.application.dto.response;

import java.time.LocalDateTime;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Custom property definition information")
public record PropertyResponse(
    @Schema(description = "Property ID", example = "1") Integer id,
    @Schema(description = "Property name", example = "color") String name,
    @Schema(description = "Creation timestamp") LocalDateTime createdAt,
    @Schema(description = "Last update timestamp") LocalDateTime updatedAt) {
}
