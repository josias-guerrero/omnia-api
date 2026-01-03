package org.josiasguerrero.products.application.usecase.Product;

import org.josiasguerrero.products.application.dto.response.ProductResponse;
import org.josiasguerrero.products.application.mapper.ProductApplicationMapper;
import org.josiasguerrero.products.domain.entity.Product;
import org.josiasguerrero.products.domain.port.ProductRepository;
import org.josiasguerrero.shared.domain.pagination.Page;
import org.josiasguerrero.shared.domain.pagination.PageRequest;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class FindAllProductsUseCase {
  private ProductRepository productRepository;
  private final ProductApplicationMapper productApplicationMapper;

  public Page<ProductResponse> execute(PageRequest pageRequest) {
    Page<Product> productsPage = productRepository.findAll(pageRequest);
    return productsPage.map(productApplicationMapper::toResponse);
  }

  /**
   * Overload to keep compatibility with no pagination
   * 
   * @return first page by default
   */
  public Page<ProductResponse> execute() {
    return execute(PageRequest.defaultRequest());
  }
}
