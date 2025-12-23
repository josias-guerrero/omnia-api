package org.josiasguerrero.products.domain.exception;

public class DuplicatePropertyNameException extends RuntimeException {

  public DuplicatePropertyNameException(String name) {
    super("Property already exists with name: " + name);
  }
}
