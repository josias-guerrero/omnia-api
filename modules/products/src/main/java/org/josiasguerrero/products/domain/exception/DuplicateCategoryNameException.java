package org.josiasguerrero.products.domain.exception;

public class DuplicateCategoryNameException extends RuntimeException {
  public DuplicateCategoryNameException(String name) {
    super("Category Already exists with name: " + name);
  }

}
