package org.josiasguerrero.products.domain.exception;

import org.josiasguerrero.products.domain.valueobject.BrandId;

public class BrandNotFoundException extends RuntimeException {
  public BrandNotFoundException(BrandId id) {
    super("Brand not found with id: " + id.value());
  }

  public BrandNotFoundException(String name) {
    super("Brand not found with name " + name);
  }
}
