package org.josiasguerrero.products.application.dto.request;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Set;

import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

public record UpdateProductRequest(
    @Size(max = 50, message = "SKU cannot exceed 50 characters") String sku,
    @Size(max = 100, message = "Name cannot exceed 100 characters") String name,
    String description,
    @Size(max = 50, message = "Barcode cannot exceed 50 characters") String barcode,
    @Positive(message = "Cost must be positive") BigDecimal cost,
    @Positive(message = "Price must be positive") BigDecimal price,
    @PositiveOrZero(message = "Stock cannot be negative") Integer stock,
    String brandId,
    Set<Integer> categoryIds,
    Map<String, String> properties) {
}
