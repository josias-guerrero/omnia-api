package org.josiasguerrero.products.domain.exception;

import org.josiasguerrero.products.domain.valueobject.PropertyId;

public class PropertyNotFoundException extends RuntimeException {
  public PropertyNotFoundException(PropertyId id) {
    super("Property not found with id: " + id.value());
  }
}
