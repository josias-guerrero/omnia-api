package org.josiasguerrero.products.domain.exception;

import org.josiasguerrero.products.domain.valueobject.Sku;

public class DuplicateSkuException extends RuntimeException {
  public DuplicateSkuException(Sku sku) {
    super("Product with SKU already exists: " + sku.value());
  }
}
