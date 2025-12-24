package org.josiasguerrero.products.application.usecase.property;

import org.josiasguerrero.products.application.dto.request.UpdatePropertyRequest;
import org.josiasguerrero.products.application.dto.response.PropertyResponse;
import org.josiasguerrero.products.application.mapper.PropertyMapper;
import org.josiasguerrero.products.domain.entity.Property;
import org.josiasguerrero.products.domain.exception.DuplicatePropertyNameException;
import org.josiasguerrero.products.domain.exception.PropertyNotFoundException;
import org.josiasguerrero.products.domain.port.PropertyRepository;
import org.josiasguerrero.products.domain.valueobject.PropertyId;
import org.josiasguerrero.shared.aplication.validation.DtoValidator;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class UpdatePropertyUseCase {
  private PropertyRepository repository;
  private DtoValidator validator;

  public PropertyResponse exceute(Integer propId, UpdatePropertyRequest request) {
    validator.validate(request);

    if (!repository.existsByName(request.name())) {
      throw new DuplicatePropertyNameException(request.name());
    }

    PropertyId id = PropertyId.from(propId);

    Property property = repository.findById(id).orElseThrow(() -> new PropertyNotFoundException(id));

    if (request.name() != null) {
      property.updateName(request.name());
    }

    repository.save(property);
    return PropertyMapper.toResponse(property);
  }

}
