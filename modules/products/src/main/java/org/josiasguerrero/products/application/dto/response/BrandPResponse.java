package org.josiasguerrero.products.application.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Lightweight brand information (nested within product)")
public record BrandPResponse(
    @Schema(description = "Brand ID", example = "1") Integer id,
    @Schema(description = "Brand name", example = "Logitech") String name) {
}
