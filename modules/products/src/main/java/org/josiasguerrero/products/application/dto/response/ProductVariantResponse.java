package org.josiasguerrero.products.application.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;

@Schema(description = "Product variant details")
public record ProductVariantResponse(
    @Schema(
            description = "Variant unique identifier",
            example = "550e8400-e29b-41d4-a716-446655440001")
        String id,
    @Schema(description = "Unique SKU code", example = "SKU-001-RED") String sku,
    @Schema(description = "Barcode (EAN/UPC)", example = "123456789012") String barcode,
    @Schema(description = "Current stock quantity", example = "50") Integer stock,
    @Schema(description = "Product cost price", example = "15.50") BigDecimal cost,
    @Schema(description = "Product sale price", example = "29.99") BigDecimal price,
    @Schema(description = "Custom properties as key-value pairs", example = "{\"color\": \"red\"}")
        Map<String, String> properties,
    @Schema(description = "Creation timestamp") LocalDateTime createdAt,
    @Schema(description = "Last update timestamp") LocalDateTime updatedAt) {}
