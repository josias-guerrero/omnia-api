package org.josiasguerrero.products.domain.exception;

import org.josiasguerrero.products.domain.valueobject.CategoryId;

public class CategoryNotFoundException extends RuntimeException {
  public CategoryNotFoundException(CategoryId id) {
    super("Category not found with id: " + id.value());
  }
}
