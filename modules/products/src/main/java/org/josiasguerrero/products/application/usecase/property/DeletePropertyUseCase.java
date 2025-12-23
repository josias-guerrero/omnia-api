package org.josiasguerrero.products.application.usecase.property;

import org.josiasguerrero.products.domain.port.PropertyRepository;
import org.josiasguerrero.products.domain.valueobject.PropertyId;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class DeletePropertyUseCase {
  private PropertyRepository repository;

  public void execute(Integer propId) {
    PropertyId id = PropertyId.from(propId);

    repository.delete(id);
  }
}
