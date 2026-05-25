package org.josiasguerrero.products.application.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(description = "Request payload for creating a category")
public record CategoryRequest(
    @NotBlank @Size(max = 50) @Schema(description = "Category name", example = "Electronics", maxLength = 50) String name,
    @Size(max = 100) @Schema(description = "Category description", example = "Electronic devices and accessories", maxLength = 100) String description) {
}
