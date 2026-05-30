package org.josiasguerrero.products.application.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import java.util.Map;

@Schema(description = "Request payload for creating a new product variant")
public record CreateProductVariantRequest(
    @NotBlank(message = "SKU is required")
        @Size(max = 50, message = "SKU cannot exceed 50 characters")
        @Schema(description = "Unique SKU code", example = "SKU-001-RED", maxLength = 50)
        String sku,
    @Size(max = 50, message = "Barcode cannot exceed 50 characters")
        @Schema(description = "Barcode (EAN/UPC)", example = "123456789012", maxLength = 50)
        String barcode,
    @NotNull(message = "Cost is required")
        @Positive(message = "Cost must be positive")
        @Schema(description = "Product cost price", example = "15.50")
        BigDecimal cost,
    @NotNull(message = "Price is required")
        @Positive(message = "Price must be positive")
        @Schema(description = "Product sale price", example = "29.99")
        BigDecimal price,
    @PositiveOrZero(message = "Stock cannot be negative")
        @Schema(description = "Current stock quantity", example = "100")
        Integer stock,
    @Schema(description = "Custom properties as key-value pairs", example = "{\"color\": \"red\"}")
        Map<String, String> properties) {}
