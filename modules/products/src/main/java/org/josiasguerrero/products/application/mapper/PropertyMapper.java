package org.josiasguerrero.products.application.mapper;

import org.josiasguerrero.products.application.dto.response.PropertyResponse;
import org.josiasguerrero.products.domain.entity.Property;

public class PropertyMapper {

  public static PropertyResponse toRseponse(Property property) {
    return new PropertyResponse(
        property.getId().value(),
        property.getName(),
        property.getCreatedAt(),
        property.getUpdatedAt());
  }
}
