package org.josiasguerrero.products.application.usecase.Product;

import org.josiasguerrero.products.application.dto.response.ProductResponse;
import org.josiasguerrero.products.application.mapper.ProductApplicationMapper;
import org.josiasguerrero.products.application.query.SearchProductsQuery;
import org.josiasguerrero.products.domain.port.ProductRepository;
import org.josiasguerrero.shared.domain.criteria.Criteria;
import org.josiasguerrero.shared.domain.criteria.CriteriaBuilder;
import org.josiasguerrero.shared.domain.criteria.Filter;
import org.josiasguerrero.shared.domain.criteria.Order;
import org.josiasguerrero.shared.domain.pagination.Page;
import org.josiasguerrero.shared.domain.pagination.PageRequest;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class SearchProductsQueryHandler {
  private final ProductRepository productRepository;
  private final ProductApplicationMapper mapper;

  public Page<ProductResponse> handle(SearchProductsQuery query) {
    CriteriaBuilder builder = Criteria.builder()
        .pageRequest(PageRequest.of(query.page(), query.size()));

    // Solo agregar filtros si tienen valor
    if (query.brandId() != null) {
      builder.filter(Filter.equal("brand.id", String.valueOf(query.brandId())));
    }

    if (query.categoryIds() != null && !query.categoryIds().isEmpty()) {
      builder.filter(Filter.anyIn("categories", query.categoryIds()));
    }

    if (query.lowStockThreshold() != null) {
      builder.filter(Filter.lessThan("stock", String.valueOf(query.lowStockThreshold())));
    }

    if (query.searchTerm() != null && !query.searchTerm().isBlank()) {
      builder.filter(Filter.contains("name", query.searchTerm()));
    }

    Criteria criteria = builder.order(Order.desc("name")).build();
    return productRepository.findByCriteria(criteria).map(mapper::toResponse);
  }
}
