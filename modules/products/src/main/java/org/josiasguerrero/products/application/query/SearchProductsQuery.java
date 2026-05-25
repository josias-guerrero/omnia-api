package org.josiasguerrero.products.application.query;

import java.util.List;

public record SearchProductsQuery(
    Long brandId,
    List<Long> categoryIds,
    Integer lowStockThreshold,
    String searchTerm,
    int page,
    int size) {
}
