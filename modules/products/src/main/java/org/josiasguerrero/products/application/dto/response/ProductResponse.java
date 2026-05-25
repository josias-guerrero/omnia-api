package org.josiasguerrero.products.application.dto.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Set;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Complete product information")
public record ProductResponse(
    @Schema(description = "Product unique identifier", example = "550e8400-e29b-41d4-a716-446655440000") String id,
    @Schema(description = "Unique SKU code", example = "SKU-001") String sku,
    @Schema(description = "Product name", example = "Wireless Mouse") String name,
    @Schema(description = "Product description", example = "A high-quality wireless mouse") String description,
    @Schema(description = "Barcode (EAN/UPC)", example = "123456789012") String barcode,
    @Schema(description = "Product cost price", example = "15.50") BigDecimal cost,
    @Schema(description = "Product sale price", example = "29.99") BigDecimal price,
    @Schema(description = "Current stock quantity", example = "100") Integer stock,
    @Schema(description = "Associated brand") BrandPResponse brand,
    @Schema(description = "Associated categories") Set<CategoryPResponse> categories,
    @Schema(description = "Custom properties as key-value pairs", example = "{\"color\": \"black\"}") Map<String, String> properties,
    @Schema(description = "Creation timestamp") LocalDateTime createdAt,
    @Schema(description = "Last update timestamp") LocalDateTime updatedAt) {
}
