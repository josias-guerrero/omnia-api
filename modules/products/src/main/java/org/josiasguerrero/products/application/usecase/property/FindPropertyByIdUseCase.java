package org.josiasguerrero.products.application.usecase.property;

import org.josiasguerrero.products.application.dto.response.PropertyResponse;
import org.josiasguerrero.products.application.mapper.PropertyMapper;
import org.josiasguerrero.products.domain.exception.PropertyNotFoundException;
import org.josiasguerrero.products.domain.port.PropertyRepository;
import org.josiasguerrero.products.domain.valueobject.PropertyId;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class FindPropertyByIdUseCase {
  private PropertyRepository repository;

  public PropertyResponse execute(Integer propId) {
    if (propId == null) {
      throw new IllegalArgumentException("Property ID must not be null");
    }
    PropertyId id = PropertyId.from(propId);
    return repository.findById(id).map(PropertyMapper::toRseponse)
        .orElseThrow(() -> new PropertyNotFoundException(id));
  }
}
