package org.josiasguerrero.products.application.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Lightweight category information (nested within product)")
public record CategoryPResponse(
    @Schema(description = "Category ID", example = "1") Integer id,
    @Schema(description = "Category name", example = "Electronics") String name) {
}
