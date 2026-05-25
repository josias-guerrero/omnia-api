package org.josiasguerrero.products.application.dto.request;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Set;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

@Schema(description = "Request payload for creating a new product")
public record CreateProductRequest(
    @NotBlank @Size(max = 50) @Schema(description = "Unique SKU code", example = "SKU-001", maxLength = 50) String sku,
    @NotBlank @Size(max = 100) @Schema(description = "Product name", example = "Wireless Mouse", maxLength = 100) String name,
    @NotNull @Positive @Schema(description = "Product cost price", example = "15.50") BigDecimal cost,
    @NotNull @Positive @Schema(description = "Product sale price", example = "29.99") BigDecimal price,
    @NotNull @PositiveOrZero @Schema(description = "Current stock quantity", example = "100") Integer stock,
    @Schema(description = "Product description", example = "A high-quality wireless mouse") String description,
    @Schema(description = "Barcode (EAN/UPC)", example = "123456789012") String barcode,
    @Schema(description = "Brand ID to associate", example = "1") Integer brandId,
    @Schema(description = "Set of category IDs to associate", example = "[1, 2, 3]") Set<Integer> categoryIds,
    @Schema(description = "Custom properties as key-value pairs", example = "{\"color\": \"black\", \"weight\": \"150g\"}") Map<String, String> properties) {
}
