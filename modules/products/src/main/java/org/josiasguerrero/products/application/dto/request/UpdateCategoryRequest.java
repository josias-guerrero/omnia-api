package org.josiasguerrero.products.application.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;

@Schema(description = "Request payload for updating a category")
public record UpdateCategoryRequest(
    @Size(max = 50) @Schema(description = "Category name", example = "Electronics", maxLength = 50) String name,
    @Size(max = 100) @Schema(description = "Category description", example = "Updated description", maxLength = 100) String description) {
}
