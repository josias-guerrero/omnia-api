package org.josiasguerrero.products.application.dto.request;

import java.util.Set;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;

@Schema(description = "Request payload for updating an existing product (partial update)")
public record UpdateProductRequest(
    @Size(max = 100, message = "Name cannot exceed 100 characters")
    @Schema(description = "Product name", example = "Wireless Mouse", maxLength = 100) String name,
    @Schema(description = "Product description", example = "Updated description") String description,
    @Schema(description = "Brand ID to associate", example = "1") String brandId,
    @Schema(description = "Set of category IDs to associate", example = "[1, 2, 3]") Set<Integer> categoryIds) {
}
