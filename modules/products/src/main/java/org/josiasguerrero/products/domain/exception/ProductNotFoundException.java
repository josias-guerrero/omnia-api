package org.josiasguerrero.products.domain.exception;

import org.josiasguerrero.products.domain.valueobject.ProductId;

public class ProductNotFoundException extends RuntimeException {
  public ProductNotFoundException(ProductId id) {
    super("Product not found with id: " + id.value());
  }
}
