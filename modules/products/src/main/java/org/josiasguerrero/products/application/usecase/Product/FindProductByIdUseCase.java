package org.josiasguerrero.products.application.usecase.Product;

import org.josiasguerrero.products.application.dto.response.ProductResponse;
import org.josiasguerrero.products.application.mapper.ProductApplicationMapper;
import org.josiasguerrero.products.domain.entity.Product;
import org.josiasguerrero.products.domain.exception.ProductNotFoundException;
import org.josiasguerrero.products.domain.port.ProductRepository;
import org.josiasguerrero.products.domain.valueobject.ProductId;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class FindProductByIdUseCase {

  ProductRepository productRepository;

  public ProductResponse execute(String productId) {
    ProductId id = ProductId.from(productId);
    if (productId == null) {
      throw new IllegalArgumentException("Product id must not be null");
    }

    Product product = productRepository.findById(id)
        .orElseThrow(() -> new ProductNotFoundException(id));

    return ProductApplicationMapper.toResponse(product);

  }
}
