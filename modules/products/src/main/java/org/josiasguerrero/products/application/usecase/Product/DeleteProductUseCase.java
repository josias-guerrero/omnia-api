package org.josiasguerrero.products.application.usecase.Product;

import org.josiasguerrero.products.domain.port.ProductRepository;
import org.josiasguerrero.products.domain.valueobject.ProductId;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class DeleteProductUseCase {

  private ProductRepository productRepository;

  public void execute(String productId) {
    ProductId id = ProductId.from(productId);
    productRepository.delete(id);
  }
}
