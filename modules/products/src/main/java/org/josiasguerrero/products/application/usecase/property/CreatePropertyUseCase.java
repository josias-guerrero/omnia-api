package org.josiasguerrero.products.application.usecase.property;

import org.josiasguerrero.products.application.dto.request.CreatePropertyRequest;
import org.josiasguerrero.products.application.dto.response.PropertyResponse;
import org.josiasguerrero.products.application.mapper.PropertyMapper;
import org.josiasguerrero.products.domain.entity.Property;
import org.josiasguerrero.products.domain.exception.DuplicatePropertyNameException;
import org.josiasguerrero.products.domain.port.PropertyRepository;
import org.josiasguerrero.shared.aplication.validation.DtoValidator;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class CreatePropertyUseCase {
  private PropertyRepository repository;
  private DtoValidator validator;

  public PropertyResponse execute(CreatePropertyRequest request) {
    validator.validate(request);

    if (repository.existsByName(request.name())) {
      throw new DuplicatePropertyNameException(request.name());
    }

    Property property = new Property(request.name());

    repository.save(property);

    return PropertyMapper.toResponse(property);
  }

}
