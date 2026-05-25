package org.josiasguerrero.products.application.dto.request;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Set;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

@Schema(description = "Request payload for updating an existing product (partial update)")
public record UpdateProductRequest(
    @Size(max = 50, message = "SKU cannot exceed 50 characters")
    @Schema(description = "Unique SKU code", example = "SKU-001", maxLength = 50) String sku,
    @Size(max = 100, message = "Name cannot exceed 100 characters")
    @Schema(description = "Product name", example = "Wireless Mouse", maxLength = 100) String name,
    @Schema(description = "Product description", example = "Updated description") String description,
    @Size(max = 50, message = "Barcode cannot exceed 50 characters")
    @Schema(description = "Barcode (EAN/UPC)", example = "123456789012", maxLength = 50) String barcode,
    @Positive(message = "Cost must be positive")
    @Schema(description = "Product cost price", example = "15.50") BigDecimal cost,
    @Positive(message = "Price must be positive")
    @Schema(description = "Product sale price", example = "29.99") BigDecimal price,
    @PositiveOrZero(message = "Stock cannot be negative")
    @Schema(description = "Current stock quantity", example = "100") Integer stock,
    @Schema(description = "Brand ID to associate", example = "1") String brandId,
    @Schema(description = "Set of category IDs to associate", example = "[1, 2, 3]") Set<Integer> categoryIds,
    @Schema(description = "Custom properties as key-value pairs", example = "{\"color\": \"black\"}") Map<String, String> properties) {
}
