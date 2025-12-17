package org.josiasguerrero.products.application.dto.request;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Set;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

public record CreateProductRequest(
    @NotBlank @Size(max = 50) String sku,
    @NotBlank @Size(max = 100) String name,
    @NotNull @Positive BigDecimal cost,
    @NotNull @Positive BigDecimal price,
    @NotNull @PositiveOrZero Integer initialStock,
    String description,
    String barcode,
    Integer brandId,
    Set<Integer> categoryIds,
    Map<Integer, String> properties) {
}
