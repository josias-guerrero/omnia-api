package org.josiasguerrero.products.application.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import java.util.Set;

@Schema(description = "Complete product information")
public record ProductResponse(
    @Schema(
            description = "Product unique identifier",
            example = "550e8400-e29b-41d4-a716-446655440000")
        String id,
    @Schema(description = "Product name", example = "Wireless Mouse") String name,
    @Schema(description = "Product description", example = "A high-quality wireless mouse")
        String description,
    @Schema(description = "Associated brand") BrandPResponse brand,
    @Schema(description = "Associated categories") Set<CategoryPResponse> categories,
    @Schema(description = "Product variants") Set<ProductVariantResponse> variants,
    @Schema(description = "Creation timestamp") LocalDateTime createdAt,
    @Schema(description = "Last update timestamp") LocalDateTime updatedAt) {}
