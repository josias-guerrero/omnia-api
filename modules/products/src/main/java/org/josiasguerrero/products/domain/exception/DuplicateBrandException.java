package org.josiasguerrero.products.domain.exception;

public class DuplicateBrandException extends RuntimeException {

  public DuplicateBrandException(String name) {
    super("Duplicate brand with name: " + name);
  }
}
