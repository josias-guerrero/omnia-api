package org.josiasguerrero.products.application.usecase.property;

import java.util.List;

import org.josiasguerrero.products.application.dto.response.PropertyResponse;
import org.josiasguerrero.products.application.mapper.PropertyMapper;
import org.josiasguerrero.products.domain.port.PropertyRepository;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class FindAllPropertiesUseCase {

  private PropertyRepository propertyRepository;

  public List<PropertyResponse> execute() {
    return propertyRepository.findAll().stream().map(PropertyMapper::toResponse).toList();
  }
}
